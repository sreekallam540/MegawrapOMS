<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.propellum.oms.entities.Order"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Order Management System</title>
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
<meta name="apple-mobile-web-app-capable" content="yes">

<link href="css/bootstrap.min.css" rel="stylesheet" type="text/css" />
<link href="css/bootstrap-responsive.min.css" rel="stylesheet"
	type="text/css" />

<link href="css/font-awesome.css" rel="stylesheet">
<link
	href="http://fonts.googleapis.com/css?family=Open+Sans:400italic,600italic,400,600"
	rel="stylesheet">

<link href="css/style.css" rel="stylesheet" type="text/css">
<link href="css/pages/signin.css" rel="stylesheet" type="text/css">
<script src="http://code.jquery.com/jquery-1.8.3.js"></script>
<script language="JavaScript">
	window.addEventListener('load', function() {
		var startDateType = document.getElementById("startDate").type;
		console.log(startDateType);
		if (startDateType != 'date') {
			document.getElementById("startDate").setAttribute('placeholder',
					'Use latest browsers for better User experience.')
			document.getElementById("endDate").setAttribute('placeholder',
					'Use latest browsers for better User experience.')
			var x = document.getElementById("startDateMoreInfo");
			x.style.display = "block";
			document.getElementById("endDateMoreInfo").style.display = "block";
		}
	})
	function viewModificationHistory(oid) {
		window.open("viewOrderModificationHistory.htm?oid=" + oid);
	}

	$(document).ready(function() {

		$('#submitButton').click(function() {
			$('#orderStatus').prop('disabled', false);
		});

	});

	function FiveDaysCheck(value) {
		var forms = document.forms[0];
		var noOfvalue;
		var val = "";
		if (value != null)
			val = value;
		if (val != null && val == 'DAILY') {
			noOfvalue = 5;
		} else {
			noOfvalue = 0;
		}

		if (noOfvalue) {
			for (i = 0; i < noOfvalue; i++) {
				forms.refreshDay[i].checked = "true";
			}
		} else {
			for (i = 0; i < 5; i++) {
				forms.refreshDay[i].checked = "";
			}
		}
	}

	function req() 
	{
		var regex = /(http|https):\/\/(\w+:{0,1}\w*)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%!\-\/]))?/;

		if (document.forms["orderForm"]["orderName"].value == "") {
			alert("Please enter Company Name");
			return false;
		} else if (document.forms["orderForm"]["spareField4"].value == "") {
			alert("Please enter Employer ID");
			return false;
		} else if (!regex.test(document.forms["orderForm"]["orderUrl"].value)) {
			alert("Please input a valid Url in orderUrl");
			return false;
		} else if (document.forms["orderForm"]["spareField7"].value == "") {
			alert("Please select Refresh Frequency");
			return false;
		} else if (document.forms["orderForm"]["startDate"].value == "") {
			alert("Please select start Date");
			return false;
		} else if (document.forms["orderForm"]["companyType"].value == "") {
			alert("Please select Industry");
			return false;
		} else if (document.forms["orderForm"]["userId"].value == "") {
			alert("Please select Account Holder.");
			return false;
		} else 
		{
			return true;
		}
	}
</script>

<style>
.hint {
	position: relative;
	display: inline-block;
	border-bottom: 1px dotted black;
}

.hint .hinttext {
	visibility: hidden;
	width: 250px;
	background-color: black;
	color: #fff;
	text-align: center;
	border-radius: 6px;
	padding: 5px 0;
	/* Position the tooltip */
	position: absolute;
	z-index: 1;
}

