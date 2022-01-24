
package com.propellum.oms.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.springframework.web.multipart.MultipartFile;

import com.propellum.oms.entities.IssueJiraDTO;
import com.propellum.oms.entities.NotificationManager;
import com.propellum.oms.entities.Order;
import com.propellum.oms.entities.OrderModification;
import com.propellum.oms.entities.OrderModificationEntry;
import com.propellum.oms.entities.OrderStatus;
import com.propellum.oms.entities.User;
import com.propellum.oms.factories.SQLHikariConnectionFactory;

import net.sf.cglib.beans.BeanMap;

/**
 * @author viral.sejpal
 * 
 */
public class OrderService
{

	DateFormat							df						= new SimpleDateFormat("HH:mm:ss");
	Calendar							calobj					= Calendar.getInstance();

	private static final Logger			LOG						= Logger.getLogger(OrderService.class);
	private static OrderService			instance				= new OrderService();
	private static String				jiraUrl					= OMSSettings.getInstance().getProperty(OMSSettings.JIRA_URL);
	private static Boolean				SendMail				= Boolean.parseBoolean(OMSSettings.getInstance().getProperty(OMSSettings.MAIL_SERVICE));
	private static Boolean				addJira					= jiraUrl.length() > 0 ? true : false;
	private static SimpleDateFormat		sdf						= new SimpleDateFormat(OMSSettings.getInstance().getProperty(OMSSettings.DATE_FORMAT, "yyyy-MM-dd"));
	private static Map<String, String>	fieldMappings			= new LinkedHashMap<String, String>();
	private static Map<String, String>	displayFieldMappings	= new LinkedHashMap<String, String>();
	ExecutorService						executorService			= Executors.newFixedThreadPool(2);

	public static OrderService getInstance()
	{
		return instance;
	}

	private String copyFileToLocal(MultipartFile multiPartFile) throws IOException
	{
		String fileName = Long.toString(System.currentTimeMillis()) + "_" + multiPartFile.getOriginalFilename();
		String fullFilePath = OMSSettings.getInstance().getProperty(OMSSettings.BULK_ORDER_FILE_DIR) + "/" + fileName;

		multiPartFile.transferTo(new File(fullFilePath));

		return fullFilePath;
	}

	private String copyFileToLocalForExcelUpload(MultipartFile multiPartFile) throws IOException
	{

		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH) + 1; // Note: zero based!
		int day = now.get(Calendar.DAY_OF_MONTH);
		int hour = now.get(Calendar.HOUR_OF_DAY);
		int minute = now.get(Calendar.MINUTE);
		int second = now.get(Calendar.SECOND);

		String fileName = "concordance_mapping_" + year + "-" + month + "-" + day + "_" + hour + "-" + minute + "-" + second + "_" + multiPartFile.getOriginalFilename();
		String fullFilePath = OMSSettings.getInstance().getProperty(OMSSettings.EXCEL_ORDER_FILE_DIR) + "\\" + fileName;

		File file = new File(fullFilePath);
		multiPartFile.transferTo(file);

