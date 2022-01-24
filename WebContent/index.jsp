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

</head>

<body>

	<div class="navbar navbar-fixed-top">

		<div class="navbar-inner">

			<div class="container">

				<a class="btn btn-navbar" data-toggle="collapse"
					data-target=".nav-collapse"> <span class="icon-bar"></span> <span
					class="icon-bar"></span> <span class="icon-bar"></span>
				</a> <a class="brand" href="index.html">
					<div class="logo"></div>
				</a>

				<div class="nav-collapse">
					<ul class="nav pull-right">

						<li class=""><a href="email:wraplive-support@propellum.com"
							class=""> Have Queries ? </a></li>








					</ul>

				</div>
				<!--/.nav-collapse -->

			</div>
			<!-- /container -->

		</div>
		<!-- /navbar-inner -->

	</div>
	<!-- /navbar -->

	<div class="contenttext">
		<div class="text123">
			<h1>Order Management System</h1>



		</div>
	</div>

	<div class="account-container">

		<div class="content clearfix">


			<div id="login" class="animate form">




				<form action="listOrders.htm" autocomplete="on" method="post">
					<h1>Log in</h1>
					<p>
						<label for="username" class="uname" data-icon="u"> Your
							email or username </label> <input id="username" name="username" required
							type="text" placeholder="myusername or mymail@mail.com" />
					</p>


					<p>
						<label for="password" class="youpasswd" data-icon="p">
							Your password </label> <input id="password" name="password" required
							type="password" placeholder="eg. X8df!90EO" />
					</p>



					<p class="login button">
						<input type="Reset" value="reset" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					</p>

					<p class="login button">
						<input type="submit" value="Login" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					</p>
				</form>

			</div>

			<div id="register" class="animate form">
				<form action="mysuperscript.php" autocomplete="on"></form>
			</div>

		</div>
		<!-- /content -->

	</div>
	<!-- /account-container -->




	<script src="js/jquery-1.7.2.min.js"></script>
	<script src="js/bootstrap.js"></script>

	<script src="js/signin.js"></script>

</body>

</html>
