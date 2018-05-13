<?php
include ("functions.php");
@$page = $_GET["page"];
switch ($page) {
	case 'deleteStudentAccount':
	/* Delete Student Account */
	@$student_number=$_GET["student_number"];
	$query = "DELETE FROM ".$prefix."Student WHERE student_number='$student_number'";
	$result=mysqli_query($connection,$query);
			if($result)
			{
				header("location:searchStudent.php");
			}
			else
			{
				header("location:error.php");
			}
			break;
	case 'deleteLecturerAccount':
	/* Delete Lecturer Account */
	@$lecturer_id=$_GET["lecturer_id"];
	$query = "DELETE FROM ".$prefix."Lecturer WHERE lecturer_id='$lecturer_id'";
	$result=mysqli_query($connection,$query);
			if($result)
			{
				header("location:searchLecturer.php");
			}
			else
			{
				header("location:error.php");
			}
			break;
	case 'addCourseAssigment':
	/* Add Course Assigment */
			$course_name=$_POST["course_name"];
			$course_code=$_POST["course_code"];
			$department_id=$_POST["department_id"];
			$section_number=$_POST["section_number"];
			
			if(empty($course_name) || empty($course_code) || empty($department_id) || empty($section_number))
			{
				header("location:error.php");
			}
			else
			{
				$query="INSERT INTO Course(course_name, course_code, department_id, section_number) VALUES('$course_name', '$course_code', '$department_id', '$section_number')";
				$result=mysqli_query($connection,$query);
				if($result)
				{
					header("location:addCourse.php");
				}
				else
				{
					header("location:error.php");
				}
			}
		break;
	case 'deleteCourseAssigment':
	/* Delete Course Assigment */
		@$course_id=$_GET["course_id"];
		$query = "DELETE FROM ".$prefix."Course WHERE course_id='$course_id'";
		$result=mysqli_query($connection,$query);
		$query = "DELETE FROM ".$prefix."Classroom WHERE course_id='$course_id'";
		$result=mysqli_query($connection,$query);
		$query = "DELETE FROM ".$prefix."Given_Lectures WHERE course_id='$course_id'";
		$result=mysqli_query($connection,$query);
		$query = "DELETE FROM ".$prefix."Taken_Lectures WHERE course_id='$course_id'";
		$result=mysqli_query($connection,$query);
			if($result)
			{
				header("location:searchCourse.php");
			}
			else
			{
				header("location:error.php");
			}
			break;
	case 'updateCourseAssigment':
		@$course_id=$_GET["course_id"];
		$course_name=$_POST["course_name"];
		$course_code=$_POST["course_code"];
		$department_id=$_POST["department_id"];
		$section_number=$_POST["section_number"];
		if(!empty($department_id))
		$query="UPDATE ".$prefix."Course SET course_name='$course_name',course_code='$course_code',department_id='$department_id',section_number='$section_number' WHERE course_id='$course_id'";
		$result=mysqli_query($connection,$query);
			if($result)
			{
				header("location:updateCourseAssigment.php?course_id=$course_id");
			}
			else
			{
				header("location:error.php");
			}
		break;
	/* Update Course Assigment */
	case 'deleteAdmin':
	/* Delete Admin */
		@$admin_id=$_GET["admin_id"];
		$query = "DELETE FROM ".$prefix."Admin WHERE admin_id='$admin_id'";
		$result=mysqli_query($connection,$query);
			if($result)
			{
				header("location:searchAdmin.php");
			}
			else
			{
				header("location:error.php");
			}
			break;
	
	case 'createNewAdmin':
	/* Create New Admin */
	
			$name=$_POST["name"];
			$surname=$_POST["surname"];
			$mail_address=$_POST["mail_address"];
			$password=$_POST["password"];
			$username=$_POST["username"];
			if($name=='' || $surname=='' || $mail_address=='' || $username=='' || $password=='')
			{
				header("location:error.php");
			}
			else
			{
				$password=md5($password);
				$query="INSERT INTO ".$prefix."Admin (name, surname, username ,mail_address, password) VALUES('$name', '$surname', '$username', '$mail_address', '$password')";
				$result=mysqli_query($connection,$query);
				if($result)
				{
					header("location:addAdmin.php");
				}
				else
				{
					header("location:error.php");
				}
			}
		break;
	case 'addSchedule':
			$course_id=$_POST["course_id"];
			$section=$_POST["section"];
			$week_day=$_POST["week_day"];
			$start=$_POST["start"];
			$finish=$_POST["finish"];
			$start2 = (int)$start;
			$finish2 = (int)$finish;
			$x = $finish2-$start2;
			if($x>0){
			$items = (string)$start2;
			while($x!=0)
				{
					
					
					if($items==9){
						$items = "0" . $items ;
						$items = $items . ":20";
						echo $items;
						echo "<br>";
					}else{
						$items = $items . ":20";
						echo $items;
						echo "<br>";
					}
				$query = "INSERT INTO Schedule (course_id,section,week_day,hour) VALUES ('$course_id','$section','$week_day','$items')";
				$query = mysqli_query($connection,$query);
				$x--;
				$items = (int)$items;
				$items++;
				$items = (string)$items;
				
				}
			}else{
			header("location:error.php");
			}
		break;
	case 'logOut':
		if(isset($_COOKIE["admin_id"]))
    	{
        	unset($_COOKIE["admin_id"]);
        	unset($_COOKIE["username"]);
        	setcookie("admin_id","", time()-3600);
        	setcookie("username","", time()-3600);
        	header("location:index.php");
   		}
		break;
default:
}

?>