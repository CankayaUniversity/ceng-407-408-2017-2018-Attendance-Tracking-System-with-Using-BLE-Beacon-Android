<?php
header('Content-Type: application/json; charset: utf8');
define('DB_USER', "rolatten_admin");
define ('DB_PASSWORD','Meloteam06');
define('DB_DATABASE','rolatten_attendancetracking');
define('DB_SERVER','localhost');

$con = mysqli_connect(DB_SERVER,DB_USER,DB_PASSWORD, DB_DATABASE) or die(mysql_error());

    mysqli_query($con, "SET NAMES utf8");
	mysqli_query($con, "SET CHARACTER SET utf8_turkish_ci");  

?>