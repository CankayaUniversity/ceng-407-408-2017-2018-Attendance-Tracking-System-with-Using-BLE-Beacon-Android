<?php 
require_once("functions.php");
check_login(); 
?>
<!DOCTYPE html>
<html lang="en">
<head>
      <meta charset="UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <meta http-equiv="X-UA-Compatible" content="ie=edge">
      <title>Çankaya Attendance Tracking System</title>
      <link rel="shortcut icon" type="image/x-icon" href="img/favicon.ico">
      <link rel="stylesheet" type="text/css" href="css/responsiveform.css">
      <link rel="stylesheet" type="text/css" href="css/spinner.css">
      <link rel="stylesheet" media="screen and (max-width: 1200px) and (min-width: 601px)" href="css/responsiveform1.css" />
      <link rel="stylesheet" media="screen and (max-width: 600px) and (min-width: 351px)" href="css/responsiveform2.css" />
      <link rel="stylesheet" media="screen and (max-width: 350px)" href="css/responsiveform3.css" />
      <link rel="stylesheet" href="css/style.css">
      <link rel="stylesheet" href="css/popup.css">
      <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css">
      <link href="https://fonts.googleapis.com/css?family=Montserrat:400,600" rel="stylesheet">

      <link href='calendar/fullcalendar.min.css' rel='stylesheet' />
      <link href='calendar/fullcalendar.print.min.css' rel='stylesheet' media='print' />
      <script src='calendar/moment.min.js'></script>
      <script src='calendar/jquery.min.js'></script>
      <script src='calendar/fullcalendar.min.js'></script>
      <!--<script src="https://code.jquery.com/jquery-3.3.1.js"></script>-->

<style>

  #calendar {
    max-width: 900px;
    margin: 0 auto;
  }

</style>
</head>
<body>
            <div class="wrapper">
            <header>
                  <nav>
                        <div class="menu-icon">
                              <i class="fa fa-bars fa-2x"></i>
                        </div>
                        <?php include("logo.php"); ?>
                        <?php include("menu.php"); ?>
                  </nav>
            </header>