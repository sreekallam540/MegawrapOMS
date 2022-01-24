
package com.propellum.oms.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.propellum.oms.entities.IssueJiraDTO;
import com.propellum.oms.entities.NotificationManager;
import com.propellum.oms.entities.OrderAPI;
import com.propellum.oms.entities.OrderDetailsAPI;
import com.propellum.oms.entities.OrderModification;
import com.propellum.oms.entities.OrderModificationEntry;
import com.propellum.oms.entities.OrderStatus;
import com.propellum.oms.entities.Status;
import com.propellum.oms.entities.User;
import com.propellum.oms.factories.SQLHikariConnectionFactory;

import net.sf.cglib.beans.BeanMap;

/**
 * @author viral.sejpal
 * 
 */
public class OrderAPIService
{

	private static final Logger				LOG				= Logger.getLogger(OrderAPIService.class);
	private static OrderAPIService			instance		= new OrderAPIService();
	private static String					jiraUrl			= OMSSettings.getInstance().getProperty(OMSSettings.JIRA_URL);
	private static Boolean					SendMail		= Boolean.parseBoolean(OMSSettings.getInstance().getProperty(OMSSettings.MAIL_SERVICE));
	private static Boolean					addJira			= jiraUrl.length() > 0 ? true : false;
	private static SimpleDateFormat			sdfapi			= new SimpleDateFormat(OMSSettings.getInstance().getProperty(OMSSettings.DATE_FORMAT_FOR_API, "dd/MM/yyyy"));
	ExecutorService							executorService	= Executors.newFixedThreadPool(2);
	private static HashMap<String, String>	refreshday		= null;
	private static SimpleDateFormat			sdf				= new SimpleDateFormat(OMSSettings.getInstance().getProperty(OMSSettings.DATE_FORMAT, "yyyy-MM-dd"));

	static
	{
		refreshday = new HashMap<String, String>();
		//		Monday,Tuesday,Wednesday,Thursday,Friday
		refreshday.put("monday", "Monday");
		refreshday.put("tuesday", "Tuesday");
		refreshday.put("wednesday", "Wednesday");
		refreshday.put("thursday", "Thursday");
		refreshday.put("friday", "Friday");

	}

	public static OrderAPIService getInstance()
	{
		return instance;
	}

	public SimpleDateFormat getSimpleDateFormatForAPI()
	{
		return sdfapi;
	}

