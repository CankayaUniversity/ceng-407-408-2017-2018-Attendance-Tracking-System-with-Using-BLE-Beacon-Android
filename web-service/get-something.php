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
				$hour = $row["hour"];
				$date = date("d.m.Y");
				$course_id = $row["course_id"];
				$section = $row["section"];
				$updatequery = "SELECT classroom_id FROM Classroom WHERE course_id = '$course_id' AND date = '$date' AND section = '$section' AND hour = '$hour'";
				$result2 = mysqli_query($con, $updatequery);
				if(mysqli_num_rows($result2) > 0){
					$row2 = mysqli_fetch_assoc($result2);
					$row["classroom_id"] = $row2["classroom_id"];
				}else
				{
					$updatequery = "INSERT INTO Classroom(course_id, section, date, hour) VALUES('$course_id', '$section', '$date', '$hour')";
					$result2 = mysqli_query($con, $updatequery);
					if($result2) $row["classroom_id"] = mysqli_insert_id($con);
				}
				$json [] = $row;
			}
			//$json ["current_time"] = date("H | l");
		}else{
			$json ["success"] = false;
		}
		echo json_encode($json);
		
	break;
	case 'precondition';
	if(empty($_POST["course_id"])){
			$json ["message"] = "Empty field error";
			echo json_encode($json);
			exit(0);
		}
	$course_id = $_POST["course_id"];
	
	$query = "SELECT * FROM Preconditions WHERE course_id = '$course_id'";
	$result = mysqli_query($con, $query);
	if(mysqli_num_rows($result) > 0){
		$json = mysqli_fetch_assoc($result);
		$json["success"] = true;
	}else
	{
		$json["success"] = false;
	}
	echo json_encode($json);
	break;
	case 'coursetime':
	if(empty($_POST["classroom_id"])){
		$json ["message"] = "Empty field error";
		$json ["success"] = false;
		echo json_encode($json);
		exit(0);
	}
	$classroom_id = $_POST["classroom_id"];
	
	$query = "SELECT hour FROM Classroom WHERE classroom_id = '$classroom_id'";
	$result = mysqli_query($con, $query);
	if(mysqli_num_rows($result) > 0){
		$row = mysqli_fetch_assoc($result);
		$json ["hour"] = $row["hour"];
		$json ["success"] = true;
	}else
	{
		$json ["success"] = false;
	}
	echo json_encode($json);
	break;
	case 'lecturer-schedule':
	if(empty($_POST["user_id"])){
			$json ["message"] = "Empty field error";
			echo json_encode($json);
			exit(0);
		}
		
		$day = date("w");
		$week = array("sunday","monday","tuesday","wednesday","thursday","friday","saturday");
		$week_day = $week[$day];
		$user_id = $_POST["user_id"];
	
	$query = "SELECT Schedule.*, Course.course_code, Lecturer.beacon_mac FROM Schedule
INNER JOIN Given_Lectures ON Given_Lectures.course_id = Schedule.course_id
INNER JOIN Course ON Course.course_id = Given_Lectures.course_id
INNER JOIN Lecturer ON Lecturer.lecturer_id = Given_Lectures.lecturer_id
WHERE Schedule.week_day = '$week_day' AND Given_Lectures.lecturer_id = '$user_id'";

		$result = mysqli_query($con, $query);
		if(mysqli_num_rows($result)>0){
			$json = array();
			while($row = mysqli_fetch_assoc($result)){
				$hour = $row["hour"];
				$date = date("d.m.Y");
				$course_id = $row["course_id"];
				$section = $row["section"];
				$updatequery = "SELECT classroom_id FROM Classroom WHERE course_id = '$course_id' AND date = '$date' AND section = '$section' AND hour = '$hour'";
				$result2 = mysqli_query($con, $updatequery);
				if(mysqli_num_rows($result2) > 0){
					$row2 = mysqli_fetch_assoc($result2);
					$row["classroom_id"] = $row2["classroom_id"];
				}else
				{
					$updatequery = "INSERT INTO Classroom(course_id, section, date, hour) VALUES('$course_id', '$section', '$date', '$hour')";
					$result2 = mysqli_query($con, $updatequery);
					if($result2) $row["classroom_id"] = mysqli_insert_id($con);
				}
				$json [] = $row;
			}	
		}else{
			$json ["success"] = false;
		}
		echo json_encode($json);
	break;
	case "get-token-status":
		if(empty($_POST["classroom_id"])){
			$json ["message"] = "Empty field error";
			$json ["success"]  =false;
			echo json_encode($json);
			exit(0);
		}
		
		$classroom_id = $_POST["classroom_id"];
		
		$query = "SELECT * FROM Token WHERE classroom_id = '$classroom_id'";
		$result = mysqli_query($con, $query);
		if(mysqli_num_rows($result) > 0){
			$row = mysqli_fetch_assoc($result);
			$time = $row["time"];
			$current_time = round(microtime(true) * 1000);
			
			if($current_time > $time){
				$json["experied"]  = true;
			}
			else
			{
				$json["experied"]= false;
			}
			$json ["success"] = true;
		}else
		$json ["success"] = false;
		echo json_encode($json);
	break;
	case "given-lectures":
		if(empty($_POST["user_id"])){
			$json ["message"] = "Empty field error";
			$json ["success"] = false;
			echo json_encode($json);
			exit(0);
		}
		$user_id = $_POST["user_id"];
		$query = "SELECT Course.course_id, course_code FROM Course
