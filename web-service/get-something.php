<?php
require_once 'db.php';


function send_error($text){
	$json["success"] = false;
	$json["message"]= $text;
	echo json_encode($json);
	exit(0);
}

function empty_field_error(){
	send_error("Empty field error");
}

function send_success(){
	$json["success"] = true;
	echo json_encode($json);
	exit(0);
}


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
			send_error("User is not found");
		}
		echo json_encode($json);
	break;
	case 'taken-courses':
		if(empty($_POST["user_id"])){
			empty_field_error();
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
			empty_field_error();
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
			empty_field_error();
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
			empty_field_error();
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
			empty_field_error();
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
		empty_field_error();
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
			empty_field_error();
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
			empty_field_error();
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
			empty_field_error();
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
			send_error("There is no lecture");
		}
		echo json_encode($json);
	break;
	case "preconditions":
		if(empty($_POST["course_id"])){
			empty_field_error();
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
			empty_field_error();
		}
		$classroom_id = $_POST["classroom_id"][0];
		$query = "SELECT * FROM Token WHERE classroom_id='$classroom_id'";
		$result = mysqli_query($con, $query);
		if(mysqli_num_rows($result) > 0) $type = "secure";
		else $type = "regular";
		$finalArray = array();
		for($i = 0; $i < count($_POST["classroom_id"]); $i++){
			$classroom_id = $_POST["classroom_id"][$i];
		$query = "SELECT Student.name, Student.surname, Student.student_number, Student.img, Taken_Lectures.student_id, COALESCE(Attended_Students.status,0) as status, COALESCE(Attended_Students.time, 0) as time, COALESCE(Attended_Students.secure_img, '') as secure_img, Classroom.course_id, Classroom.section
			FROM Taken_Lectures
			INNER JOIN Classroom ON Classroom.course_id = Taken_Lectures.course_id AND Classroom.section = Taken_Lectures.section
			LEFT JOIN Attended_Students ON Classroom.classroom_id = Attended_Students.classroom_id AND Taken_Lectures.student_id = Attended_Students.student_id
			INNER JOIN Student ON Taken_Lectures.student_id = Student.student_id WHERE Classroom.classroom_id = ".$classroom_id."
			ORDER BY status DESC, Student.student_number ASC";
		$result = mysqli_query($con, $query);
		
	if(mysqli_num_rows($result)>0){
			$json["classroom_info"]["classroom_id"] = $classroom_id;
			$json["classroom_info"]["type"] = $type;
			$json["student_info"] = array();
		while($row = mysqli_fetch_assoc($result)){
			$student_id = $row["student_id"];
			$course_id = $row["course_id"];
			$section = $row["section"];

			$attendanceQuery = "SELECT Classroom.*, COALESCE(Attended_Students.status, 0) as status
			FROM Classroom
			LEFT JOIN Attended_Students ON Classroom.classroom_id = Attended_Students.classroom_id and Attended_Students.student_id = '$student_id'
			WHERE Classroom.course_id = '$course_id' AND Classroom.section = '$section' AND Classroom.active = '1'";

			$attendanceResult = mysqli_query($con, $attendanceQuery);
			$attendedCount = 0;
			$nearlyCount = 0;
			$absentCount = 0;
			while($aRow = mysqli_fetch_assoc($attendanceResult)){
				$date = $aRow["date"]." ".$aRow["hour"].":00";
				$time 	=	strtotime($date);
				$date 	=	date('Y-m-d H:i:s',$time);
				$now	=	date("Y-m-d H:i:s");
				if($now >= $date){
					if($aRow["status"] == 2 || $aRow["status"] == 3) $attendedCount++;
					else if($aRow["status"] == 1) $nearlyCount++;
					else $absentCount++;
				}
			}
		  if($row["status"]==3) $row["status"] = 2;
			$student_info["student_id"] = $student_id;
			$student_info["student_number"] = $row["student_number"];
			$student_info["name"] = $row["name"];
			$student_info["surname"] = $row["surname"];
			$student_info["img"] = $row["img"];
			$student_info["attended"] = $attendedCount;
			$student_info["nearly"] = $nearlyCount;
			$student_info["absent"] = $absentCount;
			$student_info["status"] = $row["status"];
			$student_info["time"] = $row["time"];
			$student_info["secure_img"] = $row["secure_img"];


			$json["student_info"][] = $student_info;
		}
		$json["course_info"]["course_id"] = $course_id;
		$json["course_info"]["section"] = $section;
		$finalArray[] = $json;
	}else{
		send_error("There is not any classroom information on the database");
	}
		}
	echo json_encode($finalArray);
	break;
	case "last-15-lectures":
		if(empty($_POST["user_id"])){
			empty_field_error();
		}

		$user_id = $_POST["user_id"];
		$query = "SELECT Classroom.date, Classroom.hour, Course.course_code, COALESCE(Attended_Students.status, 0) as status FROM Taken_Lectures
