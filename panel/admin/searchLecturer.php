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
        <li class="breadcrumb-item active">Lecturers</li>
      </ol>
     
      <!-- Example DataTables Card-->
      <div class="card mb-3">
        <div class="card-header">
          <i class="fa fa-table"></i> Search Lecturer</div>
        <div class="card-body">
          <div class="table-responsive">
            <table class="table table-bordered" id="dataTable" width="100%" cellspacing="0">
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Surname</th>
                  <th>Phone Number</th>
                  <th>Mail Address</th>
                  <th>Functions</th>
                </tr>
              </thead>
             

				
              <tfoot>
                <tr>
                  <th>Name</th>
                  <th>Surname</th>
                  <th>Phone Number</th>
                  <th>Mail Address</th>
                  <th>Functions</th>
                </tr>
              </tfoot>
              
              <tbody>';
              
            /*  $query = "SELECT course_id,section_number FROM Course";
              $result = mysqli_query($connection,$query);
              while($row = mysqli_fetch_array($result))
              {
              	$course_id 		= $row["course_id"];
              	$section_number = $row["section_number"];
              	$query2			= "INSERT INTO Taken_Lectures (student_id,course_id,section) values ('1','$course_id','$section_number')";
              }*/
              
              	$query = "SELECT * FROM Lecturer";
              	$result=mysqli_query($connection, $query);
				while($row=mysqli_fetch_array($result))
				{
              echo '
                <tr>
                  <td>'.$row["name"].'</td>
                  <td>'.$row["surname"].'</td>
                  <td>'.$row["department_id"].'</td>
                  <td>'.$row["mail_address"].'</td>
                  <td><a href="action.php?page=deleteLecturerAccount&lecturer_id='.$row["lecturer_id"].'">DELETE</a></td>
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