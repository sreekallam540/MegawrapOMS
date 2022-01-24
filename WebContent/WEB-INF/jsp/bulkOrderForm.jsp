<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
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
								<i class="icon-copy"></i>
								<h3>Upload bulk orders</h3>
							</div>
							<!-- /widget-header -->

							<div class="widget-content">






								<br />








								<!----------------->




								<br /> <br /> File
								Upload:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								<form:form name="bulkProcessingForm"
									modelAttribute="bulkOrderRequest" method="post"
									action="bulkOrderProcessing.htm" enctype="multipart/form-data">
									<form:input path="multiPartFile" type="file"
										name="multiPartFile" id="multiPartFile" size="52" />
									<br>

									<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 <button type="submit">Submit</button>

								</form:form>



								<hr>

							

								<b>Instructions:</b> <label style="padding-left: 10px;">
									<br />
									<li>File should be <b>simple text file</b>, all the fields
										should be seperated with <b>TAB(\t)</b> character
								</li>
								<%
								String client = (String) request.getAttribute("client");
								if(client.equals("chronicle"))
									{
									%>
									<li>Fields Sequence should be:<br> 1) Institution Name Alias, 2) Institution Name, 3) Organization Id, 
									4) Vitae Id, 5) XML grouping by organisation ID, 6) Organization URL, 7) Crawl Url, 8) Remarks / Comments, 9) Status</li>
									<li>Status values should be <b>REQUESTED</b></li>
									<li><b>Institution Name Alias</b> would be as per Chronicle</li>
									<li><b>Institution Name</b> should be unique</li>
									<%
									}else {
									%>
									<li>Fields Sequence should be 1) Company Name, 2) Order URL, 3) Crawl URL, 4) Status  </li>
                                    <li>Status value should be <b>REQUESTED</b>
									<%}%>
									</label></div>
								<!-- <li><a href="sampleBulkFile.txt">Click here</a> to download
										a sample file</li>
								</label> <br /> <br />
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
