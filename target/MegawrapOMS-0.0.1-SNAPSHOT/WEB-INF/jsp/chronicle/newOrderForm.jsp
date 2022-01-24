
<%@page import="com.propellum.oms.web.WebConstants"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@page
	import="com.propellum.oms.entities.*,java.text.SimpleDateFormat,java.util.Calendar"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="utf-8">
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
	function viewModificationHistory(oid) {
		window.open("viewOrderModificationHistory.htm?oid=" + oid);
	}

	
	
	$(document).ready(function() {

		$('#submitButton').click(function() {
			$('#orderStatus').prop('disabled', false);
		});

	});
	
	function req()
	{
		var regex = /(http|https):\/\/(\w+:{0,1}\w*)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%!\-\/]))?/;
		if (document.forms["orderForm"]["spareField4"].value== "") {
			alert("Please enter Institution Name");
			return false;
		}
		else if (document.forms["orderForm"]["orderName"].value== "") {
			alert("Please enter Institution Name Alias");
			return false;
		}
		else if (document.forms["orderForm"]["spareField1"].value== "") {
			alert("Please enter Organization Id");
			return false;
		}
		else if (document.forms["orderForm"]["spareField2"].value== "") {
			alert("Please enter Vitae Id");
			return false;
		}
		else if (document.forms["orderForm"]["spareField3"].value== "") {
			alert("Please input a Value");
			return false;
		}
		else if(!regex .test(document.forms["orderForm"]["orderUrl"].value)) {
			alert("Please input a valid Url in orderUrl");
			return false;
		}
		else if (document.forms["orderForm"]["spareField6"].value== "") {
			alert("Please enter Account Name");
			return false;
		}
		else if (document.forms["orderForm"]["spareField7"].value== "") {
			alert("Please enter Account Number");
			return false;
		}else{
			return true;
		}
	}
	
	function disable(input)
	{
		if (input == "NO") {
			document.getElementById('remarks').disabled = true;
		} else if(input == "YES")  {
			document.getElementById('remarks').disabled = false;
		}
	}
	

	$(function () {
		var isDisabled = '${order.getSpareField5()}';
		if (isDisabled == 'YES') {
			$("#remarks").prop("disabled", false);
		} else {
			$("#remarks").prop("disabled", true);
		}
		document.getElementById('remarks').onkeyup = function () {
			  document.getElementById('count').innerHTML = "(Characters left: " + (5000 - this.value.length) + ")";
			};
	});
</script>
</head>

