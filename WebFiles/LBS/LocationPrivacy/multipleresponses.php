<?php
$username   = $_REQUEST["username"];
$locations  = $_REQUEST["locations"];
$radius     = $_REQUEST["radius"];
$searchTerm = $_REQUEST["searchTerm"];
$number = $_REQUEST["number"];
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
    $locationQuery   = "INSERT INTO reqtable (username,reqlat,reqlon,reqtime,radius) VALUES";
    $thirdPartyQuery = "INSERT INTO restable (username,reqtime,reslat,reslon,restime) VALUES";
    for ($x = 0; $x < $arrayLength; $x++) {
        $temp = explode(',', $locationsArray[$x]);
        $lat  = $temp[0];
        $lon  = $temp[1];
        if ($x == 0) {
            $locationQuery .= "";
        } else {
            $locationQuery .= ",";
        }
        $locationQuery .= "('$username','$lat','$lon','$timestamp','$radius')";
        //mysqli_query($con,"INSERT INTO reqtable (username,reqlat,reqlon,reqtime,radius) 
        //		VALUES ('$username','$lat','$lon','$timestamp','$radius')") or die(mysqli_error($con));
        $json = file_get_contents('https://maps.googleapis.com/maps/api/place/radarsearch/json?location=' . $lat . ',' . $lon . '&radius=' . $radius . '&keyword=' . $searchTerm . '&key=AIzaSyCFLGY58ZN5ESpLddviG9o_9TMjkLbWiis');
        $restimestamp       = date("Y-m-d H:i:s");
        $data               = json_decode($json);
        $i                  = 0;
        //$timestamp = date("Y-m-d H:i:s");
        foreach ($data->results as $result) {
            if ($i == 0) {
                $record["name"]   = $searchTerm;
                $record["lat"]    = (string) $result->geometry->location->lat;
                $record["lon"]    = (string) $result->geometry->location->lng;
                $record["reqlat"] = $lat;
                $record["reqlon"] = $lon;
                array_push($response["record"], $record);
                $reslat = $result->geometry->location->lat;
                $reslon = $result->geometry->location->lng;
                if ($y == 0) {
                    $thirdPartyQuery .= "";
                } else {
                    $thirdPartyQuery .= ",";
                }
                $thirdPartyQuery .= "('$username','$timestamp','$reslat','$reslon','$restimestamp')";
                //mysqli_query($con,"INSERT INTO restable (username,reqtime,reslat,reslon,restime) 
                //		VALUES ('$username','$timestamp','$reslat','$reslon','$restimestamp')") or die(mysql_error());
                $i++;
            }
            $y++;
        }
    }
    mysqli_query($con,$locationQuery) or die(mysqli_error($con));
    //echo $locationQuery;
    
    mysqli_query($con,$thirdPartyQuery) or die(mysqli_error($con));
    //echo $thirdPartyQuery;
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