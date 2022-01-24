
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@page import="com.propellum.oms.entities.Order,java.text.SimpleDateFormat,java.util.Calendar,com.propellum.oms.entities.OrderModification, com.propellum.oms.entities.OrderModificationEntry"%>


<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="utf-8">
<title>Order Management System</title>

<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
<meta name="apple-mobile-web-app-capable" content="yes">

<link href="css/bootstrap.min.css" rel="stylesheet">
<link href="css/bootstrap-responsive.min.css" rel="stylesheet">

<link href="http://fonts.googleapis.com/css?family=Open+Sans:400italic,600italic,400,600" rel="stylesheet">
<link href="css/font-awesome.css" rel="stylesheet">

<link href="css/style.css" rel="stylesheet">
<link href="css/js-form-validation.css" rel="stylesheet">



<!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
<!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

</head>

<body>

	<%@include file="header.jsp"%>

	<% Order order = (Order) request.getAttribute("order");

SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm");%>

	<script>
document.getElementById('viewordersheader').className = "active";
</script>


	<div class="main">
		<div class="main-inner">
			<div class="container">
				<div class="row">

					<div class="span12">

						<div class="widget ">

							<div class="widget-header">
								<h3>Notifications.</h3>
							</div>
						</div>

					</div>
					<div class="span6" style="height: auto;">

						<label for="username"><b>Order Name:</b> <%= order.getOrderName() %> </label>
						<label for="username"><b>URL:</b> <%= order.getOrderUrl() %></label>
						<label for="username"><b>Comments:</b> <%= order.getRemarks() %></label>

						<% if(order.getOrderModificationHistory() == null || order.getOrderModificationHistory().size() == 0) {  %>


						No Modification history found
						<% } else {  %>

						<% for(OrderModification orderModification : order.getOrderModificationHistory())  { %>
						<hr>
						<center>
							<h4>
								Modifications by
								<%= orderModification.getUserId() %>
								on
								<%= dateFormat.format(orderModification.getOrderModificationDate().getTime()) %></h4>
						</center>
						<label for="username"
							style="display: inline-block; padding: 5px 5px 0px;">Remark:
							<%= orderModification.getRemark() %>
						</label> <br>
						<% for(OrderModificationEntry modificationEntry : orderModification.getOrderModificationEntries()) { %>
						<label for="username"
							style="display: inline-block; padding: 5px 2px 0px;"><br />
						<b>Field:</b><%= modificationEntry.getField() %>
							&nbsp;&nbsp;&nbsp; <b>Old Value:</b><%= modificationEntry.getOldValue() %>
							&nbsp;&nbsp;&nbsp; <b>New Value:</b><%= modificationEntry.getNewValue() %>
						</label> <br />
						<% }  %>

						<% }  %>

						<% }  %>


					</div>
				</div>
			</div>

		</div>

	</div>

	<%@include file="footer.jsp"%>
	<script src="js/jquery-1.7.2.min.js"></script>
	<script src="js/sample-registration-form-validation.js"></script>
	<script src="js/bootstrap.js"></script>
	<script src="js/base.js"></script>


</body>

</html>
