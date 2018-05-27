<?php
require_once('functions.php');
if(isset($_COOKIE["user"])) header("location: index.php");
?>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8"/>
<title>Çankaya ATS / Login Page</title>
<link rel="stylesheet" type="text/css" href="css/spinner.css">
<link rel="stylesheet" type="text/css" href="css/responsiveform.css">
<link rel="stylesheet" media="screen and (max-width: 1200px) and (min-width: 601px)" href="css/responsiveform1.css" />
<link rel="stylesheet" media="screen and (max-width: 600px) and (min-width: 351px)" href="css/responsiveform2.css" />
<link rel="stylesheet" media="screen and (max-width: 350px)" href="css/responsiveform3.css" />
<link rel="stylesheet" type="text/css" href="css/style.css">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
</head>
<body>
<?php
	@$page = $_GET["page"];
		switch($page){
			case 'submit':
			lecturer_login($_POST["email"], $_POST["password"]);
			break;
			case 'passwordreset':
			echo '
      <div class="card mb-3">
          <div class="table-responsive">
           
           <form action="login.php?page=checkMail" method="POST">
  <div class="form-group">
    <label for="mail_address">E-Mail Address</label>
    <input type="email" class="form-control" name = "mail" id="mail">
  </div>
  <button type="submit" name="submit" class="btn btn-warning">Reset Password</button>
</form>
      </div>
    </div>
			';
			break;
			case'checkMail':
			global $db;
			$mail_address = $_POST["mail"];
			
			$query	=	"SELECT * FROM Lecturer WHERE mail_address='$mail_address'";
  			$result	=	$db->query($query);
  			if(mysqli_num_rows($result)>0){ 
  			$row = mysqli_fetch_array($result);
  			include('/home/rolatten/public_html/attendancetracking/mail.php');
  			$characters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
            $charactersLength = strlen($characters);
            $random = '';

            for($i = 0; $i < $charactersLength; $i++){
            $random .= $characters[rand(0,$charactersLength-1)];
            }

            $token = md5($random);
            $addQuery = "INSERT INTO Recovery_Keys(user_type, mail_address, token, valid) VALUES('2', '$mail_address', '$token', '1')";
            $add = $db->query($addQuery);
            if($add){
            send_recovery_mail($mail_address, $row["name"], $row["surname"], $token);
            call_loader("A recovery e-mail has been sent to your mail address", "login.php");
            }else{
            call_loader("An error has been occured, please try again.", "login.php");
            }
  			}else{
  			call_loader("There is no account associated with this e-mail", "login.php");
			}			
			break;
			default:
?>
<div class="error-message"><?php if(isset($message)) { echo $message; } ?></div>
<div id="envelope">
<form action="login.php?page=submit" method="post">

<label>Email</label>
<input name="email" placeholder="..@cankaya.edu.tr" type="text"> 
<label>Password</label>
<input name="password" placeholder="* * * * * * * *" type="password">
<input name="checker" type="checkbox" id="checker" value="1"><label>Remember Me</label>
<a href="login.php?page=passwordreset" class="rightf">Forget Password</a><br>
<center><input id="submit" type="submit" value="Login"></center>

</form>
</div>
<?php
		}
?>
</body>
</html>