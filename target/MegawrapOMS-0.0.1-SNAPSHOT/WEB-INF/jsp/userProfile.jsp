<!DOCTYPE html>
<%@page import="com.propellum.oms.web.WebConstants"%>
<%@ page
	import="com.propellum.oms.entities.*,java.text.SimpleDateFormat"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
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
<link href="css/js-form-validation.css" rel="stylesheet">



<!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
<!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

</head>

<body>

	<%@include file="header.jsp"%>
	<% try { %>
	<% User user = (User) request.getAttribute("user");
User sessionuser = (User) session.getAttribute(WebConstants.SESSION_USER_KEY);
SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
boolean companyNameReadOnly = user.getRegistrationId() > 0 && (!sessionuser.getUserType().equals(UserType.ADMIN));
%>


	<div class="main">

		<div class="main-inner">

			<div class="container">

				<div class="row">

					<div class="span12">

						<div class="widget ">

							<div class="widget-header">
								<i class="icon-user"></i>
								<h3>User Profile</h3>
							</div>
							<!-- /widget-header -->

							<% String msg = (String) request.getAttribute(WebConstants.NOTIFICATION_KEY);
						if(msg == null) msg = "";
					%>

							<% if(msg.trim().length() > 0 && msg.endsWith("===ERROR")) {  %>
							<div class="alert alert-danger">
								<%= msg %>
							</div>
							<% } else if(msg.trim().length() > 0 && msg.endsWith("===SUCCESS")) { %>
							<div class="alert alert-success">
								<%= msg %>
							</div>
							<% } else if(msg.trim().length() > 0) {  %>
							<div class="alert alert-info">
								<%= msg %>
							</div>
							<% }  %>




							<div class="widget-content">
								<div class="extratext">
									<form name="cPassForm" action="changePassword.htm"
										method="POST">
										<label for="passid">New Password:</label>
										</li> <input type="hidden" value="<%= user.getLoginId() %>"
											name="loginId"> <input type="password" name="pass"
											size="12" value="" /> <label for="passid"> Confirm
											Password:</label> <input type="password" name="cpass" size="12"
											value="" /> </br> <input type="submit" name="submit"
											value="Change Password" />
									</form>
								</div>


								<body onLoad="document.registration.userid.focus();">

									</br>
									</br>
									<form:form name="userForm" id="userForm" action="storeUser.htm"
										method="POST" commandName="user">

										<% if(user == null || user.getRegistrationId() == 0) { %>
										<% } else if(user.getCreationDateTime() != null) {  %>
			
			
			Created Date : <span style="color: blue;"><%= dateFormat.format(user.getCreationDateTime().getTime()) %></span>

										<% }  %>


										<div class="login-fields">
											<div class="field">
												<form:hidden path="registrationId" name="registrationId" />
												Login id		:

												<% if(companyNameReadOnly) {  %>
												<%= user.getLoginId() %>
												<form:hidden path="loginId" name="loginId" class="login"
													placeholder="Login Id" style="width: 500px" />
												<% } else { %>
												<form:input path="loginId" name="loginId" class="login"
													placeholder="Login Id" style="width: 500px" />
												<% }  %>

											</div>
											<!-- /field -->


											<div class="field">
												Account Name	:
												<% if(companyNameReadOnly) {  %>
												<%= user.getAccountName() %>
												<form:hidden path="accountName" id="accountName"
													name="accountName" placeholder="Account Name"
													class="login" style="width: 500px" />
												<% } else { %>
												<form:input path="accountName" id="accountName"
													name="accountName" placeholder="Account Name"
													class="login" style="width: 500px" />
												<% } %>
											</div>


											<div class="field">
												Active		:<% if(companyNameReadOnly) {  %>
												<%= user.isActive() %>
												<form:hidden name="active" id="active" path="active" />
												<% } else { %>
												<form:checkbox path="active" />
												<% } %>
											</div>
											<!-- /field -->


											<div class="field">
												User Type : 
												<% if(companyNameReadOnly) {  %>
												<%= user.getUserType() %>
												<form:hidden name="userType" id="userType" path="userType" />
												<% } else { %>
												<form:select path="userType" class='form-control'>
													<form:options items="${usertypelist}" />
												</form:select>
												<% } %>

											</div>
											<!-- /login-fields -->


											<div class="field">
												<label for="orderUrl">First Name</label>
												<form:input path="name" id="name" name="name"
													placeholder="First Name" class="login" style="width: 500px" />

											</div>

											<div class="field">
												<label for="orderUrl">Last Name</label>
												<form:input path="lastName" id="lastName" name="lastName"
													placeholder="Last Name" class="login" style="width: 500px" />

											</div>
											<!-- /field -->


										</div>

										<input type="submit" class="button btn btn-primary btn-large"
											style="float: left" value="Submit" />





									</form:form>


									<% } catch(Exception e) { out.print(e); } %>













									<br />
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


	<script src="js/sample-registration-form-validation.js"></script>

	<script src="js/bootstrap.js"></script>
	<script src="js/base.js"></script>


</body>

</html>
