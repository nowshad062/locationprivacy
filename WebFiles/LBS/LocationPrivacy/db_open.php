<?php
	require 'db_config.php';
	$con = mysqli_connect(DB_SERVER,DB_USER,DB_PASSWORD) or die(mysql_error());
	$db = mysqli_select_db($con,DB_DATABASE) or die(mysql_error()) or die(mysql_error()); 
	$mysqli = new mysqli(DB_SERVER,DB_USER,DB_PASSWORD,DB_DATABASE);
?>


