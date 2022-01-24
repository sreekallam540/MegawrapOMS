/**
 * 
 */

package com.propellum.oms.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.propellum.oms.activity.EndDateNotificationActivity;
import com.propellum.oms.entities.BulkOrderRequest;
import com.propellum.oms.entities.ExcelOrderRequest;
import com.propellum.oms.entities.Order;
import com.propellum.oms.entities.OrderAPI;
import com.propellum.oms.entities.OrderDetailsAPI;
import com.propellum.oms.entities.OrderStatus;
import com.propellum.oms.entities.Status;
import com.propellum.oms.entities.User;
import com.propellum.oms.entities.UserType;
import com.propellum.oms.services.AuthenticationService;
import com.propellum.oms.services.OrderAPIService;
import com.propellum.oms.services.OrderService;

/**
 * @author viral.sejpal
 *
 */
@EnableWebMvc
@Controller
public class OrderController
{
	private static final String				DATE_PATTERN	= "yyyy-MM-dd";
	private static final SimpleDateFormat	SDF				= new SimpleDateFormat();
	private static final Logger				LOG				= Logger.getLogger(OrderController.class);

	private String decideWebPage(User user, String jspName)
	{
		return user.getAccountName() + "/" + jspName;
	}
	//	@InitBinder
	//	public void dataBinding(WebDataBinder binder)
	//	{
	//		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");//dd-MMM-yyyy HH:mm
	//		dateFormat.setLenient(false);
	//		binder.registerCustomEditor(Date.class, "creationDate", new CustomDateEditor(dateFormat, true));
	//	}

	@RequestMapping("/orderForm.htm")
	public String orderForm(HttpServletRequest request, ModelMap map)
	{
		map.addAttribute("order", new Order());
		map.addAttribute("orderstatuslist", OrderStatus.values());
		Map<String, String> fieldMappings = OrderService.getInstance().getfieldMappings();
		map.addAttribute("OrderDisplayFieldMappings", fieldMappings);

		User user = (User) request.getSession().getAttribute(WebConstants.SESSION_USER_KEY);

		Map<String, String> usersMap = getUserMap(user);

		map.addAttribute(WebConstants.OMS_USERS, usersMap);

		return decideWebPage(user, "newOrderForm");
	}

	/**
	 * @param user
	 * @return
	 */
	private Map<String, String> getUserMap(User user)
	{
		Map<String, String> usersMap = new HashMap<String, String>();
		List<User> usersList = new ArrayList<User>();
		if(user.getUserType().equals(UserType.ADMIN))
		{
			usersList = AuthenticationService.getInstance().getAllUsers(null, null);
		}
		else if(user.getUserType().equals(UserType.JDA))
		{
			usersList = AuthenticationService.getInstance().getAllUsers(user.getAccountName(), user.getUserType());
			usersList.addAll(AuthenticationService.getInstance().getAllUsers(user.getAccountName(), UserType.CLIENT));
		}
		else
		{
			usersList = AuthenticationService.getInstance().getAllUsers(user.getAccountName(), user.getUserType());
		}
		for(User u : usersList)
		{
			usersMap.put(u.getLoginId(), u.getName());
		}
		return usersMap;
	}

	@RequestMapping("/bulkOrderForm.htm")
	public String bulkOrderForm(HttpServletRequest request, ModelMap map)
	{
		User user = (User) request.getSession().getAttribute(WebConstants.SESSION_USER_KEY);
		map.addAttribute("bulkOrderRequest", new BulkOrderRequest());
		map.addAttribute("client", user.getAccountName());
		Map<String, String> usersMap = getUserMap(user);
		map.addAttribute(WebConstants.OMS_USERS, usersMap);

		return "bulkOrderForm";
	}

