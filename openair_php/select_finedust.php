<?php
	header('Content-Type: application/json;charset=UTF-8');

            $dbhost = 'ls-d2d6d7fc5079fc96a294c6b43431fd53cf08a950.ctsshs4cylzc.ap-northeast-2.rds.amazonaws.com';
            $dbuser = 'dbmasteruser';
            $dbpass = 'th411794';
            $dbname = 'OPENAIR';
	
	error_reporting(0);
		
	$dbc = mysqli_connect($dbhost, $dbuser, $dbpass, $dbname);
	if ($dbc == null) {
		$response->error = "Connection Error";
		exit(json_encode($response));
	}
	
	mysqli_query($dbc, "set names utf8");

	$city = $_GET['city'];	

	$query = "SELECT TIME, PM10level, PM25level FROM tomorroww WHERE place='$city'";
	$result = mysqli_query($dbc, $query);
	if ($result == null) {
		$response->error = "Querying Error";
		exit(json_encode($response));
	}
	
	$json = array();
	while ($row = mysqli_fetch_assoc($result)) {
		$json['list'][] = $row;
	}
	
	mysqli_free_result($result);
	mysqli_close($dbc);
	
	exit(json_encode($json));
	
	
?>