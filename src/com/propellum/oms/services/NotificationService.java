
package com.propellum.oms.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.propellum.oms.entities.Notification;
import com.propellum.oms.factories.SQLHikariConnectionFactory;

/**
 * 
 * @author viral.sejpal
 *
 */
public class NotificationService
{

	private static NotificationService	instance	= null;
	private static SimpleDateFormat		sdf			= new SimpleDateFormat(OMSSettings.getInstance().getProperty(OMSSettings.DATE_FORMAT, "yyyy-MM-dd"));

	private NotificationService()
	{}

	public static NotificationService getInstance()
	{
		if(instance == null)
			instance = new NotificationService();

		return instance;
	}

	public List<Notification> getNotifications(int userId)
	{
		//SQLConnectionFactory connFactory = new SQLConnectionFactory();

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			conn = SQLHikariConnectionFactory.getInstance().getConnection();
			List<Notification> list = new ArrayList<Notification>();
			String query = "SELECT * FROM notifications WHERE userId = ? ORDER BY whendate DESC limit 50";

			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, userId);

			rs = pstmt.executeQuery();

			while(rs.next())
			{
				Notification notification = new Notification();

				notification.setAlreadyRead(rs.getBoolean("alreadyRead"));
				notification.setMessage(rs.getString("message"));
				notification.setNotificationId(rs.getLong("notificationId"));
				notification.setWhen(new Date(rs.getTimestamp("whendate").getTime()));
				notification.setOrderId(rs.getInt("orderId"));
				notification.setNotificationHeader(rs.getString("orderHeader"));
				notification.setOrderName(rs.getString("orderName"));

				list.add(notification);
			}

			rs.close();
			return list;

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(conn != null)
					conn.close();
				if(pstmt != null)
					pstmt.close();
				if(rs != null)
					rs.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		return null;
	}

	public boolean storeNotification(Notification notification, Set<Integer> forUsers)
	{
		//SQLConnectionFactory connFactory = new SQLConnectionFactory();
		Connection conn = null;
		PreparedStatement pstmt = null;
		try
		{
			conn = SQLHikariConnectionFactory.getInstance().getConnection();
			String query = "INSERT INTO notifications(message, whendate, alreadyRead, userId, orderId, orderHeader, orderName) VALUES(?,?,?,?,?,?,?)";

			pstmt = (PreparedStatement) conn.prepareStatement(query);

			for(int userId : forUsers)
			{
				pstmt.setString(1, notification.getMessage());
				pstmt.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
				pstmt.setBoolean(3, false);
				pstmt.setInt(4, userId);
				pstmt.setInt(5, notification.getOrderId());
				pstmt.setString(6, notification.getNotificationHeader());
				pstmt.setString(7, notification.getOrderName());

				pstmt.addBatch();
			}

			pstmt.executeBatch();
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(conn != null)
					conn.close();
				if(pstmt != null)
					pstmt.close();
			}
			catch(Exception e)
			{}
		}
		return false;
	}

	public boolean markAsRead(long notificationId)
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		try
		{
			conn = SQLHikariConnectionFactory.getInstance().getConnection();

			pstmt = conn.prepareStatement("UPDATE notifications SET alreadyRead = 1 WHERE notificationId = ?");
			pstmt.setLong(1, notificationId);

			pstmt.executeUpdate();

			pstmt.close();
			conn.close();

			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(conn != null)
					conn.close();
				if(pstmt != null)
					pstmt.close();
			}
			catch(Exception e)
			{

			}
		}
		return false;
	}

	public boolean markAsReadForUser(int userRegistrationId)
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		try
		{
			conn = SQLHikariConnectionFactory.getInstance().getConnection();

			pstmt = conn.prepareStatement("UPDATE notifications SET alreadyRead = 1 WHERE userId = ?");
			pstmt.setInt(1, userRegistrationId);

			pstmt.executeUpdate();

			pstmt.close();
			conn.close();

			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(conn != null)
					conn.close();
				if(pstmt != null)
					pstmt.close();
			}
			catch(Exception e)
			{

			}
		}
		return false;
	}

	public boolean markAsUnread(long notificationId)
	{
		try
		{

		}
		catch(Exception e)
		{

		}
		return false;
	}

	/**
	 * @param registrationId
	 * @param daysPrior
	 * @return
	 */
	public List<Integer> checkIfAlreadyNotified(int registrationId, String fromDate, String toDate)
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try
		{
			conn = SQLHikariConnectionFactory.getInstance().getConnection();
			String query = "SELECT orderId FROM notifications WHERE message like '%Approaching End Date%' AND userId = ? AND whendate BETWEEN ? AND ? ";
			List<Integer> orderIdsFound = new ArrayList<Integer>();
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, registrationId);
			pstmt.setString(2, fromDate);
			pstmt.setString(3, toDate);

			rs = pstmt.executeQuery();

			while(rs.next())
			{
				orderIdsFound.add(rs.getInt("orderId"));
			}
			rs.close();

			return orderIdsFound;

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(conn != null)
					conn.close();
				if(pstmt != null)
					pstmt.close();
				if(rs != null)
					rs.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		return null;
	}

}
