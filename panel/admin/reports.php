<?php
include ("functions.php");
include ("header.php");
  echo '<div class="content-wrapper">
    <div class="container-fluid">
      <!-- Breadcrumbs-->
      <ol class="breadcrumb">
        <li class="breadcrumb-item">
          <a href="#">Homepage</a>
        </li>
        <li class="breadcrumb-item active">Reports</li>
      </ol>
     
      <!-- Example DataTables Card-->
      <div class="card mb-3">
        <div class="card-header">
          <i class="fa fa-table"></i> Reports</div>
        <div class="card-body">
          <div class="table-responsive">
            <table class="table table-bordered" id="dataTable" width="100%" cellspacing="0">
              <thead>
                <tr>
                  <th>Sender</th>
                  <th>Report Subject</th>
                  <th>Status</th>
                  <th>Functions</th>
                </tr>
              </thead>
             

				
              <tfoot>
                <tr>
                  <th>Sender</th>
                  <th>Report Subject</th>
                  <th>Status</th>
                  <th>Functions</th>
                </tr>
              </tfoot>
              
              <tbody>';
              	$query = "SELECT * FROM Issues ";
              	$result=mysqli_query($connection, $query);
				while($row=mysqli_fetch_array($result))
				{
				$status = $row["status"];
              echo '
                <tr>
                  <td>'.$row["sender_mail"].'</td>
                  <td>'.$row["subject"].'</td>
                  <td>';
                  
                  if($status == 0)
                  echo '<button type="button" class="btn btn-danger">Not Reviewed</button></td>';
                  else if($status == 1)
                  echo '<button type="button" class="btn btn-warning">Inspecting</button></td>';
                  else
                  echo '<button type="button" class="btn btn-success">Solved</button>';
                  
                  echo '<td><a href="showReport.php?issueID='.$row["issue_id"].'">Show Detail</a></td>
                </tr>';
                
                echo '';
                }


            echo'  </tbody>
            </table>
          </div>
        </div>
        
      </div>
    </div>';
    //<!-- /.container-fluid-->
    //<!-- /.content-wrapper-->
    
include ("footer.php");
   ?>