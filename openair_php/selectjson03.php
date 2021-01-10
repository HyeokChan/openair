<?php

	$dbc = mysqli_connect('localhost', 'rnjsgur12', 'rnjsgur12', 'rnjsgur12') or die('Error Connect');
	mysqli_query($dbc, "set names utf8");
	$query = "select * from song";
	$result = mysqli_query($dbc, $query) or die("Error : Query");


	$json = array();
	if(mysqli_num_rows($result)>0)
	{
		while ($row = mysqli_fetch_assoc($result)) {
			//echo $row["name"];
			$json["list"][] = $row;
		}

	}
	echo json_encode($json);

	mysqli_free_result($result);
	mysqlu_close($dbc);


?>