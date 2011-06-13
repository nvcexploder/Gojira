package com.curiousattemptbunny.rabu.jira

import java.util.Properties;

//import com.atlassian.jira.rest.client.JiraRestClient 
//import com.atlassian.jira.rest.client.NullProgressMonitor 
//import com.atlassian.jira.rest.client.domain.Issue 
//import com.atlassian.jira.rest.client.internal.jersey.JerseyJiraRestClientFactory 
//import com.atlassian.jira.rpc.soap.jirasoapservice_v2.JiraSoapService 
//import groovy.json.JsonBuilder 
//import groovy.json.JsonOutput 

class Spike {
//	static void main(String[] args) {
//		Properties properties = new Properties()
//		properties.load new File('rabu-jira.properties').newInputStream()
//		
//		def getSetting = { key ->
//			String value = properties.getProperty(key)
//			if (value == null) {
//				System.err.println("Property '$key' is required in rabu-jira.properties.")
//				System.exit(1)
//			}
//			return value
//		}
//		
//		String url = getSetting('jiraUrl')
//		String username = getSetting('username')
//		String password = getSetting('password')
//		String search = getSetting('jqlSearch')
//		String estimateFieldName = getSetting('estimateFieldName')
//		
//		if (!url.endsWith("/")) {
//			url += "/"
//		}
//			
//		SOAPSession soapSession = new SOAPSession(new URL("${url}rpc/soap/jirasoapservice-v2"));
//		soapSession.connect(username, password);
//		
//		JiraSoapService jiraSoapService = soapSession.getJiraSoapService();
//		String authToken = soapSession.getAuthenticationToken();
//		
//		def issues = jiraSoapService.getIssuesFromJqlSearch(authToken, search, 100)
//
//		final JerseyJiraRestClientFactory factory = new JerseyJiraRestClientFactory()
//		final URI jiraServerUri = new URI(url)
//		final JiraRestClient restClient = factory.createWithBasicHttpAuthentication(jiraServerUri, username, password)
//		final NullProgressMonitor pm = new NullProgressMonitor()
//
//		issues = issues.collect { it ->
//			Issue issue = restClient.getIssueClient().getIssue(it.key, pm)
//			
//			def name = it.summary
//			def value = issue.fields.find { it.name == estimateFieldName }?.value
//			
//			return [name:name, estimate:value]
//		}
//		
//		def json = new JsonBuilder()
//		json {
//			includedFeatures (issues.findAll { it.estimate }.collect { [it.name, it.estimate] }.toArray())
//			excludedFeatures (issues.findAll { !it.estimate }.collect { [it.name, "?"] }.toArray())
//		}
//		println JsonOutput.prettyPrint(json.toString())	
//	}
}