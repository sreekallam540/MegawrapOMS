
package com.propellum.oms.web;

import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.propellum.oms.entities.ActivityInfo;
import com.propellum.oms.services.ActivitySettings;

/**
 * Application Lifecycle Listener implementation class StartupListener
 *
 */
@WebListener
public class StartupListener implements ServletContextListener
{

	/**
	 * Default constructor.
	 */
	public StartupListener()
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent arg0)
	{
		/**
		 * Cron Expression meaning 1) 0 15 10 * * ? Fire at 10:15am every day 2) 0 10,44 14 ? 3 WED Fire at 2:10pm and at 2:44pm every Wednesday in the month of March. 3) 0 15 10 ? * MON-FRI Fire at 10:15am every Monday to Friday 4) it is like seconds,
		 * minutes, Hours, day of Month, month, day of week, year eg. 0 15 10 * * ? 2005 Fire at 10:15am every day during the year 2005
		 */

		Map<String, ActivityInfo> activityMap = ActivitySettings.getInstance().getActivityMap();

		for(String activityName : activityMap.keySet())
		{
			ActivityInfo activity = activityMap.get(activityName);
			if(!activity.isEmptyOrNull())
			{
				try
				{
					@SuppressWarnings("unchecked")
					Class<? extends Job> activityClass = (Class<? extends Job>) Class.forName(activity.getActivityClass());
					JobDetail jobDetail = JobBuilder.newJob(activityClass).withIdentity(activity.getActivityName()).build();
					Trigger trigger = TriggerBuilder.newTrigger().withIdentity(activity.getActivityName()).withSchedule(CronScheduleBuilder.cronSchedule(new CronExpression(activity.getCronExpression()))).build();

					Scheduler scheduler = new StdSchedulerFactory().getScheduler();
					scheduler.start();
					scheduler.scheduleJob(jobDetail, trigger);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}

		}

	}

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent arg0)
	{
		// TODO Auto-generated method stub
	}

}