		return fullFilePath;

	}

	public List<String> insertBulkOrders(MultipartFile multipartFile, User user)
	{
		try
		{
			String accountName = user.getAccountName();
			String localFilePath = copyFileToLocal(multipartFile);

			LOG.info("OrderService : insertBulkOrders : localFilePath = " + localFilePath);
			List<String> responseList = new ArrayList<String>();

			FileInputStream fStream = new FileInputStream(localFilePath);
			InputStreamReader fSReader = new InputStreamReader(fStream, "UTF-8");
			BufferedReader bReader = new BufferedReader(fSReader);

			String line = null;
			int lineNumber = 0;
			String displayCoulmn = OMSSettings.getInstance().getProperty(OMSSettings.COLUMN_NAME_FOR_BULK_UPLOAD);
			List<String> columnName = Arrays.asList(displayCoulmn.split("\\s*,\\s*"));

			Calendar creationTime = Calendar.getInstance();
			while((line = bReader.readLine()) != null)
			{
				++lineNumber;
				String responseLine = "Line " + lineNumber + ") ";
				try
				{

					String[] lineParts = line.split("\t");
					Order order = new Order();
					order.setCreationDate(creationTime);
					if(columnName.size() == lineParts.length)
					{
						for(String colname : columnName)
						{
							String fieldName = OMSSettings.getInstance().getProperty(colname + "_" + accountName);
							int index = columnName.indexOf(colname);
							String fieldValue = lineParts[index].trim();
							setOrderDetails(fieldName, fieldValue, order);
						}
						boolean response = storeOrder(order, user);

						if(response)
							responseLine += " Order : " + order.getOrderName() + " :  Stored Successfully. ===SUCCESS";
						else
							responseLine += " Order : " + order.getOrderName() + " :  Unknown error occurred. ===ERROR";
					}
					else
					{
						responseList.add("You Have Uploaded Wrong Format Of File  " + multipartFile.getOriginalFilename());
						responseList.add("Please follow following format");
						responseList.add(displayCoulmn.replaceAll(",", "<tab>"));

					}
				}
				catch(Exception e)
				{
					responseLine += e.toString().replace("com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException", "") + ". ===ERROR";
					LOG.error(e.toString());
					e.printStackTrace();
				}
				responseList.add(responseLine);

			}
			bReader.close();
			responseList.add(0, lineNumber + " lines processed. ===INFO");
			responseList.add(0, "Bulk Order File : " + multipartFile.getOriginalFilename() + "===INFO");

			return responseList;
		}
		catch(Exception e)
		{

			LOG.error(e.toString());
			e.printStackTrace();
		}
		return null;
	}

	private void setOrderDetails(String fieldName, String fieldValue, Order order)
	{

		switch(fieldName)
		{
			case "orderName":
				order.setOrderName(fieldValue);
			break;
			case "orderUrl":
				order.setOrderUrl(fieldValue);
			break;
			case "crawlUrl":
				order.setCrawlUrl(fieldValue);
			break;
			case "orderStatus":
				order.setOrderStatus(OrderStatus.valueOf(fieldValue.toUpperCase()));
				if(OrderStatus.valueOf(fieldValue.toUpperCase()).equals(OrderStatus.LIVE))
				{
					order.setLiveDate(Calendar.getInstance());
				}
			break;
			case "remarks":
				order.setRemarks(fieldValue);
			break;
			case "companyType":
				order.setCompanyType(fieldValue);
			break;
			case "companyDescription":
				order.setCompanyDescription(fieldValue);
			break;
			case "companyIcon":
				order.setCompanyIcon(fieldValue);
			break;
			case "companySite":
				order.setCompanySite(fieldValue);
			break;
			case "clientSuccessAnalyst":
				order.setClientSuccesAnalyst(fieldValue);
			break;
			case "spareField1":
				order.setSpareField1(fieldValue);
			break;
			case "spareField2":
				order.setSpareField2(fieldValue);
			break;
			case "spareField3":
				order.setSpareField3(fieldValue);
			break;
			case "spareField4":
				order.setSpareField4(fieldValue);
			break;
			case "spareField5":
				order.setSpareField5(fieldValue);
			break;
			case "spareField6":
				order.setSpareField6(fieldValue);
			break;
			case "spareField7":
				order.setSpareField7(fieldValue);
			break;
			case "spareField8":
				order.setSpareField8(fieldValue);
			break;
			default:
			break;
		}

	}

	private String getFileExtension(File file)
	{
		String name = file.getName();
		try
		{
			return name.substring(name.lastIndexOf(".") + 1);
		}
		catch(Exception e)
		{
			return "";
		}
	}

	public boolean reloadMegawrapCompanyCache() throws Exception
	{

		if(OMSSettings.getInstance().getProperty(OMSSettings.LOGIN_URL) != null)
		{
			String url = OMSSettings.getInstance().getProperty(OMSSettings.LOGIN_URL);
			String loginId = OMSSettings.getInstance().getProperty(OMSSettings.LOGIN_ID);
			String password = OMSSettings.getInstance().getProperty(OMSSettings.PASSWORD);
			String reloadCacheUrl = OMSSettings.getInstance().getProperty(OMSSettings.RELOAD_CACHE_URL);
			Map<String, String> cookies = new HashMap<String, String>();
			try
			{
				Response res = Jsoup.connect(url).data("loginId", loginId).data("password", password).followRedirects(true).header("Content-Type", "application/x-www-form-urlencoded").method(Method.POST).execute();
				cookies = res.cookies();

				res = Jsoup.connect(reloadCacheUrl).cookies(cookies).followRedirects(true).header("Content-Type", "application/x-www-form-urlencoded").method(Method.GET).execute();

				return true;
			}
			catch(Exception e)
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}

	public List<String> excelOrderStatus(MultipartFile multipartFile, User user) throws IOException, SQLException
	{

		List<String> responseList = new ArrayList<String>();
		String localFilePath = copyFileToLocalForExcelUpload(multipartFile);

		File file = new File(localFilePath);
		String extension = getFileExtension(file);

		if(extension.equals("xml"))
		{
			boolean check = new File(file.getParentFile(), file.getName()).exists();

			LOG.info("folder contains file " + check);
			boolean insertStatus = insertexcelorder(localFilePath, user, check);
			if(insertStatus == true)
			{
				responseList.add("Order Successfully Added For File  " + file.getName());
			}
			else
			{
				responseList.add("Order Not  Added For File   " + file.getName());

			}

		}
		else
		{
			responseList.add("You Have Uploaded Wrong File  " + file.getName());
		}
		return responseList;

	}

	public boolean insertexcelorder(String localFilePath, User user, boolean fileExistflag) throws SQLException
	{
		//SQLConnectionFactory connectionFactory = new SQLConnectionFactory();
		Connection conn = null;
		PreparedStatement pStmt = null;
		File file = new File(localFilePath);

		boolean insertRowFlag = false;
		int insertRow = 0;
		try
		{
			conn = SQLHikariConnectionFactory.getInstance().getConnection();

			LOG.info("OrderService : insertExcelOrder : localFilePath = " + localFilePath);
			pStmt = conn.prepareStatement("INSERT INTO excelrequesttracker(fileName, completed, failed, user, uploaddate,modifiedby) VALUES(?,?,?,?,?,?);");

			pStmt.setString(1, file.getName());
			pStmt.setInt(2, 1);
			pStmt.setInt(3, 0);
			pStmt.setString(4, user.getLoginId());
			pStmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
			pStmt.setString(6, user.getLoginId());

			insertRow = pStmt.executeUpdate();
			if(insertRow == 1 && fileExistflag == true)
			{
				insertRowFlag = true;
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
				if(pStmt != null)
					pStmt.close();
				if(conn != null)
					conn.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

		}

		return insertRowFlag;

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

	private List<Order> getOrdersFromRS(ResultSet rs) throws SQLException
	{
		if(rs == null)
			return null;
		List<Order> returnList = new ArrayList<Order>();
		while(rs.next())
		{
			Order ord = new Order();

			ord.setOrderId(rs.getInt("orderId"));
			ord.setOrderName(rs.getString("companyName"));
			ord.setOrderUrl(rs.getString("companyUrl"));
			ord.setCrawlUrl(rs.getString("crawlUrl"));
			ord.setRemarks(rs.getString("remarks"));
			ord.setOrderStatus(OrderStatus.valueOf(rs.getString("status")));
			ord.setUserId(rs.getString("userId"));
			ord.setCompanyType(rs.getString("companyType"));
			ord.setCompanyDescription(rs.getString("companyDescription"));
			ord.setCompanyIcon(rs.getString("companyIcon"));
			ord.setCompanySite(rs.getString("companySite"));
			ord.setSpareField1(rs.getString("spareField1"));
			ord.setSpareField2(rs.getString("spareField2"));
			ord.setSpareField3(rs.getString("spareField3"));
			ord.setSpareField4(rs.getString("spareField4"));
			ord.setSpareField5(rs.getString("spareField5"));
			ord.setSpareField6(rs.getString("spareField6"));
			ord.setSpareField7(rs.getString("spareField7"));
			ord.setSpareField8(rs.getString("spareField8"));
			Calendar creationTime = Calendar.getInstance();
			creationTime.setTimeInMillis(rs.getTimestamp("creationDate").getTime());
			ord.setCreationDate(creationTime);

			if(rs.getTimestamp("liveDate") != null)
			{
				Calendar liveTime = Calendar.getInstance();
				liveTime.setTimeInMillis(rs.getTimestamp("liveDate").getTime());
				ord.setLiveDate(liveTime);
			}

			if(rs.getTimestamp("cancelledDate") != null)
			{
				Calendar cancelledDate = Calendar.getInstance();
				cancelledDate.setTimeInMillis(rs.getTimestamp("cancelledDate").getTime());
				ord.setExpiredDate(cancelledDate);
			}

			if(rs.getTimestamp("lastModified") != null)
			{
				Calendar lastModified = Calendar.getInstance();
				lastModified.setTimeInMillis(rs.getTimestamp("lastModified").getTime());
				ord.setLastModified(lastModified);
			}

			if(rs.getTime("startDate") != null)
			{
				ord.setStartDate(sdf.format(rs.getTimestamp("startDate").getTime()));
			}

			if(rs.getTime("endDate") != null)
			{
				ord.setEndDate(sdf.format(rs.getTimestamp("endDate").getTime()));
			}

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
				e.printStackTrace();
			}

		}
	}

	public List<Order> getOrders(String status, boolean modificationsRequired, String userId)
	{
		//SQLConnectionFactory connectionFactory = new SQLConnectionFactory();
		Connection conn = null;
		PreparedStatement pStmt = null;
		try
		{
			conn = SQLHikariConnectionFactory.getInstance().getConnection();
			if(status == null || status.trim().length() == 0)
			{
				pStmt = conn.prepareStatement("SELECT * From orders");
			}
			else
			{
				pStmt = conn.prepareStatement("SELECT * From orders where status = ?");
				pStmt.setString(1, status);
			}

			List<Order> orders = getOrdersFromRS(pStmt.executeQuery());

			if(orders != null && orders.size() > 0)
			{
				for(Order orderToReturn : orders)
				{
					if(modificationsRequired)
					{
						orderToReturn.setOrderModificationHistory(getOrderModifications(orderToReturn.getOrderId()));
					}
				}
			}
			return orders;
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
				e.printStackTrace();
			}

		}
	}

	public Order getOrderById(int id, boolean modificationsRequired)
	{
		//SQLConnectionFactory connectionFactory = new SQLConnectionFactory();
		Connection conn = null;
		PreparedStatement pStmt = null;
		try
		{
			conn = SQLHikariConnectionFactory.getInstance().getConnection();

			pStmt = conn.prepareStatement("SELECT * From orders where orderId = ?");
			pStmt.setInt(1, id);

			List<Order> orders = getOrdersFromRS(pStmt.executeQuery());

			if(orders != null && orders.size() > 0)
			{
				Order orderToReturn = orders.get(0);

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
				e.printStackTrace();
			}

		}
		return null;
	}

	public Order getOrderByName(String orderName, boolean modificationsRequired)
	{
		//SQLConnectionFactory connectionFactory = new SQLConnectionFactory();
		Connection conn = null;
		PreparedStatement pStmt = null;

		try
		{
			conn = SQLHikariConnectionFactory.getInstance().getConnection();

			pStmt = conn.prepareStatement("SELECT * From orders where companyName = ?");
			pStmt.setString(1, orderName);

			List<Order> orders = getOrdersFromRS(pStmt.executeQuery());

			if(orders != null && orders.size() > 0)
			{
				Order orderToReturn = orders.get(0);
				if(modificationsRequired)
				{
					orderToReturn.setOrderModificationHistory(getOrderModifications(orderToReturn.getOrderId()));
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
			{}

		}
		return null;
	}

	public Order checkURLExistsOrNot(String url, String orderName)
	{
		//SQLConnectionFactory connectionFactory = new SQLConnectionFactory();
		Connection conn = null;
		PreparedStatement pStmt = null;

		try
		{
			conn = SQLHikariConnectionFactory.getInstance().getConnection();

			pStmt = conn.prepareStatement("SELECT * From orders where companyUrl = ? or crawlUrl=? and companyName != ?");
			pStmt.setString(1, url);
			pStmt.setString(2, url);
			pStmt.setString(3, orderName);

			List<Order> orders = getOrdersFromRS(pStmt.executeQuery());

			if(orders != null && orders.size() > 0)
			{
				Order orderToReturn = orders.get(0);

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
			{}

		}
		return null;
	}

	public Map<String, Integer> getOrderCount(String columnName, String userId)
	{
		Map<String, Integer> countMap = new HashMap<String, Integer>();
		//SQLConnectionFactory connectionFactory = new SQLConnectionFactory();
		Connection conn = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		try
		{
			conn = SQLHikariConnectionFactory.getInstance().getConnection();
			pStmt = conn.prepareStatement("SELECT COUNT(" + columnName + ") as cnt," + columnName + " FROM orders GROUP BY " + columnName + "");

			rs = pStmt.executeQuery();
			while(rs.next())
			{
				countMap.put(rs.getString(columnName), rs.getInt("cnt"));
			}
			rs.close();
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
				if(rs != null)
					rs.close();
			}
			catch(Exception e)
			{}

		}
		return countMap;
	}

	public Map<String, Integer> getCustomizationCountForChronicles(String columnName, String userId)
	{
		Map<String, Integer> countMap = new HashMap<String, Integer>();
		//SQLConnectionFactory connectionFactory = new SQLConnectionFactory();
		Connection conn = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		try
		{
			conn = SQLHikariConnectionFactory.getInstance().getConnection();
			pStmt = conn.prepareStatement("SELECT COUNT(*) as cnt , STATUS FROM orders WHERE (STATUS = 'LIVE' OR  STATUS = 'TEST') AND " + columnName + " = 'YES' GROUP BY STATUS");
			//SELECT COUNT(*), STATUS FROM orders WHERE spareField5 = 'YES' GROUP BY STATUS
			rs = pStmt.executeQuery();
			while(rs.next())
			{
				countMap.put("Customization(" + rs.getString("status") + ")", rs.getInt("cnt"));
			}
			rs.close();
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
				if(rs != null)
					rs.close();
			}
			catch(Exception e)
			{}

		}
		return countMap;
	}

	public boolean storeOrder(Order order) throws Exception
	{
		String email[] = order.getClientSuccesAnalyst().split("_");
		User user = AuthenticationService.getInstance().getUserByLoginId(email[email.length - 1]);
		boolean success = storeOrder(order, user);
		return success;
	}

	public boolean storeOrder(Order order, User user) throws Exception
	{
		return storeOrder(order, user, false);
	}

	public boolean storeOrder(Order order, User user, boolean activityTriggeredOrder) throws Exception
	{
		OrderModification orderModification = null;
		if(order.getOrderId() > 0)
		{
			Order orderPrev = getOrderById(order.getOrderId(), false);
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

			order.setLiveDate(orderPrev.getLiveDate());
			order.setExpiredDate(orderPrev.getExpiredDate());

			if(!orderPrev.getOrderStatus().equals(order.getOrderStatus()))
			{
				if(order.getOrderStatus().equals(OrderStatus.LIVE))
				{
					order.setLiveDate(Calendar.getInstance());
				}
				else if(order.getOrderStatus().equals(OrderStatus.CANCELLED))
				{
					order.setExpiredDate(Calendar.getInstance());
				}
			}

			if(orderModificationEntries.size() > 0)
			{
				orderModification = new OrderModification();
				orderModification.setOrderId(order.getOrderId());
				orderModification.setOrderModificationDate(Calendar.getInstance());
				orderModification.setUserId(user.getLoginId());
				orderModification.setRemark(order.getRemarks());
				orderModification.setOrderModificationEntries(orderModificationEntries);

			}

		}
		return storeOrder(order, user, orderModification, activityTriggeredOrder);
	}

	public boolean storeOrderModification(OrderModification orderModification)
	{
		Connection conn = null;
		PreparedStatement pStmt = null;
		try
		{
			StringBuilder orderModificationEntryValues = new StringBuilder();

			for(OrderModificationEntry entry : orderModification.getOrderModificationEntries())
			{
				orderModificationEntryValues.append("$$$$$");
				orderModificationEntryValues.append(entry.getField());
				orderModificationEntryValues.append("#$#");
				orderModificationEntryValues.append(entry.getOldValue());
				orderModificationEntryValues.append("#$#");
				orderModificationEntryValues.append(entry.getNewValue());
			}
			conn = SQLHikariConnectionFactory.getDataSource().getConnection();

			pStmt = conn.prepareStatement("INSERT INTO ordermodifications(orderId, modificationDateTime, userId, remarks, orderModificationEntries) VALUES(?,?,?,?,?);");
			pStmt.setInt(1, orderModification.getOrderId());
			pStmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			pStmt.setString(3, orderModification.getUserId());
			pStmt.setString(4, orderModification.getRemark());
			pStmt.setString(5, orderModificationEntryValues.toString());

			return pStmt.execute();

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
				if(pStmt != null)
					pStmt.close();
				if(conn != null)
					conn.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

		}
		return false;

	}

	public String mailcontent(Order order, User user, OrderModification orderModification)
	{
		String tablecolumn = OMSSettings.getInstance().getProperty(OMSSettings.ORDER_TABLE_COLUMN_NAME);
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

	public String jiraContent(Order order, User user, OrderModification orderModification)
	{
		String tablecolumn = OMSSettings.getInstance().getProperty(OMSSettings.ORDER_TABLE_COLUMN_NAME);
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
					orderVal = "**Old value : **" + oEntry.getOldValue().replaceAll("\r\n", "  ") + ".\r\nNew Value : **" + oEntry.getNewValue().replaceAll("\r\n", "  ") + "**";
				}
				else
				{
					Object valStr = OrderMap.get(columnName.get(displayName.indexOf(column)));
					orderVal = valStr != null ? valStr.toString() : "";
					orderVal = orderVal.replaceAll("\r\n", "   ");
				}
				sb.append(orderVal == null || orderVal.equals("null") || orderVal.equalsIgnoreCase("") ? "-" : orderVal);
				sb.append("||\r\n");
			}
			sb.append("\n\n");
		}

		return String.valueOf(sb);
	}

	private boolean storeOrder(Order order, User user, OrderModification orderModification, boolean activityTriggered) throws Exception
	{
		//SQLConnectionFactory connectionFactory = new SQLConnectionFactory();
		Connection conn = null;
		PreparedStatement pStmt = null;
		try
		{
			conn = SQLHikariConnectionFactory.getInstance().getConnection();
			int i = 0;
			if(order.getOrderId() > 0)
			{
				StringBuilder sb = new StringBuilder();
				sb.append(
						"UPDATE orders SET companyName = ?, companyUrl = ?, crawlUrl = ?, status = ?,remarks = ?, lastModified = ?, liveDate = ?, cancelledDate = ?, startDate = ? , endDate = ?, companyType= ?, companyDescription = ?, companySite = ?, companyIcon = ?, clientSuccessAnalyst = ? ,spareField1 = ? , spareField2 = ? , spareField3 = ? , spareField4 = ? , spareField5 = ? , spareField6 = ? , spareField7 = ? , spareField8 = ? ");
				if(order.getUserId() != null && !order.getUserId().isEmpty())
					sb.append(", userId = ?");
				sb.append(" WHERE orderId = ?");
				pStmt = conn.prepareStatement(sb.toString());

				pStmt.setString(++i, order.getOrderName());
				pStmt.setString(++i, order.getOrderUrl());
				pStmt.setString(++i, order.getCrawlUrl());
				pStmt.setString(++i, order.getOrderStatus().toString());
				pStmt.setString(++i, order.getRemarks());
				pStmt.setTimestamp(++i, new Timestamp(System.currentTimeMillis()));
				pStmt.setTimestamp(++i, order.getLiveDate() != null ? new Timestamp(order.getLiveDate().getTimeInMillis()) : null);
				pStmt.setTimestamp(++i, order.getExpiredDate() != null ? new Timestamp(order.getExpiredDate().getTimeInMillis()) : null);

				if(order.getStartDate() != null && !order.getStartDate().trim().isEmpty())
				{
					Calendar c = Calendar.getInstance();
					c.setTime(sdf.parse(order.getStartDate()));
					pStmt.setTimestamp(++i, new Timestamp(c.getTimeInMillis()));
				}
				else
					pStmt.setTimestamp(++i, null);

				if(order.getEndDate() != null && !order.getEndDate().trim().isEmpty())
				{
					Calendar c = Calendar.getInstance();
					c.setTime(sdf.parse(order.getEndDate()));
					pStmt.setTimestamp(++i, new Timestamp(c.getTimeInMillis()));
				}
				else
					pStmt.setTimestamp(++i, null);

				pStmt.setString(++i, order.getCompanyType());
				pStmt.setString(++i, order.getCompanyDescription());
				pStmt.setString(++i, order.getCompanySite());
				pStmt.setString(++i, order.getCompanyIcon());
				pStmt.setString(++i, order.getClientSuccesAnalyst());
				pStmt.setString(++i, order.getSpareField1());
				pStmt.setString(++i, order.getSpareField2());
				pStmt.setString(++i, order.getSpareField3());
				pStmt.setString(++i, order.getSpareField4());
				pStmt.setString(++i, order.getSpareField5());
				pStmt.setString(++i, order.getSpareField6());
				pStmt.setString(++i, order.getSpareField7());
				pStmt.setString(++i, order.getSpareField8());
				if(order.getUserId() != null && !order.getUserId().isEmpty())
					pStmt.setString(++i, order.getUserId());
				pStmt.setInt(++i, order.getOrderId());

				LOG.info("QUERY : " + pStmt.toString());
				int response = pStmt.executeUpdate();
				LOG.info("RES : " + response);
				if(response > 0)
				{
					if(orderModification != null)
					{
						storeOrderModification(orderModification);

						try
						{
							if(addJira)
							{
								try
								{
									String jiratablecont = jiraContent(order, user, orderModification);
									JiraRestClientApi jiraApi = new JiraRestClientApi();
									IssueJiraDTO issue = new IssueJiraDTO();

									issue.setIssueSummery(OMSSettings.getInstance().getProperty("display_account_" + user.getAccountName()) + " Order Modification - " + order.getOrderName());
									issue.setIssueDescription(jiratablecont);
									issue.setIssueLabel(OMSSettings.getInstance().getProperty("display_account_" + user.getAccountName()));
									jiraApi.createIssue(issue);
								}

								catch(Exception e)
								{
									LOG.error("Exception occur while Jira Issue  :: " + e.getStackTrace());
								}
							}
							if(SendMail && !activityTriggered)
							{
								String mailtablecont = mailcontent(order, user, orderModification);
								String strContent = getStatusModification(orderModification, order, user, mailtablecont) + "<br><br>" + mailtablecont;

								executorService.execute(
										new EmailThread("Propellum | " + OMSSettings.getInstance().getProperty("display_account_" + user.getAccountName()) + " Order Modification - " + order.getOrderName(), strContent, new String[] {user.getLoginId()}));
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

					return true;
				}
			}
			else
			{
				order.setCreationDate(Calendar.getInstance());
				pStmt = conn.prepareStatement(
						"INSERT INTO orders(companyName , companyUrl , crawlUrl , status ,remarks ,userId, creationDate, lastModified, liveDate, startDate, endDate, companyType, companyDescription, companySite, companyIcon, clientSuccessAnalyst, spareField1, spareField2, spareField3, spareField4, spareField5, spareField6, spareField7, spareField8) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

				pStmt.setString(++i, order.getOrderName());
				pStmt.setString(++i, order.getOrderUrl());
				pStmt.setString(++i, order.getCrawlUrl());
				pStmt.setString(++i, order.getOrderStatus().toString());
				pStmt.setString(++i, order.getRemarks());
				pStmt.setString(++i, order.getUserId() != null && !order.getUserId().trim().isEmpty() ? order.getUserId() : user.getLoginId());
				pStmt.setTimestamp(++i, new Timestamp(order.getCreationDate().getTimeInMillis()));
				pStmt.setTimestamp(++i, new Timestamp(System.currentTimeMillis()));
				pStmt.setTimestamp(++i, order.getLiveDate() != null ? new Timestamp(order.getLiveDate().getTimeInMillis()) : null);

				if(order.getStartDate() != null && !order.getStartDate().trim().isEmpty())
				{
					Calendar c = Calendar.getInstance();
					c.setTime(sdf.parse(order.getStartDate()));
					pStmt.setTimestamp(++i, new Timestamp(c.getTimeInMillis()));
				}
				else
					pStmt.setTimestamp(++i, null);

				if(order.getEndDate() != null && !order.getEndDate().trim().isEmpty())
				{
					Calendar c = Calendar.getInstance();
					c.setTime(sdf.parse(order.getEndDate()));
					pStmt.setTimestamp(++i, new Timestamp(c.getTimeInMillis()));
				}
				else
					pStmt.setTimestamp(++i, null);

				pStmt.setString(++i, order.getCompanyType());
				pStmt.setString(++i, order.getCompanyDescription());
				pStmt.setString(++i, order.getCompanySite());
				pStmt.setString(++i, order.getCompanyIcon());
				pStmt.setString(++i, order.getClientSuccesAnalyst());
				pStmt.setString(++i, order.getSpareField1());
				pStmt.setString(++i, order.getSpareField2());
				pStmt.setString(++i, order.getSpareField3());
				pStmt.setString(++i, order.getSpareField4());
				pStmt.setString(++i, order.getSpareField5());
				pStmt.setString(++i, order.getSpareField6());
				pStmt.setString(++i, order.getSpareField7());
				pStmt.setString(++i, order.getSpareField8());

				LOG.info("QUERY : " + pStmt.toString());
				int response = pStmt.executeUpdate();
				LOG.info("RES : " + response);

				if(response > 0)
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

								issue.setIssueSummery(OMSSettings.getInstance().getProperty("display_account_" + user.getAccountName()) + " New Order - " + order.getOrderName());
								issue.setIssueDescription(jiratablecont);
								issue.setIssueLabel(OMSSettings.getInstance().getProperty("display_account_" + user.getAccountName()));
								jiraApi.createIssue(issue);
							}

							catch(Exception e)
							{
								LOG.error("Exception occur while Jira Issue  :: " + e.getStackTrace());
							}

						}
						if(SendMail && !activityTriggered)
						{
							String mailtablecont = mailcontent(order, user, orderModification);

							String strContent = "Hi " + user.getName() + ",<br><br>Thank you for the new wrap order " + order.getOrderName()
									+ ".<br><br>We are currently in the process of evaluating the URL / Requirements for the new client and will provide you with the test wrap results at the earliest.<br><br>Regards,<br>Propellum Job Wrapping Team.<br><br>"
									+ mailtablecont;
							executorService
									.execute(new EmailThread("Propellum | " + OMSSettings.getInstance().getProperty("display_account_" + user.getAccountName()) + " New Order - " + order.getOrderName(), strContent, new String[] {user.getLoginId()}));

						}
					}
					catch(Exception e)
					{
						e.toString();
					}

					try
					{
						NotificationManager.getInstance().storeNotificationForNewOrder(order, user, orderModification);
					}
					catch(Exception e)
					{
						LOG.info("ERR WHILE STORING NOTIFICATION : " + e.toString());
					}
					return true;
				}

			}

		}
		catch(Exception e)
		{
			LOG.error(e.toString());
			e.printStackTrace();
			throw e;
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
			{}

		}
		return false;

	}

	public String getAliasByCompanyName(String orderName)
	{
		//SQLConnectionFactory connectionFactory = new SQLConnectionFactory();
		Connection conn = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		String companies = "";

		try
		{
			conn = SQLHikariConnectionFactory.getInstance().getConnection();

			pStmt = conn.prepareStatement("SELECT companyName From orders where spareField4 = ?");
			pStmt.setString(1, orderName);
			rs = pStmt.executeQuery();
			if(rs != null)
			{
				while(rs.next())
				{
					sb.append(rs.getString("companyName") + ", ");
				}
				companies = sb.toString().substring(0, sb.lastIndexOf(", "));
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
				if(rs != null)
					rs.close();
			}
			catch(Exception e)
			{}

		}
		return companies;
	}

	/**
	 * @return
	 */
	public Map<String, String> getfieldMappings()
	{

		if(fieldMappings != null && fieldMappings.size() > 0)
		{
			return fieldMappings;
		}
		else
		{
			String[] fieldNames = OMSSettings.getInstance().getProperty("ordertableColumnName").split(",");
			String[] fieldDisplaynames = OMSSettings.getInstance().getProperty("ordertableColumnDisplayName").split(",");
			if(fieldNames.length == fieldDisplaynames.length)
			{
				for(int i = 0; i < fieldNames.length; i++)
				{
					fieldMappings.put(fieldNames[i], fieldDisplaynames[i]);
					displayFieldMappings.put(fieldDisplaynames[i], fieldNames[i]);
				}
			}
			else
				LOG.error("ordertableColumnName and ordertableColumnDisplayName length Mismatch. Please check settings.properties");
		}
		return fieldMappings;
	}

	public List<String> getOrlerListingTableFields()
	{
		List<String> listingFields = Arrays.asList(OMSSettings.getInstance().getProperty("orderListingTableFields").split(","));

		return listingFields;
	}

	public List<Order> getLiveOrdersByEndDate(int daysPrior)
	{
		return getLiveOrdersByEndDate(daysPrior, false);
	}

	public List<Order> getLiveOrdersByEndDate(int daysPrior, boolean excludeTodaysDay)
	{
		//SQLConnectionFactory connectionFactory = new SQLConnectionFactory();
		Connection conn = null;
		PreparedStatement pStmt = null;
		String fromDate = getFromDate(daysPrior, excludeTodaysDay);
		String toDate = getToDate(daysPrior, excludeTodaysDay);

		try
		{
			conn = SQLHikariConnectionFactory.getInstance().getConnection();

			pStmt = conn.prepareStatement("SELECT * From orders where status = 'LIVE' AND endDate between ? AND ?");
			pStmt.setString(1, fromDate);
			pStmt.setString(2, toDate);
			List<Order> orders = getOrdersFromRS(pStmt.executeQuery());
			return orders;
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
				e.printStackTrace();
			}

		}
	}

	/**
	 * @param daysPrior
	 * @return
	 */
	public String getToDate(int daysPrior, boolean excludeTodaysDay)
	{
		String toDate = "";
		Calendar calendar = Calendar.getInstance();
		if(daysPrior == 0)
			toDate = sdf.format(calendar.getTime());
		else if(daysPrior < 0)
		{
			if(excludeTodaysDay)
				calendar.add(Calendar.DAY_OF_MONTH, -1);
			toDate = sdf.format(calendar.getTime());
		}
		else
		{
			if(excludeTodaysDay)
				calendar.add(Calendar.DAY_OF_MONTH, 1);
			calendar.add(Calendar.DAY_OF_MONTH, daysPrior);
			toDate = sdf.format(calendar.getTime());
		}
		return toDate;
	}

	/**
	 * @param daysPrior
	 * @return
	 */
	public String getFromDate(int daysPrior, boolean excludeTodaysDay)
	{
		String fromDate = "";
		Calendar calendar = Calendar.getInstance();
		if(daysPrior == 0)
			fromDate = sdf.format(calendar.getTime());
		else if(daysPrior < 0)
		{
			if(excludeTodaysDay)
				calendar.add(Calendar.DAY_OF_MONTH, -1);
			calendar.add(Calendar.DAY_OF_MONTH, daysPrior);
			fromDate = sdf.format(calendar.getTime());
		}
		else
		{
			if(excludeTodaysDay)
				calendar.add(Calendar.DAY_OF_MONTH, 1);
			fromDate = sdf.format(calendar.getTime());
			calendar.add(Calendar.DAY_OF_MONTH, daysPrior);
		}
		return fromDate;
	}

	/**
	 * @param ordersEnded
	 * @param user
	 */
	public List<Order> autoCancelOrders(List<Order> ordersEnded, User user)
	{
		try
		{
			for(Order order : ordersEnded)
			{
				order.setOrderStatus(OrderStatus.CANCELLED);
				order.setUserId(user.getLoginId());
				storeOrder(order, user, true);
			}
			return ordersEnded;
		}
		catch(Exception e)
		{
			LOG.error(e);
			return null;
		}
	}

	public SimpleDateFormat getSimpleDateFormat()
	{
		return sdf;
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
				e.printStackTrace();
			}
		}

	}

	/**
	 * @param orderModification
	 * @param mailtablecont
	 * @param user
	 * @param order
	 * @return
	 */
	private String getStatusModification(OrderModification orderModification, Order order, User user, String mailtablecont)
	{
		// Use spareField7 for refresh Freq for all clients
		String defaultFilterField = "spareField7";
		OrderModificationEntry modificationEntry = getStatusOrFreqModification(orderModification, defaultFilterField);

		if(modificationEntry != null)
		{
			String value = displayFieldMappings.get(modificationEntry.getField()) + "#" + modificationEntry.getOldValue() + "#" + modificationEntry.getNewValue();
			switch(value.toLowerCase())
			{
				case "orderstatus#test#live":
					return "Hi " + user.getName() + ",<br><br>Thank you for approving the " + order.getOrderName()
							+ " wrap for production.<br>We are in the process of re-scraping the client's site and will let you know once the Live wrap is delivered.<br><br>(Please ignore this notification if the updated wrap has already been delivered)<br><br>Regards,<br>Propellum Job Wrapping Team.";

				case "orderstatus#test#cancelled":
				return "Hi " + user.getName() + ", <br><br>We have taken note of the order Modification of " + order.getOrderName() + " account.<br><br>Regards,<br>Propellum Job Wrapping Team.";

				case "orderstatus#test#on_hold":
					return "Hi " + user.getName() + ", <br><br>We will place Job Wrapping on hold for " + order.getOrderName() + " until further update.<br><br>Regards,<br>Propellum Job Wrapping Team.";

				case "orderstatus#live#test":
					return "Hi " + user.getName() + ",<br><br>We have taken note of the wrapping update for " + order.getOrderName() + " and this will be implemented in the next scheduled refresh.<br>Regards,<br>Propellum Job Wrapping Team.";

				case "orderstatus#live#cancelled":
					return "Hi " + user.getName() + ",<br><br>We have taken note of the cancellation of " + order.getOrderName() + " account and will discontinue wrapping with immediate effect.<br><br>Regards,<br>Propellum Job Wrapping Team";

				case "orderstatus#live#on_hold":
					return "Hi " + user.getName() + ",<br><br>We will discontinue wrapping jobs for " + order.getOrderName() + " until further update.<br><br>Regards,<br>Propellum Job Wrapping Team";

				case "orderstatus#cancelled#live":
					return "Hi " + user.getName() + ",<br><br>Thank you for reviving the wrap order " + order.getOrderName()
							+ " for production.<br><br> We are in the process of re-scraping the client's site and will let you know once the Live wrap is delivered.<br><br>(Please ignore this notification if the updated wrap has already been delivered)<br><br>Regards,<br>Propellum Job Wrapping Team.";

				case "orderstatus#on_hold#live":
					return "Hi " + user.getName() + ",<br><br>Thank you for reviving the wrap order " + order.getOrderName()
							+ " for production.<br><br> We are in the process of re-scraping the client's site and will let you know once the Live wrap is delivered.<br><br>(Please ignore this notification if the updated wrap has already been delivered)<br><br>Regards,<br>Propellum Job Wrapping Team.";

				case "orderstatus#cancelled#test":
					return "Hi " + user.getName() + ",<br>We have taken note of the wrapping update for " + order.getOrderName() + " and this will be implemented in the next scheduled refresh.<br><br>Regards,<br>Propellum Job Wrapping Team.";

				case "orderstatus#on_hold#test":
					return "Hi " + user.getName() + ",<br><br>We have taken note of the wrapping update for " + order.getOrderName() + " and this will be implemented in the next scheduled refresh.<br><br>Regards,<br>Propellum Job Wrapping Team.";
				
				case "orderstatus#cancelled#on_hold":
					return "Hi " + user.getName() + ",<br><br>We have taken note of the order Modification of " + order.getOrderName() + " account.<br><br>Regards,<br>Propellum Job Wrapping Team.";
				
				case "orderstatus#on_hold#cancelled":
					return "Hi " + user.getName() + ",<br><br>We have taken note of the cancellation of " + order.getOrderName() + " account.<br><br>Regards,<br>Propellum Job Wrapping Team.";

				default:
					return "Hi " + user.getName() + ",<br/><br/>" + "Thank you for the recent changes you made to the " + order.getOrderName() + " wrapping account.<br/><br/>" + "Have a great day ahead.<br/><br/>"
							+ "Regards,<br/>Propellum Job Wrapping Team.";
			}
		}

		return null;

	}

	/**
	 * @param orderModification
	 * @return
	 */
	private OrderModificationEntry getStatusOrFreqModification(OrderModification orderModification, String defaultFilterField)
	{
		List<OrderModificationEntry> listOfModifications = orderModification.getOrderModificationEntries();
		for(OrderModificationEntry o : listOfModifications)
		{
			if(o.getField().equals(fieldMappings.get("orderStatus")))
			{
				return o;
			}
		}
		for(OrderModificationEntry o : listOfModifications)
		{
			if(o.getField().equals(fieldMappings.get(defaultFilterField)))
			{
				return o;
			}
		}

		return listOfModifications.get(0);
	}
}
