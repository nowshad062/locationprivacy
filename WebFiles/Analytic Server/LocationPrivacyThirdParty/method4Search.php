<?php
header('Access-Control-Allow-Origin: *'); 
$response=array();
$username = $_REQUEST["username"];
$sdate = $_REQUEST["sdate"];
$edate = $_REQUEST["edate"];
$temp = "";
require_once __DIR__ . '/db_open.php';
if(isset($username))
{
    if($sdate==null && $edate==null)
        $url = "select username,reqtime,reallat,reallon,fakelocationscount from method4reqtable where username like '".$username."%' order by reqtime";
    else if($sdate==null || $edate==null)
        $url = "select username,reqtime,reallat,reallon,fakelocationscount from method4reqtable where username like '".$username."%' AND reqtime like '".$sdate."%' order by reqtime";
    else
        $url = "select username,reqtime,reallat,reallon,fakelocationscount from method4reqtable where username like '".$username."%' AND reqtime between '".$sdate."%' and '".$edate."%' order by reqtime";
    $response["record"]=array();
	$record=array();
	$search=mysqli_query($con,$url);
    while($result=mysqli_fetch_assoc($search))
	{
        $record["user"]=$result["username"];
        $record["time"]=$result["reqtime"];
        $record["reallat"]=$result["reallat"];
        $record["reallon"]=$result["reallon"];
        $record["fakelocationscount"]=$result["fakelocationscount"];
        array_push($response["record"],$record);     	               	       
    }
            	       
	if(empty($response))
	{
                  	       
        $response["status"]="400";
        $response["message"]="No users exist";
        echo json_encode($response);
        //echo json_encode($webresponse);        	
	}
    
	else
	{
        $response["status"]="200";
		$response["message"]="Success";
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