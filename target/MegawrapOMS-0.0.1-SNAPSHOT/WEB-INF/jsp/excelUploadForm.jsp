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
<script src="http://code.jquery.com/jquery-1.8.3.js"></script>
	<script language="JavaScript">
document.getElementById('excelorderheader').className = "active";

$(document).ready(function() {
$("#submitAction").click(function() {
	var filename=$('#multiPartFile').val();
	var extension= filename.split('.').pop();
	
	if(extension!='xml')
		{
		 $("#excelProcessingForm").submit(function(){
             return false;
         });
		 window.alert("You Have Uploaded Wrong File");
		}else
			{
			 return true;
			}
	});
	
});


</script>


	<div class="main">

		<div class="main-inner">

			<div class="container">

				<div class="row">

					<div class="span12">

						<div class="widget ">

							<div class="widget-header">
								<i class="icon-copy"></i>
								<!-- <h3>Upload bulk orders</h3> -->
							</div>
							<!-- /widget-header -->

							<div class="widget-content">






								<br />








								<!----------------->




								<br /> <br /> File
								Upload:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								<form:form name="excelProcessingForm" 
								id="excelProcessingForm"
									modelAttribute="excelOrderRequest" method="post"
									action="excelUploadProcessing.htm"
									enctype="multipart/form-data">
									<form:input path="multiPartFile" type="file"
										name="multiPartFile" id="multiPartFile" size="52" />
									<br>
									<p style="color:red;float:left">(Upload Only .xml File)</p>
									<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 <button type="submit"  id="submitAction">Submit</button>

								</form:form>



								<!--  <hr>   
                                      
                                      
                                      
                                      <b>Instructions:</b>
                                      
                                     <label style="padding-left: 10px;">
                                      <br/>
                                      <li>File should be <b>simple text file</b>, all the fields should be seperated with <b>TAB(\t)</b> character</li>
                                      
                                      <li>Fields Sequence should be 1) Order URL, 2) Order Name, 3) Status, 4) Remarks  </li>
                                      
                                      <li>Status values should be either of <b>TEST</b> or <b>LIVE</b>
                                      
                                      <li><a href="sampleBulkFile.txt">Click here</a> to download a sample file</li>
                                      
                                      
                                      </label>


                                            
                                            
											
											<br />
																						<br /> -->





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
