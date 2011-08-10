package com.curiousattemptbunny.git.jira

import graffiti.*

class Cli {
    private static final File configFile = new File('config.properties')
    Properties config = new Properties()

    @Lazy(soft=true) def commitIssues = {
        def commitLines = "git --git-dir=$config.repository/.git log --date=short --format=%h#%ad#%an#%s".execute().text.split('\n')
        def commits = []
        def commitCount = commitLines.size()
        commitLines.eachWithIndex { line, i ->
            def parts = line.split("#") as List
            parts[3] = parts.size() == 3 ? "" : parts[3..-1].join("#")
            def issues = []
            parts[3].eachMatch( /([A-Z]+-[0-9]+)/) {
                issues << [id:it[1], link:config.issueUrl+it[1]]
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
		def to = from+100
		def commits = []
		def json = new groovy.json.JsonBuilder(commitIssues[from..to])
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
