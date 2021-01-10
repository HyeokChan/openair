<?php
	session_start();
	ob_start();
	
	// 서버 측의 세션이 없다면 쿠키를 사용해 만들어줌
	if (isset($_SESSION['id']) == null) {
		//echo "세션 값 없음";
		
		// 쿠키 값이 있는지 체크
		if (isset($_COOKIE['id']) && isset($_COOKIE['email'])) {
			//echo "쿠키 값 있음";
			//file_put_contents('headers.txt', "쿠키 값 있음", FILE_APPEND);
			$userid = $_COOKIE['id'];
			$_SESSION['id'] = $userid;
		}
	}
?>

