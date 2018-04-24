<?php
header('Content-Type: application/json; charset: utf8');
//Database Information of the Web Service is hidden by from the community.
define('DB_USER', "");
define ('DB_PASSWORD','');
define('DB_DATABASE','');
define('DB_SERVER','');

$con = mysqli_connect(DB_SERVER,DB_USER,DB_PASSWORD, DB_DATABASE) or die(mysql_error());

    mysqli_query($con, "SET NAMES utf8");
	mysqli_query($con, "SET CHARACTER SET utf8_turkish_ci");  

?>