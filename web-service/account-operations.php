<?php
require_once 'db.php';
if($_SERVER["REQUEST_METHOD"]!= "POST") exit(0);
if(empty($_POST["operation"])) exit(0);

switch($_POST["operation"]){
	//Login
	case "login": 
		if(empty($_POST["type"]) || empty($_POST["username"]) || empty($_POST["password"])) exit(0);

		$type = $_POST["type"];
		$username = $_POST["username"];
		$password = md5($_POST["password"]);
		$query = "";
		if($type == "studentLogin") 
			$query = "SELECT student_id FROM Student WHERE student_number = '$username' AND password = '$password'";
		else if($type == "lecturerLogin") 
			$query = "SELECT lecturer_id FROM Lecturer WHERE mail_address = '$username' AND password = '$password'";
		$result = mysqli_query($con, $query);
		if(mysqli_num_rows($result) > 0){
			$json ["success"] = true;
			$json ["user"] = $username;
			if($type == "studentLogin") $json ["user_type"] = "student";
			else if($type == "lecturerLogin") $json ["user_type"] = "lecturer";
		}
		else
			$json ["success"] = false;
			$json ["message"] = "Wrong username or password";
		echo json_encode($json);
	break;
	//Registration 
	case "register": 
		if(empty($_POST["type"])) exit(0);
		if($_POST["type"] == "studentRegister"){
			if(empty($_POST["schoolID"]) || empty($_POST["password"]) || empty($_POST["mail"]) || empty($_POST["name"]) || empty($_POST["surname"])) exit(0);
			$studentNumber = $_POST["schoolID"];
			$password = md5($_POST["password"]);
			$email = $_POST["mail"];
			$name = $_POST["name"];
			$surname = $_POST["surname"];
			$bluetoothMAC = $_POST["BluetoothMAC"];

			$query = "INSERT INTO Student(student_number, name, surname, bluetooth_mac, mail_address, password) VALUES('$studentNumber', '$name', '$surname', '$bluetoothMAC', '$email', '$password')";
			$result = mysqli_query($con, $query);
			if($result) 
				$json ["success"] = true;
			else
				$json ["success"] = false;
			echo json_encode($json);
		}
		else if($_POST["type"] == "lecturerRegister"){
			if(empty($_POST["mail"]) || empty($_POST["password"]) || empty($_POST["name"]) || empty($_POST["surname"])) exit(0);

			$email = $_POST["mail"];
			$name = $_POST["name"];
			$surname = $_POST["surname"];
			$password = md5($_POST["password"]);

			$query = "INSERT INTO Lecturer(name, surname, mail_address, password) VALUES('$name', '$surname', '$email', '$password')";
			$result = mysqli_query($con, $query);
			if($result) 
				$json ["success"] = true;
			else
				$json ["success"] = false;
			echo json_encode($json);
		}
	break;

}
mysqli_close($con);
?>