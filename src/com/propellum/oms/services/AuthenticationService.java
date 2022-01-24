/**
 * 
 */

package com.propellum.oms.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.propellum.oms.entities.User;
import com.propellum.oms.entities.UserType;
import com.propellum.oms.factories.SQLHikariConnectionFactory;

/**
 * @author viral.sejpal
 *
 */
public class AuthenticationService
{

	public static AuthenticationService	instance		= new AuthenticationService();

	private static final Logger			LOG				= Logger.getLogger(AuthenticationService.class);

	public static final int				DUPLICATE_USER	= -10;
	public static final int				ERROR			= -1;

	private AuthenticationService()
	{

	}

	public static AuthenticationService getInstance()
	{
		return instance;
	}

	public boolean updatePassword(String loginId, String password)
	{
		//SQLConnectionFactory connectionFactory = new SQLConnectionFactory();
		Connection conn = null;
		PreparedStatement pStmt = null;
		try
		{
			conn = SQLHikariConnectionFactory.getInstance().getConnection();
			pStmt = conn.prepareStatement("UPDATE users SET  PASSWORD = ? WHERE loginId = ? ");

			int i = 0;

			String md5Password = MD5(password);

			pStmt.setString(++i, md5Password);
			pStmt.setString(++i, loginId);

			int response = pStmt.executeUpdate();
			if(response > 0)
			{
				return true;
			}
			else
				return false;

		}
		catch(com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException ve)
		{
			LOG.error("ERR : " + ve.toString());
			ve.printStackTrace();
			return false;
		}
		catch(Exception e)
		{
			LOG.error("ERR : " + e.toString());
			e.printStackTrace();
			return false;
		}
		finally
		{
			try
			{
				if(conn != null)
					conn.close();
				if(pStmt != null)
					pStmt.close();
			}
			catch(Exception e)
			{

			}
		}
	}

	public List<User> getAllUsers()
	{
		return getAllUsers(null, null);
	}

	public List<User> getAllUsers(String accountName, UserType userType)
	{
		List<User> users = new ArrayList<User>();
		//SQLConnectionFactory connectionFactory = new SQLConnectionFactory();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pStmt = null;
		try
		{
			conn = SQLHikariConnectionFactory.getInstance().getConnection();

			String query = "SELECT * FROM users ";

			if(userType != null && accountName != null && accountName.trim().length() > 0)
			{
				query += " WHERE accountName = ? AND userType = ?";
			}

			pStmt = conn.prepareStatement(query);

			if(accountName != null && accountName.trim().length() > 0)
			{
				pStmt.setString(1, accountName);
				pStmt.setString(2, userType.toString());
			}

			rs = pStmt.executeQuery();
			User user = null;
			while(rs.next())
			{
				user = new User();

				user.setLoginId(rs.getString("loginid"));
				user.setRegistrationId(rs.getInt("registrationId"));
				user.setName(rs.getString("name"));
				user.setLastName(rs.getString("lastname"));
				user.setAccountName(rs.getString("AccountName"));
				user.setActive(rs.getBoolean("active"));
				user.setUserType(UserType.valueOf(rs.getString("userType")));

				users.add(user);
			}
			rs.close();
			return users;
		}
		catch(Exception e)
		{
			LOG.error("ERR : " + e.toString());
			e.printStackTrace();
			return null;
		}
		finally
		{
			try
			{
				if(conn != null)
					conn.close();
				if(pStmt != null)
					pStmt.close();
				if(rs != null)
					rs.close();
			}
			catch(Exception e)
			{

			}
		}
	}

