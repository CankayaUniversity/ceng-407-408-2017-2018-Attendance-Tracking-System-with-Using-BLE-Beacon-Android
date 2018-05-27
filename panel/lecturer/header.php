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
      
			<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">

<!-- jQuery library -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>

<!-- Latest compiled JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
			
      <link rel="stylesheet" href="css/style.css">
      <link rel="stylesheet" href="css/popup.css">
      <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css">
      <link href="https://fonts.googleapis.com/css?family=Montserrat:400,600" rel="stylesheet">

      <link href='calendar/fullcalendar.min.css' rel='stylesheet' />
      <link href='calendar/fullcalendar.print.min.css' rel='stylesheet' media='print' />
      <script src='calendar/moment.min.js'></script>
      <script src='calendar/jquery.min.js'></script>
      <script src='calendar/fullcalendar.min.js'></script>
      <script src='js/main.js'></script>
      <!--<script src="https://code.jquery.com/jquery-3.3.1.js"></script>-->



<link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
  
 
  <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
  <script>
  $( function() {
    $( document ).tooltip({
    
      track: true
    });
  } );
  $(function () {
      $(document).tooltip({
          content: function () {
              return $(this).prop('title');
          }
      });
  });

</script>

<style>


	#calendar {
    	max-width: 1100px;
   		margin: 0 auto;
	}
	#rankSubMenu {
    	display: none; 
    	position: absolute; 
    	background-color: lightblue;
    	left: 10;
	}

.ui-draggable, .ui-droppable {
	background-position: top;
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