
package com.propellum.oms.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 * A factory for creating SQLConnection objects.
 */
public class SQLConnectionFactory
{

	/** The Constant LOG. */
	private static final Logger	LOG				= Logger.getLogger(SQLConnectionFactory.class);
	
	/** The sql connection. */
	Connection					sqlConnection;

	/** The connection open. */
	boolean						connectionOpen	= false;

	/** The prop. */
	static Properties			prop;

	static
	{
		loadConnectionProperties();
	}

	/**
	 * Load connection properties.
	 */
	public static void loadConnectionProperties()
	{
		prop = new Properties();
		try
		{

			InputStream fileInputStream = SQLConnectionFactory.class.getClassLoader().getResourceAsStream("db.properties");
			prop.load(fileInputStream);
			if(fileInputStream != null)
				fileInputStream.close();

		}
		catch(FileNotFoundException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
			LOG.error("##ERR : " + e1);
		}
		catch(IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG.error("##ERR : " + e);
		}
	}

	/**
	 * Gets the connection.
	 *
	 * @return the connection
	 */
	public Connection getConnection()
	{
		try
		{
			if(connectionOpen && !sqlConnection.isClosed())
				return sqlConnection;
		}
		catch(Exception e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String url = prop.getProperty("url");
		String dataDb = prop.getProperty("sqlDb");
		String driver = prop.getProperty("driver");
		String username = prop.getProperty("Username");
		String password = prop.getProperty("Password");
		try
		{
			Class.forName(driver);
			sqlConnection = DriverManager.getConnection(url + dataDb, username, password);
			connectionOpen = true;
		}
		catch(ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG.error("##ERR : " + e);
		}
		catch(SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG.error("##ERR : " + e);
		}
		return sqlConnection;
	}

	/**
	 * Close connection.
	 */
	public void closeConnection()
	{
		try
		{
			if(sqlConnection != null)
				sqlConnection.close();
			sqlConnection = null;
			connectionOpen = false;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			LOG.error("##ERR : " + e);
		}
	}
}
