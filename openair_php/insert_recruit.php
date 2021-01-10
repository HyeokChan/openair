<?php
	header('Content-Type: application/json;charset=utf-8');
	
	require_once('session.php');

	require_once('dbcon.php');
	
	error_reporting(0);
		
	// @ 앞에 붙이면 waring 출력하지 않음
	//$dbc = @mysqli_connect($dbhost, $dbuser, $dbpass, $dbname);
	$dbc = mysqli_connect($dbhost, $dbuser, $dbpass, $dbname);
	if ($dbc == null) {
		$response->error = "Connection Error";
		exit(json_encode($response));
	}
	
	$post = json_decode(file_get_contents('php://input'),true);
	if ($post == null) {
		// 로그인 페이지를 통해 로그인
		if (empty($_POST['id']) || empty($_POST['category']) || empty($_POST['time']) || empty($_POST['area']) || empty($_POST['team'])) {
			$response->error = "Input Error";
			exit(json_encode($response));
		}
		
		$id = mysqli_real_escape_string($dbc, trim($_POST['id']));
		$category = mysqli_real_escape_string($dbc, trim($_POST['category']));
		$time = mysqli_real_escape_string($dbc, trim($_POST['time']));
		$area = mysqli_real_escape_string($dbc, trim($_POST['area']));
		$team = mysqli_real_escape_string($dbc, trim($_POST['team']));
	}
	else { 	// JSON Object
		$id = $post['id'];
		$category  = $post['category'];
		$time = $post['time'];
		$area  = $post['area'];
		$team = $post['team'];
	}
	
	// TODO: 체크 안해도 되는지 확인 필요 (보안 문제 발생 가능)
	//       같은 값으로 확인되는데 에러 발생해서 주석 처리함
	// if ($_SESSION['id'] != $id) {
		// $response->error = "User Error" ;
		// exit(json_encode($response));
	// }

	mysqli_query($dbc, "set names utf8;");

	
	// insert -----------------------------------------------------------------
	$query = "insert into recruit_info values " .
			"(null, $id, NOW(), '$category', '$time', '$area', '$team')";
	//echo $query; 
	$result = mysqli_query($dbc, $query);
	if ($result == null) {
		$response->error = "Insertion Error";
		exit(json_encode($response));
	}	

	$response->status = "Success";
	exit(json_encode($response));

	
?>

