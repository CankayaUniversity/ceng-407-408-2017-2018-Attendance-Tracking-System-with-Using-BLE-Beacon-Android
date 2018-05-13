<?php
               
include ("functions.php");
include ("header.php");
 echo'
  <div class="content-wrapper">
    <div class="container-fluid">
      <!-- Breadcrumbs-->
      <ol class="breadcrumb">
        <li class="breadcrumb-item">
          <a href="#">Homepage</a>
        </li>
        <li class="breadcrumb-item active">Add Course Assigment</li>
      </ol>
     
      <!-- Example DataTables Card-->
      <div class="card mb-3">
        <div class="card-header">
          <i class="fa fa-table"></i> Add Course Assigment</div>
        <div class="card-body">
          <div class="table-responsive">
           
           <form action="action.php?page=addCourseAssigment" method="POST">
  <div class="form-group">
    <label for="name">Course Name</label>
    <input type="text" class="form-control" name = "course_name" id="course_name">
  </div>
  <div class="form-group">
    <label for="name">Course Code</label>
    <input type="text" class="form-control" name = "course_code" id="course_code">
  </div>
  <div class="form-group">
    <label for="surname">Department</label>
    <div class="form-group">
    
  <select class="form-control" id="department_id" name="department_id">';
  			
    			$query = "SELECT * FROM Department";
              	$result=mysqli_query($connection, $query);
				while($row=mysqli_fetch_array($result))
				{
				echo '<option>'.$row["department_id"].' - '.$row["department_name"].'</option>';
				}
				
 echo' </select>
</div>
  </div>
   <div class="form-group">
    <label for="name">Section</label>
    <input type="text" class="form-control" name = "section_number" id="section_number">
  </div>
 
  <button type="submit" name="submit" class="btn btn-danger">Add Course</button>
</form>
           
          </div>
        </div>
        
      </div>
    </div>';
    include("footer.php");
    ?>