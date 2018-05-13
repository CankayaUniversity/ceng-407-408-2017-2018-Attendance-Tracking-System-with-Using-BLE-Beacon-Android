<?php include('header.php'); ?>
            <div class="content">
                  <?php
      @$page = $_GET["page"];
      switch($page){
            case 'logout':
                   logout();
            break;
            case 'change_password':
                  @$submit = $_GET["submit"];
                  if($submit == "true"){
                        lecturer_change_password($_POST["old_password"], $_POST["new_password"], $_POST["new_password_repeat"]);
                  }else {
                        ?>
                        <div id="envelope" class="left-align">
                              <form action="index.php?page=change_password&submit=true" method="post">
                              <label>Old password</label>
                              <input type="password" placeholder="* * * * * * * *" name="old_password"/>
                              <label>New password</label>
                              <input type="password" placeholder="* * * * * * * *" name="new_password"/>
                              <label>Re-enter new password</label>
                              <input type="password" placeholder="* * * * * * * *" name="new_password_repeat"/>
                              <center><input id="submit" type="submit" value="Submit"></center>
                              </form>
                        </div>
                        <?php
                  }
            break;
            case 'upload_student_list':
                  @$submit = $_GET["submit"];
                  @$action = $_GET["action"];
                  if($submit == "true"){
					  $path = $_FILES["student_list"]["name"];
					  $ext = pathinfo($path, PATHINFO_EXTENSION);
					  if($ext == "xlsx" || $ext == "xls")
						  parse_xls($_FILES["student_list"]["tmp_name"]);
					  else {
							call_loader("Student list must have '.xls' or '.xlsx' extension", "index.php?page=upload_student_list");
					  }
                  }else if(isset($action)){
                        save_student_list($_POST["json"]);
                  }
                  else{
                        ?>
                        <div id="envelope">
                              <form action="index.php?page=upload_student_list&submit=true" method="post" enctype="multipart/form-data">
            
                              <center><input type="file" name="student_list"/></center>
                              <center><input type="submit" name="submit" value="Save"/></center>
                              </form>
                        </div>
                        
                        <?php
                  }
            break;
            case 'report_interface':
            @$classroom = $_GET["classroom"];

            if(!isset($classroom)){


            
                  //course_list();
                  ?>
                  <div id="calendar">
                       <script type="text/javascript">
                              $(document).ready(function() {
                                    $('#calendar').fullCalendar({
                                          contentHeight: 800,
                                          firstDay: 1,
                                          defaultDate: '2018-05-01',
                                          businessHours: true,
                                          eventLimit: 9,
                                          axisFormat: 'HH:mm',
                                          timeFormat: 'HH:mm',
                                          minTime: 0,
                                          maxTime: 24,
                                          events: [

                                                <?php lecture_calendar(); ?>
                                              

                                          ]
                                    });
                              });

                        </script> 
                       
                  </div>

            <?
            }
            else
            {
                  @$action = $_GET["action"];
                  switch ($action) {
                        case 'update':
                              if(isset($action)){
                                    set_attended($classroom, $_POST["student_id"]);
                                    get_attended_student_list($classroom);
                              }
                              break;
                        case 'delete':
                              if(isset($action)){
                                    delete_attended($classroom);
                                    header("location:index.php?page=report_interface");
                              }
                              break;
                        
                        default:
                        get_attended_student_list($classroom);
                  }
                  
            }
            break;
            default:
            echo "<center>Welcome to the Attendance Tracking System</center>";
                        
      }
?>
            </div>
<?php include("footer.php"); ?>
