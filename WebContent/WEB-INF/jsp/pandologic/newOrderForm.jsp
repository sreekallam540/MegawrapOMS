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

	})
	function viewModificationHistory(oid) {
		window.open("viewOrderModificationHistory.htm?oid=" + oid);
	}

	$(document).ready(function() {

		$('#submitButton').click(function() {
			$('#orderStatus').prop('disabled', false);
		});

	});

	function disable(input) {
		if (input == "NO") {
			document.getElementById('remarks').disabled = true;
		} else if (input == "YES") {
			document.getElementById('remarks').disabled = false;
		}
	}

	$(function() {
		var isDisabled = '${order.getSpareField4()}';
		if (isDisabled == 'YES') {
			$("#remarks").prop("disabled", false);
		} else {
			$("#remarks").prop("disabled", true);
		}
		document.getElementById('remarks').onkeyup = function() {
			document.getElementById('count').innerHTML = "(Characters left: "
					+ (5000 - this.value.length) + ")";
		};
	});
	
	function changeFTPPath() {
		console.log('function called');
		var isClicked = document.getElementById('spareField7').value ;
		console.log(isClicked)
		if (isClicked == 'PPR')
		{
			document.getElementById('spareField5').value = 'PS_FeedTestAffiliate2/Scrape';
		}
	}

	function req() {
		var regex = /(http|https):\/\/(\w+:{0,1}\w*)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%!\-\/]))?/;
		var numericRegex = /^\d+$/;
		if (document.forms["orderForm"]["spareField1"].value == "") {
			alert("Please enter Company Name");
			return false;
		} else if (document.forms["orderForm"]["orderName"].value == "") {
			alert("Please enter External Company Id");
			return false;
		} else if (document.forms["orderForm"]["spareField2"].value == "") {
			alert("Please enter Employer ID");
			return false;
		} else if (!numericRegex.test(document.forms["orderForm"]["spareField2"].value)) {
			alert("Please enter numeric Employer ID");
			return false;
		} else if (document.forms["orderForm"]["spareField5"].value == "") {
			alert("Please enter FTP Folder Path");
			return false;
		}else if (!numericRegex.test(document.forms["orderForm"]["spareField6"].value)) {
			alert("Please enter numeric Site ID");
			return false;
		}  else if (document.forms["orderForm"]["spareField6"].value == "") {
			alert("Please enter Site Id");
			return false;
		} else if (!regex.test(document.forms["orderForm"]["orderUrl"].value)) {
			alert("Please input a valid Url in orderUrl");
			return false;
		} else if (document.forms["orderForm"]["spareField7"].value == "") {
			alert("Please select Refresh Frequency");
			return false;
		} else if (document.forms["orderForm"]["userId"].value == "") {
			alert("Please select Account Holder.");
			return false;
		} else {
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

									<b><span style="color: green">Created Date :</span></b>
									<span style="color: blue;"><%=dateFormat.format(order.getCreationDate().getTime())%></span>
									<br />
									<b><span style="color: green">Last Modified :</span></b>
									<span style="color: black;"><%=dateFormat.format(order.getLastModified().getTime())%>
									</span>
									<br />
<hr>
									<%
										}
									%>
									
									<p></p>
									<div class="login-fields">
										<table
											style="border-collapse: separate; border-spacing: 0 0.5em;">
											<tr>
												<td><label for="spareField1">Company Name<span
														style="color: red;">*</span></label> Company Name <span
													style="color: red;"> * </span>:</td>
												<td><form:input path="spareField1" id="spareField1"
														name="spareField1" value="" placeholder="CompanyName"
														class="login" /></td>
											</tr>
											<tr>
												<td><label for="orderName">External Company Id<span
														style="color: red;">*</span></label>External Company Id <span
													style="color: red;"> * </span>:</td>
												<td>
												<form:hidden path="orderId" name="orderId" />
													<%
														if (companyNameReadOnly) {
													%> <b><span style="font-size: 150%"><%=order.getOrderName()%></span>
													<form:hidden path="orderName" name="orderName" />
													</b>
														 <%
 	} else {
 %> 											<form:input path="orderName" name="orderName" class="login"
														placeholder="Should be unique for every Company" /> <%
 	}
 %>
												</td>
											</tr>
											<tr>
												<td><label for="spareField2">Employer ID</label>Employer
													ID <span style="color: red;"> *</span> :</td>
												<td><form:input path="spareField2" id="spareField2"
														name="spareField2" value="" placeholder="Employer ID"
														class="login" /></td>
											</tr>
											<tr>
												<td><label for="spareField6">Site ID</label>Site ID <span style="color: red;"> *</span> :</td>
												<td><form:input path="spareField6" id="spareField6"
														name="spareField6" value="" placeholder="Site ID numeric field"
														class="login" /></td>
											</tr>
											<tr>
												<td><label for="spareField5">FTP Folder Path</label>FTP Folder Path <span style="color: red;"> *</span> :</td>
												<td><form:input path="spareField5" id="spareField5"
														name="spareField5" value="" placeholder="FTP Folder Path"
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
														name="spareField7" class='form-control' onchange="changeFTPPath()">
														<form:option value="">Select</form:option>
														<form:option value="DAILY" >Daily</form:option>
														<form:option value="PPR" >PPR</form:option>
													</form:select></td>
											</tr>
											<tr>


												<td><form:hidden path="userId" name="userId" value="<%=sessionuser.getLoginId()%>"/> </td>

											</tr>
											<tr>
												<td>Order Status:</td>
												<td>
													<%
														if (order == null || order.getOrderId() == 0) {
													%> <form:select id="orderStatus" path="orderStatus"
														class='form-control'>
														<form:option value="LIVE">LIVE</form:option>
													</form:select> <%
 	} else {
 %> <form:select id="orderStatus" path="orderStatus"
														class='form-control'>
														<form:option value="LIVE">LIVE</form:option>
														<form:option value="ON_HOLD">ON_HOLD</form:option>
														<form:option value="CANCELLED">CANCELLED</form:option>

													</form:select> <%
 	}
 %>
												</td>
											</tr>
											<tr>
												<td>Customization Required</td>
												<td>
													<table>
														<%
															if (order == null || order.getOrderId() == 0) {
														%>
														<tr>
															<td>NO</td>
															<td><form:radiobutton id="disableButton"
																	path="spareField4" value="NO" lable="NO"
																	checked="checked" onclick="disable('NO');" /></td>
														</tr>
														<tr>
															<td>YES</td>
															<td><form:radiobutton id="disableButton"
																	path="spareField4" value="YES" lable="YES"
																	onclick="disable('YES');" /></td>
														</tr>
														<%
															} else {
														%>

														<tr>
															<td>NO</td>
															<td><form:radiobutton id="disableButton"
																	path="spareField4" value="NO" lable="NO"
																	onclick="disable('NO');" /></td>
														</tr>
														<tr>
															<td>YES</td>
															<td><form:radiobutton id="disableButton"
																	path="spareField4" value="YES" lable="YES"
																	onclick="disable('YES');" /></td>
														</tr>
														<%
															}
														%>
													</table>
												</td>
											</tr>
											<tr>
												<td colspan="2">Remarks / Comments<br> <form:textarea
														path="remarks" id="remarks" name="remarks" value=""
														placeholder="Character limit of 5000" class="login"
														style="width: 500px" disabled="true" maxlength="5000" />
													<p id="count"
														style="color: red; float: left; font-size: 10px;"></p>
												</td>
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