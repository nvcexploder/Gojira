package com.curiousattemptbunny.git.jira

import graffiti.*

class Cli {
    private static final File configFile = new File('config.properties')
    Properties config = new Properties()

	@Lazy(soft=true) def commitHashes = {
		"git --git-dir=$config.repository/.git rev-list master".execute().text.split('\n').collect { it[0..7] }
	}()

    @Lazy(soft=true) def commitIssues = {
        def commitLines = "git --git-dir=$config.repository/.git log --pretty=oneline".execute().text.split('\n')
        def commits = []
        def commitCount = commitLines.size()
        commitLines.eachWithIndex { line, i ->
            def commit = [:]
            commit.id = commitCount - i
            commit.hash = line[0..6]
            commit.issues = []
            line.substring(line.indexOf(' ')).eachMatch( /([A-Z]+-[0-9]+)/) {
                commit.issues << it[1]
            }
            commits << commit
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
		def json = new groovy.json.JsonBuilder(commitIssues[0..100])
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
