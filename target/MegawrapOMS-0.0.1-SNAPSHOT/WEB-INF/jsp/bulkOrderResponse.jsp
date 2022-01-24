<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

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



<!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
<!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

</head>

<body>




	<%@include file="header.jsp"%>

	<script>
document.getElementById('bulkorderheader').className = "active";
</script>


	<div class="main">

		<div class="main-inner">

			<div class="container">

				<div class="row">

					<div class="span12">

						<div class="widget ">

							<div class="widget-header">
								<i class="icon-user"></i>
								<h3>Upload bulk orders response</h3>
							</div>
							<!-- /widget-header -->

							<div class="widget-content">

								File Uploaded.

								<% List<String> responseList = (List)request.getAttribute("responseList"); %>

								<% for(String msg : responseList)  { %>

								<% if(msg.endsWith("===ERROR")) {  %>
								<div class="alert alert-danger">

									<% } else if(msg.endsWith("===SUCCESS")) { %>
									<div class="alert alert-success">
										<% } else {  %>
										<div class="alert alert-info">

											<% }  %>
											<%= msg %>

										</div>

										<% }  %>

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
