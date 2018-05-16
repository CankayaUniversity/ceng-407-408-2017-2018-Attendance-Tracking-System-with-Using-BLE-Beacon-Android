<?php
define('DB_USER', "rolatten_admin");
define ('DB_PASSWORD','Meloteam06');
define('DB_DATABASE','rolatten_attendancetracking');
define('DB_SERVER','localhost');


$db = new mysqli(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);
if($db->connect_errno > 0 ){
	die('Unable to connect to database ['. $db->connect_error . ']');
}
$db->set_charset('utf8');

function call_loader($text='', $url=''){
	if(!empty($text)) echo '<center>'.$text.'</center>';
	if(!empty($url)) header("refresh:2; url=$url");
	echo '<div class="sk-folding-cube">
        <div class="sk-cube1 sk-cube"></div>
        <div class="sk-cube2 sk-cube"></div>
        <div class="sk-cube4 sk-cube"></div>
        <div class="sk-cube3 sk-cube"></div>
      </div>';
}

function check_login(){
	if(!isset($_COOKIE["user"])) header("location: login.php");

}

function login($query){
	global $db;
	$res = $db->query($query);
	if($res->num_rows > 0){
			$row = $res->fetch_object();
			if($_POST["checker"]) setcookie('user',$row->lecturer_id, time() + 3600 * 24 * 365);
			else setcookie('user', $row->lecturer_id, time() + 3600);
			call_loader("Login Success!", "index.php");
	}else{
			call_loader("Login Failed!","login.php");
		}
}

function lecturer_login($mail, $pass){
	if(empty($mail) || empty($pass)){
		call_loader("Empty Field Error", "login.php");
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
		call_loader("Password is changed!", "index.php");
	}else
	{
		call_loader("Error while updating password", "index.php");
	}
}

