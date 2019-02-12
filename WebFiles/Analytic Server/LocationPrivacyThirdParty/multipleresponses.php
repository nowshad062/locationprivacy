<?php
$username   = $_REQUEST["username"];
$locations  = $_REQUEST["locations"];
$radius     = $_REQUEST["radius"];
$searchTerm = $_REQUEST["searchTerm"];
$reallat = $_REQUEST["reallat"];
$reallon = $_REQUEST["reallon"];
$opt = "method4";
$reallandmarklat = 0.0;
$reallandmarklon = 0.0;
require_once __DIR__ . '/db_open.php';
if (isset($locations) && isset($username) && isset($searchTerm) && isset($radius)) {
    $locationsArray = explode(':', $locations);
    //echo $locationsArray;
    $response           = array();
    $response["record"] = array();
    $record             = array();
    $y                  = 0;
    date_default_timezone_set("America/Chicago");
    $timestamp       = date("Y-m-d H:i:s");
    $arrayLength     = count($locationsArray);
    $locationReqTableQuery   = "INSERT INTO method4reqtable (username, reallat, reallon, reqtime, opt, fakelocationscount) VALUES ('$username','$reallat','$reallon','$timestamp','$opt','$arrayLength')";
    $fakeLocTableQuery = "INSERT INTO fakelocation (username, reqtime, fakelat, fakelon) VALUES";
        
    for ($x = 0; $x < $arrayLength; $x++) {                                                                                      
    	$temp = explode(',', $locationsArray[$x]);
        $lat  = $temp[0];
        $lon  = $temp[1];
        if ($x == 0) {
            $fakeLocTableQuery .= "";
        } else {
            $fakeLocTableQuery .= ",";
        }
        $fakeLocTableQuery .= "('$username','$timestamp','$lat','$lon')";
        //mysqli_query($con,"INSERT INTO reqtable (username,reqlat,reqlon,reqtime,radius) 
        //		VALUES ('$username','$lat','$lon','$timestamp','$radius')") or die(mysqli_error($con));
        
    }
    mysqli_query($con,$locationReqTableQuery) or die(mysqli_error($con));
    echo $locationReqTableQuery;
    
    mysqli_query($con,$fakeLocTableQuery) or die(mysqli_error($con));
    echo $fakeLocTableQuery;
    if (empty($response)) {
        $response["status"]  = "400";
        $response["message"] = "Something went wrong, please try again.";
        echo json_encode($response);
    } else {
        $response["status"]  = "200";
        $response["message"] = "success";
        echo json_encode($response);
    }
} else {
    $response["status"]  = "400";
    $response["message"] = "Missing Values";
    echo json_encode($response);
}
require_once __DIR__ . '/db_close.php';
?>