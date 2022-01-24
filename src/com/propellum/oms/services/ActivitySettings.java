/**
 * 
 */

package com.propellum.oms.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.propellum.oms.entities.ActivityInfo;

/**
 * @author viraj.pawar
 *
 */
public class ActivitySettings
{
	private static final Logger					LOG							= Logger.getLogger("activity." + ActivitySettings.class);

	private volatile static ActivitySettings	activitySettings			= null;

	private static Map<String, ActivityInfo>	activityMap					= new HashMap<String, ActivityInfo>();

	private static Properties					property					= new Properties();

	//activity className  users  cronExpression  isEmailRequired EmailTo EmailCC

	public static final String					DOT							= ".";

	public static final String					ACTIVITY					= "activity";

	public static final String					CLASS_NAME					= "className";

	public static final String					USERS						= "users";

	public static final String					CRON						= "cronExpression";

	public static final String					EMAIL_REQUIRED				= "isEmailRequired";

	public static final String					EMAIL_TO					= "EmailTo";

	public static final String					EMAIL_CC					= "EmailCC";

	public static final String					EMAIL_ROWS					= "EmailRows";

	public static final String					EMAIL_ROWS_DISPLAY_NAMES	= "EmailRowsDisplayNames";

	public static ActivitySettings getInstance()
	{
		if(activitySettings == null)
		{
			synchronized(ActivitySettings.class)
			{
				if(activitySettings == null)
				{
					activitySettings = new ActivitySettings();
					activitySettings.loadActivityMap();
				}
			}
		}

		return activitySettings;

	}

	/**
	 * 
	 */
	private void loadActivityMap()
	{
		InputStream fileInputStream;
		Reader reader;
		try
		{
			fileInputStream = ActivitySettings.class.getClassLoader().getResourceAsStream("scheduledActivity.properties");
			reader = new InputStreamReader(fileInputStream, "UTF-8");
			property.load(reader);
			fileInputStream.close();
			LOG.info("Getting activites from property file");

			for(Object activity : property.keySet())
			{
				if(activity.toString().startsWith("activity"))//activity className  users  activityHour  dayOfMonth isEmailRequired EmailTo EmailCC
				{
					String activityName = property.getProperty(activity.toString());

					ActivityInfo activityInfo = new ActivityInfo(activityName, property.getProperty(activityName + DOT + CLASS_NAME), Arrays.asList(property.getProperty(activityName + DOT + USERS).split(";")),
							property.getProperty(activityName + DOT + CRON));
					activityInfo.setIsEmailRequired(Boolean.parseBoolean(property.getProperty(activityName + DOT + EMAIL_REQUIRED, "false")));
					if(activityInfo.isEmailRequired())
					{
						activityInfo.setEmailTo(property.getProperty(activityName + DOT + EMAIL_TO).split(";"));
						activityInfo.setEmailCC(property.getProperty(activityName + DOT + EMAIL_CC).split(";"));
					}
					activityMap.put(activityName, activityInfo);
					LOG.info(activityName + " added to activity List.");
				}
			}

		}
		catch(IOException e)
		{
			LOG.error(e.getStackTrace());
			e.printStackTrace();
		}
	}

	public static String getProperty(String key, String defaultValue)
	{
		return property.getProperty(key, defaultValue);
	}

	public Map<String, ActivityInfo> getActivityMap()
	{
		return activityMap;
	}

}
