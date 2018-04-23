<?php
define('DB_USER', "rolatten_admin");
define ('DB_PASSWORD','Meloteam06');
define('DB_DATABASE','rolatten_attendancetracking');
define('DB_SERVER','localhost');


$db = new mysqli(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);
if($db->connect_errno > 0 ){
	die('Unable to connect to database ['. $db->connect_error . ']');
}

function check_login(){
	if(!isset($_COOKIE["user"])) header("location: login.php");
}
function login($query){
	global $db;
	$res = $db->query($query);
	if($res->num_rows > 0){
			$row = $res->fetch_object();
			echo 'Login Success!';
			setcookie('user', $row->lecturer_id, time() + 3600);
			header("refresh:3; url=index.php");
		}else
			echo 'Login Failed!';
	}

function lecturer_login($mail, $pass){
	if(empty($mail) || empty($pass)){
		echo "Empty Field Error";
	}
	else{
		$pass = md5($pass);
		$query = "select lecturer_id from Lecturer where mail_address = '$mail' and password = '$pass'";
		login($query);
	}
}

function logout(){
	if(isset($_COOKIE['user'])){
		unset($_COOKIE['user']);
		setcookie('user','', time()-60);
		header('location:login.php');
	}
}
function update_password($type, $id, $pass){
	global $db;
	if($type == 'lecturer') $query = "update Lecturer set password = '$pass' where lecturer_id = '$id'";
	else if($type == 'student') $query = "update Student set password = '$pass' where student_id = '$id'";
	else if($type == 'admin') $query = "update Admin set password = '$pass' where admin_id = '$id'";

	if($db->query($query)){
		echo 'Password Changed.';
	}else
	{
		echo 'Update Error!';
	}
}

function lecturer_change_password($old, $new, $newrepeat){
	global $db;
	if(empty($old) || empty($new) || empty($newrepeat)){
			echo 'Empty Field Error';
	}

	if($new !== $newrepeat){
		echo 'Password is not matches.';
	}
	else{
		$old = md5($old);
		$new = md5($new);
		$newrepeat = md5($newrepeat);
		$id = $_COOKIE['user'];

		$query = "select lecturer_id from Lecturer where lecturer_id='$id' and password = '$old'";
		$result = $db->query($query);
		if($result->num_rows){
			update_password('lecturer', $id, $new);
		}
	}
}

function parse_xls($file){
	require_once('vendor/autoload.php');
	$spreadsheet = \PhpOffice\PhpSpreadsheet\IOFactory::load($file);
	$data = $spreadsheet -> getActiveSheet() -> toArray(null,true, true, true);
	$course_code = $data[1]["A"];
	$student_numbers = array();
	$student_names = array();
	$student_surnames = array();
	$section = $data[3]["D"];
	for($i = 3; $i <= count($data); $i++){
		$student_numbers[] = $data[$i]["A"];
		$student_names[] = $data[$i]["B"];
		$student_surnames[] = $data[$i]["C"];
	}		
	$json ["course_code"] = $course_code;
	$json ["section"] = $section;
	$json ["students"] = array();
	for($i = 0; $i < count($student_numbers); $i++){
		$json ["students"][] = array("student_number"=>$student_numbers[$i],"student_name"=>$student_names[$i],"student_surname"=>$student_surnames[$i]);
	}
	echo "<table border = '1' align='center'>
	<tr><td><b>Course Code:</b></td><td colspan='2'>".$json ["course_code"]. "</td>
	</tr>
	<tr>
	<td><b>Section:</b></td><td colspan='2'>".$json ["section"]."</td>
	</tr>
	<tr>
	<td><b>Student Number</b></td><td><b>Student Name</b></td><td><b>Student Surname</b></td>
	</tr>
	";
	foreach($json ["students"] as $student){
	echo "<tr>
	<td>".$student["student_number"]."</td><td>".$student["student_name"]."</td><td>".$student["student_surname"]."</td></tr>";
	}
	echo '</table>';
}
?>