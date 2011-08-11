package com.curiousattemptbunny.git.jira

import groovy.json.*
import graffiti.*
import com.atlassian.jira.rest.client.JiraRestClient 
import com.atlassian.jira.rest.client.NullProgressMonitor 
import com.atlassian.jira.rest.client.domain.Issue 
import com.atlassian.jira.rest.client.internal.jersey.JerseyJiraRestClientFactory 

class Cli {
    private static final File configFile = new File('config.properties')
    Properties config = new Properties()
    JerseyJiraRestClientFactory factory = new JerseyJiraRestClientFactory()
	JiraRestClient restClient
	NullProgressMonitor pm = new NullProgressMonitor()
	def issues = [:].withDefault { key ->
		return restClient.getIssueClient().getIssue(key, pm) 		
	}
	def issueStateIconUrl = [
		"Open": "/images/icons/status_open.gif",
		"In Development": "/images/icons/status_inprogress.gif",
		"In Progress": "/images/icons/status_inprogress.gif",
		"In QA": "/images/icons/status_visible.gif",
		"Pending Release": "/images/icons/status_needinfo.gif",
		"Resolved": "/images/icons/status_resolved.gif",
		"Closed": "/images/icons/status_closed.gif",
		"Completed": "/images/icons/status_closed.gif",
		"Released": "/images/icons/status_closed.gif"
	]
			
    @Lazy(soft=true) def commitIssues = {
        def commitLines = "git --git-dir=$config.repository/.git log --date=short --format=%h#%ad#%an#%s".execute().text.split('\n')
        def commits = []
        def commitCount = commitLines.size()
        commitLines.eachWithIndex { line, i ->
            def parts = line.split("#") as List
            parts[3] = parts.size() == 3 ? "" : parts[3..-1].join("#")
            def issues = []
            parts[3].eachMatch( /([A-Z]+-[0-9]+)/) {
				issues << [id:it[1], link:config.jiraUrl+"/browse/"+it[1]]
            }
            issues.each { issue ->
	            def commit = [:]
				commit.id = commitCount - i
				commit.hash = parts[0]
				commit.date = parts[1]
				commit.author = parts[2]
				commit.comment = parts[3]
				commit.issue = issue
	            commits << commit
	        }
        }
        return commits
    }()
	
	Cli() {
		if (configFile.exists()) {
            config.load configFile.newInputStream()
        }
	}

    @Get('/configured')
    def get_configured() {
        def json = new groovy.json.JsonBuilder(configured: (config.repository != null))
		json.toString()
    }

	@Get('/commits')
	def get_commits() {
		def from = ((params.from ?: 0) as Integer)
		def to = from+49
		def commits = commitIssues[from..to]
		
		restClient = factory.createWithBasicHttpAuthentication(new URI(config.jiraUrl), config.jiraUsername, config.jiraPassword)
		
		commits.each { commit ->
			if (commit.summary == null) {
				def issue = issues[commit.issue.id]
				commit.issue.summary = issue.summary
				commit.issue.status = [
					name: issue.status?.name.toString(),
					iconUrl: config.jiraUrl+issueStateIconUrl[issue.status?.name.toString()]
				]
				//println "$issue.status.name -> $issue.status.self"
			}
		}
		def json = new groovy.json.JsonBuilder(commits)
		json.toString()
	}
	
	def run() {
		Graffiti.root 'src/main/web'
		Graffiti.serve '*.css'
		Graffiti.serve this
		Graffiti.start()
	}
	
	static void main(String[] args) {
		new Cli().run()
	}
}