INNER JOIN Classroom ON Taken_Lectures.course_id = Classroom.course_id
LEFT JOIN Attended_Students ON Classroom.classroom_id = Attended_Students.classroom_id AND Taken_Lectures.student_id = Attended_Students.student_id
INNER JOIN Course ON Course.course_id = Taken_Lectures.course_id
WHERE Taken_Lectures.student_id = '$user_id' AND Classroom.active = '1'
ORDER BY Classroom.date DESC, Classroom.hour DESC";
		$count = 0;
		$result =mysqli_query($con, $query);
		if(mysqli_num_rows($result) > 0){
			$json = array();
			while($row = mysqli_fetch_assoc($result)){
				$date = $row["date"]." ".$row["hour"].":00";
                $time 	=	strtotime($date) + 60 * 50; // adds aditional 50 minutes to wait end of the course
                $date 	=	date('Y-m-d H:i:s',$time);
				$now	=	date("Y-m-d H:i:s");
				if($now >= $date){
					$json[] = $row;
					$count++;
				}
				if($count >=15) break;
			}
		}else
		{
			$json["success"] = false;
		}
		echo json_encode($json);
	break;
	case "taken-lectures":
		if(empty($_POST["user_id"])){
			empty_field_error();
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
			empty_field_error();
		}
		$user_id = $_POST["user_id"];
		$course_id = $_POST["course_id"];
		$section = $_POST["section"];
		$query = "SELECT Classroom.date, Classroom.hour, Course.course_code, COALESCE(Attended_Students.status, 0) as status FROM Taken_Lectures
INNER JOIN Classroom ON Taken_Lectures.course_id = Classroom.course_id
LEFT JOIN Attended_Students ON Classroom.classroom_id = Attended_Students.classroom_id AND Taken_Lectures.student_id = Attended_Students.student_id
INNER JOIN Course ON Course.course_id = Taken_Lectures.course_id
WHERE Taken_Lectures.student_id = '$user_id' AND Classroom.course_id = '$course_id' AND Classroom.section='$section' AND Classroom.active = '1'
ORDER BY Classroom.date DESC, Classroom.hour DESC";
		$result = mysqli_query($con, $query);
		$json = array();
		while($row = mysqli_fetch_assoc($result)){
			$date = $row["date"]." ".$row["hour"].":00";
                $time 	=	strtotime($date);
                $date 	=	date('Y-m-d H:i:s',$time);
				$now	=	date("Y-m-d H:i:s");
				if($now >= $date)
				$json[] = $row;
		}
		echo json_encode($json);
	break;
	case 'given-lectures-seperate-sections':
		if(empty($_POST["user_id"])){
			empty_field_error();
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
			send_error("There is no course information");
		}
		echo json_encode($json);
	break;
	case 'classrooms':
		if(empty($_POST["course_id"]) || empty($_POST["section"])){
			empty_field_error();
		}
		$course_id = $_POST["course_id"];
		$section = $_POST["section"];
		$done  = 0;
		$query = "SELECT Classroom.classroom_id, Classroom.date, Classroom.hour, Course.course_code FROM Classroom
INNER JOIN Course ON Classroom.course_id = Course.course_id
WHERE Classroom.course_id = '$course_id' AND Classroom.section = '$section' AND Classroom.active = '1'
ORDER BY Classroom.hour DESC";

		$result = mysqli_query($con, $query);
		if(mysqli_num_rows($result)>0){
			$json = array();
			$lectures = array();
			while($row = mysqli_fetch_assoc($result)){
				$date = $row["date"]." ".$row["hour"].":00";
                $time 	=	strtotime($date);
                $date 	=	date('Y-m-d H:i:s',$time);
				$now	=	date("Y-m-d H:i:s");
				if($now >= $date){
					$lectures[] = $row;
					$done++;
				}
			}
			$query	= "SELECT * FROM Taken_Lectures WHERE course_id='$course_id' AND section='$section'";
			$result = mysqli_query($con,$query);
			$taken = mysqli_num_rows($result);

			$query	= "SELECT * FROM Attended_Students INNER JOIN Classroom ON Attended_Students.classroom_id = Classroom.classroom_id WHERE (Classroom.course_id='$course_id' AND Classroom.section='$section' AND Classroom.active = 1) AND (Attended_Students.status = 3 OR Attended_Students.status = 2)";
			$result = mysqli_query($con, $query);
			$numberofAttended = mysqli_num_rows($result);

			$average = $numberofAttended*100 / ($taken * $done);
			$average = number_format($average,2);
			$a["done"] = $done;
			$a["taken"] = $taken;
			$a["average"] = $average;
			$json["info"] = $a;
			$json["lectures"] = $lectures;

		}else
		{
			send_error("There is no classroom information");
		}
		echo json_encode($json);
	break;
}
mysqli_close($con);
?>
