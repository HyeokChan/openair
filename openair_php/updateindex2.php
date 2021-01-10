<?php
	header('Content-Type: application/json');
	require_once('dbcon.php');
	error_reporting(0);
	$post = json_decode(file_get_contents('php://input'),true);
	if ($post == null) {
		// 로그인 페이지를 통해 로그인
		if (empty($_POST['date']) || empty($_POST['time']))
		{
			$response->error = "Input Error";
			exit(json_encode($response));
		}
		
		$date = mysqli_real_escape_string($dbc, trim($_POST['date']));
		$time = mysqli_real_escape_string($dbc, trim($_POST['time']));
		
	}
	else { 	// JSON Object를 통해 로그인
		$date = $post['date'];
		$time  = $post['time'];
	}

	$dbc = mysqli_connect($dbhost, $dbuser, $dbpass, $dbname);
	if ($dbc == null) {
		$response->error = "Connection Error";
		exit(json_encode($response));
	}

	mysqli_query($dbc, "set names utf8;");

	$query = "UPDATE index2 SET book='0' WHERE date='$date' AND time='$time'";

	$result = mysqli_query($dbc, $query);
	if ($result == null) {
		$response->error = "Update Error";
		exit(json_encode($response));
	}

	mysqli_free_result($result);
	mysqli_close($dbc);

?>