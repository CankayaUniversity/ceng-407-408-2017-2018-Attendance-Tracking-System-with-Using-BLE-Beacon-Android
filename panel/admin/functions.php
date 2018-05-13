<meta charset="utf-8">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<?php
include ("config.php");
/*	database connection 	*/
function db_connection()
{
    $conection= mysqli_connect($GLOBALS["host"], $GLOBALS["username"], $GLOBALS["pass"], $GLOBALS["dbName"]);
    
	
	//$con= mysqli_connect($host,$username,$pass,$dbName);
	if(!$conection)
	{
		die("Veritabanı bağlantısı kurulurken bir hata meydana geldi!");
	}
	return $conection;
}

global $connection,$prefix;
$prefix ="";
$connection = db_connection();
	mysqli_query($connection, "SET NAMES utf8");
	mysqli_query($connection, "SET CHARACTER SET utf8_turkish_ci");  
?>