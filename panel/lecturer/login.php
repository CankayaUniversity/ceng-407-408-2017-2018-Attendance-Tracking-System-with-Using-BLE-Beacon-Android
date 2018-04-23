<?php
require_once('functions.php');
?>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8"/>
<title>Çankaya ATS / Login Page</title>
<link rel="stylesheet" type="text/css" href="css/spinner.css">
<link rel="stylesheet" type="text/css" href="css/responsiveform.css">
<link rel="stylesheet" media="screen and (max-width: 1200px) and (min-width: 601px)" href="css/responsiveform1.css" />
<link rel="stylesheet" media="screen and (max-width: 600px) and (min-width: 351px)" href="css/responsiveform2.css" />
<link rel="stylesheet" media="screen and (max-width: 350px)" href="css/responsiveform3.css" />
</head>
<body>
<?php
	@$page = $_GET["page"];
		switch($page){
			case 'submit':
			lecturer_login($_POST["email"], $_POST["password"]);
			break;
			default:
?>
<div id="envelope">
<form action="login.php?page=submit" method="post">

<label>Email</label>
<input name="email" placeholder="..@cankaya.edu.tr" type="text">
<label>Password</label>
<input name="password" placeholder="* * * * * * * *" type="password">
<center><input id="submit" type="submit" value="Login"></center>

</form>
</div>
<?php
		}
?>
</body>
</html>