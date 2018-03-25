<?php
require_once 'db.php';
if($_SERVER["REQUEST_METHOD"]!= "POST") exit(0);
if(empty($_POST["operation"])) exit(0);
switch($_POST["operation"]){
	case "department-list":
		$query = "SELECT * FROM Department";
		$result = mysqli_query($con, $query);
		$json = array();
		while($row = mysqli_fetch_assoc($result)){
			$json [] = $row;
		}
echo json_encode($json);
	break;
	case 'course-list':
	//TODO list courses with sections
	break;
	case 'student-list':
		$query = "SELECT * FROM Student";
		$result = mysqli_query($con, $query);
		$json = array();
			while($row = mysqli_fetch_assoc($result)){
				$json [] = $row;
			}
		echo json_encode($json);
	break;
}
mysqli_close($con);
?>