INNER JOIN Given_Lectures ON Course.course_id = Given_Lectures.course_id
WHERE Given_Lectures.lecturer_id = '$user_id'";

		$result = mysqli_query($con, $query);
		$json = array();
		if(mysqli_num_rows($result)>0){
			while($row = mysqli_fetch_assoc($result)){
				$json[] = $row;
			}
		}else
		{
			$json ["message"] = "There is no lecture";
			$json["success"] = false;
		}
		echo json_encode($json);
	break;
	case "preconditions":
		if(empty($_POST["course_id"])){
			$json["message"] = "Empty field error";
			$json ["success"] = false;
			echo json_encode($json);
			exit(0);
		}
		$course_id = $_POST["course_id"];
		$middle = 0;
		$attended = 70;
		
		$query = "SELECT * FROM Preconditions WHERE course_id = '$course_id'";
		
		$result = mysqli_query($con, $query);
		if(mysqli_num_rows($result)){
			$row = mysqli_fetch_assoc($result);
			$middle = $row["middle_condition"];
			$attended = $row["attended_condition"];
		}
		
		$json ["middle"] = $middle;
		$json["attended"] = $attended;
		$json["success"] = true;
		
		echo json_encode($json);
	break;
	case "attendance-list":
		if(empty($_POST["classroom_id"])){
			$json["message"] = "Empty field error";
			$json["success"] = false;
			echo json_encode($json);
			exit(0);
		}
		$classroom_id = $_POST["classroom_id"];
		
			$query = "SELECT Student.name, Student.surname, Student.student_number, Taken_Lectures.student_id, COALESCE(Attended_Students.status,0) as status, COALESCE(Attended_Students.time, 0) as time FROM Taken_Lectures 	INNER JOIN Classroom ON Classroom.course_id = Taken_Lectures.course_id AND Classroom.section = Taken_Lectures.section 
	LEFT JOIN Attended_Students ON Classroom.classroom_id = Attended_Students.classroom_id AND Taken_Lectures.student_id = Attended_Students.student_id 
	INNER JOIN Student ON Taken_Lectures.student_id = Student.student_id WHERE Classroom.classroom_id = ".$classroom_id.
	"
	ORDER BY status DESC, Student.student_number ASC";
	$result = mysqli_query($con, $query);

	if(mysqli_num_rows($result)>0){
			$json = array();
		while($row = mysqli_fetch_assoc($result)){
			$json[]= $row;
		}
	}else{
		$json["message"] = "There is not any classroom information on the database";
		$json["success"] = false;
	}
	echo json_encode($json);
	break;
	case "last-15-lectures":
		if(empty($_POST["user_id"])){
			$json["message"] = "Empty field error";
			$json["success"] = false;
			echo json_encode($json);
			exit(0);
		}
		
		$user_id = $_POST["user_id"];
		$query = "SELECT Classroom.date, Classroom.hour, Course.course_code, COALESCE(Attended_Students.status, 0) as status FROM Taken_Lectures 
INNER JOIN Classroom ON Taken_Lectures.course_id = Classroom.course_id 
LEFT JOIN Attended_Students ON Classroom.classroom_id = Attended_Students.classroom_id AND Taken_Lectures.student_id = Attended_Students.student_id 
INNER JOIN Course ON Course.course_id = Taken_Lectures.course_id
WHERE Taken_Lectures.student_id = '$user_id'
ORDER BY Classroom.date DESC, Classroom.hour DESC
LIMIT 15";
		
		$result =mysqli_query($con, $query);
		if(mysqli_num_rows($result) > 0){
			$json = array();
			while($row = mysqli_fetch_assoc($result)){
				$time = $row["hour"];
				$time = substr($time,0,2);
				$time = ($time+1).":10";
				$date = $row["date"]." ".$time;
				$current = new DateTime();
				$lookDate = new Datetime($date);
				if($current >= $lookDate)
					$json[] = $row;
			}
		}else
		{
			$json["success"] = false;
		}
		echo json_encode($json);
	break;
	case "taken-lectures":
		if(empty($_POST["user_id"])){
			$json["message"] = "Empty field error";
			$json["success"] = false;
			echo json_encode($json);
			exit(0);
		}
		
		$user_id = $_POST["user_id"];
		
		$query = "SELECT Taken_Lectures.course_id, Course.course_code, Taken_Lectures.section FROM Taken_Lectures
