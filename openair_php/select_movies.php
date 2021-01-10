<?php
	header('Content-Type: application/json;charset=UTF-8');
	//require_once('showheader.php');
	
	// waring 출력하지 않음
	// 출력하려면 error_reporting(E_ERROR | E_WARNING | E_PARSE);
	error_reporting(0);
		
	// @ 앞에 붙이면 waring 출력하지 않음
	//$dbc = @mysqli_connect($dbhost, $dbuser, $dbpass, $dbname);
	$dbc = mysqli_connect('localhost', 'rnjsgur12', 'rnjsgur12', 'rnjsgur12');
	if ($dbc == null) {
		$response->error = "Connection Error";
		exit(json_encode($response));
	}
	
	mysqli_query($dbc, "set names utf8");
	
	// news와 WHRER가 붙지 않도록 news 다음 쌍따옴표 앞에 스페이스 주의!
	$query = "SELECT * FROM movies order by date asc,time asc where address like '%구미%'";
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