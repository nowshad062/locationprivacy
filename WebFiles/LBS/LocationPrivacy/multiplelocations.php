<?php
$username = $_REQUEST["username"];
$lat = $_REQUEST["lattitude"];
$lon = $_REQUEST["longitude"];
$radius = $_REQUEST["radius"];
$searchTerm = $_REQUEST["searchTerm"];
require_once __DIR__ . '/db_open.php';
if(isset($lat)&&isset($lon)&&isset($searchTerm)&&isset($radius))
{
date_default_timezone_set("America/Chicago");
$timestamp = date("Y-m-d H:i:s");
mysqli_query($con,"INSERT INTO reqtable (username,reqlat,reqlon,reqtime,radius) 
			VALUES ('$username','$lat','$lon','$timestamp','$radius')") or die(mysql_error());
$json = file_get_contents('https://maps.googleapis.com/maps/api/place/radarsearch/json?location='.$lat.','.$lon.'&radius='.$radius.'&keyword='.$searchTerm.'&key=AIzaSyCFLGY58ZN5ESpLddviG9o_9TMjkLbWiis');
$restimestamp = date("Y-m-d H:i:s");
$response = array();
$data = json_decode($json);
$i=0;
$response["record"]=array();
$record = array();    
//$timestamp = date("Y-m-d H:i:s");
foreach($data->results as $result) {
    $record["name"] = $searchTerm ;
    $record["lat"] =  (string)$result->geometry->location->lat ;
    $record["lon"] =  (string)$result->geometry->location->lng ;
    array_push($response["record"],$record);
    $reslat = $result->geometry->location->lat ;
    $reslon = $result->geometry->location->lng ;
     mysqli_query($con,"INSERT INTO restable (username,reqtime,reslat,reslon,restime) 
			VALUES ('$username','$timestamp','$reslat','$reslon','$restimestamp')") or die(mysql_error());
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
}
else
{
	$response["status"]="400";
	$response["message"]="Missing Values";
	echo json_encode($response);
}
require_once __DIR__ . '/db_close.php';
?>