INNER JOIN Course ON Taken_Lectures.course_id = Course.course_id
WHERE Taken_Lectures.student_id = '$user_id'";
		
		$result = mysqli_query($con, $query);
		if(mysqli_num_rows($result)>0){
			$json = array();
			while($row = mysqli_fetch_assoc($result)){
				$json[] = $row;
			}
		}else
		{
			$json ["success"] = false;
		}
		echo json_encode($json);
	break;
	case "attendance-info-calendar":
		if(empty($_POST["user_id"]) || empty($_POST["course_id"]) || empty($_POST["section"])){
			$json["message"] = "Empty field error";
			$json["success"] = false;
			echo json_encode($json);
			exit(0);
		}
		$user_id = $_POST["user_id"];
		$course_id = $_POST["course_id"];
		$section = $_POST["section"];
		$query = "SELECT Classroom.date, Classroom.hour, Course.course_code, COALESCE(Attended_Students.status, 0) as status FROM Taken_Lectures 
INNER JOIN Classroom ON Taken_Lectures.course_id = Classroom.course_id 
LEFT JOIN Attended_Students ON Classroom.classroom_id = Attended_Students.classroom_id AND Taken_Lectures.student_id = Attended_Students.student_id 
INNER JOIN Course ON Course.course_id = Taken_Lectures.course_id
WHERE Taken_Lectures.student_id = '$user_id' AND Classroom.course_id = '$course_id' AND Classroom.section='$section'
ORDER BY Classroom.date DESC, Classroom.hour DESC";
		$result = mysqli_query($con, $query);
		$json = array();
		while($row = mysqli_fetch_assoc($result)){
			$date = $row["date"]." ".$row["hour"];
			$current = new DateTime();
			$lookDate = new DateTime($date);
			if($current>=$lookDate)
				$json[] = $row;
		}
		echo json_encode($json);
	break;
	case 'given-lectures-seperate-sections':
		if(empty($_POST["user_id"])){
			$json["message"] = "Empty field error";
			$json["success"] = false;
			echo json_encode($json);
			exit(0);
		}
		$user_id = $_POST["user_id"];
		$query = "SELECT Course.course_id, Course.course_code, Course.section_number  FROM Given_Lectures
INNER JOIN Course ON Given_Lectures.course_id = Course.course_id
WHERE Given_Lectures.lecturer_id = '$user_id'
ORDER BY Course.course_code ASC";

		$result = mysqli_query($con, $query);
		if(mysqli_num_rows($result)>0){
			$json = array();
			while($row = mysqli_fetch_assoc($result)){
				$sections = $row["section_number"];
				for($i = 1; $i <= $sections; $i++){
					$arr["course_id"] = $row["course_id"];
					$arr["course_code"] = $row["course_code"];
					$arr["section"] = $i;
					$json [] = $arr;
				}
				
			}
		}else
		{
			$json["success"] = false;
			$json["message"] = "There is no course information";
		}
		echo json_encode($json);
	break;
	case 'classrooms':
		if(empty($_POST["course_id"]) || empty($_POST["section"])){
			$json["message"] = "Empty field error";
			$json["success"]= false;
			echo json_encode($json);
			exit(0);
		}
		$course_id = $_POST["course_id"];
		$section = $_POST["section"];
		
		$query = "SELECT Classroom.classroom_id, Classroom.date, Classroom.hour, Course.course_code FROM Classroom
INNER JOIN Course ON Classroom.course_id = Course.course_id
WHERE Classroom.course_id = '$course_id' AND Classroom.section = '$section'
ORDER BY Classroom.hour DESC";

		$result = mysqli_query($con, $query);
		if(mysqli_num_rows($result)>0){
			$json = array();
			while($row = mysqli_fetch_assoc($result)){
				$date = $row["date"]." ".$row["hour"];
				$current = new DateTime();
				$lookDate = new DateTime($date);
				if($current >= $lookDate)
					$json[] = $row;
			}
		}else
		{
			$json["message"] = "There is no classroom information";
			$json["success"] = false;
		}
		echo json_encode($json);
	break;
}
mysqli_close($con);
?>