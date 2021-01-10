<?php
	$headers =  getallheaders();
	// foreach($headers as $key=>$val){
		// $key . ': ' . $val . '<br>';
	// }
	//print_r($headers);
	//print_r( apache_request_headers());	
	//print_r( apache_response_headers());	
	//file_put_contents('php://stderr', print_r($foo, TRUE))

	file_put_contents('headers.txt', trim(print_r($headers, TRUE)), FILE_APPEND);
	file_put_contents('headers.txt', PHP_EOL . PHP_EOL , FILE_APPEND );

?>