	@RequestMapping("bulkOrderProcessing.htm")
	public String bulkOrderUpload(HttpServletRequest request, @ModelAttribute("bulkOrderRequest") BulkOrderRequest bulkOrderRequest, ModelMap map)
	{
		LOG.info("OrderController : bulkOrderUpload : Content Type : " + bulkOrderRequest.getMultiPartFile().getContentType() + "  " + bulkOrderRequest.getMultiPartFile().getOriginalFilename());
		User user = (User) request.getSession().getAttribute(WebConstants.SESSION_USER_KEY);
		List<String> responseList = OrderService.getInstance().insertBulkOrders(bulkOrderRequest.getMultiPartFile(), user);

		map.addAttribute("responseList", responseList);

		LOG.info(responseList.toString());

		return "bulkOrderResponse";
	}

	@RequestMapping("/excelUploadForm.htm")
	public String excelUploadrForm(HttpServletRequest request, ModelMap map)
	{
		map.addAttribute("excelOrderRequest", new ExcelOrderRequest());
		return "excelUploadForm";
	}

	@RequestMapping("excelUploadProcessing.htm")
	public String excelOrderUpload(HttpServletRequest request, @ModelAttribute("excelOrderRequest") ExcelOrderRequest excelOrderRequest, ModelMap map) throws SQLException
	{

		List<String> responseList;
		try
		{
			LOG.info("OrderController : excelOrderUpload : Content Type : " + excelOrderRequest.getMultiPartFile().getContentType() + "  " + excelOrderRequest.getMultiPartFile().getOriginalFilename());
			User user = (User) request.getSession().getAttribute(WebConstants.SESSION_USER_KEY);
			String fileName = excelOrderRequest.getMultiPartFile().getName();
			if(fileName == null)
			{
				map.addAttribute("responseList", "No File");
			}
			responseList = OrderService.getInstance().excelOrderStatus(excelOrderRequest.getMultiPartFile(), user);
			map.addAttribute("responseList", responseList);
			LOG.info(responseList.toString());
		}
		catch(IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "excelOrderResponse";
	}

	@RequestMapping("/addForm.htm")
	public String addOrderForm(HttpServletRequest request, ModelMap map)
	{
		map.addAttribute("order", new Order());
		map.addAttribute("orderstatuslist", OrderStatus.values());
		Map<String, String> fieldMappings = OrderService.getInstance().getfieldMappings();
		map.addAttribute("OrderDisplayFieldMappings", fieldMappings);

		User user = (User) request.getSession().getAttribute(WebConstants.SESSION_USER_KEY);

		Map<String, String> usersMap = getUserMap(user);

		map.addAttribute(WebConstants.OMS_USERS, usersMap);

		return decideWebPage(user, "newOrderForm");
	}

	@RequestMapping("viewOrder.htm")
	public String viewOrder(HttpServletRequest request, ModelMap map)
	{
		int orderId = Integer.parseInt(request.getParameter("oid"));
		Order order = OrderService.getInstance().getOrderById(orderId, false);

		User user = (User) request.getSession().getAttribute(WebConstants.SESSION_USER_KEY);
		Map<String, String> usersMap = getUserMap(user);

		map.addAttribute(WebConstants.OMS_USERS, usersMap);

		map.addAttribute("order", order);
		map.addAttribute("orderstatuslist", OrderStatus.values());
		return decideWebPage(user, "newOrderForm");
	}

	@RequestMapping("viewOrderByName")
	public String viewOrderByName(HttpServletRequest request, ModelMap map)
	{
		String orderName = request.getParameter("oname");
		Order order = OrderService.getInstance().getOrderByName(orderName, false);

		User user = (User) request.getSession().getAttribute(WebConstants.SESSION_USER_KEY);
		Map<String, String> usersMap = getUserMap(user);

		map.addAttribute(WebConstants.OMS_USERS, usersMap);

		Map<String, String> fieldMappings = OrderService.getInstance().getfieldMappings();
		map.addAttribute("OrderDisplayFieldMappings", fieldMappings);
		map.addAttribute("order", order);
		map.addAttribute("orderstatuslist", OrderStatus.values());
		return decideWebPage(user, "newOrderForm");
	}

	@RequestMapping("viewOrderModificationHistory.htm")
	public String viewOrderModificationHistory(HttpServletRequest request, ModelMap map)
	{
		int orderId = Integer.parseInt(request.getParameter("oid"));
		Order order = OrderService.getInstance().getOrderById(orderId, true);
		User user = (User) request.getSession().getAttribute(WebConstants.SESSION_USER_KEY);
		Map<String, String> usersMap = getUserMap(user);

		map.addAttribute(WebConstants.OMS_USERS, usersMap);
		map.addAttribute("order", order);
		map.addAttribute("orderstatuslist", OrderStatus.values());
		return "Modification";
	}

	@RequestMapping("/storeOrder.htm")
	public String storeOrder(@ModelAttribute("order") Order order, HttpServletRequest request, ModelMap map, RedirectAttributes redirectAttributes)
	{
		User user = (User) request.getSession().getAttribute(WebConstants.SESSION_USER_KEY);
		Map<String, String> fieldMappings = OrderService.getInstance().getfieldMappings();
		map.addAttribute("OrderDisplayFieldMappings", fieldMappings);
		Map<String, String> usersMap = getUserMap(user);

		map.addAttribute(WebConstants.OMS_USERS, usersMap);

		try
		{
			if(order.getOrderName() == null || order.getOrderName().trim().length() <= 1)
			{
				map.addAttribute(WebConstants.NOTIFICATION_KEY, "Please enter a valid Company Name.! ===ERROR");
				return decideWebPage(user, "newOrderForm");
			}

			if(order.getOrderUrl() == null || order.getOrderUrl().trim().length() <= 4)
			{
				map.addAttribute(WebConstants.NOTIFICATION_KEY, "Please enter a valid URL.! ===ERROR");
				return decideWebPage(user, "newOrderForm");
			}

			if(user.getAccountName().equalsIgnoreCase("jobillico") && order.getSpareField1().equalsIgnoreCase("yes") && order.getSpareField2().isEmpty())
			{

				map.addAttribute(WebConstants.NOTIFICATION_KEY, "Please enter Splitter Field Name.! ===ERROR");
				return decideWebPage(user, "newOrderForm");
			}
			Order prevOrderDetails = null;
			if(order.getOrderId() > 0)
			{
				prevOrderDetails = OrderService.getInstance().getOrderByName(order.getOrderName(), false);
			}
			Order urlExistOrder = OrderService.getInstance().checkURLExistsOrNot(order.getOrderUrl(), order.getOrderName());
			if(urlExistOrder != null && order.getOrderId() <= 0)
			{
				map.addAttribute(WebConstants.NOTIFICATION_KEY, "Duplicate URL Entry! URL already exists for OrderName ::  " + urlExistOrder.getOrderName());
				return decideWebPage(user, "newOrderForm");
			}
			if(user.getAccountName().equalsIgnoreCase("snagajob"))
			{
				String errorMsg = errorInOrder(request.getParameterValues("refreshDay"), order);

				if(!errorMsg.trim().isEmpty())
				{
					map.addAttribute(WebConstants.NOTIFICATION_KEY, errorMsg);
					return decideWebPage(user, "newOrderForm");
				}
			}
			boolean response = OrderService.getInstance().storeOrder(order, user);
			if(response)
			{
				if(prevOrderDetails != null)
				{
					if(order.getOrderStatus().equals(OrderStatus.CANCELLED) && (prevOrderDetails.getOrderStatus().equals(OrderStatus.CANCELLED)))
					{
						redirectAttributes.addFlashAttribute(WebConstants.NOTIFICATION_KEY, order.getOrderName() + " :: Order successfully updated!  Note : Order is in 'CANCELLED' state! ");
					}
					else if(order.getOrderStatus().equals(OrderStatus.ON_HOLD) && (prevOrderDetails.getOrderStatus().equals(OrderStatus.ON_HOLD)))
					{
						redirectAttributes.addFlashAttribute(WebConstants.NOTIFICATION_KEY, order.getOrderName() + " :: Order successfully updated!  Note : Order is in 'ON_HOLD' state!");
					}
					else
						redirectAttributes.addFlashAttribute(WebConstants.NOTIFICATION_KEY, order.getOrderName() + " :: Order successfully updated.! ");
				}
				else
					redirectAttributes.addFlashAttribute(WebConstants.NOTIFICATION_KEY, order.getOrderName() + " :: Order successfully added.! ");
				boolean reload = OrderService.getInstance().reloadMegawrapCompanyCache();
				LOG.info(order + "\n Megawrap " + reload);
				return "redirect:/listOrders.htm";
			}
			else
			{
				map.addAttribute(WebConstants.NOTIFICATION_KEY, "Order could not be stored.! ===ERROR");
				return decideWebPage(user, "newOrderForm");
			}

		}
		catch(MySQLIntegrityConstraintViolationException me)
		{
			if(order != null && order.getOrderId() != 0)
			{
				Order orderError = OrderService.getInstance().getOrderById(order.getOrderId(), false);
				map.addAttribute("order", orderError);
				map.addAttribute("orderstatuslist", OrderStatus.values());

			}

			if(me.getMessage().contains("Duplicate entry") && user.getAccountName().equalsIgnoreCase("chronicle"))
			{
				String companies = OrderService.getInstance().getAliasByCompanyName(order.getSpareField4());
				if(companies != null && companies.trim().length() != 0)
				{
					map.addAttribute(WebConstants.NOTIFICATION_KEY, "Order could not be stored.! Institution Name Alias is duplicate, ( " + companies + " already exists please add a unique name ).  ===ERROR");
				}
				else
				{
					map.addAttribute(WebConstants.NOTIFICATION_KEY, "Order could not be stored.! Institution Name Alias is duplicate.  ===ERROR");
				}
			}
			else
			{
				map.addAttribute(WebConstants.NOTIFICATION_KEY, "Order could not be stored.! " + me.getMessage() + "  ===ERROR");
			}
			LOG.error("ERR : " + me.toString());
			return decideWebPage(user, "newOrderForm");
		}
		catch(Exception e)
		{
			map.addAttribute(WebConstants.NOTIFICATION_KEY, "Order could not be stored.! ===ERROR");
			LOG.error("ERR : " + e.toString());
			return decideWebPage(user, "newOrderForm");
		}
	}

	/**
	 * @param order
	 * @param strings
	 * 
	 */
	private String errorInOrder(String[] refreshDays, Order order)
	{
		String msg = "";
		if(refreshDays == null)
			return "Please Select 'Refresh Days'.! ===ERROR";
		if(order == null)
			return "Issue in order.! ===ERROR";

		List<String> refreshDay = Arrays.asList(refreshDays);
		order.setSpareField8(refreshDay.toString().replaceAll("\\[|\\]", ""));

		if(order.getSpareField7() == null)
			return "Please Select 'Refresh Frequency'.! ===ERROR";

		if(order.getStartDate() == null)
			return "Please Select 'Start Date'.! ===ERROR";

		if(order.getEndDate() == null)
			return "Please Select 'End Date'.! ===ERROR";

		if(order.getStartDate().equalsIgnoreCase(order.getEndDate()))
			return "StartDate and EndDate should not be same!";

		SimpleDateFormat sdf = OrderService.getInstance().getSimpleDateFormat();
		if(!isValidDate(sdf, order.getStartDate()) && order.getOrderId() == 0)
			return "Please Select a valid 'Start Date'";

		if(!isValidDate(sdf, order.getEndDate()) && (!order.getOrderStatus().equals(OrderStatus.CANCELLED) && !order.getOrderStatus().equals(OrderStatus.ON_HOLD)))
			return "Please Select a valid 'End Date'";

		if(!isValidDateEndDate(sdf, order.getStartDate(), order.getEndDate()))
			return "Start date cannot be greater than End date";

		if(order.getSpareField7().equalsIgnoreCase("daily") && refreshDay.size() < 5)
			return "Please Select All Days in 'Refresh Days'.! ===ERROR";

		if((order.getSpareField7().equalsIgnoreCase("weekly") || order.getSpareField7().equalsIgnoreCase("monthly")) && refreshDay.size() > 1)
			return "Please Select 1 Day in 'Refresh Days' for Monthly and Weekly Refresh Frequency.! ===ERROR";

		if(!order.getSpareField4().matches("[0-9]+"))
		{
			return "Please enter numbers only for employer ID.!  ===ERROR";
		}

		return msg;
	}

	/**
	 * @param startDate
	 * @return
	 */
	private boolean isValidDate(SimpleDateFormat sdf, String date)
	{
		try
		{
			Date today = sdf.parse(sdf.format(new Date()));
			Date compareDate = sdf.parse(date);
			if(compareDate.before(today))
				return false;
			else
				return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	private boolean isValidDateEndDate(SimpleDateFormat sdf, String date1, String date2)
	{
		try
		{
			Date startDate = sdf.parse(date1);
			Date endDate = sdf.parse(date2);
			if(endDate.before(startDate))
				return false;
			else
				return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	private boolean isUrlExists()
	{

		return true;
	}

	@RequestMapping("/listOrders.htm")
	public String listOrders(HttpServletRequest request, ModelMap map, @ModelAttribute(WebConstants.NOTIFICATION_KEY) String postResult)
	{

		User user = (User) request.getSession().getAttribute(WebConstants.SESSION_USER_KEY);
		String status = request.getParameter("status");
		Map<String, String> usersMap = getUserMap(user);

		map.addAttribute(WebConstants.OMS_USERS, usersMap);

		List<Order> orderList = OrderService.getInstance().getOrders(status, false, user.getLoginId());
		Map<String, Integer> orderStatusCountMap = OrderService.getInstance().getOrderCount("status", user.getLoginId());
		if(user.getAccountName().equals("chronicle"))
		{
			orderStatusCountMap.putAll(OrderService.getInstance().getCustomizationCountForChronicles("spareField5", user.getLoginId()));
		}
		Map<String, Integer> sorted = new TreeMap<String, Integer>(Collections.reverseOrder());
		sorted.putAll(orderStatusCountMap);
		orderStatusCountMap = sorted;
		//System.out.println("OrderController : listOrders : List  " + orderList.size());
		map.addAttribute("orderList", orderList);
		map.addAttribute("orderStatusCountMap", orderStatusCountMap);
		map.addAttribute("client", user.getAccountName());

		if(user.getAccountName().equalsIgnoreCase("snagajob"))
		{
			List<Order> approachingEndDate = OrderService.getInstance().getLiveOrdersByEndDate(7);
			orderStatusCountMap.put("ApproachingEndDate", approachingEndDate.size());
			map.addAttribute("approachingEndDate", approachingEndDate);
			Map<String, String> fieldMappings = OrderService.getInstance().getfieldMappings();
			map.addAttribute("OrderDisplayFieldMappings", fieldMappings);
			map.addAttribute("listingFields", OrderService.getInstance().getOrlerListingTableFields());
			return "newOrderList";
		}
		else
		{
			Map<String, String> fieldMappings = OrderService.getInstance().getfieldMappings();
			map.addAttribute("OrderDisplayFieldMappings", fieldMappings);
			map.addAttribute("listingFields", OrderService.getInstance().getOrlerListingTableFields());
		}
		return "orderList";
	}

	@RequestMapping("/addOrder.json")
	public void addAPIOrder(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		response.setContentType("text/json");
		JSONObject json = new JSONObject();
		PrintWriter out = response.getWriter();
		String apiKey = request.getParameter("APIKey");
		String apiSecret = request.getParameter("APISecret");
		String orderName = request.getParameter("orderName");
		String url = request.getParameter("url");
		String wrapURL = request.getParameter("wrapURL");
		String remarks = request.getParameter("remarks");
		String status = request.getParameter("status");
		String csa = request.getParameter("csa");
		String j2cClientType = request.getParameter("clientType");
		String j2cStatus = request.getParameter("j2cStatus");
		if(!WebConstants.API_KEY.equals(apiKey) || !WebConstants.API_SECRET.equals(apiSecret))
		{
			json.put("Response", "Failed.");
			json.put("Reason", "APIKey/APISecret mismatch.");
			out.write(json.toString());
			return;
		}
		if(orderName == null || url == null || csa == null || orderName.trim().length() == 0 || url.trim().length() == 0 || csa.trim().length() == 0)
		{
			json.put("Response", "Failed.");
			json.put("Reason", "One or more parameter/ field missing.");
			out.write(json.toString());
			return;
		}
		Order order = new Order();
		order.setOrderName(orderName);
		order.setOrderUrl(url);
		order.setCrawlUrl(wrapURL);
		order.setRemarks(remarks);
		boolean statusFound = false;
		if(status == null || status.trim().length() == 0)
		{
			order.setOrderStatus(OrderStatus.REQUESTED);
		}
		else
		{
			for(OrderStatus os : OrderStatus.values())
			{
				if(os.name().equals(status))
				{
					statusFound = true;
					break;
				}
			}
			if(statusFound)
			{
				order.setOrderStatus(OrderStatus.valueOf(status));
			}
			else
			{
				order.setOrderStatus(OrderStatus.REQUESTED);
			}
		}
		order.setClientSuccesAnalyst(csa);
		if(j2cClientType == null || j2cClientType.trim().length() == 0)
		{
			order.setSpareField2("Scrape");
		}
		else
		{
			order.setSpareField2(j2cClientType);
		}

		if(j2cStatus == null || j2cStatus.trim().length() == 0)
		{
			order.setSpareField3("Pending");
		}
		else
		{
			order.setSpareField3(j2cStatus);
		}
		OrderService oService = OrderService.getInstance();
		Order eOrder = oService.getOrderByName(orderName, false);
		if(eOrder != null)
		{
			json.put("Response", "Failed.");
			json.put("Reason", "Order with same order name already exists.");
			out.write(json.toString());
			return;
		}

		try
		{
			order.setOrderId(0);
			boolean success = oService.storeOrder(order);
			if(success)
			{
				json.put("Response", "Success");
			}
			else
			{
				json.put("Response", "Failed");
				json.put("Reason", "Error occured while adding new order.");
			}
			out.write(json.toString());
			return;
		}
		catch(Exception e)
		{
			LOG.error("ERR : addAPIOrder :: " + e.toString());
		}
	}

	@RequestMapping("/updateOrder.json")
	public void updateAPIOrder(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		response.setContentType("text/json");
		JSONObject json = new JSONObject();
		PrintWriter out = response.getWriter();
		String apiKey = request.getParameter("APIKey");
		String apiSecret = request.getParameter("APISecret");
		String orderName = request.getParameter("orderName");
		String url = request.getParameter("url");
		String wrapURL = request.getParameter("wrapURL");
		String remarks = request.getParameter("remarks");
		String status = request.getParameter("status");
		String csa = request.getParameter("csa");
		String j2cClientType = request.getParameter("clientType");
		String j2cStatus = request.getParameter("j2cStatus");

		if(!WebConstants.API_KEY.equals(apiKey) || !WebConstants.API_SECRET.equals(apiSecret))
		{
			json.put("Response", "Failed.");
			json.put("Reason", "APIKey/APISecret mismatch.");
			out.write(json.toString());
			return;
		}

		if(orderName == null || url == null || csa == null || orderName.trim().length() == 0 || url.trim().length() == 0 || csa.trim().length() == 0)
		{
			json.put("Response", "Failed.");
			json.put("Reason", "One or more parameter/ field missing.");
			out.write(json.toString());
			return;
		}

		Order order = new Order();
		order.setOrderName(orderName);
		order.setOrderUrl(url);
		order.setCrawlUrl(wrapURL);
		order.setRemarks(remarks);
		boolean statusFound = false;
		if(status == null || status.trim().length() == 0)
		{
			order.setOrderStatus(OrderStatus.REQUESTED);
		}
		else
		{
			for(OrderStatus os : OrderStatus.values())
			{
				if(os.name().equals(status))
				{
					statusFound = true;
					break;
				}
			}
			if(statusFound)
			{
				order.setOrderStatus(OrderStatus.valueOf(status));
			}
			else
			{
				order.setOrderStatus(OrderStatus.REQUESTED);
			}
		}
		order.setClientSuccesAnalyst(csa);
		if(j2cClientType == null || j2cClientType.trim().length() == 0)
		{
			order.setSpareField2("Scrape");
		}
		else
		{
			order.setSpareField2(j2cClientType);
		}

		if(j2cStatus == null || j2cStatus.trim().length() == 0)
		{
			order.setSpareField3("Pending");
		}
		else
		{
			order.setSpareField3(j2cStatus);
		}

		OrderService oService = OrderService.getInstance();
		Order eOrder = oService.getOrderByName(orderName, false);
		if(eOrder == null)
		{
			json.put("Response", "Failed.");
			json.put("Reason", "No order exists with given order name.");
			out.write(json.toString());
			return;
		}

		try
		{
			order.setOrderId(eOrder.getOrderId());
			boolean success = oService.storeOrder(order);
			if(success)
			{
				json.put("Response", "Success");
			}
			else
			{
				json.put("Response", "Failed");
				json.put("Reason", "Error occured while adding new order.");
			}
			out.write(json.toString());
			return;
		}
		catch(Exception e)
		{
			LOG.error("ERR : updateAPIOrder :: " + e.toString());
		}
	}

	@RequestMapping("/cancelOrder.json")
	public void cancelAPIOrder(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		response.setContentType("text/json");
		JSONObject json = new JSONObject();
		PrintWriter out = response.getWriter();
		String apiKey = request.getParameter("APIKey");
		String apiSecret = request.getParameter("APISecret");
		String orderName = request.getParameter("orderName");
		if(!WebConstants.API_KEY.equals(apiKey) || !WebConstants.API_SECRET.equals(apiSecret))
		{
			json.put("Response", "Failed.");
			json.put("Reason", "APIKey/APISecret mismatch.");
			out.write(json.toString());
			return;
		}
		if(orderName == null || orderName.trim().length() == 0)
		{
			json.put("Response", "Failed.");
			json.put("Reason", "One or more parameter/ field missing.");
			out.write(json.toString());
			return;
		}
		Order order = OrderService.getInstance().getOrderByName(orderName, false);
		if(order == null)
		{
			json.put("Response", "Failed.");
			json.put("Reason", "No order exists with given order name.");
			out.write(json.toString());
			return;
		}
		try
		{

			order.setOrderStatus(OrderStatus.CANCELLED);
			boolean success = OrderService.getInstance().storeOrder(order);
			if(success)
			{
				json.put("Response", "Success");
			}
			else
			{
				json.put("Response", "Failed");
				json.put("Reason", "Error occured while cancelling order.");
			}
			out.write(json.toString());
			return;
		}
		catch(Exception e)
		{
			LOG.error("ERR : cancelAPIOrder :: " + e.toString());
		}
	}

	@RequestMapping("/orderStatus.json")
	public void orderStatusAPIOrder(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		response.setContentType("text/json");
		JSONObject json = new JSONObject();
		PrintWriter out = response.getWriter();
		String apiKey = request.getParameter("APIKey");
		String apiSecret = request.getParameter("APISecret");
		String orderName = request.getParameter("orderName");
		if(!WebConstants.API_KEY.equals(apiKey) || !WebConstants.API_SECRET.equals(apiSecret))
		{
			json.put("Response", "Failed.");
			json.put("Reason", "APIKey/APISecret mismatch.");
			out.write(json.toString());
			return;
		}
		if(orderName == null || orderName.trim().length() == 0)
		{
			json.put("Response", "Failed.");
			json.put("Reason", "One or more parameter/ field missing.");
			out.write(json.toString());
			return;
		}

		Order order = OrderService.getInstance().getOrderByName(orderName, false);
		if(order == null)
		{
			json.put("Response", "Failed.");
			json.put("Response", "No order exists with given order name.");
			out.write(json.toString());
			return;
		}
		try
		{
			Map<String, Object> objectMap = new LinkedHashMap<String, Object>();
			objectMap.put("Order Name", order.getOrderName());
			objectMap.put("URL", order.getOrderUrl());
			objectMap.put("Order Status", order.getOrderStatus());
			objectMap.put("Order Date", formatDate(order.getCreationDate()));
			objectMap.put("Expiry Date", formatDate(order.getExpiredDate()));
			objectMap.put("Last Modified Date", formatDate(order.getLastModified()));
			objectMap.put("Remarks", order.getRemarks());
			objectMap.put("Client Success Analyst", order.getClientSuccesAnalyst());
			objectMap.put("J2C Client Type", order.getSpareField2());
			objectMap.put("J2C Status", order.getSpareField3());
			ObjectMapper mapper = new ObjectMapper();
			String jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMap);
			out.write(jsonInString);
			return;
		}
		catch(Exception e)
		{
			LOG.error("ERR : orderStatusAPIOrder :: " + e.toString());
		}
	}

	private String formatDate(Calendar calendar)
	{
		if(calendar != null)
		{
			Date date = new Date(calendar.getTimeInMillis());
			SDF.applyPattern(DATE_PATTERN);
			String sDate = SDF.format(date);
			return sDate;
		}
		return null;
	}

	@RequestMapping("/triggerManuallyEndDateNotificationActivity.htm")
	public void sendBillingMail(ModelMap map, HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		//		User user = (User) request.getSession().getAttribute(WebConstants.SESSION_USER_KEY);
		Gson gson = new Gson();
		Map<String, String> message = new HashMap<String, String>();
		try
		{
			new EndDateNotificationActivity().triggerManually();
			message.put("message", "Request Registered. Report will be sent shortly!");
		}
		catch(Exception e)
		{
			LOG.error(e.getStackTrace());
			message.put("message", e.getMessage());
		}
		response.getWriter().write(gson.toJson(message));
	}

	/**
	 * Methods added for ListOrder API
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/order")
	public @ResponseBody List<OrderAPI> getAllOrders(HttpServletRequest request, @RequestParam(value = "start", defaultValue = "50") Integer start)
	{
		return OrderAPIService.getInstance().getAllOrdersForAPI(start);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/order/{companyname}")
	@ResponseBody
	public List<Object> getOrderByCompName(HttpServletRequest request, @PathVariable("companyname") String companyname)
	{
		User user = (User) request.getAttribute("user");
		return OrderAPIService.getInstance().getOrderAPIByCompanyName(user, companyname);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/order", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Status addOrder(HttpServletRequest request, @Validated @RequestBody OrderDetailsAPI order) throws Exception
	{
		User user = (User) request.getAttribute("user");
		return OrderAPIService.getInstance().addOrder(user, order);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/order/{id}")
	@ResponseBody
	public Status updateOrder(HttpServletRequest request, @PathVariable(value = "id") int id, @RequestBody OrderDetailsAPI order) throws Exception
	{
		User user = (User) request.getAttribute("user");
		return OrderAPIService.getInstance().updateOrder(user, order, id);
	}

}
