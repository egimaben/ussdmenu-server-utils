$(document).ready(function(){
$("#submit").click(function(){
var msisdn = $("#msisdn").val();
var message = $("#message").val();
var url = $("#url").val();
// Returns successful data submission message when the entered information is stored in database.
var dataString = 'msisdn='+ msisdn + '&message='+ message;
if(msisdn=='')
{
alert("msisdn missing");
}
else if(message=='')
{
alert("message missing");
}

else
{
// AJAX Code To Submit Form.
$.ajax({
type: "POST",
url: "http://localhost/ussd/receiver.php",
//url:"http://localhost:8080/simtest",
//url: "php/ajaxsubmit.php",
data: dataString,
cache: false,
success: function(result){
$('#display').html(result);
},
      error: function(xhr,status,error) {
		  $('#display').html(error);
		  
      }
});
}
return false;
});
});