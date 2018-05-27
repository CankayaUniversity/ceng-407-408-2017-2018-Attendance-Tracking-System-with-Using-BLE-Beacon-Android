<?php
header('content-type text/html charset=utf-8');
define('DB_USER', "rolatten_admin");
define ('DB_PASSWORD','Meloteam06');
define('DB_DATABASE','rolatten_attendancetracking');
define('DB_SERVER','localhost');


$con = mysqli_connect(DB_SERVER,DB_USER,DB_PASSWORD, DB_DATABASE) or die(mysql_error());

    mysqli_query($con, "SET NAMES utf8");
	mysqli_query($con, "SET CHARACTER SET utf8_turkish_ci");  


function show_mail_and_token_form(){
	?>
	<html>
	<body>
	<form action="activation.php?submit=activate" method="post">
	<table border="0">
	<tr>
	<td>Mail Address:</td><td><input type="text" name="mail"/></td>
	</tr>
	<tr>
	<td>Token:</td><td><input type="password" name="token"/></td>
	</tr>
	<tr>
	<td colspan="2" align="center"><input type="submit" value="Activate Account"/></td>
	</tr>
	</table>
	</form>
	</body>
	</html>
	<?php
}

function activate($user_type, $mail){
	global $con;
	$link="http://attendancesystem.xyz";
	if($user_type == 1)
		$query = "UPDATE Student SET active = 1 WHERE mail_address= '$mail'";
	else if($user_type == 2)
		$query = "UPDATE Lecturer SET active = 1 WHERE mail_address = '$mail'";
		
		$result = mysqli_query($con, $query);
		if($result){
			echo "<center><b>Account is activated</b></center>";
			return true;
		}else
		{
			echo "<center><b>Failed to activate account. Please try again later</b></center>";
			return false;
		}
}

function disable_token($mail, $token)
{
	global $con;
	$query = "UPDATE Activation_Keys SET valid=0 WHERE mail_address='$mail' AND token='$token'";
	return mysqli_query($con, $query);
	
}
@$submit = $_GET["submit"];

if(!isset($submit)){
	@$mail =$_GET["mail"];
	@$token = $_GET["token"];
	if(!isset($mail) || !isset($token)) {
		show_mail_and_token_form();
		exit;
	}
	$mail = str_replace("__at__","@",$mail);


	$query = "SELECT * FROM Activation_Keys WHERE mail_address = '$mail' AND token='$token'";
	$result = mysqli_query($con, $query);
	if(mysqli_num_rows($result)>0){
		$row = mysqli_fetch_assoc($result);
		if($row["valid"] == 1)
		{
			$link="http://attendancesystem.xyz";
			if(activate($row["user_type"] , $mail) && disable_token($mail, $token)){					
				header("refresh:2 ;url=".$link);
			}
			else header("refresh:2 ;url=".$link);
		}
		else
		{
		echo "<center><b>Invalid token</b></center>";
		$link="http://attendancesystem.xyz";
		header("refresh:2 ;url=".$link);
		exit(0);
		}
	}else
	{
		echo "<center><b>Invalid token</b></center>";
		$link="http://attendancesystem.xyz";
		header("refresh:2 ;url=".$link);
		exit(0);
	}
}else if($submit == "activate"){
	$mail = $_POST["mail"];
	$token = $_POST["token"];
	if(empty($mail) || empty($token)){
		echo "<center><b>Empty field error</b></center>";
		$link="http://attendancesystem.xyz/attendancetracking/activation.php";
		header("refresh:2 ;url=".$link);
		exit(0);
	}

	$query = "SELECT * FROM Activation_Keys WHERE mail_address = '$mail' AND token='$token'";
	$result = mysqli_query($con, $query);
	if(mysqli_num_rows($result)>0){
		$row = mysqli_fetch_assoc($result);
		if($row["valid"] == 1)
		{
			$link="http://attendancesystem.xyz";
			if(activate($row["user_type"] , $mail) && disable_token($mail, $token)){					
				header("refresh:2 ;url=".$link);
			}
			else header("refresh:2 ;url=".$link);
		}
		else
		{
		echo "<center><b>Invalid token</b></center>";
		$link="http://attendancesystem.xyz";
		header("refresh:2 ;url=".$link);
		exit(0);
		}
	}else
	{
		echo "<center><b>Invalid token</b></center>";
		$link="http://attendancesystem.xyz";
		header("refresh:2 ;url=".$link);
		exit(0);
	}
}
?>

