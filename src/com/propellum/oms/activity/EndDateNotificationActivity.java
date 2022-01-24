/**
 * 
 */

package com.propellum.oms.activity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.propellum.oms.entities.ActivityInfo;
import com.propellum.oms.entities.NotificationManager;
import com.propellum.oms.entities.Order;
import com.propellum.oms.entities.User;
import com.propellum.oms.services.ActivitySettings;
import com.propellum.oms.services.AuthenticationService;
import com.propellum.oms.services.OrderService;

import net.sf.cglib.beans.BeanMap;

/**
 * @author viraj.pawar
 *
 */
public class EndDateNotificationActivity implements Job
{
	private static final Logger	LOG	= Logger.getLogger("activity." + ActivitySettings.class);

	private ActivityInfo		activityInfo;

	public EndDateNotificationActivity()
	{}

	public ActivityInfo getActivityInfo()
	{
		return activityInfo;
	}

	public void setActivityInfo(ActivityInfo activityInfo)
	{
		this.activityInfo = activityInfo;
	}

	/* (non-Javadoc)
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException
	{
		setActivityInfo();
		runActivity();

	}

	public void triggerManually()
	{
		setActivityInfo();
		runActivity();
	}

	/**
	 * 
	 */
	private void runActivity()
	{
		if(activityInfo != null)
		{
			OrderService os = OrderService.getInstance();
			int daysPrior = Integer.parseInt(ActivitySettings.getProperty(activityInfo.getActivityName() + ".daysPriorTo", "7"));
			List<Order> approachingEndDate = os.getLiveOrdersByEndDate(daysPrior);
			User user = AuthenticationService.getInstance().getUserByLoginId(activityInfo.getUsers().get(0));
			List<Order> ordersToNotify = new ArrayList<Order>();
			ordersToNotify.addAll(NotificationManager.getInstance().checkIfNotified(approachingEndDate, user, os.getFromDate(-daysPrior, false), os.getToDate(1, false)));

			List<Order> ordersEnded = os.getLiveOrdersByEndDate(-1, true);
			if(ordersEnded != null && !ordersEnded.isEmpty())
			{
				try
				{
					ordersToNotify.addAll(os.autoCancelOrders(ordersEnded, user));
				}
				catch(Exception e)
				{
					LOG.error(e.getStackTrace());
				}
			}
			else
				LOG.info("There are no Expired Orders. ");

			if(ordersToNotify != null && !ordersToNotify.isEmpty() && activityInfo.isEmailRequired())
			{
				sendEmail(ordersToNotify, user);
			}
		}
		else
			LOG.error("EndDateNotificationActivity : Activity info is null");

	}

	/**
	 * 
	 */
	private void setActivityInfo()
	{
		String className = this.getClass().getCanonicalName();
		className = className.substring(className.lastIndexOf(".") + 1);
		this.activityInfo = ActivitySettings.getInstance().getActivityMap().get(className);
	}

	/**
	 * @param ordersToNotify
	 */
	private void sendEmail(List<Order> ordersToNotify, User user)
	{

		try
		{
			String mailtablecont = mailcontentTableForEndDate(ordersToNotify, user);
			String strContent = "<div> Hi " + user.getName() + "<br>Please note that the following accounts are either approaching their expiry date or have been cancelled. <br><br>" + mailtablecont + "<br/><br/>"
					+ "Kindly review and advise.<br><br>For further details please visit <a href=\"http://snagajob.propellum.com/\" target=\"_blank\" > http://snagajob.propellum.com/</a><br><br><b>Regards,<br>Propellum Team </b><div class=\"yj6qo\"></div><div class=\"adL\">"
					+ "</div></div>";
			LOG.info(strContent);
			String subject = "End-date:-" + user.getAccountName().toUpperCase();
			try
			{
				//AlertCommunicator.getInstance().sendCustomizedAlertTo(subject, strContent, activityInfo.getEmailTo(), activityInfo.getEmailCC());
			}
			catch(Exception e)
			{
				LOG.error("Error sending mail : " + e.toString());
				e.printStackTrace();
			}
		}
		catch(Exception e)
		{
			LOG.error("Error in creating mailtablecont : " + e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * @param ordersToNotify
	 * @return
	 */
	private String mailcontentTableForEndDate(List<Order> ordersToNotify, User user)
	{
		Map<String, String> rows = getRowHeaders();
		StringBuffer sb = new StringBuffer("<table width=\"70%\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\">");
		sb.append(
				"<thead><tr><th bgcolor=\"#F0D8BF\">Sr. No.</th><th bgcolor=\"#9CE6FE\">Company Name</th><th bgcolor=\"#F0D8BF\">EmployerId</th><th bgcolor=\"#9CE6FE\">Frequency</th><th bgcolor=\"#9CE6FE\">EndDate</th><th bgcolor=\"#F0D8BF\">Status</th><th bgcolor=\"#F0D8BF\">Account Representative</th></tr></thead><tbody>");
		int rowNo = 0;
		for(Order order : ordersToNotify)
		{
			BeanMap o = BeanMap.create(order);
			sb.append("<tr><td height=\"30\" align=\"center\" valign=\"middle\">" + (++rowNo) + "</td>");
			for(String row : rows.keySet())
			{
				if(row.equalsIgnoreCase("userid"))
				{
					String orderUserName = o.get(row) != null ? o.get(row).toString() : "";
					String username = orderUserName.trim().isEmpty() ? user.getName() : AuthenticationService.getInstance().getUserByLoginId(orderUserName).getName();
					sb.append("<td height=\"30\" align=\"center\" valign=\"middle\">" + username + "</td>");
				}
				else
				{
					String toAppend = o.get(row) != null ? o.get(row).toString() : " ";
					sb.append("<td height=\"30\" align=\"center\" valign=\"middle\">" + toAppend + "</td>");
				}
			}
			sb.append("</tr>");
		}
		sb.append("</tbody></table>");
		return sb.toString();
	}

	/**
	 * @return
	 */
	private Map<String, String> getRowHeaders()
	{
		String[] rows = ActivitySettings.getProperty(activityInfo.getActivityName() + ActivitySettings.DOT + ActivitySettings.EMAIL_ROWS, "").split(";");
		String[] displayName = ActivitySettings.getProperty(activityInfo.getActivityName() + ActivitySettings.DOT + ActivitySettings.EMAIL_ROWS_DISPLAY_NAMES, "").split(";");
		Map<String, String> rowsMap = new LinkedHashMap<String, String>();
		if(rows.length == displayName.length)
		{
			for(int i = 0; i < rows.length; i++)
			{
				rowsMap.put(rows[i], displayName[i]);
			}
			return rowsMap;
		}
		else
			return null;
	}

	public static void main(String[] args)
	{
		EndDateNotificationActivity ed = new EndDateNotificationActivity();
		ed.setActivityInfo();
		ed.triggerManually();
	}
}
