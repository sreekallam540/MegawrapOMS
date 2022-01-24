<%@page import="java.util.Map.Entry"%>
<%@page import="java.util.Map"%>
<%@page import="com.propellum.oms.entities.Order"%>
<%@page
	import="java.util.List, java.text.SimpleDateFormat, java.util.Calendar"%>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<title>Order Management System</title>
<!-- <meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
<meta name="apple-mobile-web-app-capable" content="yes"> -->



<link
	href="http://fonts.googleapis.com/css?family=Open+Sans:400italic,600italic,400,600"
	rel="stylesheet">
<link href="css/font-awesome.css" rel="stylesheet">

<link href="css/style.css" rel="stylesheet">


<style>
.span6 .demo-container {
    margin: 0px 0px;
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
    background: rgb(0,128,200);
}
.count-wrap { 
    display: inline-block; 
    margin: 20px 30px 20px 30px;    
    text-align: center;
}

.count-wrap > .count + span {
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

</style>

</head>
<body>
	<%@include file="header.jsp"%>

<script>
	document.getElementById('viewordersheader').className = "active";
</script>

	<% List<Order> orderList = (List)request.getAttribute("orderList"); 
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
	String client = (String) request.getAttribute("client");
	Map<String,Integer> orderStatuCountMap=	(Map<String,Integer>)request.getAttribute("orderStatusCountMap");
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
	<div class="span6" >
	
	<!-- IE < 10 does not like giving a tbody a height.  The workaround here applies the scrolling to a wrapped <div>. -->
						<!--[if lte IE 9]>
<div class="old_ie_wrapper">
<!--<![endif]-->
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
			<!-- Status Count Icons  Start-->
			<%
                if( orderStatuCountMap!=null &&  orderStatuCountMap.size()>0){%>
					<p class="text-left" >
					
						<%  for(Map.Entry<String, Integer> entry : orderStatuCountMap.entrySet()){ %>
						<span class="count-wrap"> <span
						class="count count-<%=entry.getKey()%>"><%=entry.getValue() %></span>
						<span><%=entry.getKey()%></span>
										</span>
										<%} %>
									</p>
			<% }%>
				<!-- Status Count Icons END -->		
<div class="container">					
	<table data-empty="There are no rows" class="table" data-paging="true" data-paging-count-format="{PL} of {TR}" data-paging-size="15" data-sorting="true" data-filtering="true"  >
		<thead>
			<tr style="background-color: grey;">
				<th data-sortable="true" data-sort-value="CompanyName"><% if(client.equals("chronicle")) {%>	Institution Name Alias  <% }else {	%>	Company Name <%}%></th>
				<th data-sortable="true" data-sort-value="status">Status</th>
				<th data-sortable="true" data-sort-value="LastModifiedDate" data-sorted="true" data-direction="DESC" data-type="date" data-format-string="dd MMMM YYYY">Last Modified Date</th>
				<% if(client.equals("chronicle")) {%> <th data-sortable="true" data-breakpoints="xs" data-sort-value="Customization">Customization (YES/NO)</th><% }%>
				<th data-type="html" data-sortable="false">Option</th>
			</tr>
		</thead>
		
		<tbody>
			<% if(orderList == null || orderList.size() == 0) {  %>	<tr><th colspan="4" align="center" style="align: center"> No orders found</th></tr><% } %>
				<% for(Order order : orderList) {   %>
					<tr><td><%= order.getOrderName() %></td>
						<td><%= order.getOrderStatus() %></td>
						<td	data-value="<%= order.getLastModified().getTime()%>"></td>
						<% if(client.equals("chronicle")) {%><td><%= order.getSpareField5() %></td><% }%>
						<td><u><a href="viewOrder.htm?oid=<%= order.getOrderId()%>">View</a></u></td>
					</tr><% } %>
		
		</tbody>
		<tfoot><tr><td><div id="paging-ui-container"></div></td></tr></tfoot>
	</table>
</div>	
 </div>
</div></div></div></div>

<%@include file="footer.jsp"%>
<script src="js/footable/jquery-3.4.1.js"></script>
<script src="js/footable/footable.js"></script>
<script src="js/footable/footable.min.js"></script>

<script type="text/javascript">
jQuery(function($){
	$('.table').footable({
		"paging": {
			"enabled": true
		},
		"filtering": {
			"enabled": true
		},
		"sorting": {
			"enabled": true
		},
		"columns": $.get("docs/content/columns.json"),
		"rows": $.get("docs/content/rows.json")
	});
});

FooTable.MyFiltering = FooTable.Filtering.extend({
	construct: function(instance){
		this._super(instance);
		this.statuses = ['LIVE','TEST','CANCELED','ON_HOLD'];
		this.def = 'Any Status';
		this.$status = null;
	},
	$create: function(){
		this._super();
		var self = this,
			$form_grp = $('<div/>', {'class': 'form-group'})
				.append($('<label/>', {'class': 'sr-only', text: 'Status'}))
				.prependTo(self.$form);

		self.$status = $('<select/>', { 'class': 'form-control' })
			.on('change', {self: self}, self._onStatusDropdownChanged)
			.append($('<option/>', {text: self.def}))
			.appendTo($form_grp);

		$.each(self.statuses, function(i, status){
			self.$status.append($('<option/>').text(status));
		});
	},
	_onStatusDropdownChanged: function(e){
		var self = e.data.self,
			selected = $(this).val();
		if (selected !== self.def){
			self.addFilter('status', selected, ['status']);
		} else {
			self.removeFilter('status');
		}
		self.filter();
	},
	draw: function(){
		this._super();
		var status = this.find('status');
		if (status instanceof FooTable.Filter){
			this.$status.val(status.query.val());
		} else {
			this.$status.val(this.def);
		}
	}
});

FooTable.components.register('filtering', FooTable.MyFiltering);

</script>
	<%--  <div class="main">
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

					<div class="span6">

						<!-- IE < 10 does not like giving a tbody a height.  The workaround here applies the scrolling to a wrapped <div>. -->
						<!--[if lte IE 9]>
<div class="old_ie_wrapper">
<!--<![endif]-->
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

						<div class="demo-container">
							<div class="tab-content">
								<div class="tab-pane active" id="demo">
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									<%
                if( orderStatuCountMap!=null &&  orderStatuCountMap.size()>0){%>
									<p class="text-left">
										<%  for(Map.Entry<String, Integer> entry : orderStatuCountMap.entrySet()){ %>
										<span class="count-wrap"> <span
											class="count count-<%=entry.getKey()%>"><%=entry.getValue() %></span>
											<span><%=entry.getKey()%></span>
										</span>
										<%} %>
									</p>
									<% }%>
									<p>

										Search: <input id="filter" type="text" />
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Status: <select
											class="filter-status">																					
											<option selected>ALL</option>
											<%
												
												if(client.equals("chronicle"))
												{
												%>
											<option value="TEST">TEST</option>
											<%}else{
												%>
												<option value="REQUESTED">REQUESTED</option>
												<% } %>
												<option value="LIVE">LIVE</option>
												<option value="ON_HOLD">ON_HOLD</option>
												<option value="CANCELED">CANCELED</option>
										</select> <a href="#clear" class="clear-filter" title="clear filter">[clear]</a>

										<span class="row-count"></span>

									</p>
									<table class="table demo" data-filter="#filter"
										data-page-size="14" data-limit-navigation="7">
										<thead>
											<tr>
												<th data-toggle="true">
												<%
												
												if(client.equals("chronicle"))
												{
												%>
												Institution Name Alias
												<%
												}else {
												%>
												Company Name
												<%}%>
												</th>
												
												<th>Status</th>


												<th data-hide="phone,tablet" data-name="last modified">
													Last Modified Date</th>
												<th data-hide="phone,tablet">Customization (YES/NO)</th>
												<th data-hide="phone,tablet">Option</th>
											</tr>
										</thead>


										<tbody>
											<% if(orderList == null || orderList.size() == 0) {  %>
											<tr>
												<th colspan="4" align="center" style="align: center">
													<center>No orders found</center>
												</th>
											</tr>
											<% }  %>




											<% for(Order order : orderList) {   %>


											<tr>												
												<td><%= order.getOrderName() %></td>
												<td><%= order.getOrderStatus() %></td>
												<td
													data-value="<%= order.getLastModified().getTimeInMillis()%>"><%= dateFormat.format(order.getLastModified().getTime()) %></td>
													<td><%= order.getSpareField5() %></td>
												<td><u><a
														href="viewOrder.htm?oid=<%= order.getOrderId()%>">View</a></u></td>
											</tr>
											<% } %>
										</tbody>
										<tfoot class="hide-if-no-paging">
											<tr>
												<td colspan="5">
													<div class="pagination pagination-centered"></div>
												</td>
											</tr>
										</tfoot>
									</table>
								</div>



								<!--<div class="widget widget-nopad">
            dfddf
          </div>-->
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
		<br /> <br /> <br />




		<!-- /main --------------------------------------------------------------------------------->
		<%@include file="footer.jsp"%>


		<!-- /footer -->
		<!-- Le javascript
================================================== -->
		<!-- Placed at the end of the document so the pages load faster -->

		<script src="js/jquery-1.7.2.min.js"></script>

		<script src="js/bootstrap.js"></script>
		<script src="js/base.js"></script>
		<script type="text/javascript">
    $(function () {
		   $('table').footable({ bookmarkable: { enabled: true }}).bind({
            'footable_filtering': function (e) {
                var selected = $('.filter-status').find(':selected').text();
                if(selected=='ALL')
                	selected = "";
                if (selected && selected.length > 0) {
                    e.filter += (e.filter && e.filter.length > 0) ? ' ' + selected : selected;
                    e.clear = !e.filter;
                }
            },
            'footable_filtered': function() {
                var count = $('table.demo tbody tr:not(.footable-filtered)').length;
                $('.row-count').html('&nbsp;&nbsp;&nbsp;&nbsp;<b>'+count + '</b> records found');
            }
        });
		
	

        $('.clear-filter').click(function (e) {
        	var count = $('table.demo tbody tr:not(.footable-filtered)').length;
        	 $('.row-count').html('&nbsp;&nbsp;&nbsp;&nbsp;<b>'+count + '</b> records found');
            e.preventDefault();
            $('.filter-status').val('Select your option');
            $('table.demo').trigger('footable_clear_filter');
           
        });

        $('.filter-status').change(function (e) {
        	var count = $('table.demo tbody tr:not(.footable-filtered)').length;
            $('.row-count').html('&nbsp;&nbsp;&nbsp;&nbsp;<b>'+count + '</b> records found');
            e.preventDefault();
            $('table.demo').data('footable-filter').filter( $('#filter').val() );
        });
        var count = $('table.demo tbody tr:not(.footable-filtered)').length;
        $('.row-count').html('&nbsp;&nbsp;&nbsp;&nbsp;<b>'+count + '</b> records found');
    });
	
	
	
	
	
</script>
		<script
			src="http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"
			type="text/javascript"></script>
		<script>
        if (!window.jQuery) {
            document.write('<script src="js/jquery-1.9.1.min.js"><\/script>');
        }
    </script>
		<script src="js123/footable.js?v=2-0-1" type="text/javascript"></script>
		<script src="js123/footable.sort.js?v=2-0-1" type="text/javascript"></script>
		<script src="js123/footable.filter.js?v=2-0-1" type="text/javascript"></script>
		<script src="js123/footable.paginate.js?v=2-0-1"
			type="text/javascript"></script>




		<script src="js/bootstrap-tab.js" type="text/javascript"></script>
		<script src="js/demos.js" type="text/javascript"></script>  --%>
</body>
</html>