	public User getUserByRegistrationId(int regId)
	{
		//SQLConnectionFactory connectionFactory = new SQLConnectionFactory();
		Connection conn = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		try
		{
			conn = SQLHikariConnectionFactory.getInstance().getConnection();
			pStmt = conn.prepareStatement("SELECT * FROM users where registrationId=?");

			pStmt.setInt(1, regId);

			rs = pStmt.executeQuery();
			User user = null;
			if(rs.next())
			{
				user = new User();

				user.setLoginId(rs.getString("loginid"));
				user.setRegistrationId(rs.getInt("registrationId"));
				user.setName(rs.getString("name"));
				user.setLastName(rs.getString("lastname"));
				user.setAccountName(rs.getString("AccountName"));
				user.setActive(rs.getBoolean("active"));
				user.setUserType(UserType.valueOf(rs.getString("userType")));
			}
			else
			{
				return null;
			}

			rs.close();
			return user;
		}
		catch(Exception e)
		{
			LOG.error("ERR : " + e.toString());
			e.printStackTrace();
			return null;
		}
		finally
		{
			try
			{
				if(conn != null)
					conn.close();
				if(pStmt != null)
					pStmt.close();
				if(rs != null)
					rs.close();
			}
			catch(Exception e)
			{

			}
		}
	}

	public User getUserByLoginId(String loginId)
	{
		//SQLConnectionFactory connectionFactory = new SQLConnectionFactory();
		Connection conn = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		try
		{
			conn = SQLHikariConnectionFactory.getInstance().getConnection();
			pStmt = conn.prepareStatement("SELECT * FROM users where loginId=?");

			pStmt.setString(1, loginId);

			rs = pStmt.executeQuery();
			User user = null;
			if(rs.next())
			{
				user = new User();

				user.setLoginId(rs.getString("loginid"));
				user.setRegistrationId(rs.getInt("registrationId"));
				user.setName(rs.getString("name"));
				user.setLastName(rs.getString("lastname"));
				user.setAccountName(rs.getString("AccountName"));
				user.setActive(rs.getBoolean("active"));
				user.setUserType(UserType.valueOf(rs.getString("userType")));
			}
			else
			{
				return null;
			}

			rs.close();
			return user;
		}
		catch(Exception e)
		{
			LOG.error("ERR : " + e.toString());
			e.printStackTrace();
			return null;
		}
		finally
		{
			try
			{
				if(conn != null)
					conn.close();
				if(pStmt != null)
					pStmt.close();
				if(rs != null)
					rs.close();
			}
			catch(Exception e)
			{

			}
		}
	}

	public User authorize(String loginId, String password)
	{
		//SQLConnectionFactory connectionFactory = new SQLConnectionFactory();
		Connection conn = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		try
		{
			conn = SQLHikariConnectionFactory.getInstance().getConnection();
			pStmt = conn.prepareStatement("SELECT * FROM users where loginid = ? AND password= ?");

			String md5pass = MD5(password);

			pStmt.setString(1, loginId);
			pStmt.setString(2, md5pass);

			rs = pStmt.executeQuery();
			User user = null;
			if(rs.next())
			{
				user = new User();

				user.setLoginId(rs.getString("loginid"));
				user.setRegistrationId(rs.getInt("registrationId"));
				user.setName(rs.getString("name"));
				user.setLastName(rs.getString("lastname"));
				user.setAccountName(rs.getString("AccountName"));
				user.setActive(rs.getBoolean("active"));
				user.setUserType(UserType.valueOf(rs.getString("userType")));
			}
			else
			{
				return null;
			}

			rs.close();
			return user;
		}
		catch(Exception e)
		{
			LOG.error("ERR : " + e.toString());
			e.printStackTrace();
			return null;
		}
		finally
		{
			try
			{
				if(conn != null)
					conn.close();
				if(pStmt != null)
					pStmt.close();
				if(rs != null)
					rs.close();
			}
			catch(Exception e)
			{

			}
		}
	}

