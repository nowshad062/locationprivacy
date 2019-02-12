<?php
$username = $_REQUEST["username"];
$lat = $_REQUEST["lattitude"];
$lon = $_REQUEST["longitude"];
$searchTerm = $_REQUEST["searchTerm"];
require_once __DIR__ . '/db_open.php';
if(isset($username)&&isset($lat)&&isset($lon)&&isset($searchTerm))
{
date_default_timezone_set("America/Chicago");
$timestamp = date("Y-m-d H:i:s");
mysqli_query($con,"INSERT INTO reqtable (username,reqlat,reqlon,reqtime,radius) 
			VALUES ('$username','$lat','$lon','$timestamp','')") or die(mysql_error());
$json = file_get_contents('https://maps.googleapis.com/maps/api/place/nearbysearch/json?location='.$lat.','.$lon.'&rankby=distance&keyword='.$searchTerm.'&key=AIzaSyCFLGY58ZN5ESpLddviG9o_9TMjkLbWiis');
$restimestamp = date("Y-m-d H:i:s");
$response = array();
$data = json_decode($json);
$i=0;
$response["record"]=array();
$record = array();
foreach($data->results as $result) {
    if($i==0){
    $record["name"] =  $result->name ;
    $record["lat"] =  (string)$result->geometry->location->lat ;
    $record["lon"] =  (string)$result->geometry->location->lng ;
    array_push($response["record"],$record);
    $reslat = $result->geometry->location->lat ;
    $reslon = $result->geometry->location->lng ;
     mysqli_query($con,"INSERT INTO restable (username,reqtime,reslat,reslon,restime) 
			VALUES ('$username','$timestamp','$reslat','$reslon','$restimestamp')") or die(mysql_error());
    $i++;
    }
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