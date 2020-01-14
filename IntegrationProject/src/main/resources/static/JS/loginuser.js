
function getQueryVariable(variable)
{
       var query = window.location.search.substring(1);
       var vars = query.split("&");
       for (var i=0;i<vars.length;i++) {
               var pair = vars[i].split("=");
               if(pair[0] == variable){return pair[1];}
       }
       return(false);
}


function getUserLogin(){

	$.ajax({
	    url: '/smartspace/users/login/'+$("#smartspace").val()+'/'+$("#email").val(),
	    type: 'GET',
	    data: '',
	    //contentType: 'application/json',
	    success: function (data) {
	       //$('#target').html(data.msg);
	
	    	document.location.href="http://localhost:8087/mainWindow.html?"+
	    	"username="+data.username+
	    	"&usersmartspace="+$("#smartspace").val()+
	    	"&email="+$("#email").val()+
	    	"&role="+data.role+
	    	"&points="+data.points+
	    	"&avatar="+data.avatar;
	    
	    },
	   // data: JSON.stringify(user)
	    error: function(e){
	    	console.log(e);
	    	
	    }
	});
	

	
}