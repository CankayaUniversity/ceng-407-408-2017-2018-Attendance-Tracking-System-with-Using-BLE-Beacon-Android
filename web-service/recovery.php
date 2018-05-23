<?php
header('content-type text/html charset=utf-8');
define('DB_USER', "rolatten_admin");
define ('DB_PASSWORD','Meloteam06');
define('DB_DATABASE','rolatten_attendancetracking');
define('DB_SERVER','localhost');


$con = mysqli_connect(DB_SERVER,DB_USER,DB_PASSWORD, DB_DATABASE) or die(mysql_error());

    mysqli_query($con, "SET NAMES utf8");
	mysqli_query($con, "SET CHARACTER SET utf8_turkish_ci");  


function show_form($mail, $token, $user_type){
	
	?><html>
	<body>
	<form action="recovery.php?submit=true" method="post">
	<input type="hidden" name="user_type" value="<?=$user_type?>">
	<input type="hidden" name="mail" value="<?=$mail?>">
	<input type="hidden" name="token" value="<?=$token?>">
	<table border="0">
	<tr>
	<td>New Password</td>
	<td><input type="password" name="new_password"/></td>
	</tr>
	<tr>
	<td>Re-enter new password</td>
	<td><input type="password" name="new_password_repeat"/></td>
	</tr>
	<tr>
	<td colspan="2" align="center"><input type="submit" name="submit" value="Change Password"/></td>
	</tr>
	</table>
	</form>
	</body>
	</html>
	
	<?php
}

function show_mail_and_token_form(){
	?>
	<html>
	<body>
	<form action="recovery.php?submit=show_form" method="post">
	<table border="0">
	<tr>
	<td>Mail Address:</td><td><input type="text" name="mail"/></td>
	</tr>
	<tr>
	<td>Token:</td><td><input type="password" name="token"/></td>
	</tr>
	<tr>
	<td colspan="2" align="center"><input type="submit" value="Change Password"/></td>
	</tr>
	</table>
	</form>
	</body>
	</html>
	<?php
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


	$query = "SELECT * FROM Recovery_Keys WHERE mail_address = '$mail' AND token='$token'";
	$result = mysqli_query($con, $query);
	if(mysqli_num_rows($result)>0){
		$row = mysqli_fetch_assoc($result);
		if($row["valid"] == 1)
			show_form($mail, $token, $row["user_type"]);
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
}else if($submit == "show_form"){
	$mail = $_POST["mail"];
	$token = $_POST["token"];
	if(empty($mail) || empty($token)){
		echo "<center><b>Empty field error</b></center>";
		$link="http://attendancesystem.xyz/attendancetracking/recovery.php";
		header("refresh:2 ;url=".$link);
		exit(0);
	}
	
	$mail = str_replace("__at__","@",$mail);


	$query = "SELECT * FROM Recovery_Keys WHERE mail_address = '$mail' AND token='$token'";
	$result = mysqli_query($con, $query);
	if(mysqli_num_rows($result)>0){
		$row = mysqli_fetch_assoc($result);
		if($row["valid"] == 1)
			show_form($mail, $token, $row["user_type"]);
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
else{
	$user_type =$_POST["user_type"];
	$mail = $_POST["mail"];
	$token = $_POST["token"];
	$new_password = $_POST["new_password"];
	$new_password_repeat = $_POST["new_password_repeat"];
	
	if(empty($new_password) || empty($new_password_repeat)){
		echo "<center><b>Empty field error</b></center>";
		$link = "recovery.php?mail=".str_replace("@","__at__",$mail)."&token=".$token;
		header("refresh:2 ;url=".$link);
		exit(0);
	}
	
	$pattern = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[!_*.-]).{6,}$";
	if(!preg_match($pattern, $new_password)){
		echo "<center><b>Your password should include at least:<br>1 Uppercase<br>1 Lowercase<br>1 Digit<br>1 Special character<br>And minimum 6 characters</b></center>";
		$link = "recovery.php?mail=".str_replace("@","__at__",$mail)."&token=".$token;
		header("refresh:2;url=".$link);
		exit(0);
	}
	
	if($new_password != $new_password_repeat){
		echo "<center><b>Passwords do not match</b></center>";
		$link="recovery.php?mail=".str_replace("@","__at__",$mail)."&token=".$token;
		header("refresh:2 ;url=".$link);
		exit(0);
	}
	
	$password = md5($new_password);
	if($user_type==1) $query = "UPDATE Student SET password='$password' WHERE mail_address='$mail'";
	else if($user_type==2) $query= "UPDATE Lecturer SET password='$password' WHERE mail_address='$mail'";
	
	$result = mysqli_query($con, $query);
	if(!$result){
		echo "<center><b>An error has been occurred while updating password</b></center>";
		$link="recovery.php?mail=".str_replace("@","__at__",$mail)."&token=".$token;
		header("refresh:2 ;url=".$link);
		exit(0);
	}
	
	$query = "UPDATE Recovery_Keys SET valid='0' WHERE mail_address='$mail' AND token='$token'";
	$result = mysqli_query($con, $query);
	
		echo "<center><b>Password is successfully changed</b></center>";
		$link="http://attendancesystem.xyz";
		header("refresh:2 ;url=".$link);
		exit(0);
	
	
}
?>

