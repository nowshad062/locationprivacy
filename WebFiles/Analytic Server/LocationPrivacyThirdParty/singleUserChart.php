<?php
header('Access-Control-Allow-Origin: *'); 
$response=array();
$username = $_REQUEST["username"];
require_once __DIR__ . '/db_open.php';
if(isset($username))
{
    $url = "select username,opt,reqtime from reqtable where username = '".$username."' UNION select username,opt,reqtime from method4reqtable where username = '".$username."'";
    $response["record"]=array();
    $record=array();
    $search1=mysqli_query($con,$url);
    while($result=mysqli_fetch_assoc($search1))
	{     		
        $temp=str_replace("-",",",$result["reqtime"]);
        $temp1=str_replace(" ",",",$temp);
        $temp2='new Date('.str_Replace(":",",",$temp1).')';
        $dateTime = $result["reqtime"];//'2012-12-31 23:59:59';
        $unixTime = new DateTime($dateTime);
        $record["x"] =  $unixTime->getTimestamp() * 1000;
        //$record["x"]=($temp2);
        //$record["x"]=$result["reqtime"];
        if($result["opt"]=="no")
            $record["y"]=0;
        if($result["opt"]=="partial")
            $record["y"]=1;
        if($result["opt"]=="complete")
            $record["y"]=2;
        if($result["opt"]=="method4")
            $record["y"]=3;
        array_push($response["record"],$record);
    }
            	       
	if(empty($response))
	{
                  	       
        $response["status"]="400";
        $response["message"]="No users exist";        
        echo json_encode($response);        
	}
    
	else
	{
        $response["status"]="200";
		$response["message"]="Success";		        
        $str1 = json_encode($response);
        //$str2 = str_Replace("\"n","n",$str1);
        //$str3 = str_Replace(")\"",")",$str2);
        echo $str1;
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