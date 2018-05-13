<?php
$admin_id = $_COOKIE["admin_id"];
if(!isset($admin_id)) header("location:index.php");
echo "<center><b>We did NOT your request<br>Redirecting to homepage</b><center>";
header("Refresh: 2; url=index.php");

?>