	public List<OrderAPI> getAllOrdersForAPI(int start)
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<OrderAPI> orderList = new ArrayList<OrderAPI>();
		try
		{
			conn = SQLHikariConnectionFactory.getInstance().getConnection();

			pstmt = conn.prepareStatement("SELECT * From orders LIMIT " + start);
			rs = pstmt.executeQuery();
			if(rs == null)
				return null;

			while(rs.next())
			{
				OrderAPI order = new OrderAPI();
				order.setOid(rs.getInt("orderId"));
				order.setCompanyname(rs.getString("companyName"));
				order.setEmployerid(rs.getString("spareField4"));
				order.setRefreshfrequency(rs.getString("spareField7"));
				order.setRefreshdays(rs.getString("spareField8"));
				order.setStartdate(sdfapi.format(rs.getTimestamp("startDate").getTime()));
				order.setEnddate(sdfapi.format(rs.getTimestamp("endDate").getTime()));
				order.setOrderstatus(rs.getString("status").toUpperCase());
				orderList.add(order);
			}
		}
		catch(Exception e)
		{
			LOG.error(e.toString());
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(pstmt != null)
					pstmt.close();
				if(conn != null)
					conn.close();
			}
			catch(Exception e)
			{
				LOG.error(e.getStackTrace());
			}
		}

		return orderList;
	}

	public List<Object> getOrderAPIByCompanyName(User user, String companyName)
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Object> orderList = new ArrayList<Object>();
		try
		{
			conn = SQLHikariConnectionFactory.getInstance().getConnection();

			pstmt = conn.prepareStatement("SELECT * From orders where companyName = ?");
			pstmt.setString(1, companyName);
			rs = pstmt.executeQuery();
			if(rs.next())
			{
				OrderAPI order = new OrderAPI();
				order.setOid(rs.getInt("orderId"));
				order.setCompanyname(rs.getString("companyName"));
				order.setEmployerid(rs.getString("spareField4"));
				order.setRefreshfrequency(rs.getString("spareField7"));
				order.setRefreshdays(rs.getString("spareField8"));
				order.setStartdate(sdfapi.format(rs.getTimestamp("startDate").getTime()));
				order.setEnddate(sdfapi.format(rs.getTimestamp("endDate").getTime()));
				order.setOrderstatus(rs.getString("status").toUpperCase());
				orderList.add(order);

			}
			else
			{
				Status status = new Status();
				status.setMessage("No Company Found, Please Enter Valid Company Name!");
				status.setStatus("ERROR");
				orderList.add(status);
				return orderList;

			}

		}
		catch(Exception e)
		{
			LOG.error(e.toString());
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(pstmt != null)
					pstmt.close();
				if(conn != null)
					conn.close();
			}
			catch(Exception e)
			{
				LOG.error(e.getStackTrace());
			}
		}

		return orderList;
	}

	public Status addOrder(User user, OrderDetailsAPI order) throws Exception
	{
		return storeOrder(order, user, 0);
	}

	public Status addOrder(User user, OrderDetailsAPI order, OrderModification orderModification)
	{
		Status status = new Status();

		StringBuffer sg = validateFields(order, true);
		Connection conn = null;
		PreparedStatement pstmt = null;
		String insertQuery = "INSERT INTO `orders`(`companyName`,`spareField4`,`companyUrl`, `crawlUrl`,`spareField7`,`spareField8`,`startDate`,`endDate`,`companyType`,`status`,`spareField5`,`spareField3`,`remarks`,`spareField6`,`creationDate`,`lastModified`,userId) VALUES  (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try
		{
			if(sg.toString().equalsIgnoreCase(""))
			{
				conn = SQLHikariConnectionFactory.getInstance().getConnection();
				pstmt = conn.prepareStatement(insertQuery);
				int i = 0;
				pstmt.setString(++i, order.getCompanyname());
				pstmt.setString(++i, order.getEmployerid());
				pstmt.setString(++i, order.getJoburl());
				pstmt.setString(++i, order.getJoburl());
				pstmt.setString(++i, order.getRefreshfrequency().toUpperCase());
				pstmt.setString(++i, order.getRefreshdays().toUpperCase());
				if(order.getStartdate() != null || !order.getStartdate().trim().isEmpty())
				{
					Calendar c = Calendar.getInstance();
					c.setTime(sdfapi.parse(order.getStartdate()));
					pstmt.setTimestamp(++i, new Timestamp(c.getTimeInMillis()));
				}
				if(order.getEnddate() != null || !order.getEnddate().trim().isEmpty())
				{
					Calendar c = Calendar.getInstance();
					c.setTime(sdfapi.parse(order.getEnddate()));
					pstmt.setTimestamp(++i, new Timestamp(c.getTimeInMillis()));
				}
				pstmt.setString(++i, order.getIndustry());
				pstmt.setString(++i, order.getOrderstatus().toUpperCase());
				pstmt.setString(++i, order.getAutoimport().toUpperCase());
				pstmt.setString(++i, order.getCombinewrapfiles().toUpperCase());
				pstmt.setString(++i, order.getRemarks());
				pstmt.setString(++i, order.getAdditionalnotes());
				pstmt.setTimestamp(++i, new Timestamp(System.currentTimeMillis()));
				pstmt.setTimestamp(++i, new Timestamp(System.currentTimeMillis()));
				pstmt.setString(++i, "Team");
				int recordcnt = pstmt.executeUpdate();
				if(recordcnt > 0)
				{
					String jiratablecont = jiraContent(order, user, orderModification);
					try
					{
						if(addJira)
						{
							try
							{
								JiraRestClientApi jiraApi = new JiraRestClientApi();
								IssueJiraDTO issue = new IssueJiraDTO();

								issue.setIssueSummery(OMSSettings.getInstance().getProperty("display_account_" + user.getAccountName()) + " New Order - " + order.getCompanyname());
								issue.setIssueDescription(jiratablecont);
								issue.setIssueLabel(OMSSettings.getInstance().getProperty("display_account_" + user.getAccountName()));
								jiraApi.createIssue(issue);
							}

							catch(Exception e)
							{
								LOG.error("Exception occur while Jira Issue  :: " + e.getStackTrace());
							}

						}
						if(SendMail)
						{
							String mailtablecont = mailcontent(order, user, orderModification);

							String strContent = "Hi " + user.getName() + ",<br/><br/>" + mailtablecont + "<br/><br/>" + "Thank you for the new order " + order.getCompanyname() + ".<br/><br/>" + "Have a great day ahead.<br/><br/>"
									+ "Regards,<br/>Team Propellum.";
							executorService
									.execute(new EmailThread("Propellum | " + OMSSettings.getInstance().getProperty("display_account_" + user.getAccountName()) + " New Order - " + order.getCompanyname(), strContent, new String[] {user.getLoginId()}));

						}
					}
					catch(Exception e)
					{
						LOG.error(e.getStackTrace());
					}

					try
					{
						NotificationManager.getInstance().storeNotificationForNewOrder(order, user, orderModification);
					}
					catch(Exception e)
					{
						LOG.info("ERR WHILE STORING NOTIFICATION : " + e.toString());
					}

					status.setMessage("Order Added Successfully");
					status.setStatus("SUCCESS");
				}

			}
			else
			{
				status.setMessage(sg.toString());
				status.setStatus("FAILED");
			}

		}
		catch(Exception e)
		{
			LOG.error("Order not addedd " + e.getMessage());
			LOG.error("Order not addedd " + e.getStackTrace());
		}

		return status;
	}

	public Status updateOrder(User user, OrderDetailsAPI order, int id) throws Exception
	{
		return storeOrder(order, user, id);
	}

	public Status updateOrder(User user, OrderDetailsAPI order, OrderModification orderModification, int id)
	{
		Status status = new Status();
		StringBuffer sg = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String updateQuery = "UPDATE `orders` set `companyName`= ?, `spareField4`= ?, `companyUrl`= ?, `crawlUrl`= ?, `spareField7`= ?,`spareField8` = ?,`startDate`=?,`endDate`=? ,`companyType`=?,`status`=?,`spareField5`=?,`spareField3`=?,`remarks`=?,`spareField6`=?, userId='Team', lastModified=? where orderId=?";

		try
		{
			conn = SQLHikariConnectionFactory.getInstance().getConnection();
			pstmt = conn.prepareStatement(updateQuery);
			int i = 0;
			if(order.getOid() <= 0 || order.getOid() != id)
			{
				status.setMessage("Please Enter Valid OrderID.");
				status.setStatus("Error");

				return status;
			}
			if(order.getOid() > 0)
			{
				sg = validateFields(order, false);
				if(sg.toString().equalsIgnoreCase(""))
				{
					pstmt.setString(++i, order.getCompanyname());
					pstmt.setString(++i, order.getEmployerid());
					pstmt.setString(++i, order.getJoburl());
					pstmt.setString(++i, order.getJoburl());
					pstmt.setString(++i, order.getRefreshfrequency().toUpperCase());
					pstmt.setString(++i, order.getRefreshdays().toUpperCase());
					if(order.getStartdate() != null || !order.getStartdate().trim().isEmpty())
					{
						Calendar c = Calendar.getInstance();
						c.setTime(sdfapi.parse(order.getStartdate()));
						pstmt.setTimestamp(++i, new Timestamp(c.getTimeInMillis()));
					}
					if(order.getEnddate() != null || !order.getEnddate().trim().isEmpty())
					{
						Calendar c = Calendar.getInstance();
						c.setTime(sdfapi.parse(order.getEnddate()));
						pstmt.setTimestamp(++i, new Timestamp(c.getTimeInMillis()));
					}

					pstmt.setString(++i, order.getIndustry());
					pstmt.setString(++i, order.getOrderstatus().toUpperCase());
					pstmt.setString(++i, order.getAutoimport().toUpperCase());
					pstmt.setString(++i, order.getCombinewrapfiles().toUpperCase());
					pstmt.setString(++i, order.getRemarks());
					pstmt.setString(++i, order.getAdditionalnotes());

					//updating LastModified 
					Calendar c = Calendar.getInstance();
					c.setTime(sdfapi.parse(order.getEnddate()));
					pstmt.setTimestamp(++i, new Timestamp(c.getTimeInMillis()));

					pstmt.setLong(++i, order.getOid());

					int recordcnt = pstmt.executeUpdate();
					if(recordcnt > 0)
					{
						if(orderModification != null)
						{
							OrderService.getInstance().storeOrderModification(orderModification);
							String mailtablecont = mailcontent(order, user, orderModification);
							String jiratablecont = jiraContent(order, user, orderModification);
							try
							{
								if(addJira)
								{
									try
									{
										JiraRestClientApi jiraApi = new JiraRestClientApi();
										IssueJiraDTO issue = new IssueJiraDTO();

										issue.setIssueSummery(OMSSettings.getInstance().getProperty("display_account_" + user.getAccountName()) + " Order Modification - " + order.getCompanyname());
										issue.setIssueDescription(jiratablecont);
										issue.setIssueLabel(OMSSettings.getInstance().getProperty("display_account_" + user.getAccountName()));
										jiraApi.createIssue(issue);
									}

									catch(Exception e)
									{
										LOG.error("Exception occur while Jira Issue  :: " + e.getStackTrace());
									}
								}
								if(SendMail)
								{

									String strContent = "Hi " + user.getName() + ",<br/><br/>" + mailtablecont + "<br/><br/>" + "Thank you for the recent changes you made to the " + order.getCompanyname() + " wrapping account.<br/><br/>"
											+ "Have a great day ahead.<br/><br/>" + "Regards,<br/>Team Propellum.";

									/*AlertCommunicator.getInstance().sendCustomizedAlert("Propellum | " + OMSSettings.getInstance().getProperty("display_account_" + user.getAccountName()) + " Order Modification - " + order.getOrderName(), strContent,
											new String[] {user.getLoginId()});*/

									executorService.execute(new EmailThread("Propellum | " + OMSSettings.getInstance().getProperty("display_account_" + user.getAccountName()) + " Order Modification - " + order.getCompanyname(), strContent,
											new String[] {user.getLoginId()}));
								}
							}
							catch(Exception e)
							{
								LOG.error("Error in Email  :: " + e.getStackTrace());
							}

							try
							{
								NotificationManager.getInstance().storeNotification(order, user, orderModification);
							}
							catch(Exception e)
							{
								LOG.info("ERR WHILE STORING NOTIFICATION : " + e.toString());
							}

						}

						status.setMessage("Order Updated Successfully");
						status.setStatus("SUCCESS");

					}
					else
					{
						status.setMessage("Order Not Updated");
						status.setStatus("ERROR");
					}
				}
				else
				{
					status.setMessage(sg.toString());
					status.setStatus("FAILED");
				}
			}

		}
		catch(Exception e)
		{
			LOG.error(e.getMessage(), e);
		}
		return status;
	}

	/**
	 * Validate while adding or updating order
	 */

	private StringBuffer validateFields(OrderDetailsAPI order, boolean isInsert)
	{
		StringBuffer bf = new StringBuffer();
		String compName = order.getCompanyname();
		String employerId = order.getEmployerid();
		String jobURL = order.getJoburl();
		String frequency = order.getRefreshfrequency();
		String refreshOn = order.getRefreshdays();
		String startDate = order.getStartdate();
		String endDate = order.getEnddate();
		String industry = order.getIndustry();
		String status = order.getOrderstatus();
		String autoimport = order.getAutoimport();
		String combineWrap = order.getCombinewrapfiles();

		try
		{
			if((compName != null && compName != "" && compName.trim().length() > 0) && (employerId != null && employerId != "") && (jobURL != null && jobURL != "") && (frequency != null && frequency != "") && (refreshOn != null && refreshOn != "")
					&& (startDate != null) && (endDate != null) && (industry != null && industry != "") && (status != null && status != "") && (autoimport != null && autoimport != "") && (combineWrap != null && combineWrap != ""))
			{
				if(isInsert)
				{
					if(order.getOid() > 0)
					{
						bf.append("Please remove oid field, it's not required at the time of adding order");
						return bf;
					}
					if(!getOrderAPIByCompanyName(null, compName).isEmpty())
					{
						Object companyNameObj = getOrderAPIByCompanyName(null, compName).get(0);

						if(companyNameObj instanceof OrderAPI)
						{
							OrderAPI entity = (OrderAPI) companyNameObj;
							String companyName = entity.getCompanyname();

							if(companyName.equalsIgnoreCase(compName))
							{
								bf.append("Companyname already present, Duplicate Entry.");
								return bf;
							}
						}

					}
				}

				if(!employerId.matches("[0-9]+"))
				{
					bf.append("Please enter only numbers for employer ID.!");
					return bf;
				}

				//Frequency
				if(frequency.equalsIgnoreCase("Twice_A_Week") || frequency.equalsIgnoreCase("Thrice_A_Week") || frequency.equalsIgnoreCase("Five_Times_A_Week") || frequency.equalsIgnoreCase("Twice_A_Month") || frequency.equalsIgnoreCase("Weekly")
						|| frequency.equalsIgnoreCase("Monthly") || frequency.equalsIgnoreCase("Once_A_Week") || frequency.equalsIgnoreCase("Daily"))
				{
					String[] c1 = refreshOn.split(",");
					String valid = refreshdayValidate(c1, order);
					Set<String> refreshOnSet = getRefreshDaysSize(refreshOn);
					if(valid.equalsIgnoreCase(""))
					{
						if(frequency.equalsIgnoreCase("Twice_A_Week"))
						{
							if(refreshOnSet.size() != 2)
							{
								bf.append("Please select 2 refresh day(s) for frequency " + frequency);
								return bf;

							}
						}
						else if(frequency.equalsIgnoreCase("Thrice_A_Week"))
						{
							if(refreshOnSet.size() != 3)
							{
								bf.append("Please select 3 refresh day(s) for frequency " + frequency);
								return bf;
							}
						}
						else if(frequency.equalsIgnoreCase("Daily"))
						{
							if(refreshOnSet.size() != 5)
							{
								bf.append("Please select 5 refresh day(s) for frequency " + frequency);
								return bf;
							}
						}
						else if(frequency.equalsIgnoreCase("Twice_A_Month"))
						{
							if(refreshOnSet.size() != 1)
							{
								bf.append("Please select 1 refresh day(s) for frequency " + frequency);
								return bf;
							}
						}
						else if(frequency.equalsIgnoreCase("Weekly"))
						{
							if(refreshOnSet.size() != 1)
							{
								bf.append("Please select 1 refresh day(s) for frequency " + frequency);
								return bf;
							}
						}
						else if(frequency.equalsIgnoreCase("Monthly"))
						{
							if(refreshOnSet.size() != 1)
							{
								bf.append("Please select 1 refresh day(s) for frequency " + frequency);
								return bf;
							}
						}
						else if(frequency.equalsIgnoreCase("Once_A_Week"))
						{
							if(refreshOnSet.size() != 1)
							{
								bf.append("Please select 1 refresh day(s) for frequency " + frequency);
								return bf;
							}
						}

					}
					else
					{
						bf.append(valid);
					}

				}
				else
				{
					bf.append("Refresh Frequency can hold only one of the these values : Daily, Weekly or Monthly ");
					return bf;
				}

				if(!(refreshOn.toLowerCase().contains("Monday".toLowerCase()) || refreshOn.toLowerCase().contains("Tuesday".toLowerCase()) || refreshOn.toLowerCase().contains("Wednesday".toLowerCase())
						|| refreshOn.toLowerCase().contains("Thursday".toLowerCase()) || refreshOn.toLowerCase().contains("Friday".toLowerCase())))
				{
					bf.append("RefreshDays can hold only : Monday, Tuesday, Wednesday, Thursday, Friday");
					return bf;
				}

				//start date end validation
				Date date1;
				Date date2;

				try
				{
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					date1 = sdf.parse(startDate);
					date2 = sdf.parse(endDate);
					Date todaysDate = sdf.parse(sdf.format(new Date()));
					if(isInsert)
					{
						if(!todaysDate.equals(date1) && date1.before(todaysDate))
						{
							bf.append("Start Date should be today's date or greater than today's date");
							return bf;
						}
					}

					if(date2.before(date1))
					{
						bf.append(" Please enter endDate should be greater than startDate.");
						return bf;
					}

					if(date1.equals(date2))
					{
						bf.append("StartDate and EndDate should not be same. ");
						return bf;
					}

				}
				catch(ParseException e)
				{
					// TODO Auto-generated catch block
					bf.append(" Please enter Valid Date format dd/MM/yyyy for endDate and startDate.");

				}

				//			industry validation
				if((industry != null) && (industry.equalsIgnoreCase(order.getIndustry())))
				{
					String h[] = industry.split(",");

					if(h.length > 4)
					{
						bf.append("Maximum 4 Industries can be selected!");
						return bf;
					}
				}
				//Orderstatus
				if(!(status.equalsIgnoreCase("Cancel") || status.equalsIgnoreCase("On_Hold") || status.equalsIgnoreCase("Test") || status.equalsIgnoreCase("Live")))
				{
					bf.append("OrderStatus can hold only one of the these Live, Cancel, On_Hold, Test ");
					return bf;
				}
				//Autoimport
				if(!(autoimport.equalsIgnoreCase("Y") || autoimport.equalsIgnoreCase("N")))
				{
					bf.append("AutoImport can hold only one of the two values 'Y' or 'N'.");
					return bf;
				}
				//combineWrap
				if(!(combineWrap.equalsIgnoreCase("Y") || combineWrap.equalsIgnoreCase("N")))
				{
					bf.append("CombineWrap can hold only one of the two values 'Y' or 'N'.");
					return bf;
				}

			}
			else
			{

				if((compName == null || compName == ""))
				{
					bf.append("Please enter Company Name cannot be blank .");
					return bf;
				}
				if(employerId == null || employerId == "")
				{
					bf.append("Please enter EmployerId cannot be blank .");
					return bf;
				}
				if(jobURL == null || jobURL == "" || jobURL.trim().length() <= 4)
				{
					bf.append("Please enter valid jobURL!");
					return bf;
				}
				if(frequency == null || frequency == "")
				{
					bf.append("Please enter refresh frequency cannot be blank .");
					return bf;
				}
				if(industry == null || industry == "")
				{
					bf.append("Please enter industry cannot be blank .");
					return bf;
				}

				if(status == null || status == "")
				{
					bf.append("Please enter status cannot be blank .");
					return bf;
				}
				if(autoimport == null || autoimport == "")
				{
					bf.append("Please enter autoimport cannot be blank .");
					return bf;
				}
				if(combineWrap == null || combineWrap == "")
				{
					bf.append("Please enter combineWrap cannot be blank .");
					return bf;
				}
				if(refreshOn == "" || refreshOn == null)
				{
					bf.append("Please Enter refreshDays cannot be blank ");
					return bf;
				}
				if(startDate == "" || startDate == null)
				{
					bf.append("Please Enter startDate cannot be blank ");
					return bf;
				}
				if(endDate == "" || endDate == null)
				{
					bf.append("Please Enter endDate cannot be blank ");
					return bf;
				}

			}

		}
		catch(Exception e)
		{
			bf.append(e.getMessage());
		}

		return bf;
	}

	private String refreshdayValidate(String c[], OrderDetailsAPI order)
	{

		String error = "";
		StringBuilder refreshdays = new StringBuilder();
		for(String s : c)
		{

			if(s == null || (s == null && s.trim().length() == 0))
			{
				error = "Please enter refresh day from  the following only:Monday,Tuesday,Wednesday,Thursday,Friday";
				break;
			}
			else
			{
				for(Map.Entry<String, String> entry : refreshday.entrySet())
				{
					if(entry.getKey().equalsIgnoreCase(s.trim()))
					{
						refreshdays.append(entry.getValue());
						refreshdays.append(",");
					}

				}

			}

		}
		String refreshday = refreshdays.toString();
		order.setRefreshdays(refreshday.replaceAll(",$", ""));
		return error;
	}

	private Set<String> getRefreshDaysSize(String refreshon)
	{
		String[] strArr = refreshon.split(",");
		Set<String> set = new HashSet<String>(Arrays.asList(strArr));
		return set;
	}

	public Status storeOrder(OrderDetailsAPI order, User user, int id) throws Exception
	{
		OrderModification orderModification = null;
		if(order.getOid() > 0)
		{
			OrderDetailsAPI orderPrev = getOrderById(id, true);
			BeanMap orderPrevMap = BeanMap.create(orderPrev);
			BeanMap orderMap = BeanMap.create(order);

			List<OrderModificationEntry> orderModificationEntries = new ArrayList<OrderModificationEntry>();

			for(Object prevKey : orderPrevMap.keySet())
			{
				if(orderMap.get(prevKey) == null)
				{
					continue;
				}
				String value = orderMap.get(prevKey).toString();
				String prevValue = orderPrevMap.get(prevKey) == null ? "" : orderPrevMap.get(prevKey).toString();

				if(!value.equals(prevValue))
				{
					OrderModificationEntry entry = new OrderModificationEntry();
					String fieldName = prevKey.toString();
					if(fieldName != null)
					{
						fieldName = OMSSettings.getInstance().getProperty(fieldName + "_" + user.getAccountName()) != null ? OMSSettings.getInstance().getProperty(fieldName + "_" + user.getAccountName()) : fieldName;
					}

					entry.setField(fieldName);
					entry.setOldValue(prevValue);
					entry.setNewValue(value);
					entry.setOrderModificationId(-1);

					orderModificationEntries.add(entry);
				}
			}

			if(!orderPrev.getOrderstatus().equals(order.getOrderstatus()))
			{
				if(order.getOrderstatus().equals(OrderStatus.LIVE))
				{
					order.setStartdate(Calendar.getInstance().toString());
				}
				else if(order.getOrderstatus().equals(OrderStatus.CANCELLED))
				{
					order.setEnddate(Calendar.getInstance().toString());
				}
			}

			if(orderModificationEntries.size() > 0)
			{
				orderModification = new OrderModification();
				orderModification.setOrderId(order.getOid());
				orderModification.setOrderModificationDate(Calendar.getInstance());
				orderModification.setUserId(user.getLoginId());
				orderModification.setRemark(order.getRemarks());
				orderModification.setOrderModificationEntries(orderModificationEntries);

			}

		}
		if(id > 0)
		{
			return updateOrder(user, order, orderModification, id);
		}
		else
		{
			return addOrder(user, order, orderModification);
		}

	}

	public String mailcontent(OrderDetailsAPI order, User user, OrderModification orderModification)
	{
		String tablecolumn = OMSSettings.getInstance().getProperty(OMSSettings.ORDER_TABLE_COLUMN_NAME_FOR_API);
		String displayCoulmn = OMSSettings.getInstance().getProperty(OMSSettings.ORDER_TABLE_DISPLAY_COLUMN_NAME);
		List<String> columnName = Arrays.asList(tablecolumn.split("\\s*,\\s*"));
		List<String> displayName = Arrays.asList(displayCoulmn.split("\\s*,\\s*"));

		StringBuffer sb = new StringBuffer("<table border='0' cellspacing='0' cellpadding='5'>");

		BeanMap OrderMap = BeanMap.create(order);

		Map<String, OrderModificationEntry> orderModificationMap = new HashMap<String, OrderModificationEntry>();
		if(orderModification != null)
		{
			List<OrderModificationEntry> orderModificationEntry = orderModification.getOrderModificationEntries();
			if(orderModificationEntry.size() > 0)
			{
				for(OrderModificationEntry e : orderModificationEntry)
				{
					orderModificationMap.put(e.getField(), e);
				}
			}
		}
		if(order != null)
		{
			for(String column : displayName)
			{
				sb.append("<tr><td style='font-weight:600;' width='20%'>" + column + "</td><td>:&nbsp;&nbsp;&nbsp;&nbsp;");
				String orderVal = null;
				if(orderModificationMap.containsKey(column))
				{
					OrderModificationEntry oEntry = orderModificationMap.get(column);
					orderVal = "Old value : <del>" + oEntry.getOldValue() + "</del> .<br/>&nbsp;&nbsp;&nbsp;New Value :<b> " + oEntry.getNewValue() + "</b>";
				}
				else
				{
					Object valStr = OrderMap.get(columnName.get(displayName.indexOf(column)));
					orderVal = valStr != null ? valStr.toString() : "";
				}
				sb.append(orderVal == null || orderVal.equals("null") ? "" : orderVal);
				sb.append("</td></tr>");
			}
			sb.append("</table>");
		}

		return String.valueOf(sb);
	}

	public String jiraContent(OrderDetailsAPI order, User user, OrderModification orderModification)
	{
		String tablecolumn;

		tablecolumn = OMSSettings.getInstance().getProperty(OMSSettings.ORDER_TABLE_COLUMN_NAME_FOR_API);

		String displayCoulmn = OMSSettings.getInstance().getProperty(OMSSettings.ORDER_TABLE_DISPLAY_COLUMN_NAME);
		List<String> columnName = Arrays.asList(tablecolumn.split("\\s*,\\s*"));
		List<String> displayName = Arrays.asList(displayCoulmn.split("\\s*,\\s*"));

		StringBuffer sb = new StringBuffer("\r\n");

		BeanMap OrderMap = BeanMap.create(order);

		Map<String, OrderModificationEntry> orderModificationMap = new HashMap<String, OrderModificationEntry>();
		if(orderModification != null)
		{
			List<OrderModificationEntry> orderModificationEntry = orderModification.getOrderModificationEntries();
			if(orderModificationEntry.size() > 0)
			{
				for(OrderModificationEntry e : orderModificationEntry)
				{
					orderModificationMap.put(e.getField(), e);
				}
			}
		}
		if(order != null)
		{
			for(String column : displayName)
			{
				sb.append("||" + column + " :   |");
				String orderVal = null;
				if(orderModificationMap.containsKey(column))
				{
					OrderModificationEntry oEntry = orderModificationMap.get(column);
					orderVal = "**Old value : **" + oEntry.getOldValue().replaceAll("\r\n", "  ") + ".\r\n**New Value : **" + oEntry.getNewValue().replaceAll("\r\n", "  ") + "**";
				}
				else
				{
					Object valStr = OrderMap.get(columnName.get(displayName.indexOf(column)));
					orderVal = valStr != null ? valStr.toString() : "";
					orderVal = orderVal.replaceAll("\r\n", "   ");
				}
				sb.append(orderVal == null || orderVal.equals("null") || orderVal.equalsIgnoreCase("") ? "" : orderVal);
				sb.append("||\r\n");
			}
			sb.append("\n\n");
		}

		return String.valueOf(sb);
	}

	class EmailThread implements Runnable
	{
		String		subjectStr;
		int			threadCount;
		String		strContent;
		String[]	email;
		boolean		isDebug	= true;

		EmailThread(String subjectStr, String strContent, String[] email)
		{
			++threadCount;
			this.subjectStr = subjectStr;
			this.strContent = strContent;
			this.email = email;
		}

		@Override
		public void run()
		{
			try
			{
				//AlertCommunicator.getInstance().sendCustomizedAlert(subjectStr, strContent, email);
			}
			catch(Exception e)
			{
				// TODO Auto-generated catch block
				LOG.error(e.getMessage(), e);
			}
		}

	}

	public OrderDetailsAPI getOrderById(int id, boolean modificationsRequired)
	{
		//SQLConnectionFactory connectionFactory = new SQLConnectionFactory();
		Connection conn = null;
		PreparedStatement pStmt = null;
		try
		{
			conn = SQLHikariConnectionFactory.getInstance().getConnection();

			pStmt = conn.prepareStatement("SELECT * From orders where orderId = ?");
			pStmt.setInt(1, id);

			List<OrderDetailsAPI> orders = getOrdersFromRS(pStmt.executeQuery());

			if(orders != null && orders.size() > 0)
			{
				OrderDetailsAPI orderToReturn = orders.get(0);

				if(modificationsRequired)
				{
					orderToReturn.setOrderModificationHistory(getOrderModifications(id));
				}
				return orderToReturn;
			}

		}
		catch(Exception e)
		{
			LOG.error(e.toString());
			e.printStackTrace();
			return null;
		}
		finally
		{
			try
			{
				if(pStmt != null)
					pStmt.close();
				if(conn != null)
					conn.close();
			}
			catch(Exception e)
			{
				LOG.error(e.getMessage(), e);
			}

		}
		return null;
	}

	private List<OrderDetailsAPI> getOrdersFromRS(ResultSet rs) throws SQLException
	{
		if(rs == null)
			return null;
		List<OrderDetailsAPI> returnList = new ArrayList<OrderDetailsAPI>();
		while(rs.next())
		{
			OrderDetailsAPI ord = new OrderDetailsAPI();

			ord.setOid(rs.getInt("orderId"));
			ord.setCompanyname(rs.getString("companyName"));
			ord.setJoburl(rs.getString("companyUrl"));
			ord.setRemarks(rs.getString("remarks"));
			ord.setOrderstatus(rs.getString("status").toUpperCase());
			ord.setIndustry(rs.getString("companyType"));
			ord.setCombinewrapfiles(rs.getString("spareField3"));
			ord.setEmployerid(rs.getString("spareField4"));
			ord.setAutoimport(rs.getString("spareField5"));
			ord.setAdditionalnotes(rs.getString("spareField6"));
			ord.setRefreshfrequency(rs.getString("spareField7"));
			ord.setRefreshdays(rs.getString("spareField8"));

			if(rs.getTime("startDate") != null)
			{
				ord.setStartdate(sdf.format(rs.getTimestamp("startDate").getTime()));
			}

			if(rs.getTime("endDate") != null)
			{
				ord.setEnddate(sdf.format(rs.getTimestamp("endDate").getTime()));
			}

			returnList.add(ord);
		}
		rs.close();
		return returnList;
	}

	private List<OrderModification> getOrderModificationsFromRS(ResultSet rs) throws SQLException
	{
		if(rs == null)
			return null;

		List<OrderModification> returnList = new ArrayList<OrderModification>();
		while(rs.next())
		{
			OrderModification ord = new OrderModification();

			ord.setOrderId(rs.getInt("orderId"));
			ord.setOrderModificationId(rs.getInt("modificationId"));
			Calendar creationTime = Calendar.getInstance();
			creationTime.setTimeInMillis(rs.getTimestamp("modificationDateTime").getTime());

			ord.setOrderModificationDate(creationTime);

			// new java.sql.Date(cal.getTimeInMillis();

			ord.setRemark(rs.getString("remarks"));
			ord.setUserId(rs.getString("userId"));

			String orderModiificationEntries = rs.getString("orderModificationEntries");
			String[] orderMofifications = orderModiificationEntries.split("\\$\\$\\$\\$\\$");

			List<OrderModificationEntry> modifications = new ArrayList<OrderModificationEntry>();
			for(int i = 1; i < orderMofifications.length; i++)
			{
				String orderModification = orderMofifications[i];

				String[] parts = orderModification.split("#\\$#");//#$#we,kkl#$#

				String field = parts[0];
				String oldValue = parts[1];
				String newValue = parts[2];

				OrderModificationEntry modificationEntry = new OrderModificationEntry();
				modificationEntry.setField(field);
				modificationEntry.setNewValue(newValue);
				modificationEntry.setOldValue(oldValue);
				modificationEntry.setOrderModificationId(ord.getOrderModificationId());

				modifications.add(modificationEntry);
			}

			ord.setOrderModificationEntries(modifications);

			returnList.add(ord);
		}
		rs.close();
		return returnList;
	}

	private List<OrderModification> getOrderModifications(int orderId)
	{
		//SQLConnectionFactory connectionFactory = new SQLConnectionFactory();
		Connection conn = null;
		PreparedStatement pStmt = null;
		try
		{
			conn = SQLHikariConnectionFactory.getInstance().getConnection();
			pStmt = conn.prepareStatement("SELECT * From ordermodifications where orderId= ?");
			pStmt.setInt(1, orderId);

			List<OrderModification> ordersModifications = getOrderModificationsFromRS(pStmt.executeQuery());
			return ordersModifications;
		}
		catch(Exception e)
		{
			LOG.error(e.getMessage(), e);
			return null;
		}
		finally
		{
			try
			{
				if(pStmt != null)
					pStmt.close();
				if(conn != null)
					conn.close();
			}
			catch(Exception e)
			{
				LOG.error(e.getMessage(), e);
			}

		}
	}

}
