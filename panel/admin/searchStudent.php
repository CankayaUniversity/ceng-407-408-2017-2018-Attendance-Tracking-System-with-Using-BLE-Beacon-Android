<?php
           
include ("functions.php");


include ("header.php");
 echo' <div class="content-wrapper">
    <div class="container-fluid">
      <!-- Breadcrumbs-->
      <ol class="breadcrumb">
        <li class="breadcrumb-item">
          <a href="#">Homepage</a>
        </li>
        <li class="breadcrumb-item active">Students</li>
      </ol>
     
      <!-- Example DataTables Card-->
      <div class="card mb-3">
        <div class="card-header">
          <i class="fa fa-table"></i> Search Student</div>
        <div class="card-body">
          <div class="table-responsive">
            <table class="table table-bordered" id="dataTable" width="100%" cellspacing="0">
              <thead>
                <tr>
                  <th>Student ID</th>
                  <th>Name</th>
                  <th>Surname</th>
                  <th>Mail Address</th>
                  <th>Functions</th>
                </tr>
              </thead>
             

				

              <tfoot>
                <tr>
                  <th>Student ID</th>
                  <th>Name</th>
                  <th>Surname</th>
                  <th>Mail Address</th>
                  <th>Functions</th>
                </tr>
              </tfoot>
              
              <tbody>';
              	$query = "SELECT * FROM Student";
              	$result=mysqli_query($connection, $query);
				while($row=mysqli_fetch_array($result))
				{
              echo '
                <tr>
                  <td>'.$row["student_number"].'</td>
                  <td>'.$row["name"].'</td>
                  <td>'.$row["surname"].'</td>
                  <td>'.$row["mail_address"].'</td>
                  <td><a href="action.php?page=deleteStudentAccount&student_number='.$row["student_number"].'">DELETE</a></td>
                </tr>';
                }

             echo' </tbody>
            </table>
          </div>
        </div>
        
      </div>
    </div>';
    include("footer.php")
    
?>