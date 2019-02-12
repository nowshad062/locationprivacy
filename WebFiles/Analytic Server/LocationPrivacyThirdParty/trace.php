<?php
header('Access-Control-Allow-Origin: *'); 
require_once __DIR__ . '/db_open.php';
$username = $_REQUEST["username"];
date_default_timezone_set("America/Chicago");
$timestamp = date("Y-m-d H:i:s");
$url = "select reqlat, reqlon from reqtable where username = '".$username."'";
$response = array();
$i=0;
$response["list"]=array();
$record = array();
$search=mysqli_query($con,$url);
	while($result=mysqli_fetch_assoc($search))
	{
       $record["label"]=$result["reqlat"];
        $record["y"]=floatval($result["reqlon"]);
        array_push($response["list"],$record);    	       
    }
if(empty($response))
	{
		$response["status"]="400";
		$response["message"]="Something went wrong, please try again.";
		echo json_encode($response);	
	}
	else
	{
       // $response["status"]="200";
        //$response["message"]="success";
        echo json_encode($response);
	}    
require_once __DIR__ . '/db_close.php';
?>