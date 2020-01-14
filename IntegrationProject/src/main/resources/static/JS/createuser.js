



function getFormUser(){
	var username1= document.getElementById("username").value;
	var userType1= document.getElementById("username-choice").value;
	var avatar1= document.getElementById("avatar").value;
	var rates = document.getElementsByName('userRole-choice');
	var rate_value;
	for(var i = 0; i < rates.length; i++){
	    if(rates[i].checked){
	    	var usernameRole1 = rates[i].value;
	    }
	}
	console.log(usernameRole1);
	var user = {
            email: $("#email").val(),
            role:usernameRole1,
            username:username1 +'-'+ userType1,
            avatar:avatar1
        }
console.log(user);
	
$.ajax({
    url: '/smartspace/users',
    type: 'POST',
    dataType: 'json',
    contentType: 'application/json',
    success: function (data) {
       //$('#target').html(data.msg);
    	document.location.href="http://localhost:8087/loginuser.html";
    
    },
    data: JSON.stringify(user)
});
}

