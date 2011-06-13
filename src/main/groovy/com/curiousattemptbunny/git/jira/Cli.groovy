package com.curiousattemptbunny.git.jira

import graffiti.*

class Cli {
	Properties config = new Properties()
	@Lazy(soft=true) def commitHashes = {
		"git --git-dir=$config.repository/.git rev-list master".execute().text.split('\n').collect { it[0..7] }
	}()
	
	Cli() {
		config.load new File('config.properties').newInputStream()
	}
	
	@Get('/commits')
	def get_commits() {
		def json = new groovy.json.JsonBuilder()
		json {
			commits( commitHashes.findAll { it.startsWith(params.q) }.collect { [hash:it] } )
		}
		json.toString()
	}

	@Get('/issues')
	def get_issues() {
		def _issues = []
		"git --git-dir=$config.repository/.git log ${params.from}..${params.to} --pretty=format:%s".execute().text.eachMatch( /([A-Z]+-[0-9]+)/) {
			_issues << it[1]
		}
		_issues = _issues.unique().sort()
		
		def json = new groovy.json.JsonBuilder()
		json {
			issues( _issues.collect { [id:it] } )
		}
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
