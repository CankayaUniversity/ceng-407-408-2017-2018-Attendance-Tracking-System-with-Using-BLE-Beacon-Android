<?php
include ("functions.php");
$username = $_POST["username"];
$password = md5($_POST["password"]);
		$result="";
		$query = "SELECT * FROM Admin WHERE username='$username' AND password='$password'";
		if(mysqli_num_rows(mysqli_query($connection,$query))>0)
			$result = mysqli_fetch_array(mysqli_query($connection,$query));
		if(empty($result))
        	header("location:index.php");
		else
		{
			setcookie("admin_id",$result["admin_id"],time()+3600);
			setcookie("username",$username,time()+3600);
        	header("location:admin.php");
		}

?>