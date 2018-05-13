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
        <li class="breadcrumb-item active">Add Schedule</li>
      </ol>
     
      <!-- Example DataTables Card-->
      <div class="card mb-3">
        <div class="card-header">
          <i class="fa fa-table"></i> Add Schedule</div>
        <div class="card-body">
          <div class="table-responsive">
           
           <form action="action.php?page=addSchedule" method="POST">
           <div class="form-group">
    <label for="surname">Course</label>
    <div class="form-group">
           <select class="form-control" id="course_id" name="course_id">';
  			
    			$query = "SELECT * FROM Course";
              	$result=mysqli_query($connection, $query);
				while($row=mysqli_fetch_array($result))
				{
				echo '<option>'.$row["course_id"].' - '.$row["course_name"].'</option>';
				}
				
 echo' </select>
 
</div>
  </div>
  <div class="form-group">
    <label for="name">Section</label>
    <input type="text" class="form-control" name = "section" id="section">
  </div>
     <div class="form-group">
    <label for="surname">Week day</label>
    <div class="form-group">
           <select class="form-control" id="week_day" name="week_day">
           <option id="Monday">Monday</option>
           <option id="Tuesday">Tuesday</option>
           <option id="Wednesday">Wednesday</option>
           <option id="Thursday">Thursday</option>
           <option id="Friday">Friday</option>
           <option id="Saturday">Saturday</option>
           <option id="Sunday">Sunday</option>
				</select>
 
</div>
  </div>
  <div class="form-group">
    <label for="surname">Start Hours</label>
    <div class="form-group">
           <select class="form-control" id="start" name="start">
           <option id="09:20">09:20</option>
           <option id="10:20">10:20</option>
           <option id="11:20">11:20</option>
           <option id="12:20">12:20</option>
           <option id="13:20">13:20</option>
           <option id="14:20">14:20</option>
           <option id="15:20">15:20</option>
           <option id="16:20">16:20</option>
				</select>
 
</div>
  </div>
   <div class="form-group">
    <label for="surname">Finish Hours</label>
    <div class="form-group">
           <select class="form-control" id="finish" name="finish">
           <option id="10:10">10:10</option>
           <option id="11:10">11:10</option>
           <option id="12:10">12:10</option>
           <option id="13:10">13:10</option>
           <option id="14:10">14:10</option>
           <option id="15:10">15:10</option>
           <option id="16:10">16:10</option>
           <option id="17:10">17:10</option>
				</select>
 
</div>
  </div>
 
  <button type="submit" name="submit" class="btn btn-danger">Add Schedule</button>
</form>
           
          </div>
        </div>
        
      </div>
    </div>';
    include("footer.php");
    ?>