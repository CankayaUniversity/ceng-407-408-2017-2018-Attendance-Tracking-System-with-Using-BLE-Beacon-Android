<?php
include ("functions.php");
include ("header.php");

$issueID = $_GET["issueID"];
$statusGET = $_GET["status"];
if($statusGET!=""){
	$query = "UPDATE Issues SET status=$statusGET WHERE issue_id='$issueID'";
	$query = mysqli_query($connection,$query);
	header('Location : showReport.php?issueID='.$issueID.'');
}
  echo '<div class="content-wrapper">
    <div class="container-fluid">
      <!-- Breadcrumbs-->
      <ol class="breadcrumb">
        <li class="breadcrumb-item">
          <a href="#">Homepage</a>
        </li>
        <li class="breadcrumb-item active">Show Reports</li>
      </ol>
     
      <!-- Example DataTables Card-->
      <div class="card mb-3">
        <div class="card-header">
          <i class="fa fa-table"></i>Show Reports</div>
        <div class="card-body">
          <div class="table-responsive">';
            
            $query = "SELECT * FROM Issues WHERE issue_id='$issueID'";
            $row = mysqli_fetch_array(mysqli_query($connection,$query));
            $status = $row["status"];
           
            
          echo '</div>';
          
           echo '
            <div class="row">
    <div class="col-sm-2"><b>Sender : </b></div>
    <div class="col-sm-10"><b>'.$row["sender_mail"].'</b></div>
  </div>
  <hr>
  <div class="row">
    <div class="col-sm-2"><b>Subject : </b></div>
    <div class="col-sm-10"><b>'.$row["subject"].'</b></div>
  </div>
  <hr>
  <div class="row">
    <div class="col-sm-2"><b>Messages : </b></div>
    <div class="col-sm-10"><b>'.$row["message"].'</b></div>
    </div><hr>
  ';
  echo '  <div class="row">
    <div class="col-sm-2"><b>Status</b></div>
    <div class="col-sm-10">';
  				  if($status == 0)
                  echo '<button type="button" class="btn btn-danger">Not Reviewed</button></td>';
                  else if($status == 1)
                  echo '<button type="button" class="btn btn-warning">Inspecting</button></td>';
                  else
                  echo '<button type="button" class="btn btn-success">Solved</button>';
          echo' </div>
          </div><hr>
          <div class="row">
          	<div class="col-sm-12"><b><center>UPDATE STATUS</center></b></div>
          </div><hr>
          <div class="row">
          	<div class="col-sm-4"><a href="showReport.php?issueID='.$issueID.'&status=0"><center><button type="button" class="btn btn-danger">Not Reviewed</button></center></a></div>
          	<div class="col-sm-4"><a href="showReport.php?issueID='.$issueID.'&status=1"><center><button type="button" class="btn btn-warning">Inspecting</button></center></a></div>
          	<div class="col-sm-4"><a href="showReport.php?issueID='.$issueID.'&status=2"><center><button type="button" class="btn btn-success">Solved</button></center></a></div>
          </div>
        </div>
        ';
        
        
        echo '
      </div>
    </div>';
    //<!-- /.container-fluid-->
    //<!-- /.content-wrapper-->
    
include ("footer.php");
   ?>