<%@page import="net.sf.cglib.beans.BeanMap"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="java.util.Map"%>
<%@page import="com.propellum.oms.entities.Order"%>
<%@page import="java.util.List, java.text.SimpleDateFormat, java.util.Calendar"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>Order Management System</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
<meta name="apple-mobile-web-app-capable" content="yes">

<link href="https://cdn.datatables.net/1.10.23/css/jquery.dataTables.min.css" rel="stylesheet" type="text/css">

<style>
.span6 .demo-container {
	margin: 0 10px;
}

.text-left {
	text-align: center;
}

#status_select select, option {
  width: 200px!important;
}

#example_filter input {
  	border-radius: 5px;
  	width : 180px!important;
  	float: right;
	
	align: center!important;
}

table.dataTable thead th {
    text-align: left;!important
}

div.dataTables_length select
{
width: 80px!important;
}
.dataTables_length {
    float: right;
}
.pull-left{float:left!important;}
.pull-right{float:right!important;}

option {
  overflow: hidden;
  white-sapce: no-wrap;
  text-overflow: ellipsis;
}

.count {
	font-size: 14px;
	display: inline-block;
	padding-left: 15px;
	font-weight: 600;
	background: #000;
	padding: 5px 12px;
	color: #fff;
	border-radius: 50%;
}

.count-TEST {
	background: rgb(0, 128, 200);
}

.count-wrap {
	display: inline-block;
	margin: 20px 30px 20px 30px;
	text-align: center;
}

.count-wrap>.count+span {
	display: block;
	font-size: 14px;
	font-weight: 600;
	width: auto;
}

.count-CANCELED {
	background: red;
}

.count-ON_HOLD {
	background: orange;
}

.count-REQUESTED {
	background: #cc8400;
}

.count-LIVE {
	background: #00b300;
}

.count-Customization\(TEST\) {
	background: #9370DB;
}

.count-Customization\(LIVE\) {
	background: #9932CC;
}

/* Popup container - can be anything you want */
/* .popup {
	position: relative;
	display: inline-block;
	cursor: pointer;
	-webkit-user-select: none;
	-moz-user-select: none;
	-ms-user-select: none;
	user-select: none;
} */

/* The actual popup */
.popup .popuptext {
	background-color: #555;
	color: #fff;
	display: block;
	/*width: 1000px;
	
	
	text-align: center;
	border-radius: 6px;
	padding: 8px 0;
	
	position: absolute;
	z-index: 1;
	bottom: 125%;
	left: 50%;
	margin-left: 100px;
	margin-bottom: 800px;
	*/
}

/* Popup arrow */
/* .popup .popuptext::after {
	content: "";
	position: absolute;
	top: 50%;
	left: 50%;
	margin-left: -5px;
	border-width: 5px;
	border-style: solid;
	border-color: #555 transparent transparent transparent;
} */

/* Toggle this class - hide and show the popup */
.popup .show {
	visibility: visible;
	-webkit-animation: fadeIn 1s;
	animation: fadeIn 1s;
}
.hint {
	position: relative;
	display: inline-block;
	border-bottom: 1px dotted black;
}

/* Add animation (fade in the popup) */
@
-webkit-keyframes fadeIn {
	from {opacity: 0;
}

to {
	opacity: 1;
}

}
@
keyframes fadeIn {
	from {opacity: 0;
}

to {
	opacity: 1;
}
}
</style>




