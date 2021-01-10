<?php
	header('Content-Type: application/json');
	
	require_once("session.php");

	require_once('dbcon.php');
	
	//require_once('showheader.php');
	
	// waring 출력하지 않음
	// 출력하려면 error_reporting(E_ERROR | E_WARNING | E_PARSE);
	error_reporting(0);
		
	//echo $_POST['email'], $_POST['pass'], $_POST['pass2'], $_POST['nick'];
		
	$post = json_decode(file_get_contents('php://input'),true);
	if ($post == null) {
		// 로그인 페이지를 통해 로그인
		if (empty($_POST['email']) || empty($_POST['pass']) || 
			empty($_POST['pass2']) || empty($_POST['nick'])) {
			$response->error = "Input Error";
			exit(json_encode($response));
		}
		
		$email = mysqli_real_escape_string($dbc, trim($_POST['email']));
		$pass = mysqli_real_escape_string($dbc, trim($_POST['pass']));
		$pass2 = mysqli_real_escape_string($dbc, trim($_POST['pass2']));
		$nick = mysqli_real_escape_string($dbc, trim($_POST['nick']));
	}
	else { 	// JSON Object를 통해 로그인
		$email = $post['email'];
		$pass  = $post['pass'];
		$pass2  = $post['pass2'];
		$nick  = $post['nick'];
	}
	
	if ($pass != $pass2) {
		$response->error = "Password Confirmation Error";
		exit(json_encode($response));
	}
	
	// @ 앞에 붙이면 waring 출력하지 않음
	//$dbc = @mysqli_connect($dbhost, $dbuser, $dbpass, $dbname);
	$dbc = mysqli_connect($dbhost, $dbuser, $dbpass, $dbname);
	if ($dbc == null) {
		$response->error = "Connection Error";
		exit(json_encode($response));
	}


	mysqli_query($dbc, "set names utf8;");

	// e-mail 체크 -----------------------------------------------------------------
	$query = "select id from users where email = '$email'";
    $result = mysqli_query($dbc, $query);
	if ($result == null) {
		$response->error = "Querying Error";
		exit(json_encode($response));
	}
    if (mysqli_num_rows($result) != 0) { 
		$response->error = "Registered e-mail";
		exit(json_encode($response));
	}
	mysqli_free_result($result);
	
	// insert -----------------------------------------------------------------
	$query = "insert into users values " .
			"(null, '$email', SHA('$pass'), '$nick')";
	//echo $query;
	$result = mysqli_query($dbc, $query);
	if ($result == null) {
		$response->error = "Insertion Error";
		exit(json_encode($response));
	}	
	
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
		$response->message = "Login by signup";
		exit(json_encode($response));
	}
	else {
		mysqli_free_result($result);
		mysqli_close($dbc);
	
		$response->error = "Login Failed";
		exit(json_encode($response));
	}
	
?>

