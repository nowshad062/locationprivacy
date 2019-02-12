<?php
require_once __DIR__ . '/db_open.php';
$reqtime = $_REQUEST["reqtime"];
$user = $_REQUEST["user"];
date_default_timezone_set("America/Chicago");
$timestamp = date("Y-m-d H:i:s");
$url = "select username,reqlat,reqlon,reqtime from reqtable where username like '".$user."%' and reqtime like '".$reqtime."%'";
$response = array();
$i=0;
$response["record"]=array();
$record = array();
$search=mysqli_query($con,$url);
	while($result=mysqli_fetch_assoc($search))
	{
       
		
		$record["username"]=$result["username"];
		$record["curlat"]=$result["reqlat"];		
		$record["curlon"]=$result["reqlon"];
        $record["reqtime"]=$result["reqtime"];
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