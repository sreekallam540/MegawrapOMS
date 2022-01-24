/**
 * 
 */

package com.propellum.oms.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.propellum.oms.services.AuthenticationService;
import com.propellum.oms.services.NotificationService;

/**
 * @author viral.sejpal
 *
 */
public class NotificationManager
{

	private static final Logger			LOG			= Logger.getLogger(NotificationManager.class);
	private static NotificationManager	instance	= null;

	private NotificationManager()
	{}

	public static NotificationManager getInstance()
	{
		if(instance == null)
		{
			instance = new NotificationManager();
		}
		return instance;
	}

	public void storeNotificationForNewOrder(Order order, User user, OrderModification modification)
	{
		try
		{
			Set<Integer> userIds = getAllUsersForNotifications(user.getAccountName(), order);
			userIds.add(user.getRegistrationId());

			//ACCOUNT SPECIFIC
			Notification notification = new Notification();

			notification.setAlreadyRead(false);
			notification.setOrderId(order.getOrderId());
			notification.setWhen(new Date());
			notification.setMessage("Requested by " + user.getName());
			notification.setOrderName(order.getOrderName());

			String message = "New Order : " + order.getOrderName() + ".";
			notification.setNotificationHeader(message);
			NotificationService.getInstance().storeNotification(notification, userIds);

		}
		catch(Exception e)
		{
			LOG.error("Exception in NotificationManger : storeNotification : Exception : ");
			LOG.error(e.getMessage(), e);
		}

	}

	public void storeNotificationForNewOrder(OrderDetailsAPI order, User user, OrderModification modification)
	{
		try
		{
			Set<Integer> userIds = getAllUsersForNotifications(user.getAccountName(), order);
			userIds.add(user.getRegistrationId());

			//ACCOUNT SPECIFIC
			Notification notification = new Notification();

			notification.setAlreadyRead(false);
			notification.setOrderId(order.getOid());
			notification.setWhen(new Date());
			notification.setMessage("Requested by " + user.getName());
			notification.setOrderName(order.getCompanyname());

			String message = "New Order : " + order.getCompanyname() + ".";
			notification.setNotificationHeader(message);
			NotificationService.getInstance().storeNotification(notification, userIds);

		}
		catch(Exception e)
		{
			LOG.error("Exception in NotificationManger : storeNotification : Exception : ");
			LOG.error(e.getMessage(), e);
		}

	}

	public void storeNotification(Order order, User user, OrderModification modification)
	{
		try
		{
			Set<Integer> userIds = getAllUsersForNotifications(user.getAccountName(), order);
			userIds.add(user.getRegistrationId());

			if(order.getOrderId() > 0 && modification != null)
			{
				Notification notification = new Notification();

				notification.setAlreadyRead(false);
				notification.setOrderId(order.getOrderId());
				notification.setWhen(new Date());
				notification.setOrderName(order.getOrderName());

				String message = "Order : " + order.getOrderName() + " Modified.";
				notification.setNotificationHeader(message);

				String finalMessage = "";

				for(OrderModificationEntry entry : modification.getOrderModificationEntries())
				{
					if(entry.getField().equalsIgnoreCase("orderStatus"))
					{
						String sepMessage = "Status changed to " + entry.getNewValue();
						notification.setMessage(sepMessage);
						NotificationService.getInstance().storeNotification(notification, userIds);
					}
					else
					{
						if(finalMessage.trim().length() == 0)
						{
							finalMessage = "Fields Modified : " + entry.getField();
						}
						else
						{
							finalMessage += ", " + entry.getField();
						}
					}
				}
				if(finalMessage.length() > 0)
				{
					notification.setMessage(finalMessage);
					NotificationService.getInstance().storeNotification(notification, userIds);
				}
			}

			//}
			//DEFULT
			else
			{

			}
		}
		catch(Exception e)
		{
			LOG.error("Exception in NotificationManger : storeNotification : Exception : ");
			LOG.error(e.getMessage(), e);
		}

	}

