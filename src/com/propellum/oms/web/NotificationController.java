
package com.propellum.oms.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.propellum.oms.entities.Notification;
import com.propellum.oms.entities.User;
import com.propellum.oms.services.NotificationService;

@Controller
public class NotificationController
{

	@RequestMapping("notifications.htm")
	public String notifications(HttpServletRequest request, ModelMap map)
	{
		User sessionUser = (User) request.getSession().getAttribute(WebConstants.SESSION_USER_KEY);
		List<Notification> listOfNotifications = NotificationService.getInstance().getNotifications(sessionUser.getRegistrationId());

		map.addAttribute("notificationList", listOfNotifications);

		return "notificationlist";
	}

	@RequestMapping("markAllAsRead.htm")
	public String markAllAsRead(HttpServletRequest request)
	{
		try
		{
			User sessionUser = (User) request.getSession().getAttribute(WebConstants.SESSION_USER_KEY);
			int userId = sessionUser.getRegistrationId();

			NotificationService.getInstance().markAsReadForUser(userId);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return "redirect:notifications.htm";
	}

	@RequestMapping("markAsRead.htm")
	public void markAsRead(HttpServletRequest request)
	{
		String notificationId = null;
		try
		{
			notificationId = request.getParameter("nid");

			long nid = Long.parseLong(notificationId);
			NotificationService.getInstance().markAsRead(nid);

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
