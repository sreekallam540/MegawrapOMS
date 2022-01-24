
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

function viewModificationHistory(oid)
{
	window.open("viewOrderModificationHistory.htm?oid="+oid);
}
$(document).ready(function() {
	
	
 	
	/* if ($('#organization').val() != '' && $('#organization').val() != 'undefined') {
		
		$('select').val("yes");
	$("#organizationfield").show();
					
	
} else	{
	$("#feedSplitter select").remove();
	
	$("#organizationfield").hide();
	$('#feedSplitter select option[value="no"]').attr('selected','true');
	$('#feedSplitter select option[value="yes"]').removeAttr('selected');
	//console.log($("#feedSplitter select").val());

}  */
			
	

		 	$('#feedSplitter').change(function() {
				if ($(this).val() == "yes") {
					$('#organizationfield').css({'display':'block'});
					$("#organizationfield").show();
				} else {
					$("#organizationfield").hide();
				}
				
			});
		 	
		 	$('#submitButton').click(function(){
		 		if($('#feedSplitter').val()=='no')
		 		{
		 				$('#organization').val(' ');
		 		} 
		 		$('#orderStatus').prop('disabled', false);
		 	});
		 	
		 	
		 	
		});
</script>

</head>

<body>

	<%@include file="../header.jsp"%>

	<% 
User sessionuser = (User) session.getAttribute(WebConstants.SESSION_USER_KEY);
Order order = (Order) request.getAttribute("order"); 

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

								<form:form name="orderForm" id="orderForm" action="storeOrder.htm" method="POST" modelAttribute="order">
								
									

									<% if(order == null || order.getOrderId() == 0) {
									 } else {  %>
			
			Created Date : <span style="color: blue;"><%= dateFormat.format(order.getCreationDate().getTime()) %></span>
									<br /> 	Last Modified : <span style="color: black;"><%= dateFormat.format(order.getLastModified().getTime()) %>
									</span>
									<br />

									<% if(order.getLiveDate() != null) {  %>
			Order Live Date : <span style="color: green"><%= dateFormat.format(order.getLiveDate().getTime()) %>
									</span>
									<br />
									<% } %>
									<% if(order.getExpiredDate() != null) {  %>
			Order Expired Date : <span style="color: red"><%= dateFormat.format(order.getExpiredDate().getTime()) %>
									</span>
									<% } %>
									<% } %>


									<div class="login-fields">
										<div class="field">
											<label for="orderName">Company Name <span
												style="color: red;"></span></label>

											<form:hidden path="orderId" name="orderId" />
											Company Name</span>

											<% if(companyNameReadOnly) {  %>

											<%= order.getOrderName() %>
											<form:hidden path="orderName" name="orderName" class="login"
												placeholder="Company Name" style="width: 500px" />
											<% } else { %>
											<span style="color: red;"> *</span>
											<form:input path="orderName" name="orderName" class="login"
												placeholder="Company Name" style="width: 500px" />
											<% }  %>

										</div>
										<!-- /field -->
										
									<input type="hidden" id="orderstatusnew" value=<%=order.getOrderStatus()%> >	
										
									<div class="field">
											 Company ID<span style="color: red;"> *</span>
											<form:input path="spareField3" id="companyId" name="companyId"
												value="" placeholder="Company ID" class="login"
												style="width: 500px" />
										</div>
												
												<div class="field">
											<label for="orderUrl">Company Description</label> Company Description
											<form:textarea path="companyDescription" id="companyDescription" name="companyDescription"
												placeholder="Company Description" class="login"
												style="width: 500px" />

										</div>
												


										<div class="field">
											<label for="orderUrl">Source Url <span
												style="color: red;"> *</span></label> URL <span style="color: red;">
												*</span>
											<form:input path="orderUrl" type="url" id="orderUrl" name="orderUrl"
												placeholder="Source URL" class="login" style="width: 500px" />

										</div>
										<!-- /field -->

										<div class="field">
											<label for="orderUrl">Url used for wrapping</label> URL for
											wrapping
											<form:input path="crawlUrl" type="url" id="crawlUrl" name="crawlUrl"
												placeholder="Url used for wrapping" class="login"
												style="width: 500px" />

										</div>


										<div class="field">
											<label for="remarks">Comments:</label> Comments
											<form:textarea path="remarks" id="remarks" name="remarks"
												value="" placeholder="Comments" class="login"
												style="width: 500px" />
										</div>
										<!-- /field -->


										<div class="field">
											Status </br>
											<% if(order == null || order.getOrderId() == 0) { %>
											<form:select id="orderStatus"  path="orderStatus" class='form-control' disabled="true" >
												<form:option value="REQUESTED">REQUESTED</form:option>
												<form:option value="LIVE">LIVE</form:option>
												<form:option value="ON_HOLD">ON_HOLD</form:option>
												<form:option value="CANCELED">CANCELED</form:option>
												
											</form:select>
									<% } else {  %>
											<form:select id="orderStatus"  path="orderStatus" class='form-control'  >
												<form:option value="REQUESTED">REQUESTED</form:option>
												<form:option value="LIVE">LIVE</form:option>
												<form:option value="ON_HOLD">ON_HOLD</form:option>
												<form:option value="CANCELED">CANCELED</form:option>
												
											</form:select>
											<%} %>
										</div>
										<!-- /login-fields -->

										<div class="field">
											Feed Splitter </br>
											<form:select id="feedSplitter" path="spareField1" class='form-control'>
												<form:option value="no">No</form:option>
												<form:option value="yes">Yes</form:option>
											</form:select>
										</div>
										
										
										<%
										
										if( order.getSpareField2()==null  || order.getSpareField2().equalsIgnoreCase("No")   || order.getSpareField2().trim().length()==0){ %>
								
										<div class="field" id="organizationfield" style="display: none;">
											Splitter Field Name</br>
											<form:input id="organization" path="spareField2" class='form-control'/>
										</div>
									<%} else{%>
										<div class="field" id="organizationfield" >
											Splitter Field Name </br>
											<form:input id="organization" path="spareField2" class='form-control'/>
										</div>
										<%} %>

									</div>
									
									</div>

									<input type="submit" id="submitButton"   class="button btn btn-primary btn-large"
										style="float: left" value="Submit" />

									<% if(order == null || order.getOrderId() == 0) { %>
									<% } else {  %>

									<input type="button" class="button btn btn-primary btn-large"
										style="float: right" value="View Modification History"
										onClick="viewModificationHistory('<%= order.getOrderId() %>')" />
									<% } %>

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


	<% if(order == null || order.getOrderId() == 0) { %>

	<script>
document.getElementById('neworderheader').className = "active";
document.getElementById('orderFormHeader').innerHTML = "Add New Order";
</script>
	<% } else {  %>
	<script>
document.getElementById('viewordersheader').className = "active";
document.getElementById('orderFormHeader').innerHTML = "Update Order";

</script>
	<% } %>

	<%@include file="../footer.jsp"%>

	<script src="js/jquery-1.7.2.min.js"></script>
	<script src="js/bootstrap.js"></script>

	<script src="js/signin.js"></script>

</body>

</html>