<body>

	<%@include file="../header.jsp"%>

	<%
		User sessionuser = (User) session
				.getAttribute(WebConstants.SESSION_USER_KEY);
		Order order = (Order) request.getAttribute("order");

		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"dd-MMM-yyyy HH:mm");
		boolean companyNameReadOnly = order.getOrderId() > 0
				&& (!sessionuser.getUserType().equals(UserType.ADMIN));
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
									String msg = (String) request
											.getAttribute(WebConstants.NOTIFICATION_KEY);
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

								<form:form name="orderForm" id="orderForm" action="storeOrder.htm" method="POST" modelAttribute="order" onsubmit="return req()">



									<%
										if (order == null || order.getOrderId() == 0) {
											
											} else {
									%>
			
			Created Date : <span style="color: blue;"><%=dateFormat.format(order.getCreationDate().getTime())%></span>
									<br /> 	Last Modified : <span style="color: black;"><%=dateFormat.format(order.getLastModified().getTime())%> </span>
									<br />

									<%
										if (order.getLiveDate() != null) {
									%>
			Order Live Date : <span style="color: green"><%=dateFormat.format(order.getLiveDate().getTime())%> </span>
									<br />
									<%
										}
									%>
									<%
										if (order.getExpiredDate() != null) {
									%>
			Order Expired Date : <span style="color: red"><%=dateFormat.format(order.getExpiredDate().getTime())%> </span>
									<%
										}
									%>
									<%
										}
									%>

										<div class="field">
											Institution Name<span style="color: red;"> *</span>
											<form:input path="spareField4" id="spareField4"               
												name="spareField4" value="" placeholder="Institution name"
												class="login" style="width: 500px" />
										</div>
										
										
									<div class="login-fields">
										<div class="field">
											<label for="orderName">Institution Name Alias<span
												style="color: red;"></span></label>

											<form:hidden path="orderId" name="orderId" />
											Institution Name Alias : 

											<%
												if (companyNameReadOnly) {
											%>

											<%=order.getOrderName()%>
											<form:hidden path="orderName" name="orderName" class="login"
												placeholder="Should be unique for every Institution" style="width: 500px" />
											<%
												} else {
											%>
											<span style="color: red;"> *</span>
											<form:input path="orderName" name="orderName" class="login"
												placeholder="Should be unique for Institution" style="width: 500px"/>
											<%
												}
											%>

										</div>
										<!-- /field -->
										
										<input type="hidden" id="orderstatusnew"
											value=<%=order.getOrderStatus()%>>
								
										
										<div class="field">
											Organization Id<span style="color: red;"> *</span>
											<form:input path="spareField1" id="spareField1"               
												name="spareField1" value="" placeholder="Organization Id"
												class="login" style="width: 500px" />
										</div>
										
										<div class="field">
											Vitae Id<span style="color: red;"> *</span>
											<form:input path="spareField2" id="spareField2"               
												name="spareField2" value="" placeholder="Vitae Id"
												class="login" style="width: 500px" />
										</div>
									
										<div class="field">
										<label for="spareField3">	
											XML grouping by organisation ID</label>XML grouping by organisation ID<span style="color: red;"> *</span>
											<table>
											<%
												if (order == null || order.getOrderId() == 0) {
											%>
												<tr><td>NO</td><td><form:radiobutton path = "spareField3" value = "NO" lable="NO" checked="checked"/></td></tr>
                								<tr><td>YES</td><td><form:radiobutton path = "spareField3" value = "YES" lable="YES" /></td></tr>
                							<%
												} else {
											%>
												<tr><td>NO</td><td><form:radiobutton path = "spareField3" value = "NO" lable="NO" /></td></tr>
                								<tr><td>YES</td><td><form:radiobutton path = "spareField3" value = "YES" lable="YES" /></td></tr>
											<% } %>
											</table>
										</div>
										<div class="field">
											Account Name<span style="color: red;"> *</span>
											<form:input path="spareField6" id="spareField6"               
												name="spareField6" value="" placeholder="Account Name"
												class="login" style="width: 500px" />
										</div>
										<div class="field">
											Account Number<span style="color: red;"> *</span>
											<form:input path="spareField7" id="spareField7"               
												name="spareField7" value="" placeholder="Account Number"
												class="login" style="width: 500px" />
										</div>
										<div class="field">
											<label for="orderUrl">Organization URL <span
												style="color: red;"> *</span></label>Organization URL <span style="color: red;">
												*</span>
											<form:input path="orderUrl" type="url" id="orderUrl"
												name="orderUrl" placeholder="Organization URL" class="login"
												style="width: 500px" />


											<p style="color: red; float: left; font-size: 10px;">(Ex.
												https://www.google.com/)</p>
											<br>

										</div>


										<!-- /field -->

										<div class="field">
											<label for="crawlUrl">Crawl Url</label> Crawl Url
											<form:input path="crawlUrl" type="url" id="crawlUrl"
												name="crawlUrl" placeholder="Crawl Url"
												class="login" style="width: 500px" />
											<p style="color: red; float: left; font-size: 10px;">(Ex.
												https://www.google.com/careers)</p>
											<br>

										</div>

										<div class="field">
										<label for="spareField5">	
											Customization Required</label>Customization Required
											<table>
											<%
												if (order == null || order.getOrderId() == 0) {
											%>
												<tr><td>NO</td><td><form:radiobutton id = "disableButton" path = "spareField5" value = "NO" lable="NO" checked="checked" onclick = "disable('NO');"/></td></tr>
                								<tr><td>YES</td><td><form:radiobutton id = "disableButton" path = "spareField5" value = "YES" lable="YES" onclick = "disable('YES');" /></td></tr>
                							<%
												} else {
											%>
											
												<tr><td>NO</td><td><form:radiobutton id = "disableButton" path = "spareField5" value = "NO" lable="NO" onclick = "disable('NO');"/></td></tr>
                								<tr><td>YES</td><td><form:radiobutton id = "disableButton" path = "spareField5" value = "YES" lable="YES" onclick = "disable('YES');"/></td></tr>
											<% } %>
											</table>
										</div>

										<!-- /field -->
										<div class="field">
											<label for="remarks">Remarks / Comments</label> Remarks / Comments:
											<form:textarea path="remarks" id="remarks" name="remarks"
												value="" placeholder="Character limit of 5000" class="login"
												style="width: 500px" disabled="true" maxlength="5000"/>
												<p id="count" style="color: red; float: left; font-size: 10px;"></p>
												
										</div>
										<br>
										<div class="field">
										<label for="remarks">Order Status </label>Order Status:
											
											<%
												if (order == null || order.getOrderId() == 0) {
											%>
											<form:select id="orderStatus" path="orderStatus"
												class='form-control'>
												<form:option value="TEST">TEST</form:option>
												<form:option value="LIVE">LIVE</form:option>
											

											</form:select>
											<%
												} else {
											%>
											<form:select id="orderStatus" path="orderStatus"
												class='form-control'>
												<form:option value="TEST">TEST</form:option>
												<form:option value="LIVE">LIVE</form:option>
												<form:option value="ON_HOLD">ON_HOLD</form:option>
												<form:option value="CANCELLED">CANCELLED</form:option>

											</form:select>
											<%
												}
											%>
										</div>
										
										<!-- /login-fields -->
										

									</div>
									
							</div>

							<input type="submit" id="submitButton"
								class="button btn btn-primary btn-large" style="float: left"
								value="Submit" />

							<%
								if (order == null || order.getOrderId() == 0) {
							%>
							<%
								} else {
							%>

							<input type="button" class="button btn btn-primary btn-large"
								style="float: right" value="View Modification History"
								onClick="viewModificationHistory('<%=order.getOrderId()%>')" />
							<%
								}
							%>

							</form:form>


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