function lecturer_change_password($old, $new, $newrepeat){
	global $db;
	if(empty($old) || empty($new) || empty($newrepeat)){
		call_loader("Empty Filed Error", "index.php?page=change_password");
	}

	if($new !== $newrepeat){
		call_loader("Password does not match", "index.php?page=change_password");
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
function sort_students($arr){
	for($i = 0; $i < count($arr); $i++){
		for($j = 0; $j < count($arr); $j++){
			if($arr[$i]["section"]<$arr[$j]["section"]){
				$temp = $arr[$i];
				$arr[$i] = $arr[$j];
				$arr[$j] = $temp;
			}
		}
	}
	
	for($i = 0; $i < count($arr); $i++){
		for($j = 0; $j < count($arr); $j++){
			if($arr[$i]["student_number"]<$arr[$j]["student_number"]){
				$temp = $arr[$i];
				$arr[$i] = $arr[$j];
				$arr[$j] = $temp;
			}
		}
	}
	return $arr;
}
function parse_xls($file){
	require_once('vendor/autoload.php');
	$spreadsheet = \PhpOffice\PhpSpreadsheet\IOFactory::load($file);
	$data = $spreadsheet -> getActiveSheet() -> toArray(null,true, true, true);
	$course_code = $data[1]["A"];
	if(!isset($course_code)){
		call_loader("Parse exception", "index.php?page=upload_student_list");
		return;
	}
	$student_numbers = array();
	$student_names = array();
	$student_surnames = array();
	$student_sections = array();
	for($i = 3; $i <= count($data); $i++){
		if(isset($data[$i]["A"]) && isset($data[$i]["B"]) && isset($data[$i]["C"])){ 
			$student_numbers[] = $data[$i]["A"];
			$student_names[] = $data[$i]["B"];
			$student_surnames[] = $data[$i]["C"];
			$student_sections[] = $data[$i]["D"];
		}
	}		
	
	$json ["course_code"] = $course_code;
	$json ["students"] = array();
	if(!isset($student_names) || !isset($student_surnames) || !isset($student_sections)){
		call_loader("Parse exception", "index.php?page=upload_student_list");
		return;
	}
	
	for($i = 0; $i < count($student_numbers); $i++){
		$json ["students"][] = array("student_number"=>$student_numbers[$i],"student_name"=>$student_names[$i],"student_surname"=>$student_surnames[$i], "section"=>$student_sections[$i]);
	}
	$json["students"] = sort_students($json["students"]);
	echo "<table border = '1' align='center' padding='3'>
	<tr><td><b>Course Code:</b></td><td colspan='3'>".$json ["course_code"]. "</td>
	</tr>
	<tr>
	<td><b>Student Number</b></td><td><b>Student Name</b></td><td><b>Student Surname</b></td><td><b>Section</b></td>
	</tr>
	";
	foreach($json ["students"] as $student){
	echo "<tr>
	<td>".$student["student_number"]."</td><td>".$student["student_name"]."</td><td>".$student["student_surname"]."</td><td>".$student["section"]."</td></tr>";
	}
	echo "<tr><td colspan=4><center> <form action='index.php?page=upload_student_list&action=save' method='post'>
	<input type='hidden' name='json' value='".json_encode($json)."'><input type='submit' value='Save'/></form> </center></td></tr>";
	echo '</table>';
	echo $json_encode($json);
}
function find_max($arr){
	$max = 0;
	for($i=0; $i < count($arr); $i++){
		if($max < $arr[$i]) $max = $arr[i];
	}
	return $max;
}

function save_student_list($json){
	global $db;
	$json = json_decode($json);
	$course_code = $json->course_code;
	$query = "SELECT course_id FROM Course WHERE course_code='$course_code'";
	$result = $db->query($query);
	if($result->num_rows){
		$row = $result->fetch_object();
		$course_id = $row->course_id;
	}else
	{
		call_loader("There is not any course information on database", "index.php");
		return;
	}
	$user_id = $_COOKIE["user"];
	$query = "SELECT * FROM Given_Lectures WHERE lecturer_id ='$user_id' AND course_id='$course_id'";
	$result = $db->query($query);
	if($result->num_rows == 0){
		$query = "INSERT INTO Given_Lectures(lecturer_id, course_id) VALUES('$user_id', '$course_id')";
		$result = $db->query($query);
		if(!$result){
			call_loader("An error has been occured while inserting to database1", "index.php");
			return;
		}
	}
	
	foreach($json->students as $student){
		//Is student exists?
		$query = "SELECT * FROM Student WHERE student_number = '$student->student_number'";
		$result = $db->query($query);
		if($result->num_rows == 0){
			//create account
			$query = "INSERT INTO Student(student_number, name, surname, allow_register) VALUES('$student->student_number', '$student->student_name', '$student->student_surname', '1')";
			$result = $db->query($query);
			if(!$result){
				call_loader("An error has been occured while inserting to database", "index.php");
				return;
			}
			$student_id = $db->insert_id;						
		}else
		{
			//Already exists
			$row = $result->fetch_object();
			$student_id = $row->student_id;
		}
		
		//Is there a course assignment between student and course before?
		$query = "SELECT * FROM Taken_Lectures WHERE student_id = '$student_id' AND course_id = '$course_id'";
		$result = $db->query($query);
		if($result->num_rows>0){
			//There is a course assignment so update
			$query = "UPDATE Taken_Lectures SET section='$student->section' WHERE student_id='$student_id' AND course_id='$course_id'";
			$result=$db->query($query);
			if(!$result){
				call_loader("An error has been occured while updating the database", "index.php");
				return;
			}
		}else
		{
			//There is not a course assignment so insert
			$query = "INSERT INTO Taken_Lectures(student_id, course_id, section) VALUES('$student_id', '$course_id', '$student->section')";
			$result = $db->query($query);
			if(!$result){
				call_loader("An error has been occured while inserting to database", "index.php");
				return;
			}
		}
	}
	call_loader("The student list has been successfully saved.", "index.php");
}


function course_list(){
	global $db;
	$query = "select Given_Lectures.*, Course.course_code from Given_Lectures inner join Course on Given_Lectures.course_id = Course.course_id WHERE Given_Lectures.lecturer_id = " . $_COOKIE["user"];
	
	$result = $db->query($query);
	if($result->num_rows){
		echo "<select name='courselist'>";
	while ($row = $result->fetch_object()) {

    echo "<option value='" . $row->course_id ."'>" . $row->course_code."</option>";
	}
	echo "</select>";
	}
	

}
function lecture_calendar(){
	global $db;
	
$query = "SELECT Classroom.*, Course.course_code FROM Classroom INNER JOIN Course on Classroom.course_id = Course.course_id INNER JOIN Given_Lectures ON Given_Lectures.course_id = Course.course_id WHERE Given_Lectures.lecturer_id = " . $_COOKIE["user"];

$result = $db->query($query);
if($result->num_rows){

	while($row = $result->fetch_object()){
		$date = strtotime($row->date);
		$date = date("Y-m-d", $date);
		echo "
		{ id: " . $row->course_id . ",
		title: '" . $row->course_code . "',
		start: '" . $date . "T". $row->hour .":00',
		url: 'index.php?page=report_interface&classroom=" . $row->classroom_id ."' },";
	}

}

}

function get_attended_student_list($classroom){
	global $db;
	$id=1;
	$query = "SELECT Student.name, Student.surname, Student.student_number, Taken_Lectures.student_id, COALESCE(Attended_Students.status,0) as status, COALESCE(Attended_Students.time, 0) as time FROM Taken_Lectures 	INNER JOIN Classroom ON Classroom.course_id = Taken_Lectures.course_id AND Classroom.section = Taken_Lectures.section 
	LEFT JOIN Attended_Students ON Classroom.classroom_id = Attended_Students.classroom_id AND Taken_Lectures.student_id = Attended_Students.student_id 
	INNER JOIN Student ON Taken_Lectures.student_id = Student.student_id WHERE Classroom.classroom_id = ".$classroom;

	$result = $db->query($query);
	if($result->num_rows){
		echo "<center><table class='listofstudents'>
							<thead><tr>
							<th>#</th>
							<th width='180'>Student Photo</th>
							<th width='200'>Student Number</th>
							<th>Name</th>
							<th>Surname</th>
							<th>Time</th>
							<th width='250'></th>
							</tr></thead>";
		while($row = $result->fetch_object()){
			$time = $row->time;
			if($time > 60000){
				$mins = floor($time / 60000);
				$secs  = ($time - ( $mins * 60000))/1000;
			}else{
				$mins=0;
				$secs = $time/1000;
			}
			if($mins <=9) $mins="0".$mins;
			if($secs <=9) $secs = "0".$secs;
			echo "<tr class='status".$row->status."'><td>".$id."</td>
			<td width='180'><center><img src='img/default.png' height='100' width='100'/></center></td>
			<td width='200'>".$row->student_number.
			"</td><td>".$row->name.
			"</td><td>".$row->surname.
			"</td><td>".$mins.":".$secs.
			"</td><td width='250'>";
			$id++;
			 if ($row->status!=2 && $row->status!=3) {
				echo "<center><form action= 'index.php?page=report_interface&classroom=".$classroom."&action=update' method='post' class='attendance'>
				<input type='hidden' name='student_id' value='".$row->student_id."'/>
				<input type='submit' value='Mark as attended' class='attendance'></form></center></td></tr>";
				}else echo "</td></tr>";
		}
		echo "<tr><td colspan=7 class='attendance2'><center><form action= 'index.php?page=report_interface&classroom=".$classroom."&action=delete' method='post' class='attendance2'>
										<input type='hidden' name='student_id' value='".$row->classroom_id."'/>
										<input type='submit' value='Cancel a Lecture' class='attendance2'></form></center>
										</td></tr></table></center>";
	}
}

function delete_attended($classroom_id){
	global $db;
	if(empty($classroom_id)){
		call_loader("An error has been occured!", "index.php");
		return;
	}
	
	$query = "UPDATE Classroom SET active = 0 WHERE classroom_id = '$classroom_id'";
	$result = $db->query($query);
	if($result)
		call_loader("The classroom has been successfully canceled", "index.php?page=report_interface");
	else
		call_loader("An error has been occured while canceling lecture", "index.php?page=report_interface");
}
	

function set_attended($classroom_id, $student_id){
	global $db;

	$query = "SELECT * FROM Attended_Students WHERE classroom_id = '$classroom_id' AND student_id = '$student_id'";
	$result = $db->query($query);
	if($result->num_rows){
		$query = "UPDATE Attended_Students SET status = '3' WHERE classroom_id = '$classroom_id' AND student_id = '$student_id'";
		$result = $db->query($query);
		if($result){
			//UPDATE SUCCESS
		}
		else
		{
			//UPDATE FAILED
		}
	}else
	{
		$query = "INSERT INTO Attended_Students (classroom_id, student_id, status) VALUES('$classroom_id', '$student_id', '3')";
		$result = $db->query($query);
		if($result){
			//INSERT SUCCESS
		}
		else
		{
			//INSERT FAILED
		}
	}
}
?>


