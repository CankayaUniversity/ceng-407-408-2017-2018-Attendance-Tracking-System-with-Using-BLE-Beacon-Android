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
                  if($submit == "true"){
                        parse_xls($_FILES["student_list"]["tmp_name"]);
                  }else{
                        ?>
                        <div id="envelope">
                              <form action="index.php?page=upload_student_list&submit=true" method="post" enctype="multipart/form-data">
                              <label>Student list</label>
                              <input type="file" name="student_list"/>
                              <center><input type="submit" name="submit" value="Save"/>
                              </form>
                        </div>
                        <?php
                  }
            break;
            default:
            echo "<center>Welcome to the Attendance Tracking System</center>";
      }
?>
            </div>
<?php include("footer.php"); ?>
