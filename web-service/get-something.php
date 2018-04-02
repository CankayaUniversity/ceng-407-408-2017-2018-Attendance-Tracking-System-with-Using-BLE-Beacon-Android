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
	$query = "SELECT * FROM Course";
	$result = mysqli_query($con, $query);
	$json = array();
	while($row = mysqli_fetch_assoc($result)){
	$json [] = $row;
}
	echo json_encode($json);

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
	case 'user-info':
		if(empty($_POST["user_id"]) || empty($_POST["user_type"])){
			$json ["message"] = "Empty field error";
			echo json_encode($json);
			exit(0);
		}
		$userId = $_POST["user_id"];
		$userType = $_POST["user_type"];
		if($userType == "student")
			$query = "SELECT * FROM Student WHERE student_id = '$userId'";
		else if($userType == "lecturer")
			$query = "SELECT * FROM Lecturer WHERE lecturer_id = '$userId'";

		$result = mysqli_query($con, $query);
		if(mysqli_num_rows($result) > 0 ){
			$json = mysqli_fetch_assoc($result);
			$json["success"] = true;
		}
		else
		{
			$json ["success"] = false;
			$json ["message"] = "User is not found";
		}
		echo json_encode($json);
	break;
}
mysqli_close($con);
?>