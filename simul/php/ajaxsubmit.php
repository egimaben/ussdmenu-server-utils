<?php
//Fetching Values from URL
$msisdn=$_POST['msisdn'];
$message=$_POST['message'];

echo 'Current PHP version: ' . phpversion();
echo "MSISDN = $msisdn";
echo "MESSAGE = $message";

?>