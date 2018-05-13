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
        <li class="breadcrumb-item active">Course</li>
      </ol>
     
      <!-- Example DataTables Card-->
      <div class="card mb-3">
        <div class="card-header">
          <i class="fa fa-table"></i> Search Course</div>
        <div class="card-body">
          <div class="table-responsive">
            <table class="table table-bordered" id="dataTable" width="100%" cellspacing="0">
              <thead>
                <tr>
                  <th>Course Name</th>
                  <th>Course Code</th>
                  <th>Department</th>
                  <th>Sections</th>
                  <th>Functions</th>
                </tr>
              </thead>
             

              <tfoot>
                <tr>
                  <th>Course Name</th>
                  <th>Course Code</th>
                  <th>Department</th>
                  <th>Sections</th>
                  <th>Functions</th>
                </tr>
              </tfoot>
              
              <tbody>';
              
              	$query = "SELECT * FROM Course";
              	$result=mysqli_query($connection, $query);
				while($row=mysqli_fetch_array($result))
				{
				$department_id=$row["department_id"];
				$query2 = "SELECT * FROM Department WHERE department_id='$department_id'";
              	$result2=mysqli_query($connection, $query2);
              	$row2=mysqli_fetch_array($result2);
              echo '
                <tr>
                  <td>'.$row["course_name"].'</td>
                  <td>'.$row["course_code"].'</td>
                  <td>'.$row2["department_name"].'</td>
                  <td>'.$row["section_number"].'</td>
                  <td><a href="updateCourseAssigment.php?course_id='.$row["course_id"].'">UPDATE</a> | <a href="action.php?page=deleteCourseAssigment&course_id='.$row["course_id"].'">DELETE</a></td>
                </tr>';
                }


             echo' </tbody>
            </table>
          </div>
        </div>
        
      </div>
    </div>';
    include("footer.php");
    ?>