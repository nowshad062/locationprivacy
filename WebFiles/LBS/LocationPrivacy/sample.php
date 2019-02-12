<?php
header('Access-Control-Allow-Origin: *'); 
$response=array();
$username = $_REQUEST["username"];
$password = $_REQUEST["password"];
require_once __DIR__ . '/db_open.php';
if(isset($username)&&isset($password))
{
    $stmt = $mysqli -> prepare ("select fname,lname,username,emailid,phone from users where username= ? and password= ?");
    $stmt -> bind_param("ss", $username, $password);
    //$stmt -> bind_param("s", $password);
    $stmt -> execute();
    $search = $stmt -> get_result();
	/*$search=mysqli_query($con,"select fname,lname,username,emailid,phone from users where username='".$username."'and password='".$password."'");*/
	while($result= $search -> fetch_assoc())
	{
		$response["record"]=array();
		$record=array();
		$record["fname"]=$result["fname"];
		$record["lname"]=$result["lname"];
		$record["username"]=$result["username"];
		$record["emailid"]=$result["emailid"];		
		$record["phone"]=$result["phone"];
		array_push($response["record"],$record);
	}
	if(empty($response))
	{
		$response["status"]="400";
		$response["message"]="Username and password doesnot match";
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