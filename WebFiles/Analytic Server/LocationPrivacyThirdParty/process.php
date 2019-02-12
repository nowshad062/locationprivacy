<?php
$username = $_REQUEST["username"];
$lat = $_REQUEST["lattitude"];
$lon = $_REQUEST["longitude"];
$searchTerm = $_REQUEST["searchTerm"];
$option = $_REQUEST["option"];
$slat = $_REQUEST["slat"];
$slon = $_REQUEST["slon"];
$radius = $_REQUEST["radius"];
require_once __DIR__ . '/db_open.php';
if(isset($username)&&isset($lat)&&isset($lon)&&isset($searchTerm))
{
date_default_timezone_set("America/Chicago");
$timestamp = date("Y-m-d H:i:s");
mysqli_query($con,"INSERT INTO reqtable (username,reallat,reallon,reqtime,slat,slon,opt,radius) 
			VALUES ('$username','$lat','$lon','$timestamp','$slat','$slon','$option','$radius')") or die(mysql_error());
$json = file_get_contents('https://maps.googleapis.com/maps/api/place/nearbysearch/json?location='.$slat.','.$slon.'&rankby=distance&keyword='.$searchTerm.'&key=AIzaSyCFLGY58ZN5ESpLddviG9o_9TMjkLbWiis');
$json1 = file_get_contents('https://maps.googleapis.com/maps/api/place/nearbysearch/json?location='.$lat.','.$lon.'&rankby=distance&keyword='.$searchTerm.'&key=AIzaSyCFLGY58ZN5ESpLddviG9o_9TMjkLbWiis');
$restimestamp = date("Y-m-d H:i:s");
$response = array();
$data = json_decode($json);
$data1 = json_decode($json1);
$i=0;
$j=0;
foreach($data1->results as $result1) {
if($j==0)
    {
        $reallandmarklat = $result1->geometry->location->lat;
        $reallandmarklon = $result1->geometry->location->lng;
        $j++;
    }
}
$response["record"]=array();
$record = array();
foreach($data->results as $result) {
    if(strcmp($option,"no")==0 || strcmp($option,"partial")==0){
    if($i==0){
    $record["name"] =  $result->name ;
    $record["opt"] = $option;
    $record["reallat"] = (string)$lat;
    $record["reallon"] = (string)$lon;
    $record["slat"] = (string)$slat;
    $record["slon"] = (string)$slon;
    $record["reallandmarklat"] = (string)$reallandmarklat;
    $record["reallandmarklon"] = (string)$reallandmarklon;
    $record["reslandmarklat"] =  (string)$result->geometry->location->lat ;
    $record["reslandmarklon"] =  (string)$result->geometry->location->lng ;
    $record["reqtime"] = $timestamp;
    $record["restime"] = $restimestamp;
    array_push($response["record"],$record);
    $reslandmarklat = $result->geometry->location->lat ;
    $reslandmarklon = $result->geometry->location->lng ;
     mysqli_query($con,"INSERT INTO restable (username,opt,reqtime,reallat,reallon,slat,slon,reallandmarklat,reallandmarklon,reslandmarklat,reslandmarklon,restime) 
			VALUES ('$username','$option','$timestamp','$lat','$lon','$slat','$slon','$reallandmarklat','$reallandmarklon','$reslandmarklat','$reslandmarklon','$restimestamp')") or die(mysql_error());
    $i++;
    }
    }
    if(strcmp($option,"complete")==0){
    $record["name"] =  $result->name ;
    $record["opt"] = $option;
    $record["reallat"] = (string)$lat;
    $record["reallon"] = (string)$lon;
    $record["slat"] = (string)$slat;
    $record["slon"] = (string)$slon;
    $record["reallandmarklat"] = (string)$reallandmarklat;
    $record["reallandmarklon"] = (string)$reallandmarklon;
    $record["reslandmarklat"] =  (string)$result->geometry->location->lat ;
    $record["reslandmarklon"] =  (string)$result->geometry->location->lng ;
    $record["reqtime"] = $timestamp;
    $record["restime"] = $restimestamp;
    array_push($response["record"],$record);
    $reslandmarklat = $result->geometry->location->lat ;
    $reslandmarklon = $result->geometry->location->lng ;
     mysqli_query($con,"INSERT INTO restable (username,opt,reqtime,reallat,reallon,slat,slon,reallandmarklat,reallandmarklon,reslandmarklat,reslandmarklon,restime) 
			VALUES ('$username','$option','$timestamp','$lat','$lon','$slat','$slon','$reallandmarklat','$reallandmarklon','$reslandmarklat','$reslandmarklon','$restimestamp')") or die(mysql_error());
        
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