
function getQueryVariable(variable) {
	var query = window.location.search.substring(1);
	var vars = query.split("&");
	for (var i = 0; i < vars.length; i++) {
		var pair = vars[i].split("=");
		if (pair[0] == variable) {
			return pair[1];
		}
	}
	return (false);
}

window.onload = function() {
	var table = document.getElementById("employeeTable");

	if (getQueryVariable('role') == "ADMIN") {

		$.ajax({
			url : '/smartspace/admin/users/'
					+ getQueryVariable('usersmartspace') + '/'
					+ getQueryVariable('email'),
			type : 'GET',
			data : '',
			success : function(data) {
				var counter = 1;
				for (var i = 0; i < data.length; i++) {
					var row = table.insertRow(-1);
					var cell0 = row.insertCell(0);
					var cell1 = row.insertCell(1);
					var cell2 = row.insertCell(2);
					var cell3 = row.insertCell(3);
					var cell4 = row.insertCell(4);
					var cell5 = row.insertCell(5);
					var cell6 = row.insertCell(6);
					console.log(data);
					cell0.innerHTML = counter;
					counter++;
					cell1.innerHTML = data[i].username;
					
					var key=data[i].key;
					cell2.innerHTML = key.smartspace;
					cell3.innerHTML = key.email;
					cell4.innerHTML = data[i].points;
					cell5.innerHTML = data[i].role;
					cell6.innerHTML = data[i].avatar;
				}

			},
			error : function(e) {
			}

		});
	} else {

		// add one row and 7 cols
		var row = table.insertRow(1);
		var cell0 = row.insertCell(0);
		var cell1 = row.insertCell(1);
		var cell2 = row.insertCell(2);
		var cell3 = row.insertCell(3);
		var cell4 = row.insertCell(4);
		var cell5 = row.insertCell(5);
		var cell6 = row.insertCell(6);

		// insert data to cells

		cell0.innerHTML = 1;
		cell1.innerHTML = getQueryVariable('username');
		cell2.innerHTML = getQueryVariable('usersmartspace');
		cell3.innerHTML = getQueryVariable('email');
		cell4.innerHTML = getQueryVariable('points');
		cell5.innerHTML = getQueryVariable('role');
		cell6.innerHTML = getQueryVariable('avatar');

	}

}