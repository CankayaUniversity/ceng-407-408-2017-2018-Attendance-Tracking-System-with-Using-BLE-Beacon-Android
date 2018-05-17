<?php
include 'classes/class.phpmailer.php';
function send_recovery_mail($to, $name, $surname, $token){
$mail = new PHPMailer();
$mail->IsSMTP();
$mail->SMTPAuth = true;
$mail->Host = 'smtp.attendancesystem.xyz';
$mail->Port = 587;
$mail->Username = 'noreply@attendancesystem.xyz';
$mail->Password = 'Meloteam06';
$mail->SetFrom($mail->Username, 'Attendance Tracking System');
$mail->AddAddress($to, $name." ".$surname);
$mail->CharSet = 'UTF-8';
$mail->Subject = 'Reset Password';
$secure_mail = str_replace("@", "__at__", $to);
$mail->MsgHTML('
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>

<head>

    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

    <title>Forgot Password</title>

    <style>

        body {

            background-color: #FFFFFF; padding: 0; margin: 0;

        }

    </style>

</head>

<body style="background-color: #FFFFFF; padding: 0; margin: 0;">

<table border="0" cellpadding="0" cellspacing="10" height="100%" bgcolor="#FFFFFF" width="100%" style="max-width: 650px;" id="bodyTable">

    <tr>

        <td align="center" valign="top">

            <table border="0" cellpadding="0" cellspacing="0" width="100%" id="emailContainer" style="font-family:Arial; color: #333333;">

                <!-- Logo -->

                <tr>

                    <td align="left" valign="top" colspan="2" style="border-bottom: 1px solid #CCCCCC; padding-bottom: 10px;">

                        <center><img alt="Attendance Tracking" border="0" src="http://attendancesystem.xyz/attendancetracking/assets/logo.png" title="Attendance Tracking" class="sitelogo" width="60%" style="max-width:250px;" /></center>

                    </td>

                </tr>

                <!-- Title -->

                <tr>

                    <td align="left" valign="top" colspan="2" style="border-bottom: 1px solid #CCCCCC; padding: 20px 0 10px 0;">

                        <span style="font-size: 18px; font-weight: normal;">FORGOT PASSWORD</span>

                    </td>

                </tr>

                <!-- Messages -->

                <tr>

                    <td align="left" valign="top" colspan="2" style="padding-top: 10px;">

                        <span style="font-size: 12px; line-height: 1.5; color: #333333;">
						Dear '.$name.' '.$surname.'<br/><br/>

                            We have sent you this email in response to your request to reset your password on Attendance Tracking System. 

                            <br/><br/>

                            To reset your password for Attendance Tracking System, please follow the link below:<br/>
							
                            <a href="http://attendancesystem.xyz/attendancetracking/recovery.php?mail='.$secure_mail.'&token='.$token.'">Link</a>

                            <br/><br/>
							If your browser does not support this link, please copy the token below and enter it from the web page below.
							<br/>
							Token :  '.$token.'<br/>
							<a href="http://attendancesystem.xyz/attendancetracking/recovery.php">Link</a>
							<br/> <br/>

                            We recommend that you keep your password secure and not share it with anyone.If you feel your password has been compromised, you can change it by going to settings menu on your Android application.

                            <br/><br/><br/>
							Attendance Tracking System Development Team
                       

                        </span>

                    </td>

                </tr>

            </table>

        </td>

    </tr>

</table>

</body>

</html>');
$mail->send();
}

function send_register_validation_mail($to, $name, $surname){
	
}


?>