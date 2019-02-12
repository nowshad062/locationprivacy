<?php
header('Access-Control-Allow-Origin: *'); 
$response=array();
$username = $_REQUEST["username"];
$sdate = $_REQUEST["sdate"];
$edate = $_REQUEST["edate"];
$opt = $_REQUEST["option"];
$temp = "";
require_once __DIR__ . '/db_open.php';
if(isset($username))
{
    if($sdate==null && $edate==null && $opt==null)
        $url = "select username,reallat,reallon,reqtime,opt from reqtable where username like '".$username."%' UNION select username,reallat,reallon,reqtime,opt from method4reqtable where username like '".$username."%'";
    else if($sdate==null || $edate==null)
        $url = "select username,reallat,reallon,reqtime,opt from reqtable where username like '".$username."%' AND reqtime like '".$sdate."%' AND opt like '%".$opt."' UNION select username,reallat,reallon,reqtime,opt from method4reqtable where username like '".$username."%' AND reqtime like '".$sdate."%' AND opt like '%".$opt."'";
    else if($sdate==null && $edate==null && $username==null)
        $url = "select username,reallat,reallon,reqtime,opt from reqtable where username like '".$username."%' AND opt like '%".$opt."' UNION select username,reallat,reallon,reqtime,opt from method4reqtable where username like '".$username."%' AND opt like '%".$opt."'";
    else
        $url = "select username,reallat,reallon,reqtime,opt from reqtable where username like '".$username."%' AND opt like '%".$opt."' AND reqtime between '".$sdate."%' and '".$edate."%' UNION select username,reallat,reallon,reqtime,opt from method4reqtable where username like '".$username."%' AND opt like '%".$opt."' AND reqtime between '".$sdate."%' and '".$edate."%'";
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
        $record["opt"]=$result["opt"];
		$record["curlat"]=$result["reallat"];		
		$record["curlon"]=$result["reallon"];
        $record1["opt"]=$result["opt"];
        $record1["lat"]=floatval($result["reallat"]);
        $record1["lng"]=floatval($result["reallon"]);
        $record["reqtime"]=$result["reqtime"];
        array_push($response["record"],$record);
        array_push($response[$temp],$record1);       	               	       
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