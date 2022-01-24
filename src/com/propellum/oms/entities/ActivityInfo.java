/**
 * 
 */

package com.propellum.oms.entities;

import java.util.Arrays;
import java.util.List;

/**
 * @author viraj.pawar
 *
 */
public class ActivityInfo
{

	private String			activityName;

	/** The user. */
	private List<String>	users;

	/** The activity class. */
	private String			activityClass;

	/** The activity hour. */
	private String			cronExpression;

	/** If email service is required after execution */
	private boolean			isEmailRequired;

	/** Mail To. */
	private String[]		emailTo;

	/** Mail CC. */
	private String[]		emailCC;

	/**
	 * @param activityName2
	 * @param property
	 * @param asList
	 */
	public ActivityInfo(String activityName, String activityClass, List<String> users, String cronExpression)
	{
		this.activityName = activityName;
		this.activityClass = activityClass;
		this.users = users;
		this.cronExpression = cronExpression;
	}

	public String getActivityName()
	{
		return activityName;
	}

	public void setActivityName(String activityName)
	{
		this.activityName = activityName;
	}

	@Override
	public String toString()
	{
		return "ActivityInfo [activityName=" + activityName + ", users=" + users + ", activityClass=" + activityClass + ", cronExpression=" + cronExpression + ", isEmailRequired=" + isEmailRequired + ", emailTo=" + Arrays.toString(emailTo) + ", emailCC="
				+ Arrays.toString(emailCC) + "]";
	}

	public List<String> getUsers()
	{
		return users;
	}

	public void setUsers(List<String> users)
	{
		this.users = users;
	}

	public String getActivityClass()
	{
		return activityClass;
	}

	public void setActivityClass(String activityClass)
	{
		this.activityClass = activityClass;
	}

	public String getCronExpression()
	{
		return cronExpression;
	}

	public void setCronExpression(String cronExpression)
	{
		this.cronExpression = cronExpression;
	}

	public boolean isEmailRequired()
	{
		return isEmailRequired;
	}

	public void setIsEmailRequired(boolean isEmailRequired)
	{
		this.isEmailRequired = isEmailRequired;
	}

	public String[] getEmailTo()
	{
		return emailTo;
	}

	public void setEmailTo(String[] emailTo)
	{
		this.emailTo = emailTo;
	}

	public String[] getEmailCC()
	{
		return emailCC;
	}

	public void setEmailCC(String[] emailCC)
	{
		this.emailCC = emailCC;
	}

	/**
	 * @return
	 */
	public boolean isEmptyOrNull()
	{
		if(this.activityName == null || this.activityName.trim().isEmpty())
			return true;
		if(this.activityClass == null || this.activityClass.trim().isEmpty())
			return true;
		if(this.cronExpression == null || this.cronExpression.trim().isEmpty())
			return true;

		return false;
	}

}
