<?php
	header('Content-Type: application/json');
	
	require_once("session.php");

	require_once('dbcon.php');
	
	//require_once('showheader.php');
	
	// error_reporting(E_ERROR | E_WARNING | E_PARSE);
	error_reporting(0);

	// 서버 측의 세션 체크
	if (isset($_SESSION['id']) != null) {
		$response->status  = "Success";
		$response->message = "Login by Session";
		exit(json_encode($response));
	}

	$post = json_decode(file_get_contents('php://input'),true);
	if ($post == null) {
		// 로그인 페이지를 통해 로그인
		if (empty($_POST['email']) || empty($_POST['pass'])) {
			$response->error = "Input Error";
			exit(json_encode($response));
		}
		else {
			$email = mysqli_real_escape_string($dbc, trim($_POST['email']));
			$pass  = mysqli_real_escape_string($dbc, trim($_POST['pass']));
		}
	}
	else { 	// JSON Object를 통해 로그인
		$email = $post['email'];
		$pass  = $post['pass'];
	}
	
	$dbc = mysqli_connect($dbhost, $dbuser, $dbpass, $dbname);
	if ($dbc == null) {
		$response->error = "Connection Error";
		exit(json_encode($response));
	}

	mysqli_query($dbc, "set names utf8;");

	$query = "SELECT id, email FROM users " . 
		"WHERE email = '$email' AND pass = SHA('$pass')";
	$result = mysqli_query($dbc, $query);
	if ($result == null) {
		$response->error = "Querying Error";
		exit(json_encode($response));
	}
	
	if (mysqli_num_rows($result) == 1) {
		$row = mysqli_fetch_assoc($result);
		$userid = $row['id'];
		$_SESSION['id'] = $userid;
		
		// 현재 시간부터 하루 (24시간) 동안 쿠키 유지
		setcookie('email', $row['email'], time() + (60 * 60 * 24));
		setcookie('id', $row['id'], time() + (60 * 60 * 24));    
				
		// 로그인 성공
		mysqli_free_result($result);
		mysqli_close($dbc);
		
		$response->status  = "Success";
		$response->message = "Login by email";
		exit(json_encode($response));
	}
	else {
		mysqli_free_result($result);
		mysqli_close($dbc);
	
		$response->error = "Login Failed";
		exit(json_encode($response));
	}
?>