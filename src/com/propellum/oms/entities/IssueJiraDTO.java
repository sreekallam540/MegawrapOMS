
package com.propellum.oms.entities;

//import com.atlassian.jira.rest.client.api.domain.IssueType;

public class IssueJiraDTO
{
	String	issueDescription;
	String	issueSummery;
	String	assigneeName;
	//IssueType	issueType;
	String	issueLabel;
	String	issueReporter;

	/**
	 * @return the issueDescription
	 */
	public String getIssueDescription()
	{
		return issueDescription;
	}

	/**
	 * @param issueDescription
	 *            the issueDescription to set
	 */
	public void setIssueDescription(String issueDescription)
	{
		this.issueDescription = issueDescription;
	}

	/**
	 * @return the issueSummery
	 */
	public String getIssueSummery()
	{
		return issueSummery;
	}

	/**
	 * @param issueSummery
	 *            the issueSummery to set
	 */
	public void setIssueSummery(String issueSummery)
	{
		this.issueSummery = issueSummery;
	}

	/**
	 * @return the assigneeName
	 */
	public String getAssigneeName()
	{
		return assigneeName;
	}

	/**
	 * @param assigneeName
	 *            the assigneeName to set
	 */
	public void setAssigneeName(String assigneeName)
	{
		this.assigneeName = assigneeName;
	}

	/**
	 * @return the issueType
	 */
	/*public IssueType getIssueType()
	{
		return issueType;
	}*/

	/**
	 * @param issueType
	 *            the issueType to set
	 */
	/*public void setIssueType(IssueType issueType)
	{
		this.issueType = issueType;
	}
	*/
	/**
	 * @return the issueLabel
	 */
	public String getIssueLabel()
	{
		return issueLabel;
	}

	/**
	 * @param issueLabel
	 *            the issueLabel to set
	 */
	public void setIssueLabel(String issueLabel)
	{
		this.issueLabel = issueLabel;
	}

	/**
	 * @return the issueReporter
	 */
	public String getIssueReporter()
	{
		return issueReporter;
	}

	/**
	 * @param issueReporter
	 *            the issueReporter to set
	 */
	public void setIssueReporter(String issueReporter)
	{
		this.issueReporter = issueReporter;
	}

}
