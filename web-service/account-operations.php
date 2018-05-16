<?php
require_once 'db.php';
if($_SERVER["REQUEST_METHOD"]!= "POST") exit(0);
if(empty($_POST["operation"])) exit(0);

switch($_POST["operation"]){
	//Login
	case "login": 
		if(empty($_POST["type"]) || empty($_POST["username"]) || empty($_POST["password"])) {
				$json ["success"] = false;
				$json["message"] = "Empty field error";
				echo json_encode($json);
				exit(0);
			}

		$type = $_POST["type"];
		$username = $_POST["username"];
		$password = md5($_POST["password"]);
		$query = "";
		if($type == "studentLogin") 
			$query = "SELECT * FROM Student WHERE student_number = '$username' AND password = '$password'";
		else if($type == "lecturerLogin") 
			$query = "SELECT * FROM Lecturer WHERE mail_address = '$username' AND password = '$password'";
		$result = mysqli_query($con, $query);
		if(mysqli_num_rows($result) > 0){
			$row = mysqli_fetch_assoc($result);
			$json = $row;
			$json ["success"] = true;
			if($type == "studentLogin") $json ["user_type"] = "student";
			else if($type == "lecturerLogin") $json ["user_type"] = "lecturer";
		}
		else{
			$json ["success"] = false;
			$json ["message"] = "Wrong username or password";
		}
		echo json_encode($json);
	break;
	//Registration 
	case "register": 
		if(empty($_POST["type"])) exit(0);
		if($_POST["type"] == "studentRegister"){
			if(empty($_POST["schoolID"]) || empty($_POST["password"]) || empty($_POST["mail"]) || empty($_POST["name"]) || empty($_POST["surname"]) || empty($_POST["image"])) {
				$json ["success"] = false;
				$json["message"] = "Empty field error";
				echo json_encode($json);
				exit(0);
			}
			$studentNumber = $_POST["schoolID"];
			$password = md5($_POST["password"]);
			$email = $_POST["mail"];
			$name = $_POST["name"];
			$surname = $_POST["surname"];
			$bluetoothMAC = $_POST["BluetoothMAC"];
			$image = $_POST["image"];
			$query = "SELECT * FROM Student WHERE student_number = '$studentNumber'";
			$result = mysqli_query($con, $query);
			$exists = false;
			$student_id = 0;
			if(mysqli_num_rows($result) > 0){
				$exists = true;
				$student_id= $row["student_id"];
				$row = mysqli_fetch_assoc($result);
				if($row["allow_register"] == 0){
					$json["message"] = "The user already exists on the system";
					$json["success"] = false;
					echo json_encode($json);
					exit(0);
				}
			}
			if($exists){
				$path = "student_images/$student_id.jpg";
					file_put_contents($path, base64_decode($image));
				$query = "UPDATE Student SET student_number='$studentNumber', name='$name', surname='$surname', bluetooth_mac, '$bluetoothMAC', mail_address='$email', password='$password' img = '$path' WHERE student_id='$student_id'";
				$result = mysqli_query($con, $query);
				if($result) {
					$json["success"] = true;
				}
				else{
					$json["message"] = "An error has been occurred while doing update operation";
					$json["success"] = false;
				}
				echo json_encode($json);
				exit(0);
			}
			
			$query = "INSERT INTO Student(student_number, name, surname, bluetooth_mac, mail_address, password) VALUES('$studentNumber', '$name', '$surname', '$bluetoothMAC', '$email', '$password')";
			$result = mysqli_query($con, $query);
			if($result) {
				$last = mysqli_insert_id($con);
				$path = "student_images/$last.jpg";
				file_put_contents($path, base64_decode($image));
				$query = "UPDATE Student SET img = '$path' WHERE student_id='$last'";
				$result = mysqli_query($con, $query);
				if($result){
					$json ["success"] = true;
				}else
				{
					$json["success"] = false;
					$json["message"] = "An error has been occurred while doing update operation";
				}
			}
			else{
				$json ["success"] = false;
				$json ["message"] = "An error has occured while doing insert operation";
			}
			
			echo json_encode($json);
		}
		else if($_POST["type"] == "lecturerRegister"){
			if(empty($_POST["mail"]) || empty($_POST["password"]) || empty($_POST["name"]) || empty($_POST["surname"])) {
				$json ["success"] = false;
				$json["message"] = "Empty field error";
				echo json_encode($json);
				exit(0);
			}

			$email = $_POST["mail"];
			$name = $_POST["name"];
			$surname = $_POST["surname"];
			$password = md5($_POST["password"]);
			$departmentID = $_POST["departmentID"];

			$query = "INSERT INTO Lecturer(name, surname, department_id, mail_address, password) VALUES('$name', '$surname', '$departmentID', '$email', '$password')";
			$result = mysqli_query($con, $query);
			if($result) 
				$json ["success"] = true;
			else{
				$json ["success"] = false;
				$json ["message"] = "An error has been occured while doing insert operation";
			}
			echo json_encode($json);
		}
	break;
	case "change-password":
		if(empty($_POST["old_password"]) || empty($_POST["user_id"]) || empty($_POST["user_type"]) || empty($_POST["new_password"]))
		{
			$json ["success"] = false;
			$json ["message"] = "Empty field error";
			echo json_encode($json);
			exit(0);
		}
		$oldPassword = md5($_POST["old_password"]);
		$userId = $_POST["user_id"];
		$userType = $_POST["user_type"];
		$newPassword = md5($_POST["new_password"]);

		if($userType == "student")
			$query = "SELECT student_id FROM Student WHERE student_id = '$userId' AND password = '$oldPassword'";
		else if ($userType == "lecturer")
			$query = "SELECT lecturer_id FROM Lecturer WHERE lecturer_id = '$userId' AND password = '$oldPassword'";
		$result = mysqli_query($con, $query);
		if(mysqli_num_rows($result) <= 0){
			$json ["success"] = false;
			$json ["message"] = "Password does not match with your old password";
			echo json_encode($json);
			exit(0);
		}
		if($userType == "student")
			$query = "UPDATE Student SET password = '$newPassword' WHERE student_id = '$userId'";
		else if($userType == "lecturer")
			$query = "UPDATE Lecturer SET password = '$newPassword' WHERE lecturer_id = '$userId'";
		$result = mysqli_query($con, $query);

		if($result)
			$json ["success"] = true;
		else{
			$json ["success"] = false;
			$json ["message"] = "An error has been occured while doing update operation";
		}
		echo json_encode($json);
	break;
	case "recovery":
		if(empty($_POST["mail_address"]) || empty($_POST["user_type"])){
			$json["message"] = "Empty field error";
			$json["success"] = false;
			echo json_encode($json);
			exit(0);
		}
		$user_type = $_POST["user_type"];
		$mail_address = $_POST["mail_address"];
		if($user_type == "student") $query = "SELECT name, surname FROM Student WHERE mail_address = '$mail_address'";
		else if($user_type == "lecturer") $query = "SELECT name, surname FROM Lecturer WHERE mail_address = '$mail_address'";
		
		$result = mysqli_query($con, $query);
		if(mysqli_num_rows($result)>0){
			$row = mysqli_fetch_assoc($result);
			if($user_type == "student") $u_type = 1;
			else if($user_type == "lecturer") $u_type = 2;
			
			$characters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
			$charactersLength = strlen($characters);
			$random = '';
			 
			for($i = 0; $i < $charactersLength; $i++){
			$random .= $characters[rand(0,$charactersLength-1)];
			}
			
			$token = md5($random);
			
			$query = "INSERT INTO Recovery_Keys(user_type, mail_address, token, valid) VALUES('$u_type', '$mail_address', '$token', '1')";
			$result = mysqli_query($con, $query);
			if($result){
				include 'mail.php';
				send_recovery_mail($mail_address, $row["name"], $row["surname"], $token);
				$json["success"] = true;
			}else
			{
				$json["message"] = "An error has been occurred while doing your action";
				$json["success"] = false;
			}
		}else{
			$json["message"] = "There is not any user that has e-mail address you entered";
			$json["success"] = false;
		}
		echo json_encode($json);
	break;
}
mysqli_close($con);
?>