	public void storeNotification(OrderDetailsAPI order, User user, OrderModification modification)
	{
		try
		{
			Set<Integer> userIds = getAllUsersForNotifications(user.getAccountName(), order);
			userIds.add(user.getRegistrationId());

			if(order.getOid() > 0 && modification != null)
			{
				Notification notification = new Notification();

				notification.setAlreadyRead(false);
				notification.setOrderId(order.getOid());
				notification.setWhen(new Date());
				notification.setOrderName(order.getCompanyname());

				String message = "Order : " + order.getCompanyname() + " Modified.";
				notification.setNotificationHeader(message);

				String finalMessage = "";

				for(OrderModificationEntry entry : modification.getOrderModificationEntries())
				{
					if(entry.getField().equalsIgnoreCase("orderStatus"))
					{
						String sepMessage = "Status changed to " + entry.getNewValue();
						notification.setMessage(sepMessage);
						NotificationService.getInstance().storeNotification(notification, userIds);
					}
					else
					{
						if(finalMessage.trim().length() == 0)
						{
							finalMessage = "Fields Modified : " + entry.getField();
						}
						else
						{
							finalMessage += ", " + entry.getField();
						}
					}
				}
				if(finalMessage.length() > 0)
				{
					notification.setMessage(finalMessage);
					NotificationService.getInstance().storeNotification(notification, userIds);
				}
			}

			//}
			//DEFULT
			else
			{

			}
		}
		catch(Exception e)
		{
			LOG.error("Exception in NotificationManger : storeNotification : Exception : ");
			LOG.error(e.getMessage(), e);
		}

	}

	public Set<Integer> getAllUsersForNotifications(String accountName, Order order)
	{
		Set<Integer> userIds = new HashSet<Integer>();
		String[] email = null;
		User JDAuser = null;
		try
		{

			List<User> clientUserList = AuthenticationService.getInstance().getAllUsers(accountName, UserType.CLIENT);
			for(User user : clientUserList)
			{
				userIds.add(user.getRegistrationId());
			}

			List<User> userList = AuthenticationService.getInstance().getAllUsers(accountName, UserType.JDA);
			for(User user : userList)
			{
				userIds.add(user.getRegistrationId());
			}

			if(order.getClientSuccesAnalyst() != null)
			{
				email = order.getClientSuccesAnalyst().split("_");
				JDAuser = AuthenticationService.getInstance().getUserByLoginId(email[email.length - 1]);
			}

			if(JDAuser != null)
				userIds.add(JDAuser.getRegistrationId());

			//}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return userIds;
	}

	public Set<Integer> getAllUsersForNotifications(String accountName, OrderDetailsAPI order)
	{
		Set<Integer> userIds = new HashSet<Integer>();
		String[] email = null;
		User JDAuser = null;
		try
		{

			List<User> clientUserList = AuthenticationService.getInstance().getAllUsers(accountName, UserType.CLIENT);
			for(User user : clientUserList)
			{
				userIds.add(user.getRegistrationId());
			}

			List<User> userList = AuthenticationService.getInstance().getAllUsers(accountName, UserType.JDA);
			for(User user : userList)
			{
				userIds.add(user.getRegistrationId());
			}

			if(JDAuser != null)
				userIds.add(JDAuser.getRegistrationId());
			else
				userIds.add(1);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return userIds;
	}

	public List<Order> checkIfNotified(List<Order> orders, User user, String fromDate, String toDate)
	{
		try
		{
			List<Integer> alreadyNotified = NotificationService.getInstance().checkIfAlreadyNotified(user.getRegistrationId(), fromDate, toDate);
			List<Order> ordersNotNotified = new ArrayList<Order>();
			for(Order order : orders)
			{
				if(!alreadyNotified.contains(order.getOrderId()))
					ordersNotNotified.add(order);
			}
			if(ordersNotNotified != null && ordersNotNotified.size() > 0)
				storeNotificationForEndDate(ordersNotNotified, user);

			return ordersNotNotified;
		}
		catch(Exception e)
		{
			return null;
		}
	}

	public boolean storeNotificationForEndDate(List<Order> orders, User user)
	{
		try
		{
			List<User> userList = AuthenticationService.getInstance().getAllUsers(user.getAccountName(), user.getUserType());

			Set<Integer> userIds = new HashSet<Integer>();
			for(User u : userList)
				userIds.add(u.getRegistrationId());

			//ACCOUNT SPECIFIC
			for(Order order : orders)
			{
				Notification notification = new Notification();
				notification.setAlreadyRead(false);
				notification.setOrderId(order.getOrderId());
				notification.setWhen(new Date());
				notification.setMessage("Approaching End Date : " + order.getEndDate());
				notification.setOrderName(order.getOrderName());
				String message = "Order : " + order.getOrderName() + ".";
				notification.setNotificationHeader(message);
				NotificationService.getInstance().storeNotification(notification, userIds);
			}
			return true;
		}
		catch(Exception e)
		{
			LOG.error("Exception in NotificationManger : storeNotification : Exception : ");
			LOG.error(e.getMessage(), e);
			return false;
		}

	}

}
