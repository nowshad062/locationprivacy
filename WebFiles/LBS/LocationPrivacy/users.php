<?php
require_once __DIR__ . '/db_open.php';

date_default_timezone_set("America/Chicago");
$timestamp = date("Y-m-d H:i:s");
$url = "select username from users";
$response = array();
$i=0;
$response["record"]=array();
$record = array();
$search=mysqli_query($con,$url);
	while($result=mysqli_fetch_assoc($search))
	{
       
		
		$record["username"]=$result["username"];
        array_push($response["record"],$record);
        	       
    }
if(empty($response))
	{
		$response["status"]="400";
		$response["message"]="Something went wrong, please try again.";
		echo json_encode($response);	
	}
	else
	{
        $response["status"]="200";
        $response["message"]="success";
        echo json_encode($response);
	}    
require_once __DIR__ . '/db_close.php';
?>