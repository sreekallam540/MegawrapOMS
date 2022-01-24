/**
 * 
 */

package com.propellum.oms.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author viral.sejpal
 * 
 */
public class OMSSettings
{

	public static final String			BULK_ORDER_FILE_DIR				= "bulkorderfiledir";

	public static final String			EXCEL_ORDER_FILE_DIR			= "excelorderfiledir";

	private static final OMSSettings	instance						= new OMSSettings();

	public static final String			ORDER_TABLE_COLUMN_NAME			= "ordertableColumnName";

	public static final String			ORDER_TABLE_COLUMN_NAME_FOR_API	= "ordertableColumnNameForAPI";

	public static final String			ORDER_TABLE_DISPLAY_COLUMN_NAME	= "ordertableColumnDisplayName";

	public static final String			COLUMN_NAME_FOR_BULK_UPLOAD		= "columnNameForBulkUpload";

	public static final String			ITS_URL							= "ITS_url";

	public static final String			MAIL_SERVICE					= "mailService";

	public static final String			LOGIN_URL						= "login_url";					//Used for Megawrap Company Master Reload Cache

	public static final String			RELOAD_CACHE_URL				= "reload_cache_url";			//Used for Megawrap Company Master Reload Cache

	public static final String			LOGIN_ID						= "login_id";					//Used for Megawrap Company Master Reload Cache

	public static final String			PASSWORD						= "password";					//Used for Megawrap Company Master Reload Cache

	public static final String			DATE_FORMAT						= "date_format";

	public static final String			DATE_FORMAT_FOR_API				= "date_format_for_API";

	public static final String			JIRA_URL						= "JIRA_URL";

	public static final String			JIRA_USERNAME					= "JIRA_USERNAME";

	public static final String			JIRA_PASSWORD					= "JIRA_PASSWORD";

	public static final String			PROJECT_KEY						= "projectKey";

	public static final String			PROJECT							= "project";

	public static final String			ISSUETYPE						= "issueType";

	private Properties					prop							= null;

	public static OMSSettings getInstance()
	{
		return instance;
	}

	private OMSSettings()
	{
		prop = new Properties();
		try
		{

			InputStream fileInputStream = SQLConnectionFactory.class.getClassLoader().getResourceAsStream("settings.properties");
			prop.load(fileInputStream);
			if(fileInputStream != null)
				fileInputStream.close();

		}
		catch(FileNotFoundException e1)
		{
			e1.printStackTrace();
			//			LOG.info("##ERR : " + e1);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			//			LOG.info("##ERR : " + e);
		}
	}

	public String getProperty(String key)
	{
		return prop.getProperty(key);
	}

	public String getProperty(String key, String defaultVal)
	{
		if(prop.containsKey(key))
			return prop.getProperty(key);
		else
			return defaultVal;
	}

}
