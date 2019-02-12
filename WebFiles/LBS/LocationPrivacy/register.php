<?php	
header('Access-Control-Allow-Origin: *'); 	
	$response=array();
	//$studentid	=$_REQUEST["studentid"];
	$fname		= $_REQUEST["firstname"];
	$lname 		= $_REQUEST["lastname"];
    $username	= $_REQUEST["username"];
	$email		= $_REQUEST["email"];
    $password 	= $_REQUEST["password"];
    $phone 		= $_REQUEST["phone"];
	//$sciseqid	= $_REQUEST["sciseqid"];
	//$advisorid	= $_REQUEST["advisorid"];
	//$deptid		= $_REQUEST["deptid"];
	//$degreeplanyear	= $_REQUEST["degreeplanyear"];
	//$length		= strpos($email,"@");
	//$hash		= md5(rand(0,1000));
	//$verificationcode=rand(100000,10000000);
	
	require_once __DIR__ . '/db_open.php';
	if(isset($fname)&&isset($lname)&&isset($email)&&isset($password)&&isset($phone)&&isset($username))
	{
		$search = mysqli_query ($con,"SELECT username FROM users WHERE emailid='".$email."'or username='".$username."'");
		
		while($result=mysqli_fetch_assoc($search))
		{
			$response["record"]=array();
			$record=array();
			$record["username"]=$result["username"];
			array_push($response["record"],$record);
            //echo "User does not exist previously";
		}
		if(empty($response))
		{
		
			/*mysqli_query($con,"INSERT INTO studentdetail (studentid,fname,lname,username,emailid,Password,phone,sciseqid,advisorid,deptid,verified,hash) 
			VALUES ('$studentid','$fname','$lname','$username','$email','".md5($password)."','$phone','$sciseqid','$advisorid','$deptid',0,'$hash')") or die(mysql_error());*/
            
            mysqli_query($con,"INSERT INTO users (fname,lname,username,emailid,password,phone) 
			VALUES ('$fname','$lname','$username','$email','".$password."','$phone')") or die(mysql_error());
			
		
			$response["status"]="200";
			$response["message"]="Registeration Successfull";
			echo json_encode($response);
		
            //$to = '$email';
			//$subject = 'PV Schedule Email Verification';
			//$message = 'Hello '.$fname.', 

			//Thanks for signing up!
			//Your username is: '.$username.'

			//Please click the link below to activate your account:
			//http://localhost/schdulerprojectfilesbackup/verify.php?username='.$username.'&hash='.$hash.'';

			//$from = "PVSchedule <noreply-pvschedule@pvamu.edu>";
			//$headers = "From:" . $from;
			//mail($email,$subject,$message,$headers);
		
		}
		else
		{
			$response["status"]="400";
			$response["message"]="Already Registered";
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