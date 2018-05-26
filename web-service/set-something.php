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
	case "course-assignment":
		if(empty($_POST["user_type"]) || empty($_POST["user_id"]) || empty($_POST["course_id"]) || empty($_POST["section"])) {
			empty_field_error();
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
			send_success();
		}else{
			send_error("An error has been occured while doing insert operation");
		}
		echo json_encode($json);
	break;
	case "beacon-mac":
		if(empty($_POST["user_id"]) || empty($_POST["beacon_mac"])){
			empty_field_error();
		}
		$user_id = $_POST["user_id"];
		$beacon = $_POST["beacon_mac"];

		$query = "UPDATE Lecturer SET beacon_mac = '$beacon' WHERE lecturer_id = '$user_id'";
		$result = mysqli_query($con, $query);
		if($result){
			send_success();
		}else{
			send_error("An error has been occured while doing update operation");
		}
	break;
	case "attendance":
	if(empty($_POST["user_id"]) || empty($_POST["classroom_id"]) || empty($_POST["total_time"])){
			empty_field_error();
	}
	
	$user_id = $_POST["user_id"];
	$classroom_id = $_POST["classroom_id"];
	$total_time = $_POST["total_time"];
	
	$query = "SELECT * FROM Token WHERE classroom_id = '$classroom_id'";
	$result = mysqli_query($con, $query);
	if(mysqli_num_rows($result) > 0){
		send_success();
	}
	$query = "SELECT Preconditions.* FROM Preconditions 
	INNER JOIN Classroom ON Classroom.course_id = Preconditions.course_id 
	WHERE Classroom.classroom_id = '$classroom_id'";
	$result = mysqli_query($con, $query);
	$middle = 0;
	$attended = 70;
	if(mysqli_num_rows($result) > 0){
		$row = mysqli_fetch_assoc($result);
		$middle = $row["middle_condition"];
		$attended = $row["attended_condition"];
	}
	if($attended == 0) $attended = 70;
	$percent = $total_time * 100 / (50 * 60 * 1000); // calculations
	if($percent >= $attended){
		$status = 2;
	}else if($middle != 0 && $percent >= $middle){
		$status = 1;
	}else
		$status = 0;
	
		$query = "SELECT * FROM Attended_Students WHERE classroom_id = '$classroom_id' AND student_id = '$user_id'";
		$result = mysqli_query($con, $query);
		if(mysqli_num_rows($result)> 0){
			$row = mysqli_fetch_assoc($result);
			if($row["status"] == 3) $status = 3;
			$query = "UPDATE Attended_Students SET status = '$status', time = '$total_time' WHERE classroom_id = '$classroom_id' AND student_id = '$user_id'";
			$result = mysqli_query($con, $query);
			if($result){
				send_success();
			}else
			{
				send_error("error on updating");
			}
		}else
		{
			$query = "INSERT INTO Attended_Students(classroom_id, student_id, status, time) VALUES('$classroom_id', '$user_id', '$status', '$total_time')";
			$result = mysqli_query($con, $query);
			if($result){
				send_success();
			}else
			{
				send_error("error on inserting");
			}
			
		}
	
	echo json_encode($json);
	
	break;
	case 'token':
	if(empty($_POST["classroom_id"]) || empty($_POST["token_value"])){
		empty_field_error();
	}
	$classroom_id = $_POST["classroom_id"];
	$token_value = md5($_POST["token_value"]);
	$time = round(microtime(true) * 1000) + (5*60*1000); // 5 minutes later
	$query = "INSERT INTO Token (classroom_id, token_value, time) VALUES('$classroom_id', '$token_value', '$time')";
	$result = mysqli_query($con, $query);
	if($result){
		send_success();
	}
	else
	{
		send_error("error on inserting");
	}
	break;
	case 'enter-token':
	if(empty($_POST["classroom_id"]) || empty($_POST["token_value"])){
		empty_field_error();
	}
	$classroom_id = $_POST["classroom_id"];
	$token_value = md5($_POST["token_value"]);
	$query = "SELECT * FROM Token WHERE classroom_id ='$classroom_id'";
	$result = mysqli_query($con,$query);
	if(mysqli_num_rows($result)>0){
		$row = mysqli_fetch_assoc($result);
		$time = $row["time"];
		$current_time = round(microtime(true) * 1000);
		if($current_time<=$time){
			$json["expired"] = false;
			if($token_value == $row["token_value"]) $json["success"] = true;
			else {
				$json["success"] = false;
				$json["message"] = "Tokens do not match";
			}
		}else
		{
			$json["expired"] = true;
			$json["success"] = true;
		}
	}else
	{
		$json["expired"] = false;
		$json["success"] = false;
		$json["message"] = "Secure mod is not enabled for this course";
	}
	echo json_encode($json);
	break;
	case "preconditions":
		if(empty($_POST["course_id"]) || empty($_POST["middle"]) || empty($_POST["attended"])){
			empty_field_error();
		}
		$course_id = $_POST["course_id"];
		$middle = $_POST["middle"];
		$attended = $_POST["attended"];
		
		$query = "SELECT * FROM Preconditions WHERE course_id = '$course_id'";
		
		$result = mysqli_query($con, $query);
		if(mysqli_num_rows($result)>0){
				$query = "UPDATE Preconditions SET middle_condition = '$middle', attended_condition = '$attended' WHERE course_id='$course_id'";
				$result = mysqli_query($con, $query);
				if(!$result){
					send_error("Update error");
				}else
				{
					send_success();
				}
		}
		else
		{
			$query = "INSERT INTO Preconditions (course_id, middle_condition, attended_condition) VALUES('$course_id', '$middle', '$attended')";
			$result = mysqli_query($con, $query);
			if(!$result){
				send_error("Insert error");
			}else
			{
				send_success();
		}
		}
	break;
	case 'issue':
		if(empty($_POST["subject"]) || empty($_POST["message"]) || empty($_POST["sender_mail"])){
			empty_field_error();
		}
		$subject = $_POST["subject"];
		$message = $_POST["message"];
		$sender_mail = $_POST["sender_mail"];
		$query = "INSERT INTO Issues(subject, message, sender_mail) VALUES('$subject', '$message', '$sender_mail')";
		$result = mysqli_query($con, $query);
		if($result){
			send_success();
		}else
		{
			send_error("Error while inserting database");
		}
	break;
	case 'cancel-classroom':
		if(empty($_POST["classroom_id"])){
			empty_field_error();
		}
		$classroom_id = $_POST["classroom_id"];
		$query = "UPDATE Classroom SET active = 0 WHERE classroom_id='$classroom_id'";
		$result = mysqli_query($con, $query);
		if($result)
			send_success();
		else{
			send_error("Error while updating database");
		}
	break;
	case 'mark-as-attended':
		if(empty($_POST["classroom_id"]) || empty($_POST["student_id"])){
			empty_field_error();
		}
		$classroom_id = $_POST["classroom_id"];
		$student_id = $_POST["student_id"];
		
		$query = "SELECT * FROM Attended_Students WHERE classroom_id = '$classroom_id' AND student_id = '$student_id'";
		
		$result = mysqli_query($con, $query);
		if(mysqli_num_rows($result)>0){
			$query = "UPDATE Attended_Students SET status = '3' WHERE classroom_id = '$classroom_id' AND student_id = '$student_id'";
			$result = mysqli_query($con, $query);
			if($result) send_success();
			else{
				send_error("Error while updating database");
			}
		}else
		{
			$query = "INSERT Attended_Students(classroom_id, student_id, status) VALUES('$classroom_id', '$student_id', '3')";
			$result = mysqli_query($con, $query);
			if($result) {
				send_success();
			}
			else {
				send_error("Error while inserting database");
			}
		}
	break;
	case 'secure-image':
		if(empty($_POST["user_id"]) || empty($_POST["classroom_id"]) || empty($_POST["image"])){
			empty_field_error();
		}
		$classroom_id = $_POST["classroom_id"];
		$user_id = $_POST["user_id"];
		$image = $_POST["image"];
		$path = "secure_images/".$classroom_id."_".$user_id.".jpg";
		
		$query = "SELECT * FROM Attended_Students WHERE classroom_id = '$classroom_id' AND student_id='$user_id'";
		$result = mysqli_query($con, $query);
		if(mysqli_num_rows($result) > 0){
			$query = "UPDATE Attended_Students SET status='0', secure_img = '$path' WHERE classroom_id='$classroom_id' AND student_id='$user_id'";
		}else
		{
			$query = "INSERT INTO Attended_Students(classroom_id, student_id, status, secure_img) VALUES('$classroom_id', '$user_id', '0', '$path')";
		}
		
		$result = mysqli_query($con, $query);
		if($result){
			file_put_contents($path, base64_decode($image));
			send_success();
		}else
		{
			send_error("Error while saving your photo. Please try again.");
		}
	break;
}

?>