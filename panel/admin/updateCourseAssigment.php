<?php
include "functions.php";
include ("header.php");
echo '
  <div class="content-wrapper">
    <div class="container-fluid">
      <!-- Breadcrumbs-->
      <ol class="breadcrumb">
        <li class="breadcrumb-item">
          <a href="#">Homepage</a>
        </li>
        <li class="breadcrumb-item active">Update Course Assigment</li>
      </ol>
     
      <!-- Example DataTables Card-->
      <div class="card mb-3">
        <div class="card-header">
          <i class="fa fa-table"></i> Update Course Assigment</div>
        <div class="card-body">
          <div class="table-responsive">';
           
          @$course_id=$_GET["course_id"];
          
    		$query2 = "SELECT * FROM Course WHERE course_id='$course_id'";
			$result2 =mysqli_query($connection, $query2);
			$row2	= mysqli_fetch_array($result2);
			$department_id=$row2["department_id"];
          echo ' <form action="action.php?page=updateCourseAssigment&course_id='.$row2["course_id"].'" method="POST">
  <div class="form-group">
    <label for="name">Course Name</label>
    <input type="text" class="form-control" value = "'.$row2["course_name"].'" name = "course_name" id="course_name">
  </div>
  <div class="form-group">
    <label for="name">Course Code </label>
    <input type="text" class="form-control" value="'.$row2["course_code"].'" name = "course_code" id="course_code">
  </div>
  <div class="form-group">
    <label for="name">Section</label>
    <input type="text" class="form-control" value="'.$row2["section_number"].'" name = "section_number" id="section_number">
  </div>
  <div class="form-group">
    <label for="surname">Department</label>
    <div class="form-group">
    
  <select class="form-control" value = "'.$department_id.'" id="department_id" name="department_id">';
  				
  				$query2 = "SELECT * FROM Department WHERE department_id='$department_id'";
				$result2 =mysqli_query($connection, $query2);
				$row2	= mysqli_fetch_array($result2);
    			$query = "SELECT * FROM Department";
              	$result=mysqli_query($connection, $query);
              	echo '<option selected>'.$row2["department_id"].' - '.$row2["department_name"].'<option> ';
				while($row=mysqli_fetch_array($result))
				{
				echo '<option>'.$row["department_id"].' - '.$row["department_name"].'</option>';
				}
 echo ' </select>
</div>
  </div>
   
 
  <button type="submit" name="submit" class="btn btn-danger">Update Course</button>
</form>
          </div>
        </div>
        
      </div>
    </div>
   
';
          include("footer.php");
				?> 