	public boolean updateUser(User user)
	{
		//SQLConnectionFactory connectionFactory = new SQLConnectionFactory();
		Connection conn = null;
		PreparedStatement pStmt = null;
		try
		{
			conn = SQLHikariConnectionFactory.getInstance().getConnection();
			pStmt = conn.prepareStatement("UPDATE users SET NAME = ?,lastname= ?,  accountName = ? , active = ? , userType = ? WHERE registrationId = ? ");

			int i = 0;
			pStmt.setString(++i, user.getName());
			pStmt.setString(++i, user.getLastName());
			pStmt.setString(++i, user.getAccountName());
			pStmt.setBoolean(++i, user.isActive());
			pStmt.setString(++i, user.getUserType().toString());

			pStmt.setInt(++i, user.getRegistrationId());

			int response = pStmt.executeUpdate();
			return response > 0;

		}
		catch(com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException ve)
		{
			LOG.error("ERR : " + ve.toString());
			ve.printStackTrace();
			return false;
		}
		catch(Exception e)
		{
			LOG.error("ERR : " + e.toString());
			e.printStackTrace();
			return false;
		}
		finally
		{
			try
			{
				if(conn != null)
					conn.close();
				if(pStmt != null)
					pStmt.close();
			}
			catch(Exception e)
			{

			}
		}
	}

	public int createUser(User userToBeCreated, String password)
	{
		//SQLConnectionFactory connectionFactory = new SQLConnectionFactory();
		Connection conn = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		try
		{
			conn = SQLHikariConnectionFactory.getInstance().getConnection();
			pStmt = conn.prepareStatement("INSERT INTO users(loginid, PASSWORD, NAME, lastname, accountName, creationDate, active, userType) " + "VALUES(?,?,?,?,?,?,?,?)");

			int i = 0;

			String md5Password = MD5(password);

			pStmt.setString(++i, userToBeCreated.getLoginId());
			pStmt.setString(++i, md5Password);
			pStmt.setString(++i, userToBeCreated.getName());
			pStmt.setString(++i, userToBeCreated.getLastName());
			pStmt.setString(++i, userToBeCreated.getAccountName());
			pStmt.setTimestamp(++i, new Timestamp(System.currentTimeMillis()));
			pStmt.setBoolean(++i, userToBeCreated.isActive());
			pStmt.setString(++i, userToBeCreated.getUserType().toString());

			int response = pStmt.executeUpdate();
			if(response > 0)
			{
				try
				{
					//AlertCommunicator.getInstance().sendCustomizedAlert("OMS :: User Created : " + userToBeCreated.getLoginId(),
						//	"login id : " + userToBeCreated.getLoginId() + " and Password : " + password + ", account = " + userToBeCreated.getAccountName(), new String[] {"wraplive-support@propellum.com"});
				}
				catch(Exception e)
				{

				}
				PreparedStatement pStmt1 = conn.prepareStatement("Select max(registrationId) from users where loginId = ?");

				pStmt1.setString(1, userToBeCreated.getLoginId());

				rs = pStmt1.executeQuery();
				int registrationId = 0;

				if(rs.next())
				{
					registrationId = rs.getInt(1);
				}

				rs.close();
				return registrationId;
			}

		}
		catch(com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException ve)
		{
			LOG.error("ERR : " + ve.toString());
			ve.printStackTrace();
			return DUPLICATE_USER;
		}
		catch(Exception e)
		{
			LOG.error("ERR : " + e.toString());
			e.printStackTrace();
			return ERROR;
		}
		finally
		{
			try
			{
				if(conn != null)
					conn.close();
				if(pStmt != null)
					pStmt.close();
				if(rs != null)
					rs.close();
			}
			catch(Exception e)
			{

			}
		}
		return 0;
	}

	private static String MD5(String md5)
	{
		try
		{
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			byte[] array = md.digest(md5.getBytes());
			StringBuffer sb = new StringBuffer();
			for(int i = 0; i < array.length; ++i)
			{
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
			}
			return sb.toString();
		}
		catch(java.security.NoSuchAlgorithmException e)
		{}
		return null;
	}

	public static void main(String[] asd)
	{
		User user = new User();

		user.setLoginId("viral.sejpal@propellum.com");
		user.setName("Viral Sejpal");
		user.setAccountName("experteer");
		user.setUserType(UserType.CLIENT);
		user.setActive(true);

		System.out.println(AuthenticationService.getInstance().createUser(user, "pass@123"));

		System.out.println(MD5("viralsejpal"));
	}

}