</head>
<body>
	<%@include file="header.jsp"%>

	<script>
		document.getElementById('viewordersheader').className = "active";

		function changeFilter(val) {
			var x = document.getElementById("myPopup");
			x.style.display = "none";
			$('#example').trigger('footable-filter');
		}

		//When the user clicks on div, open the popup
		function popupFunction() {
			/* var popup = document.getElementById("myPopup");
			popup.classList.toggle("show");
			 */
			var x = document.getElementById("myPopup");
			if (x.style.display === "none") {
				x.style.display = "block";
			} else {
				x.style.display = "none";
			}
		}
	</script>

	<%
		List<Order> orderList = (List) request.getAttribute("orderList");
		List<Order> approachingEndDate = (List) request.getAttribute("approachingEndDate");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
		Map<String, Integer> orderStatuCountMap = (Map<String, Integer>) request.getAttribute("orderStatusCountMap");
		Map<String, String> fieldMappings = (Map<String, String>) request.getAttribute("OrderDisplayFieldMappings");
		List<String> listingFields = (List<String>) request.getAttribute("listingFields");
		
	%>
	
	<div class="main">
		<div class="main-inner">
			<div class="container">
				<div class="row">

					<div class="span12">

						<div class="widget ">

							<div class="widget-header">
								<i class="icon-list"></i>
								<h3 id="orderFormHeader">All Orders</h3>
							</div>
						</div>

					</div>

					<div class="span6" style="height: auto; padding: 0px 0px 23px;">

						<%
							String msg = (String) request.getAttribute(WebConstants.NOTIFICATION_KEY);
							if (msg == null || msg == "")
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
						<div class="demo-container">
							<div class="tab-content">
								<div class="tab-pane active" id="demo">
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									<%
										if (orderStatuCountMap != null && orderStatuCountMap.size() > 0) {
									%>
									<p class="text-left">
										<%
											for (Map.Entry<String, Integer> entry : orderStatuCountMap.entrySet()) {
													if (entry.getKey().equalsIgnoreCase("ApproachingEndDate")) {
										%>
										<span class="count-wrap" onclick="popupFunction()"> <span
											class="count count-<%=entry.getKey()%>"><%=entry.getValue()%></span>
											<span><%=entry.getKey()%></span>
										</span>
										<%
											} else {
										%>
										<span class="count-wrap">
										<span class="count count-<%=entry.getKey()%>"><%=entry.getValue()%></span>
										<span><%=entry.getKey()%></span>
										</span>
										<%
											}
												}
										%>
									</p>
									<%
										}
									%>
							<p>
								<div class="popup" id="myPopup">
										<div class="popuptext">
											<table class="display" align="center"  cellpadding="10">
												<thead>
												<tr><td  align="center" colspan="<%=listingFields.size()+2%>"><h3>Orders Approaching End Date.</h3> <div class="hint" onclick="popupFunction()">
														click here to Hide</div> </td></tr>
														<tr><td  align="center" colspan="<%=listingFields.size()+2%>"><hr></td></tr>
													<tr>
														<th align="left" >ID</th>
														<%
															for (String field : listingFields) {
																String fieldName = fieldMappings.containsKey(field) ? fieldMappings.get(field) : field;
														%>
														<th align="left" ><%=fieldName%></th>
														
														<%
															}
														%>
														<th align="left" >Action</th>
													</tr>


												</thead>
												<tbody>
													<%
														if (approachingEndDate == null || approachingEndDate.isEmpty()) {
													%>
													<tr align="center" >
														<td colspan="<%=listingFields.size()%>">No Orders approaching End Date.</td>
													</tr>
													<%
														}
														for (Order order : approachingEndDate) {
															BeanMap orderMap = BeanMap.create(order);
													%>

													<tr align="left" >
														<td><%=order.getOrderId()%></td>
														<%
															for (String field : listingFields) {
														%>
														<td><%=orderMap.get(field) != null ? orderMap.get(field) : ""%></td>

														<%
															}
														%>
														<td><a href="viewOrder.htm?oid=<%=order.getOrderId()%>">View</a></td>
													</tr>
													<%
														}
													%>
												</tbody>
											</table>
										</div>
									</div>
									
									<hr>
									

									<table class="display" id="example">
										<thead>

											<tr>
												<th>ID</th>
												<%
													for (String field : listingFields) {
														String fieldName = fieldMappings.containsKey(field) ? fieldMappings.get(field) : field;
												%>
												<th><%=fieldName%></th>
												<%
													}
												%>
												<th>Action</th>
											</tr>
										</thead>
										<tbody>
											<%
												for (Order order : orderList) {
													BeanMap orderMap = BeanMap.create(order);
											%>

											<tr>
												<td><%=order.getOrderId()%></td>
												<%
													for (String field : listingFields) {
												%>
												<td><%=orderMap.get(field) != null ? orderMap.get(field) : ""%>
												</td>

												<%
													}
												%>
												<td><a
													href="viewOrder.htm?oid=<%=order.getOrderId()%>">View</a></td>
											</tr>
											<%
												}
											%>
										</tbody>
									</table>
								</div>

								<!-- /widget -->
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
	</div>
	<br />
	<br />
	<br />


	<%@include file="footer.jsp"%>
	<script src="https://code.jquery.com/jquery-3.5.1.js"></script>

	<script src="https://cdn.datatables.net/1.10.23/js/jquery.dataTables.min.js"></script>
	
	<script type="text/javascript">
	$(document).ready(function() {
	    $('#example').DataTable( {
	    	 "dom": '<"pull-left"f><"pull-right"l>tip',
	    	 "lengthMenu": [[10, 50, 100, -1], [10, 50, 100, "All"]],
	    	 "aaSorting": [[ 1, "asc" ]],
	        initComplete: function () {
	        	$('#example_filter').append('<div id="status_select"><table><tr><td><label align="left"><b>Order Status:</label><b></td>');
	            this.api().columns(7).every( function () {
	                var column = this;
	                var select = $('<select><option value="">ALL</option></select>')
	                    .appendTo( $('#status_select') )
	                    .on( 'change', function () {
	                        var val = $.fn.dataTable.util.escapeRegex(
	                            $(this).val()
	                        );
	 
	                        column
	                            .search( val ? '^'+val+'$' : '', true, false )
	                            .draw();
	                    } );
	 
	                column.data().unique().sort().each( function ( d, j ) {
	                    select.append( '<option value="'+d+'">'+d+'</option></div>' )
	                } );
	            } );
	        }
	    } );
	} );
	</script>
	
	
</body>
</html>
