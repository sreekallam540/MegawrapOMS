package com.propellum.oms.factories;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.propellum.oms.services.SQLConnectionFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class SQLHikariConnectionFactory
{
	private static final Logger		LOG				= Logger.getLogger(SQLHikariConnectionFactory.class);
	
	public static DataSource dataSource=null;
	
	private static SQLHikariConnectionFactory sqlHikariConnectionFactory=null;
	
	public static Properties				prop;

	static
	{
		loadConnectionProperties();
	}
	
	private SQLHikariConnectionFactory(){
		
		final HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setDriverClassName(prop.getProperty("driver"));
        hikariConfig.setJdbcUrl(prop.getProperty("url") + prop.getProperty("sqlDb"));
        hikariConfig.setUsername(prop.getProperty("Username"));
        hikariConfig.setPassword(prop.getProperty("Password"));

        hikariConfig.setMaximumPoolSize(Integer.parseInt(prop.getProperty("maxConnections")));
        hikariConfig.setAutoCommit(true);

        dataSource = new HikariDataSource(hikariConfig);
	}
	public static SQLHikariConnectionFactory getInstance(){
		if(sqlHikariConnectionFactory == null)
			sqlHikariConnectionFactory = new SQLHikariConnectionFactory();
		return sqlHikariConnectionFactory;
	}
	
	public static DataSource getDataSource() {
	    if (dataSource == null) {
	        final HikariConfig hikariConfig = new HikariConfig();

	        hikariConfig.setDriverClassName(prop.getProperty("driver"));
	        hikariConfig.setJdbcUrl(prop.getProperty("url") + prop.getProperty("sqlDb"));
	        hikariConfig.setUsername(prop.getProperty("Username"));
	        hikariConfig.setPassword(prop.getProperty("Password"));

	        hikariConfig.setMaximumPoolSize(Integer.parseInt(prop.getProperty("maxConnections")));
	        hikariConfig.setAutoCommit(true);

	       dataSource = new HikariDataSource(hikariConfig);
	    }
	    return dataSource;
	}
	
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
			LOG.info("##ERR : " + e1);
		}
		catch(IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG.info("##ERR : " + e);
		}
	}
	public Connection getConnection()
	{
		Connection	connection=null;
		try
		{
			connection = dataSource.getConnection();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return connection;
	}
}

