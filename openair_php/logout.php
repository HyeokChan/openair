<?php
	header('Content-Type: application/json');
	
	require_once("session.php");

	require_once('dbcon.php');
	
	require_once('showheader.php');
	
	// error_reporting(E_ERROR | E_WARNING | E_PARSE);
	error_reporting(0);
	
	// 서버 측의 세션 체크
	if (isset($_SESSION['id']) == FALSE) {
		$response->error = "No Login";
		exit(json_encode($response));
	}
	
	// 세션 변수 초기화 (기존 세션 삭제 역할 수행)
	$_SESSION = array();

	if (isset($_COOKIE[session_name()]) != 0) {
		// 현재 시간보다 한 시간 전으로 설정하여 시간 무효화
		setcookie(session_name(), '', time() - (60 * 60 * 10000));
	}

	session_destroy();
	
	setcookie('id', '', time() - (60 * 60 * 10000));
	setcookie('email', '', time() - (60 * 60 * 10000));
	
	$response->status  = "Success";
	$response->message = "Logout";
	exit(json_encode($response));
?>
