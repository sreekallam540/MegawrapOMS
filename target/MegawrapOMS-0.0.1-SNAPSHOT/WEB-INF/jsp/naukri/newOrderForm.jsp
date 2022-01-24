
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
									<%=msg%>
								</div>
								<%
									} else if (msg.trim().length() > 0 && msg.endsWith("===SUCCESS")) {
								%>
								<div class="alert alert-success">
									<%=msg%>
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
									action="storeOrder.htm" method="POST" modelAttribute="order">



									<%
										if (order == null || order.getOrderId() == 0) {
											} else {
									%>
			
			Created Date : <span style="color: blue;"><%=dateFormat.format(order.getCreationDate()
							.getTime())%></span>
									<br /> 	Last Modified : <span style="color: black;"><%=dateFormat.format(order.getLastModified()
							.getTime())%> </span>
									<br />

									<%
										if (order.getLiveDate() != null) {
									%>
			Order Live Date : <span style="color: green"><%=dateFormat.format(order.getLiveDate()
								.getTime())%> </span>
									<br />
									<%
										}
									%>
									<%
										if (order.getExpiredDate() != null) {
									%>
			Order Expired Date : <span style="color: red"><%=dateFormat.format(order.getExpiredDate()
								.getTime())%> </span>
									<%
										}
									%>
									<%
										}
									%>


									<div class="login-fields">
										<div class="field">
											<label for="orderName">Company Name <span
												style="color: red;"></span></label>

											<form:hidden path="orderId" name="orderId" />
											Company Name</span>

											<%
												if (companyNameReadOnly) {
											%>

											<%=order.getOrderName()%>
											<form:hidden path="orderName" name="orderName" class="login"
												placeholder="Company Name" style="width: 500px" />
											<%
												} else {
											%>
											<span style="color: red;"> *</span>
											<form:input path="orderName" name="orderName" class="login"
												placeholder="Company Name" style="width: 500px" />
											<%
												}
											%>

										</div>
										<!-- /field -->

										<input type="hidden" id="orderstatusnew"
											value=<%=order.getOrderStatus()%>>

										<div class="field">
											Company ID<span style="color: red;"> *</span>
											<form:input path="spareField3" id="companyId"               
												name="companyId" value="" placeholder="Company ID"
												class="login" style="width: 500px" />
										</div>

										<div class="field">
											<label for="orderUrl">Company Description</label> Company
											Description
											<form:textarea path="companyDescription"
												id="companyDescription" name="companyDescription"
												placeholder="Company Description" class="login"
												style="width: 500px" />

										</div>



										<div class="field">
											<label for="orderUrl">Source Url <span
												style="color: red;"> *</span></label> URL <span style="color: red;">
												*</span>
											<form:input path="orderUrl" type="url" id="orderUrl"
												name="orderUrl" placeholder="Source URL" class="login"
												style="width: 500px" />


											<p style="color: red; float: left; font-size: 10px;">(Ex.
												https://www.google.com/)</p>
											<br>

										</div>


										<!-- /field -->

										<div class="field">
											<label for="orderUrl">Url used for wrapping</label> URL for
											wrapping
											<form:input path="crawlUrl" type="url" id="crawlUrl"
												name="crawlUrl" placeholder="Url used for wrapping"
												class="login" style="width: 500px" />
											<p style="color: red; float: left; font-size: 10px;">(Ex.
												https://www.google.com/)</p>
											<br>

										</div>



										<!-- /field -->


										<div class="field">
											Status </br>
											<%
												if (order == null || order.getOrderId() == 0) {
											%>
											<form:select id="orderStatus" path="orderStatus"
												class='form-control' disabled="true">
												<form:option value="REQUESTED">REQUESTED</form:option>
												<form:option value="LIVE">LIVE</form:option>
												<form:option value="ON_HOLD">ON_HOLD</form:option>
												<form:option value="CANCELED">CANCELED</form:option>

											</form:select>
											<%
												} else {
											%>
											<form:select id="orderStatus" path="orderStatus"
												class='form-control'>
												<form:option value="REQUESTED">REQUESTED</form:option>
												<form:option value="LIVE">LIVE</form:option>
												<form:option value="ON_HOLD">ON_HOLD</form:option>
												<form:option value="CANCELED">CANCELED</form:option>

											</form:select>
											<%
												}
											%>
										</div>
										<!-- /login-fields -->

										<div class="field">
											Industry</br>
											<form:select id="industryCode" path="spareField1"
												class='form-control'
												style="width: width: 335px !importsant;">
												<form:option value="2">Travel / Hotels / Restaurants / Airlines / Railways</form:option>
												<form:option value="3">Textiles / Garments / Accessories</form:option>
												<form:option value="4">Automobile / Auto Anciliary / Auto Components</form:option>
												<form:option value="6">Chemicals / PetroChemical / Plastic / Rubber</form:option>
												<form:option value="7">BPO / Call Centre / ITES</form:option>
												<form:option value="8">Accounting / Finance</form:option>
												<form:option value="9">FMCG / Foods / Beverage</form:option>
												<form:option value="10">Consumer Electronics / Appliances / Durables</form:option>
												<form:option value="12">Construction / Engineering / Cement / Metals</form:option>
												<form:option value="13">Export / Import</form:option>
												<form:option value="14">Banking / Financial Services / Broking</form:option>
												<form:option value="15">IT-Hardware & Networking</form:option>
												<form:option value="16">Industrial Products / Heavy Machinery</form:option>
												<form:option value="17">Insurance</form:option>
												<form:option value="18">Courier / Transportation / Freight / Warehousing</form:option>
												<form:option value="19">Media / Entertainment / Internet</form:option>
												<form:option value="20">Medical / Healthcare / Hospitals</form:option>
												<form:option value="21">Office Equipment / Automation</form:option>
												<form:option value="22">Pharma / Biotech / Clinical Research</form:option>
												<form:option value="23">Oil and Gas / Energy / Power / Infrastructure</form:option>
												<form:option value="24">Retail / Wholesale</form:option>
												<form:option value="25">IT-Software / Software Services</form:option>
												<form:option value="26">Education / Teaching / Training</form:option>
												<form:option value="27">Telecom/ISP</form:option>
												<form:option value="28">Semiconductors / Electronics</form:option>
												<form:option value="29">Other</form:option>
												<form:option value="30">Architecture / Interior Design</form:option>
												<form:option value="31">Fresher / Trainee / Entry Level</form:option>
												<form:option value="32">Advertising / PR / MR / Event Management</form:option>
												<form:option value="33">Agriculture / Dairy</form:option>
												<form:option value="34">Recruitment / Staffing</form:option>
												<form:option value="35">Gems / Jewellery</form:option>
												<form:option value="36">Legal</form:option>
												<form:option value="37">NGO / Social Services / Regulators / Industry Associations</form:option>
												<form:option value="38">Printing / Packaging</form:option>
												<form:option value="39">Real Estate / Property</form:option>
												<form:option value="40">Security / Law Enforcement</form:option>
												<form:option value="41">Fertilizers / Pesticides</form:option>
												<form:option value="42">Government / Defence</form:option>
												<form:option value="43">Pulp and Paper</form:option>
												<form:option value="44">Shipping / Marine</form:option>
												<form:option value="45">Tyres</form:option>
												<form:option value="46">Aviation / Aerospace Firms</form:option>
												<form:option value="47">Facility Management</form:option>
												<form:option value="48">KPO / Research / Analytics</form:option>
												<form:option value="49">Glass / Glassware</form:option>
												<form:option value="50">Brewery / Distillery</form:option>
												<form:option value="51">Water Treatment / Waste Management</form:option>
												<form:option value="52">Strategy / Management Consulting Firms</form:option>
												<form:option value="53">Iron and Steel</form:option>
												<form:option value="54">Mining / Quarrying</form:option>
												<form:option value="55">Electricals / Switchgears</form:option>
												<form:option value="56">Animation / Gaming</form:option>
												<form:option value="57">Food Processing</form:option>
												<form:option value="58">Publishing</form:option>
												<form:option value="59">Wellness / Fitness / Sports</form:option>
												<form:option value="60">Ceramics / Sanitary ware</form:option>
												<form:option value="61">Heat Ventilation / Air Conditioning</form:option>
												<form:option value="63">Internet / Ecommerce</form:option>
												<form:option value="64">Sugar</form:option>
												<form:option value="65">Broadcasting</form:option>
												<form:option value="66">Leather</form:option>
											</form:select>
										</div>


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
	</div>


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
