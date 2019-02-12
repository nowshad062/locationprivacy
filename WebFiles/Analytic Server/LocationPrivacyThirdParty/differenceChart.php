<?php
header('Access-Control-Allow-Origin: *'); 
$response=array();
$username = $_REQUEST["username"];
$sdate = $_REQUEST["sdate"];
$edate = $_REQUEST["edate"];
$opt = $_REQUEST["option"];
$rt = $_REQUEST["reqtime"];
$temp = "";
$tempDist = 0;
require_once __DIR__ . '/db_open.php';
if(isset($username))
{
    if($username!=null && $rt!=null)
        $url = "select username,opt,reqtime,reallat,reallon,slat,slon,reallandmarklat,reallandmarklon,reslandmarklat,reslandmarklon,restime from restable where username = '".$username."' AND reqtime = '".$rt."'";
    else if($sdate==null && $edate==null && $opt==null)
        $url = "select username,opt,reqtime,reallat,reallon,slat,slon,reallandmarklat,reallandmarklon,reslandmarklat,reslandmarklon,restime from restable where username like '".$username."%'";
    else if($sdate==null || $edate==null)
        $url = "select username,opt,reqtime,reallat,reallon,slat,slon,reallandmarklat,reallandmarklon,reslandmarklat,reslandmarklon,restime from restable where username like '".$username."%' AND reqtime like '".$sdate."%' AND opt like '%".$opt."'";
    else if($sdate==null && $edate==null && $username==null)
        $url = "select username,opt,reqtime,reallat,reallon,slat,slon,reallandmarklat,reallandmarklon,reslandmarklat,reslandmarklon,restime from restable where username like '".$username."%' AND opt like '%".$opt."'";
    else
        $url = "select username,opt,reqtime,reallat,reallon,slat,slon,reallandmarklat,reallandmarklon,reslandmarklat,reslandmarklon,restime from restable where username like '".$username."%' AND opt like '%".$opt."' AND reqtime between '".$sdate."%' and '".$edate."%'";
    $response["record"]=array();
    $response["data"]=array();
    // $webresponse[""]=array();
		$record=array();
        $record1=array();
	$search=mysqli_query($con,$url);
	while($result1=mysqli_fetch_assoc($search))
	{     		
        $temp = $result1["username"];
		$response[$temp]=array();
    }
    $search1=mysqli_query($con,$url);
    while($result=mysqli_fetch_assoc($search1))
	{     		
    $record["username"] =  $result["username"] ;
    $record["opt"] = $result["opt"];
    $record["reqtime"] = $result["reqtime"];
    $record["reallat"] = $result["reallat"];
    $record["reallon"] = $result["reallon"];
    $record["slat"] = $result["slat"];
    $record["slon"] = $result["slon"];
    $theta = $result["reallon"] - $result["slon"];
    $dist = sin(deg2rad($result["reallat"])) * sin(deg2rad($result["slat"])) +  cos(deg2rad($result["reallat"])) * cos(deg2rad($result["slat"])) * cos(deg2rad($theta));
    $dist = acos($dist);
    $dist = rad2deg($dist);
    $miles = $dist * 60 * 1.1515;
    //$unit = strtoupper($unit);
    $record["diff1"] = (string)$miles;
    $record["reallandmarklat"] = $result["reallandmarklat"];
    $record["reallandmarklon"] = $result["reallandmarklon"];
    $record["reslandmarklat"] =  $result["reslandmarklat"] ;
    $record["reslandmarklon"] =  $result["reslandmarklon"] ;
    $theta1 = $result["reallandmarklon"] - $result["reslandmarklon"];
    $dist1 = sin(deg2rad($result["reallandmarklat"])) * sin(deg2rad($result["reslandmarklat"])) +  cos(deg2rad($result["reallandmarklat"])) * cos(deg2rad($result["reslandmarklat"])) * cos(deg2rad($theta1));
    $dist1 = acos($dist1);
    $dist1 = rad2deg($dist1);
    $miles1 = $dist1 * 60 * 1.1515;
    $record["diff2"] = (string)$miles1;
    $record["restime"] = $result["restime"];
    if($record["opt"]=="partial"){
        $record1["x"] = round($miles,2);
        $record1["y"] = round($miles1,2);
    }
    //if($tempDist < $miles1){
      //  unset($response["record"]);
        //$response["record"]=array();
       array_push($response["record"],$record);   
   // }
    //$tempDist = $miles1;
    array_push($response["data"],$record1);       	               	       
    }
            	       
	if(empty($response))
	{
                  	       
        $response["status"]="200";
        $response["message"]="success";
        echo json_encode($response);
        //echo json_encode($webresponse);        	
	}
    
	else
	{
        $response["status"]="400";
		$response["message"]="No users exist";
		echo json_encode($response);
       // echo json_encode($webresponse);
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