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
		if($userType == "student"){
			$query = "SELECT * FROM Student WHERE student_id = '$userId'";
		}
		else if($userType == "lecturer")
			$query = "SELECT * FROM Lecturer WHERE lecturer_id = '$userId'";

		$result = mysqli_query($con, $query);
		if(mysqli_num_rows($result) > 0 ){
			$json = mysqli_fetch_assoc($result);
			$json["success"] = true;
			$json ["user_type"] = $userType;
		}
		else
		{
			$json ["success"] = false;
			$json ["message"] = "User is not found";
		}
		echo json_encode($json);
	break;
	case 'taken-courses':
		if(empty($_POST["user_id"])){
			$json ["message"] = "Empty field error";
			echo json_encode($json);
			exit(0);
		}
		$user_id = $_POST["user_id"];
		$query = "SELECT * FROM Taken_Lectures WHERE student_id = '$user_id'";
		$result = mysqli_query($con, $query);
		if(mysqli_num_rows($result) > 0){
			$json = array();
			while($row = mysqli_fetch_assoc($result)){
				$json [] = $row;
			}
		}
		else{
			$json["success"] = false;
		}
		echo json_encode($json);
	break;
	case 'given-courses':
		if(empty($_POST["user_id"])){
			$json ["message"] = "Empty field error";
			echo json_encode($json);
			exit(0);
		}
		$user_id = $_POST["user_id"];
		$query = "SELECT * FROM Given_Lectures WHERE lecturer_id = '$user_id'";
		$result = mysqli_query($con, $query);
		if(mysqli_num_rows($result) > 0){
		$json = array();
		while($row = mysqli_fetch_assoc($result)){
			$json [] = $row;
		}
	}else{
		$json["success"] = false;
	}
	echo json_encode($json);
	break;
	case 'beacon-taken-courses':
		if(empty($_POST["user_id"])){
			$json ["message"] = "Empty field error";
			echo json_encode($json);
			exit(0);
		}
		$user_id = $_POST["user_id"];
		$query = "SELECT Lecturer.beacon_mac FROM Lecturer 
		INNER JOIN Given_Lectures ON Lecturer.lecturer_id = Given_Lectures.lecturer_id 
		INNER JOIN Taken_Lectures ON Given_Lectures.course_id = Taken_Lectures.course_id WHERE Taken_Lectures.student_id = '$user_id'";
		$result = mysqli_query($con, $query);
		if(mysqli_num_rows($result)>0){
			$json = array();
			while($row = mysqli_fetch_assoc($result)){
				$json [] = $row;
			}
		}
		else{
			$json ["success"] = false;
		}
		echo json_encode($json);
	break;
	case 'schedule':
		if(empty($_POST["user_id"])){
			$json ["message"] = "Empty field error";
			echo json_encode($json);
			exit(0);
		}
		$day = date("w");
		$week = array("sunday","monday","tuesday","wednesday","thursday","friday","saturday");
		$week_day = $week[$day];
		$user_id = $_POST["user_id"];
		$query = "SELECT Schedule.*, Taken_Lectures.student_id, Lecturer.beacon_mac, Course.course_code FROM Schedule 
		INNER JOIN Taken_Lectures ON Taken_Lectures.course_id = Schedule.course_id 
		INNER JOIN Given_Lectures ON Taken_Lectures.course_id = Given_Lectures.course_id 
		INNER JOIN Lecturer ON Given_Lectures.lecturer_id = Lecturer.lecturer_id 
		INNER JOIN Course ON Taken_Lectures.course_id = Course.course_id
		WHERE Taken_Lectures.student_id = '$user_id' AND Taken_Lectures.section = Schedule.section AND Schedule.week_day = '$week_day'";
		
		$result = mysqli_query($con, $query);
		if(mysqli_num_rows($result)>0){
			$json = array();
			while($row = mysqli_fetch_assoc($result)){
				$json [] = $row;
			}
			//$json ["current_time"] = date("H | l");
		}else{
			$json ["success"] = false;
		}
		echo json_encode($json);
		
	break;
}
mysqli_close($con);
?>