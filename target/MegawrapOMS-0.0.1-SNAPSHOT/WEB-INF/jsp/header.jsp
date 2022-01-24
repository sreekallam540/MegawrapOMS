
<%@page import="com.propellum.oms.entities.UserType"%>
<%@page import="com.propellum.oms.entities.User"%>
<%@page import="com.propellum.oms.web.WebConstants"%>
<%@page import="com.propellum.oms.services.OMSSettings"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>Order Management System</title>
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
<meta name="apple-mobile-web-app-capable" content="yes">
<link href="css/bootstrap.min.css" rel="stylesheet">
<link href="css/bootstrap-responsive.min.css" rel="stylesheet">
<link
	href="http://fonts.googleapis.com/css?family=Open+Sans:400italic,600italic,400,600"
	rel="stylesheet">
<link href="css/font-awesome.css" rel="stylesheet">
<link href="css/style.css" rel="stylesheet">
<link href="css/pages/dashboard.css" rel="stylesheet">

<% User sessionUser = (User) session.getAttribute(WebConstants.SESSION_USER_KEY); %>
<% String clientImage = "", clientHeight = "", clientWidth = "";


try {
String temp = OMSSettings.getInstance().getProperty("logo_" + sessionUser.getAccountName());
clientImage = temp.split(",")[2];
clientHeight = temp.split(",")[0];
clientWidth = temp.split(",")[1];
}catch(Exception e)
{}


%>
<style>
.dropdown:hover .dropdown-menu {
    display: block;
}
.dropdown-menu {
    margin-top: 0px;
}
</style>


</head>
<body>
	<div class="navbar navbar-fixed-top">
		<div class="navbar-inner">
			<div class="container">
				<a class="btn btn-navbar" data-toggle="collapse"
					data-target=".nav-collapse"><span class="icon-bar"></span><span
					class="icon-bar"></span><span class="icon-bar"></span> </a><a
					class="brand"> <img src="img/simplewings.jpg"
					width="42" height="71"> Order Management System
				</a>
				<div class="nav-collapse">

					<ul class="nav pull-right">	
						<li class="dropdown"><a href="#" class="dropdown-toggle">
						<i class="icon-user"></i> <%= sessionUser.getName() %>
								{ Account : <%= sessionUser.getAccountName() %> }
								<b	class="caret"></b></a>
							<ul class="dropdown-menu">
								<li><a href="viewProfile.htm" target="_blank">Profile</a></li>								
								<% if(sessionUser.getUserType().equals(UserType.ADMIN)) {  %>
								<li><a href="viewAllUsers.htm" target="_blank">OMS Users</a></li>
								<li><a href="createUserForm.htm" target="_blank">Create OMS User</a></li>
								<% } %>
								<li><a href="logout.htm">Logout</a></li>
							</ul></li>
						<br />
						<img src="<%= clientImage %>" width="<%= clientWidth %>"
							height="<%= clientHeight %>" style="float: right;" />
					</ul>
				</div>
				<!--/.nav-collapse -->
			</div>
			<!-- /container -->
		</div>
		<!-- /navbar-inner -->
	</div>
	<!-- /navbar -->
	<div class="subnavbar">
		<div class="subnavbar-inner">
			<div class="container">
				<ul class="mainnav">
				<%if(sessionUser.getAccountName().equals("chronicle")) {%>
					<li id="viewordersheader"><a href="listOrders.htm"><i
							class="icon-list"></i><span>View Orders</span> </a></li>
					<li id="neworderheader"><a href="addForm.htm"><i
							class="icon-file-alt"></i><span>New Order</span> </a></li>
					<li id="notificationsheader"><a href="notifications.htm"><i
							class="icon-info-sign"></i><span>Order History</span> </a></li>
				<%}else{%> 
					<li id="notificationsheader"><a href="notifications.htm"><i
							class="icon-info-sign"></i><span>Notifications</span> </a></li>
					<li id="neworderheader"><a href="addForm.htm"><i
							class="icon-file-alt"></i><span>New Order</span> </a></li>
					<li id="viewordersheader"><a href="listOrders.htm"><i
							class="icon-list"></i><span>View Orders</span> </a></li>
				<%} %>

					<!-- <li><a href="charts.html"><i class="icon-bar-chart"></i><span>Order     Charts</span> </a> </li> -->
					   
					   
					   <%if(sessionUser.getAccountName().equals("jobillico")) {%>
					<li id="excelorderheader"><a href="excelUploadForm.htm"><i
							class="icon-copy"></i><span>Concordance Mapping</span> </a></li>
							<%} else if(sessionUser.getAccountName().equals("snagajob") || sessionUser.getAccountName().equals("pandologic"))
							{
							%>
								
							<%	
							} else{%>
						 <li id="bulkorderheader"><a href="bulkOrderForm.htm"><i class="icon-copy"></i><span>Bulk Upload</span> </a> </li> 
							<%} %>
					<!--<li class="dropdown"><a href="javascript:;" class="dropdown-toggle" data-toggle="dropdown"> <i class="icon-long-arrow-down"></i><span>Drops</span> <b class="caret"></b></a>
          <ul class="dropdown-menu">
            <li><a href="#">Order Id</a></li>
            <li><a href="#">FAQ</a></li>
            <li><a href="#">Order List</a></li>
            <li><a href="#">Login</a></li>
            <li><a href="#">Signup</a></li>
            <li><a href="#">Contact Us</a></li>
          </ul>
        </li>
      </ul>
    </div>
    <!-- /container -->
			</div>
			<!-- /subnavbar-inner -->
		</div>
		<!-- /subnavbar ---------------------------------------------------------------->
</body>
</html>