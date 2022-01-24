<%@ page import="java.util.*, com.propellum.oms.entities.Notification"%>
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
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
<link
	href="http://fonts.googleapis.com/css?family=Open+Sans:400italic,600italic,400,600"
	rel="stylesheet">
<link href="css/font-awesome.css" rel="stylesheet">

<link href="css/style.css" rel="stylesheet">



<!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
<!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

<script language="Javascript">
	
		function markAsRead(id)
		{
			alert(id);
		}
		
		function showOrderdd(id)
		{
			window.location = "viewOrder.htm?oid=" + id;
			alert(id);
		}
		
		
		function showOrder(oname)
		{
			 $(this).css('background-color', '#ffffb3');
			window.location = "viewOrderByName.htm?oname=" + oname;
			
		}

		function showOrder(oname, nid)
		{
			markAsRead(nid);
			$(this).css('background-color', '#ffffb3');
			window.location = "viewOrderByName.htm?oname=" + oname;
			
		}
		
		function markAsRead(nid)
		{
		$.ajax({
			  url: "markAsRead.htm?nid="+nid,
			  context: document.body
			}).done(function() {
			  alert("Done");
			});
		}
	
		
		function markAllAsRead()
		{
			window.location = "markAllAsRead.htm";
		}
	</script>

</head>

<body>

	<%@include file="header.jsp"%>

	<div class="main">

		<div class="main-inner">

			<div class="container">

				<div class="row">

					<div class="span12">

						<div class="widget ">

							<div class="widget-header">
								<i class="icon-info"></i>
								<h3>Notifications</h3>
								<span style="float: right; padding-right: 25px;"> <a
									href="javascript:markAllAsRead()">Mark all as read</a>
								</span>


							</div>
							<!-- /widget-header -->

							<div class="widget-content">



								<style>
h1,h2,h3,h4,h5,h6 {
	margin: 0;
	font-family: inherit;
	font-weight: bold;
	color: inherit;
	text-rendering: optimizelegibility;
}

h4 {
	font-size: 14px;
}

h4,h5,h6 {
	line-height: 18px;
}

p {
	margin: 0 0 9px;
	font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
	font-size: 13px;
	line-height: 18px;
}

.close {
	float: right;
	font-size: 20px;
	font-weight: bold;
	line-height: 18px;
	color: #000000;
	text-shadow: 0 1px 0 #ffffff;
	opacity: 0.2;
	filter: alpha(opacity = 20);
}

.close:hover {
	color: #000000;
	background: #0080c8;
	text-decoration: none;
	opacity: 0.4;
	filter: alpha(opacity = 40);
	cursor: pointer;
}

.alert {
	padding: 8px 35px 8px 14px;
	margin-bottom: 18px;
	text-shadow: 0 1px 0 rgba(255, 255, 255, 0.5);
	background-color: #333333;
	border: 1px solid #f9f6f1;
	-webkit-border-radius: 4px;
	-moz-border-radius: 4px;
	border-radius: 4px;
	color: #000;
}

.alert-heading {
	color: black;
}

.alert .close {
	position: relative;
	top: -2px;
	right: -21px;
	line-height: 18px;
}

.alert-success {
	background-color: #333333;
	border-color: #333333;
	color: #000;
}

.alert-danger,.alert-error {
	background-color: #f2dede;
	border-color: #f9f6f1;
	color: #000;
}

.alert-info {
	background-color: #ffd480;
	border-color: #f9f6f1;
	color: #000;
}

.alert-block {
	background-color: #ffffb3;
	padding-top: 14px;
	padding-bottom: 14px;
}

.alert-block>p,.alert-block>ul {
	margin-bottom: 0;
}

.alert-block p+p {
	margin-top: 5px;
}

close3 {
	background: gray;
}

.alert:hover {
	cursor: pointer;
}
</style>

								<script>
document.getElementById('notificationsheader').className = "active";
</script>

								<% List<Notification> notificationList = (List<Notification>) request.getAttribute("notificationList"); %>


								<% for(Notification notification : notificationList) { %>

								<% if(notification.isAlreadyRead()) { %>
								<div class="alert alert-block"
									onclick="showOrder('<%= notification.getOrderName()%>')">
									<% } else {  %>
									<div class="alert alert-info"
										onclick="showOrder('<%= notification.getOrderName()%>','<%= notification.getNotificationId() %>')">

										<% } %>

										<span class="alert-heading"><b><%= notification.getNotificationHeader() %></b></span>
								<% if(!notification.isAlreadyRead()) { %>
								<span style="float: right"> <a href=""
											onclick="markAsRead('<%= notification.getNotificationId() %>')">Mark
												as read</a>
										</span> 
									<% } %>
										<br />
										<%= notification.getMessage() %>
										<span style="float: right"> <%= notification.getWhen() %>
										</span>












									</div>
									<% } %>


								</div>


							</div>





						</div>
						<!-- /widget-content -->

					</div>
					<!-- /widget -->

				</div>
				<!-- /span8 -->




			</div>
			<!-- /row -->

		</div>
		<!-- /container -->

	</div>
	<!-- /main-inner -->

	</div>
	<!-- /main -->



	<%@include file="footer.jsp"%>


	<script src="js/jquery-1.7.2.min.js"></script>

	<script src="js/bootstrap.js"></script>
	<script src="js/base.js"></script>


</body>

</html>
