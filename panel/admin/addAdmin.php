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
        <li class="breadcrumb-item active">Add Admin</li>
      </ol>
     
      <!-- Example DataTables Card-->
      <div class="card mb-3">
        <div class="card-header">
          <i class="fa fa-table"></i> Add Admin</div>
        <div class="card-body">
          <div class="table-responsive">
           
           <form action="action.php?page=createNewAdmin" method="POST">
  <div class="form-group">
    <label for="name">Name</label>
    <input type="text" class="form-control" name = "name" id="name">
  </div>
  <div class="form-group">
    <label for="surname">Surname</label>
    <input type="text" class="form-control" name = "surname" id="surname">
  </div>
  <div class="form-group">
    <label for="surname">Username</label>
    <input type="text" class="form-control" name = "username" id="username">
  </div>
  <div class="form-group">
    <label for="mail_address">E-Mail Address</label>
    <input type="email" class="form-control" name = "mail_address" id="mail_address">
  </div>
  <div class="form-group">
    <label for="password">Password:</label>
    <input type="password" class="form-control" id="password" name="password">
  </div>
  <button type="submit" name="submit" class="btn btn-danger">Add Admin</button>
</form>
           
          </div>
        </div>
        
      </div>
    </div>';
    
    include("footer.php");
    
    ?>