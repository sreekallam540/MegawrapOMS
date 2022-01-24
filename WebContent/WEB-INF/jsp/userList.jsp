<%@page import="com.propellum.oms.entities.User"%>
<%@page
	import="java.util.List, java.text.SimpleDateFormat, java.util.Calendar"%>
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

<link href="css123/footable.core.css?v=2-0-1" rel="stylesheet"
	type="text/css" />
<link href="tablecss/footable-demos.css" rel="stylesheet"
	type="text/css" />








</head>
<body>
	<%@include file="header.jsp"%>

	<script>
document.getElementById('viewordersheader').className = "active";
</script>

	<% List<User> userList = (List)request.getAttribute("userList"); 
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
%>




	<div class="main">
		<div class="main-inner">
			<div class="container">
				<div class="row">

					<div class="span12">

						<div class="widget ">

							<div class="widget-header">
								<h3 id="orderFormHeader">All Users</h3>
							</div>
						</div>

					</div>

					<div class="span6">
						</br>
						<!-- IE < 10 does not like giving a tbody a height.  The workaround here applies the scrolling to a wrapped <div>. -->
						<!--[if lte IE 9]>
<div class="old_ie_wrapper">
<!--<![endif]-->


						<div class="demo-container">
							<div class="tab-content">
								<div class="tab-pane active" id="demo">
									<p>
										Search: <input id="filter" type="text" />
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Active: <select
											class="filter-status">
											<option></option>
											<option value="live">true</option>
											<option value="disabled">false</option>
										</select> <a href="#clear" class="clear-filter" title="clear filter">[clear]</a>
										<span class="row-count"></span>
									</p>
									<table class="table demo" data-filter="#filter"
										data-page-size="15">
										<thead>
											<tr>
												<th data-toggle="true">Login Id</th>
												<th>Status</th>
												<th data-hide="phone,tablet">Name</th>
												<th data-hide="phone,tablet">Account Name</th>
												<th data-hide="phone,tablet">User Type</th>
												<th data-hide="phone,tablet">Option</th>
											</tr>
										</thead>


										<tbody>
											<% if(userList == null || userList.size() == 0) {  %>
											<tr>
												<th colspan="4" align="center" style="align: center">
													<center>No Users found</center>
												</th>
											</tr>
											<% }  %>




											<% for(User user : userList) {   %>


											<tr>
												<td><%= user.getLoginId() %></td>
												<td><%= user.isActive() %></td>
												<td><%= user.getName() %></td>
												<td><%= user.getAccountName() %></td>
												<td><%= user.getUserType() %></td>
												<td><u><a
														href="viewUser.htm?rid=<%= user.getRegistrationId()%>">View</a></u></td>
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
                if (selected && selected.length > 0) {
                    e.filter += (e.filter && e.filter.length > 0) ? ' ' + selected : selected;
                    e.clear = !e.filter;
                }
            },
            'footable_filtered': function() {
                var count = $('table.demo tbody tr:not(.footable-filtered)').length;
                $('.row-count').html(count + ' rows found');
            }
        });
		
	

        $('.clear-filter').click(function (e) {
            e.preventDefault();
            $('.filter-status').val('');
            $('table.demo').trigger('footable_clear_filter');
            $('.row-count').html('');
        });

        $('.filter-status').change(function (e) {
            e.preventDefault();
            $('table.demo').data('footable-filter').filter( $('#filter').val() );
        });
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
		<script src="js/demos.js" type="text/javascript"></script>
</body>
</html>
