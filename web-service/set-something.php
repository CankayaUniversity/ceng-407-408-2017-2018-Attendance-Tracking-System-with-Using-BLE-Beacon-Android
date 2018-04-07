<?php
require_once 'db.php';
if($_SERVER["REQUEST_METHOD"]!= "POST") exit(0);
if(empty($_POST["operation"])) exit(0);
switch($_POST["operation"]){
	case "course-assignment":
		if(empty($_POST["user_type"]) || empty($_POST["user_id"]) || empty($_POST["course_id"]) || empty($_POST["section"])) {
			$json ["success"] = false;
			$json["message"] = "Empty field error";
			echo json_encode($json);
			exit(0);
		}
		$userType = $_POST["user_type"];
		$userId = $_POST["user_id"];
		$courseId = $_POST["course_id"];
		$section = $_POST["section"];

		if($userType == "student")
			$query = "INSERT INTO Taken_Lectures(student_id, course_id, section) VALUES('$userId', '$courseId', '$section')"; 
		else if($userType == "lecturer")
			$query = "INSERT INTO Given_Lectures(lecturer_id, course_id, section) VALUES('$userId', '$courseId', '$section')";
		
		$result = mysqli_query($con, $query);
		if($result){
			$json ["success"] = true;
		}else{
			$json ["success"] = false;
			$json ["message"] = "An error has been occured while doing insert operation";
		}
		echo json_encode($json);
	break;
	case "beacon-mac":
		if(empty($_POST["user_id"]) || empty($_POST["beacon_mac"])){
			$json ["success"] = false;
			$json ["message"] = "Empty field error";
			echo json_encode($json);
			exit(0);
		}
		$user_id = $_POST["user_id"];
		$beacon = $_POST["beacon_mac"];

		$query = "UPDATE Lecturer SET beacon_mac = '$beacon' WHERE lecturer_id = '$user_id'";
		$result = mysqli_query($con, $query);
		if($result){
			$json ["success"] = true;
		}else{
			$json ["success"] = false;
			$json ["message"] = "An error has been occured while doing update operation";
		}
		echo json_encode($json);
	break;
}

?>