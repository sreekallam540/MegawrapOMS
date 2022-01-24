
package com.propellum.oms.services;

import java.net.URI;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;

import com.atlassian.jira.rest.client.api.AuthenticationHandler;
import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.IssueType;
import com.atlassian.jira.rest.client.api.domain.Project;
import com.atlassian.jira.rest.client.api.domain.input.ComplexIssueInputFieldValue;
import com.atlassian.jira.rest.client.api.domain.input.FieldInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.propellum.oms.entities.IssueJiraDTO;

import io.atlassian.util.concurrent.Promise;

public class JiraRestClientApi
{
	private static final Logger	LOG				= Logger.getLogger(JiraRestClientApi.class);
	private static String		JIRA_URL		= OMSSettings.getInstance().getProperty(OMSSettings.JIRA_URL);;
	private static String		JIRA_USERNAME	= OMSSettings.getInstance().getProperty(OMSSettings.JIRA_USERNAME);;
	private static String		JIRA_PASSWORD	= OMSSettings.getInstance().getProperty(OMSSettings.JIRA_PASSWORD);
	private static String		PROJECT_KEY		= OMSSettings.getInstance().getProperty(OMSSettings.PROJECT_KEY);
	private static String		Issue_Type		= OMSSettings.getInstance().getProperty(OMSSettings.ISSUETYPE);

	URI							uri;
	private JiraRestClient		restClient;
	public static Properties	prop;

	/*public static void loadConnectionProperties()
	{
		prop = new Properties();
		try
		{
	
			InputStream fileInputStream = SQLConnectionFactory.class.getClassLoader().getResourceAsStream("jiraSettings.properties");
			prop.load(fileInputStream);
	
			JIRA_URL = prop.getProperty("JIRA_URL");
			JIRA_USERNAME = prop.getProperty("JIRA_USERNAME");
			JIRA_PASSWORD = prop.getProperty("JIRA_PASSWORD");
			PROJECT_KEY = prop.getProperty("projectKey");
	
			if(fileInputStream != null)
				fileInputStream.close();
	
		}
		catch(FileNotFoundException e1)
		{
			e1.printStackTrace();
			LOG.info("##ERR : " + e1);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			LOG.info("##ERR : " + e);
		}
	}*/

	public JiraRestClientApi()
	{

	}

	public JiraRestClient getJiraRestClient()
	{
		return new AsynchronousJiraRestClientFactory().createWithBasicHttpAuthentication(getJiraUri(), JIRA_USERNAME, JIRA_PASSWORD);
	}

	public URI getJiraUri()
	{
		return URI.create(JIRA_URL);
	}

	public Boolean createIssue(IssueJiraDTO issueDTO)
	{
		//loadConnectionProperties();
		restClient = getJiraRestClient();

		BasicProject project = null;
		IssueType issueType = null;
		IssueInputBuilder builder = null;
		try
		{
			final Iterable<BasicProject> projects = restClient.getProjectClient().getAllProjects().claim();

			for(BasicProject projectStr : projects)
			{
				if(projectStr.getKey().equalsIgnoreCase(PROJECT_KEY))
				{
					project = projectStr;
				}
			}
			System.out.println("Jira for API ");
			Promise<Project> projectType = restClient.getProjectClient().getProject(PROJECT_KEY);

			for(IssueType type : (projectType.get()).getIssueTypes())
			{
				if(type.getName().equalsIgnoreCase(Issue_Type))
				{
					issueType = type;
				}
			}

			builder = new IssueInputBuilder(project, issueType, issueDTO.getIssueSummery());
			builder.setProject(project);
			builder.setDescription(issueDTO.getIssueDescription());
			builder.setPriorityId(1L);
			builder.setFieldInput(new FieldInput("assignee", ComplexIssueInputFieldValue.with("accountId", "557058:0fa57746-30a2-498c-9e34-9306679d0be7")));

			IssueInput input = builder.build();

			IssueRestClient client = restClient.getIssueClient();
			BasicIssue issue = client.createIssue(input).claim();

			System.out.println(issue.getKey());
			LOG.error("Jira Created for  " + issueDTO.getIssueSummery() + " ID is :: " + issue.getKey());

		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}

		return true;

	}

	public void deleteIssue(String issueKey, boolean deleteSubtasks)
	{
		restClient.getIssueClient().deleteIssue(issueKey, deleteSubtasks).claim();
	}

	public static void main(String[] args)
	{
		//loadConnectionProperties();
		URI jiraServerUri = URI.create("https://propellum.atlassian.net");

		AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
		IssueType issueType = null;

		AuthenticationHandler auth = new BasicHttpAuthenticationHandler("wraplive-support@propellum.com", "LFWcVWUMnD4aJVkGP29t2992");
		JiraRestClient restClient = factory.create(jiraServerUri, auth);
		//IssueRestClient issueClient = restClient.getIssueClient();
		Promise<Project> projectType = restClient.getProjectClient().getProject("MEG");
		try
		{
			for(IssueType type : (projectType.get()).getIssueTypes())
			{
				if(type.getName().equalsIgnoreCase(prop.getProperty("issueType")))
				{
					issueType = type;
				}
			}
		}
		catch(InterruptedException | ExecutionException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try
		{
			IssueInputBuilder iib = new IssueInputBuilder();
			iib.setProjectKey("MEG");
			iib.setSummary("Test Summary");
			iib.setIssueType(issueType);
			iib.setDescription("Test Description");
			iib.setPriorityId(3L);

			//IssueInput issue = iib.build();
			//BasicIssue issueObj = issueClient.createIssue(issue).claim();
			//System.out.println("Issue " + issueObj.getKey() + " created successfully");

			iib.setFieldInput(new FieldInput("assignee", ComplexIssueInputFieldValue.with("accountId", "557058:0fa57746-30a2-498c-9e34-9306679d0be7")));
			restClient.getIssueClient().updateIssue("MEG-1828", iib.build()).claim();

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
}
