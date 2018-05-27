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
		return;
	}
	$upper = preg_match('@[A-Z]@', $new);
	$lower = preg_match('@[a-z]@', $new);
	$digit = preg_match('@[0-9]@', $new);
	$special = preg_match('@[!_*.-]@', $new);

	if(!$upper || !$lower || !$digit || !$special || strlen($new)<6){
		call_loader("Your password should include at least:<br>1 Uppercase<br>1 Lowercase<br>1 Digit<br>1 Special character<br>And minimum 6 characters","index.php?page=change_password");
		return;
	}

	if($new !== $newrepeat){
		call_loader("Password does not match", "index.php?page=change_password");
		return;
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
		}else{
			call_loader("Wrong old password!", "index.php?page=change_password");
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

function call_items_dropdown(){
global $db;
            $user_id = $_COOKIE["user"];
            $query = "SELECT Course.course_id, Course.course_code, Course.section_number  FROM Given_Lectures
			INNER JOIN Course ON Given_Lectures.course_id = Course.course_id
			WHERE Given_Lectures.lecturer_id = '$user_id'
			ORDER BY Course.course_code ASC";


            	echo '
            	<div class="row">
            	<div class="col-sm-6"><p align="right">
            	Select a course : </p></div>
  				<div class="col-sm-6">
            	<div class="dropdown">

  <button class="btn btn-success dropdown-toggle" type="button" data-toggle="dropdown">Course
  <span class="caret"></span></button>
  <ul class="dropdown-menu"> ';


  $result = $db->query($query);
        if(mysqli_num_rows($result)>0){
            while($row = mysqli_fetch_assoc($result)){
                $sections = $row["section_number"];
                for($i = 1; $i <= $sections; $i++){

    echo '<li><a href="index.php?page=report_interface&course_id='.$row["course_id"].'&section='.$i.'">'.$row["course_code"].' - '.$i.'</a></li>';
   }

            }
        }
  echo '
  </ul>
</div>
</div>
</div> ';
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
			$query = "INSERT INTO Student(student_number, name, surname, allow_register) VALUES('$student->student_number', '$student->student_name', '$student->student_surname','1')";
			$result = $db->query($query);
			if(!$result){
				call_loader("An error has been occured while inserting to database2", "index.php");
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
	$course_id = $_GET["course_id"];
	$section = $_GET["section"];
$query = "SELECT Classroom.*, Course.course_code FROM Classroom INNER JOIN Course on Classroom.course_id = Course.course_id INNER JOIN Given_Lectures ON Given_Lectures.course_id = Course.course_id WHERE Given_Lectures.course_id='$course_id' AND Classroom.section='$section' AND Classroom.active = 1 AND Given_Lectures.lecturer_id = " . $_COOKIE["user"];

$result = $db->query($query);
if($result->num_rows){

	while($row = $result->fetch_object()){

				$date = $row->date." ".$row->hour.":00";
                $time 	=	strtotime($date);
                $date 	=	date('Y-m-d H:i:s',$time);
				$now	=	date("Y-m-d H:i:s");

                if($now >= $date){
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

}

function get_attended_student_list($classroom){
	global $db;
	$id=1;
	$query = "SELECT * FROM Token WHERE classroom_id ='$classroom'";
	$result = $db->query($query);
	if($result->num_rows) $type = "secure";
	else $type = "regular";

	$query = "SELECT Classroom.course_id, Classroom.section, Student.student_id, Student.name, Student.surname, Student.student_number,Student.img, Taken_Lectures.student_id, COALESCE(Attended_Students.status,0) as status, COALESCE(Attended_Students.time, 0) as time, COALESCE(Attended_Students.secure_img, '') as secure_img  FROM Taken_Lectures 	INNER JOIN Classroom ON Classroom.course_id = Taken_Lectures.course_id AND Classroom.section = Taken_Lectures.section
	LEFT JOIN Attended_Students ON Classroom.classroom_id = Attended_Students.classroom_id AND Taken_Lectures.student_id = Attended_Students.student_id
	INNER JOIN Student ON Taken_Lectures.student_id = Student.student_id WHERE Classroom.classroom_id = ".$classroom." ORDER BY status DESC,student_number ASC";

	$result = $db->query($query);
	$students = array();
	$attended_count = 0;
	$nearly_count = 0;
	$absent_count = 0;
	if($result->num_rows){
		while($row = $result->fetch_assoc()){
			$time = $row["time"];
			if($time > 60000){
				$mins = floor($time / 60000);
				$secs  = ($time - ( $mins * 60000))/1000;
				$secs = floor($secs);
			}else{
				$mins=0;
				$secs = $time/1000;
				$secs = floor($secs);
			}
			if($mins <=9) $mins="0".$mins;
			if($secs <=9) $secs = "0".$secs;
			$row["time"] = $mins.":".$secs;
			if($row["status"] == 0) $absent_count++;
			else if($row["status"] == 1) $nearly_count++;
			else $attended_count++;
			$students[] = $row;
		}

	}
	$number_of_students = $absent_count + $nearly_count + $attended_count;
	echo "<center><table border=0 cellpadding='8' cellspacing='8'><tr><td class='absent'> Absent:&nbsp; $absent_count&nbsp;&nbsp;</td><td class='nearly'> Nearly:&nbsp; $nearly_count&nbsp;&nbsp;</td><td class='attended'> Attended:&nbsp; $attended_count&nbsp;&nbsp;  </td><td class='total'> Total student: $number_of_students </td></tr></table></center>";
	echo "<center><table class='listofstudents'  id='example'>
							<thead><tr>
							<th>#</th>
							<th width='180'>Student Photo</th>
							<th width='200'>Student Number</th>
							<th>Name</th>
							<th>Surname</th>";

							if($type == "regular") echo "<th>Participation</th>";
							else echo "<th>Taken Photo</th>";

							echo "<th width='250'></th>
							</tr></thead>";

	foreach($students as $student){
		if(empty($student["img"])){
			$student["img"]="student_images/default.png";

			}
			$student_id = $student["student_id"];
			$course_id = $student["course_id"];
			$section = $student["section"];

			$attendanceQuery = "SELECT Classroom.*, COALESCE(Attended_Students.status, 0) as status
			FROM Classroom
			LEFT JOIN Attended_Students ON Classroom.classroom_id = Attended_Students.classroom_id and Attended_Students.student_id = '$student_id'
			WHERE Classroom.course_id = '$course_id' AND Classroom.section = '$section' AND Classroom.active = '1'";
			$attendanceResult = $db->query($attendanceQuery);
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
            $total = $attendedCount + $nearlyCount + $absentCount;
            $attendedCount = ($attendedCount/$total)*100;
            $nearlyCount = ($nearlyCount/$total)*100;
            $absentCount = ($absentCount/$total)*100;
			$attendedCount=number_format($attendedCount,1);
			$nearlyCount=number_format($nearlyCount ,1);
			$absentCount =number_format($absentCount ,1);
			$infoTittle ="<font color=\"success\"><b>Attended</font> : ".$attendedCount."%<br><font color=\"#d5c70a\">Nearly</font> : ".$nearlyCount."%<br><font color=\"#e20505\">Absent</font> : ".$absentCount."%";
			$image_prefix = "http://attendancesystem.xyz/attendancetracking/";
		echo "<label for='age' >
		<tr id='age' title='".$infoTittle."' class='status".$student["status"]."'><td>".$id."</td>
			<td width='180' '><center><img src='".$image_prefix.$student["img"]."' height='100' width='100' class='round'/></center></td>
			<td width='200'>".$student["student_number"].
			"</td><td>".$student["name"].
			"</td><td>".$student["surname"]."</td>";
	if($type == "regular")
	echo "<td>".$student["time"]."</td>";
	else {
		if(!empty($student["secure_img"]))
		echo "<td><img src='".$image_prefix.$student["secure_img"]."' height='200px' width='150px'/></td>";
		else
		echo "<td><img src='".$image_prefix."assets/no_image.png' height='200px' width='150px'/></td>";
}

	echo"<td width='250'>";
			$id++;
			 if ($student["status"]!=2 && $student["status"]!=3) {
				echo "<center><form action= 'index.php?page=report_interface&classroom=".$classroom."&action=update&state=true' method='post' class='attendance'>
				<input type='hidden' name='student_id' value='".$student["student_id"]."'/>
				<input type='submit' value='Mark as attended' class='attendance'>

				</label>
				</form></center>



				</td>

				</tr>
				";
			}else {
				echo "<center><form action= 'index.php?page=report_interface&classroom=".$classroom."&action=update&state=false' method='post' class='attendance'>
				<input type='hidden' name='student_id' value='".$student["student_id"]."'/>
				<input type='submit' value='Mark as absent' class='attendance'>

				</label>
				</form></center>";


			}

				echo "</td>


    </tr>";
	}

				echo "<tr><td colspan=7 class='attendance2'><center><form action= 'index.php?page=report_interface&classroom=".$classroom."&action=delete' method='post' class='attendance2'>
										<input type='hidden' name='student_id' value='".$row->classroom_id."'/>
										<input type='submit' value='Cancel Lecture' class='attendance2'></form></center>
										</td></tr></table></center>";
}

function delete_attended($classroom_id){
	global $db;
	$query = "DELETE FROM Attended_Students WHERE classroom_id = '$classroom_id'";
	$result = $db->query($query);
	if($result){
		$query = "DELETE FROM Classroom WHERE classroom_id = '$classroom_id'";
		$result = $db->query($query);
		if($result){
			// DELETION SUCCESSFUL
		}
		else
		{
			// DELETION FAILED
		}
	}else
	{
		// DELETION FAILED
	}
}


function set_attended($classroom_id, $student_id, $state){
	global $db;

	$query = "SELECT * FROM Attended_Students WHERE classroom_id = '$classroom_id' AND student_id = '$student_id'";
	$result = $db->query($query);
	if ($state){
	if($result->num_rows)
		$query = "UPDATE Attended_Students SET status = '3' WHERE classroom_id = '$classroom_id' AND student_id = '$student_id'";
	else 	$query = "INSERT INTO Attended_Students (classroom_id, student_id, status) VALUES('$classroom_id', '$student_id', '3')";
		$result = $db->query($query);

}else{

	if($result->num_rows)
		$query = "UPDATE Attended_Students SET status = '0' WHERE classroom_id = '$classroom_id' AND student_id = '$student_id'";
	else 	$query = "INSERT INTO Attended_Students (classroom_id, student_id, status) VALUES('$classroom_id', '$student_id', '0')";
		$result = $db->query($query);

}
}

function print_course_info($course_id, $section){
	global $db;
	$now			=	date("Y-m-d H:i:s");
    $doneCourse	=	0;
    $query		=	"SELECT * FROM Classroom WHERE course_id='$course_id' AND section='$section' AND active='1'";
    $result		=	$db->query($query);
    while($row			=	$result->fetch_object()){
		$date = $row->date." ".$row->hour.":00";
		$time = strtotime($date);
        $date = date('Y-m-d H:i:s',$time);
        $query		=	"SELECT * FROM Classroom WHERE course_id='$course_id' AND section='$section' AND active='1'";
		if($now > $date){
			$doneCourse++;
         }
    }

    $query	= "SELECT * FROM Taken_Lectures WHERE course_id='$course_id' AND section='$section'";
    $result = $db->query($query);
    $takenLectures = $result->num_rows;

    $query	= "SELECT * FROM Attended_Students INNER JOIN Classroom ON Attended_Students.classroom_id = Classroom.classroom_id WHERE (Classroom.course_id='$course_id' AND Classroom.section='$section' AND Classroom.active = 1) AND (Attended_Students.status = 3 OR Attended_Students.status = 2)";
    $result = $db->query($query);
    $numberofAttended = $result->num_rows;
    $average = $numberofAttended*100 / ($takenLectures * $doneCourse);
    $average = number_format($average,2);

	echo '<div class="alert alert-success"><center><strong>'.$doneCourse.'</strong> course has been done until today.</div>
          <div class="alert alert-info"><center><strong>'.$takenLectures.'</strong> students are taking this course.</div>
         <div class="alert alert-danger"><center>Participation in the course <strong>'.$average.'%</strong></div>
                ';
}


?>