.hint:hover .hinttext {
	visibility: visible;
}
</style>
</head>
<body>
	<%@include file="../header.jsp"%>

	<%
		User sessionuser = (User) session.getAttribute(WebConstants.SESSION_USER_KEY);
		Order order = (Order) request.getAttribute("order");
		Map<String, String> usersMap = (Map<String, String>) request.getAttribute(WebConstants.OMS_USERS);
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
		boolean companyNameReadOnly = order.getOrderId() > 0 && (!sessionuser.getUserType().equals(UserType.ADMIN));
	%>



	<div class="main">
		<div class="main-inner">
			<div class="container">
				<div class="row">

					<div class="span12">

						<div class="widget ">

							<div class="widget-header">
								<i class="icon-file-alt"></i>
								<h3 id="orderFormHeader">Add New Order</h3>
							</div>
						</div>
					</div>
					<div class="span6" style="height: auto; padding: 0px 0px 23px;">
						<div class="account-container register" style="width: 600px;">
							<div class="content clearfix">
								<%
									String msg = (String) request.getAttribute(WebConstants.NOTIFICATION_KEY);
									if (msg == null)
										msg = "";
								%>

								<%
									if (msg.trim().length() > 0 && msg.endsWith("===ERROR")) {
								%>
								<div class="alert alert-danger">
									<%=msg.replaceAll("===ERROR", "")%>
								</div>
								<%
									} else if (msg.trim().length() > 0 && msg.endsWith("===SUCCESS")) {
								%>
								<div class="alert alert-success">
									<%=msg.replaceAll("===SUCCESS", "")%>
								</div>
								<%
									} else if (msg.trim().length() > 0) {
								%>
								<div class="alert alert-info">
									<%=msg%>
								</div>
								<%
									}
								%>

								<form:form name="orderForm" id="orderForm"
									action="storeOrder.htm" method="POST" modelAttribute="order"
									onsubmit="return req()">

									<%
										if (order == null || order.getOrderId() == 0) {

											} else if (order.getCreationDate() != null) {
									%>
			
			<b><span style="color: green">Created Date :</span></b> <span style="color: blue;"><%=dateFormat.format(order.getCreationDate().getTime())%></span>
									<br /> 	<b><span style="color: green">Last Modified :</span></b> <span style="color: black;"><%=dateFormat.format(order.getLastModified().getTime())%>
									</span>
									<br />

									<%
										}
									%>
									<hr>
									<p></p>
									<div class="login-fields">
										<table
											style="border-collapse: separate; border-spacing: 0 0.5em;">

											<tr>
												<td><label for="orderName">Company Name<span
														style="color: red;">*</span></label> <form:hidden path="orderId"
														name="orderId" /> Company Name <span style="color: red;">
														* </span>:</td>
												<td>
													<%
														if (companyNameReadOnly) {
													%> <b><span style="font-size: 150%"><%=order.getOrderName()%></span></b>
													<form:input type="hidden" path="orderName" name="orderName"
														class="login"
														placeholder="Should be unique for every Company" /> <%
 	} else {
 %> <form:input path="orderName" name="orderName" class="login"
														placeholder="Should be unique for every Company" /> <%
 	}
 %>
												</td>
											</tr>
											<tr>
												<td><label for="spareField4">Employer ID</label>Employer ID <span style="color: red;"> *</span> :</td>
												<td><form:input path="spareField4" id="spareField4"
														name="spareField4" value="" placeholder="Employer ID"
														class="login" /></td>
											</tr>
											<tr>
												<td colspan="2"><label for="orderUrl">Job Url</label>
													Job Url <span style="color: red;"> *</span> :<form:input
														path="orderUrl" type="url" id="orderUrl" name="orderUrl"
														placeholder="Job Url" class="login" style="width: 500px" />
													<p style="color: gray; float: left; font-size: 10px;">(Ex.
														https://www.google.com/careers)</p> <br></td>
											</tr>

											<tr>
												<td><label for="spareField7">Refresh Frequency</label>Refresh
													Frequency <span style="color: red;"> *</span> :</td>
												<td><form:select path="spareField7" id="spareField7"
														name="spareField7" class='form-control'
														onchange="FiveDaysCheck(this.value)">
														<%-- onchange="FiveDaysCheck(this.name,this.value,'FiveDays')" --%>
														<form:option value="">Select</form:option>
														<form:option value="DAILY">Daily</form:option>
														<form:option value="MONTHLY">Monthly</form:option>
														<form:option value="WEEKLY">Weekly</form:option>
													</form:select>
													<div class="hint">
														more info.
															<div class="hinttext">
																<ol>
																	<li>Select at least one day in refresh Days for
																		'Weekly' and 'Monthly'.</li>
																
																<li>Select all days in Refresh Days in case of
																		'Daily'.</li>
																</ol>
															</div>
													</div></td>


											</tr>
											<tr>
												<td>Refresh Days <span style="color: red; size: 8px">*
												</span></td>

												<td><form:input type="hidden" path="spareField8"
														id="spareField8" name="spareField8" value=""
														placeholder="Refresh Days" class="login" /> <!-- <select id="refreshDays"
														name="refreshDays" class='form-control' multiple> -->
													<table
														style="border-collapse: separate; border-spacing: 2em;">
														<tr>
															<td><input style="width: 30px" class="refreshDay"
																<%if (order.getSpareField8() != null && order.getSpareField8().contains("MONDAY")) {%>
																checked <%}%> type="checkbox" id="refreshDay"
																name="refreshDay" value="MONDAY" />MON</td>
															<td><input style="width: 30px" class="refreshDay"
																type="checkbox"
																<%if (order.getSpareField8() != null && order.getSpareField8().contains("TUESDAY")) {%>
																checked <%}%> 
																id="refreshDay" name="refreshDay"
																value="TUESDAY" />TUE</td>
															<td><input style="width: 30px" class="refreshDay"
																type="checkbox"
																<%if (order.getSpareField8() != null && order.getSpareField8().contains("WEDNESDAY")) {%>
																checked <%}%> 
																id="refreshDay" name="refreshDay" value="WEDNESDAY" />WED</td>
															<td><input style="width: 30px" class="refreshDay"
																type="checkbox"
																<%if (order.getSpareField8() != null && order.getSpareField8().contains("THURSDAY")) {%>
																checked <%}%> id="refreshDay" name="refreshDay"
																value="THURSDAY" />THU</td>
															<td><input style="width: 30px" class="refreshDay"
																type="checkbox"
																<%if (order.getSpareField8() != null && order.getSpareField8().contains("FRIDAY")) {%>
																checked <%}%> id="refreshDay" name="refreshDay"
																value="FRIDAY" />FRI</td>
														</tr>
													</table> <!-- </select>  --></td>
											</tr>
											<tr>
												<td>Start Date :</td>
												<td><form:input type="date" path="startDate"
														id="startDate" name="startDate" value=""
														placeholder="Start Date" class="login" />
													<div class="hint" id='startDateMoreInfo'
														style="width: 65px; display: none;">
														more info.<span class="hinttext">Date Format :
															dd-mm-yyyy </span>
													</div></td>

											</tr>
											<tr>
												<td>End Date :</td>
												<td><form:input type="date" path="endDate" id="endDate"
														name="endDate" value="" placeholder="End Date"
														class="login" />
													<div class="hint" style="width: 65px; display: none"
														id='endDateMoreInfo'>
														more info. <span class="hinttext">Date Format :
															dd-mm-yyyy</span>
													</div></td>

											</tr>
											<tr>
												<td>Industry :</td>
												<td><form:select path="companyType" id="companyType"
														name="companyType" placeholder="Industry"
														class='form-control'>
														<form:option value="Accounting And Finance">Accounting And Finance</form:option>
														<form:option value="Administration And Office Support">Administration And Office Support</form:option>
														<form:option value="Agriculture And Environment">Agriculture And Environment</form:option>
														<form:option value="Automotive">Automotive</form:option>
														<form:option value="Computers And Technology">Computers And Technology</form:option>
														<form:option value="Construction">Construction</form:option>
														<form:option value="Customer Service">Customer Service</form:option>
														<form:option value="Education">Education</form:option>
														<form:option value="Food And Restaurant">Food And Restaurant</form:option>
														<form:option value="Government And Military">Government And Military</form:option>
														<form:option value="Healthcare">Healthcare</form:option>
														<form:option value="Hotel And Hospitality">Hotel And Hospitality</form:option>
														<form:option value="Installation And Repair">Installation And Repair</form:option>
														<form:option value="Law Enforcement And Security">Law Enforcement And Security</form:option>
														<form:option value="Legal">Legal</form:option>
														<form:option value="Maintenance And Janitorial">Maintenance And Janitorial</form:option>
														<form:option value="Management">Management</form:option>
														<form:option value="Media And Entertainment">Media And Entertainment</form:option>
														<form:option value="Personal Care And Services">Personal Care And Services</form:option>
														<form:option value="Retail">Retail</form:option>
														<form:option value="Sales And Marketing">Sales And Marketing</form:option>
														<form:option value="Salon/Spa/Fitness">Salon/Spa/Fitness</form:option>
														<form:option value="Social Services">Social Services</form:option>
														<form:option value="Transportation">Transportation</form:option>
														<form:option value="Warehouse And Production">Warehouse And Production</form:option>
														<form:option value="Wellness">Wellness</form:option>
														<form:option value="Work at Home">Work at Home</form:option>

													</form:select>
											</tr>
											<tr>
												<td>Account holder</td>

												<td><form:select id="userId" path="userId" class='form-control'>
														<form:option value="">Select</form:option>
														<form:option value="Team">Team</form:option>
													</form:select></td>

											</tr>
											<tr>
												<td>Order Status:</td>
												<td>
													<%
														if (order == null || order.getOrderId() == 0) {
													%> <form:select id="orderStatus" path="orderStatus" class='form-control'>
														<form:option value="TEST">TEST</form:option>
														<form:option value="LIVE">LIVE</form:option>
													</form:select> <%
 												} else {
 %> <form:select id="orderStatus" path="orderStatus" class='form-control'>
														<form:option value="TEST">TEST</form:option>
														<form:option value="LIVE">LIVE</form:option>
														<form:option value="ON_HOLD">ON_HOLD</form:option>
														<form:option value="CANCELLED">CANCELLED</form:option>

													</form:select> <%
 	}
 %>
													<div class="hint">
														<b>ASSIGNED is now LIVE.</b>
													</div>
												</td>
											</tr>
											<tr>
												<td>Auto Import<span style="color: red;"> *</span> :
												</td>
												<td><form:select path="spareField5" id="spareField5"
														name="spareField5" value="" placeholder="Auto Import"
														class="login">
														<form:option value="Y">Y</form:option>
														<form:option value="N">N</form:option>
													</form:select>
													<div class="hint">
														more info.
															<div class="hinttext">
																
																		<div align="center">Help for Snagajob Auto
																			Import</div>

																<ol>
																	<li>Select 'Yes' if you want this wrap to be
																		included in the zip file for Auto-import process.</li>
																
																
																	<li>If 'No' is selected, the individual XML
																		file will be uploaded on the FTP server.</li>
																</ol>
															</div>
													</div></td>

											</tr>
											<tr>
												<td>Combine wrap files<span style="color: red;">
														*</span> :
												</td>
												<td><form:select path="spareField3" id="spareField3"
														name="spareField3" value=""
														placeholder="Combine wrap files" class="login">
														<form:option value="Y">Y</form:option>
														<form:option value="N">N</form:option>

													</form:select>

													<div class="hint">
														more info 
															<div class="hinttext">
																
																	
																		<div align="center">Help for Snagajob Combine
																			wrap</div>		
																<ol>
																	<li>Select 'Yes' if you want
																		this wrap to be merged with other orders with same
																		employer id.</li>
																
																<li>
																	If 'No' is selected, the individual XML
																		file will be delivered.</li>
																</ol>
															</div>
													</div></td>

											</tr>
											<tr>
												<td colspan="2">Remarks : <br> <form:textarea
														path="remarks" id="remarks" name="remarks" value=""
														placeholder="Character limit of 5000" class="login"
														style="width: 500px" maxlength="5000" />
													<p id="count"
														style="color: red; float: left; font-size: 10px;"></p>
												</td>
											</tr>
											<tr>
												<td colspan="2">Additional Notes : <br> <form:textarea
														path="spareField6" id="spareField6" name="spareField6"
														value="" placeholder="Character limit of 5000"
														class="login" style="width: 500px" maxlength="5000" />
													<p id="count"
														style="color: red; float: left; font-size: 10px;"></p>
											</tr>
											<tr>
												<td><input type="submit" id="submitButton"
													class="btn btn-primary btn-xsm" style="width: 150px"
													style="float: left" value="Submit" /></td>
												<%
													if (order == null || order.getOrderId() == 0) {
												%>
												<td></td>
												<%
													} else {
												%><td><input type="button"
													class="btn btn-primary btn-xsm"
													style="width: 250px; align: right" style="float: right"
													value="View Modification History"
													onClick="viewModificationHistory('<%=order.getOrderId()%>')" /></td>
												<%
													}
												%>
											</tr>
										</table>
									</div>
								</form:form>

							</div>
							<!-- /field -->

						</div>

					</div>
				</div>
				<!-- /span6 -->
			</div>
			<!-- /row -->
		</div>
		<!-- /container -->
	</div>
	<!-- /main-inner -->



	<%
		if (order == null || order.getOrderId() == 0) {
	%>

	<script>
		document.getElementById('neworderheader').className = "active";
		document.getElementById('orderFormHeader').innerHTML = "Add New Order";
	</script>
	<%
		} else {
	%>
	<script>
		document.getElementById('viewordersheader').className = "active";
		document.getElementById('orderFormHeader').innerHTML = "Update Order";
	</script>
	<%
		}
	%>

	<%@include file="../footer.jsp"%>

	<script src="js/jquery-1.7.2.min.js"></script>
	<script src="js/bootstrap.js"></script>

	<script src="js/signin.js"></script>
</body>
</html>