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
        $url = "select username,reqlat,reqlon,reqtime from reqtable where username like '".$username."%' order by reqtime";
    else if($sdate==null || $edate==null)
        $url = "select username,reqlat,reqlon,reqtime from reqtable where username like '".$username."%' AND reqtime like '".$sdate."%' order by reqtime";
    else
        $url = "select username,reqlat,reqlon,reqtime from reqtable where username like '".$username."%' AND reqtime between '".$sdate."%' and '".$edate."%' order by reqtime";
    $response["record"]=array();
      $response["search"]=array();
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
        $temp = $result["username"];
		//$response[$temp]=array();
        $record["username"]=$result["username"];
		$record["curlat"]=$result["reqlat"];		
		$record["curlon"]=$result["reqlon"];
        $record1["lat"]=floatval($result["reqlat"]);
        $record1["lng"]=floatval($result["reqlon"]);
        $record["reqtime"]=$result["reqtime"];
        $record1["reqtime"]=$result["reqtime"];
        //$response["search"]=$response[$temp];
        //array_push($response["search"],$response[$temp]);
        array_push($response["record"],$record);
        array_push($response[$temp],$record1);       	               	       
    }
            	       
	if(empty(!$response))
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