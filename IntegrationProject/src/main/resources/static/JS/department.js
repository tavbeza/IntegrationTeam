function depertmentForm() {
	var name1 = document.getElementById("departmentname").value;
	var lat1 = document.getElementById("lat").value;
	var lng1 = document.getElementById("lng").value;
	var departmentManagerKey1 = document.getElementById("departmentManagerKey").value;

	var department = {
		name : name1,
		latlng : {
			lat : lat1,
			lng : lng1
		},
		expired : false,
		elementType : "Department",
		elementProperties : {
			departmentManager : departmentManagerKey1//,
//			employees : null,
//			tasks : null
		}
	};

	$
			.ajax({
				url : '/smartspace/elements/'
						+ getQueryVariable("usersmartspace") + '/'
						+ getQueryVariable("email"),
				type : 'POST',
				dataType : 'json',
				contentType : 'application/json',
				success : function(data) {
					// $('#target').html(data.msg);

					document.location.href = "http://localhost:8087/departmentbar.html?"
							+ "username="
							+ getQueryVariable("username")
							+ "&usersmartspace="
							+ getQueryVariable("usersmartspace")
							+ "&email="
							+ getQueryVariable("email")
							+ "&role="
							+ getQueryVariable("role")
							+ "&points="
							+ getQueryVariable("points")
							+ "&avatar="+ getQueryVariable("avatar");

				},
				data : JSON.stringify(department)

			});

}

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

	$.ajax({
				url : '/smartspace/elements/'
						+ getQueryVariable('usersmartspace') + '/'
						+ getQueryVariable('email')
						+ '?search=type&value=Department',
				type : 'GET',
				data : '',
				success : function(data) {

					var table = document.getElementById("departmentTable");

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
						var cell7 = row.insertCell(7);
						var cell8 = row.insertCell(8);
						var cell9 = row.insertCell(9);
						var cell10 = row.insertCell(10);
						var cell11 = row.insertCell(11);

						cell0.innerHTML = counter;
						counter++;
						var key = data[i].key;
						cell1.innerHTML = key.smartspace + '#' + key.id;
						cell2.innerHTML = data[i].name;
						var keyCreator = data[i].creator;

						cell3.innerHTML = keyCreator.smartspace + '#'
								+ keyCreator.email;
						var latlng = data[i].latlng;
						cell4.innerHTML = latlng.lat;
						cell5.innerHTML = latlng.lng;
						var properties = data[i].elementProperties;
						cell6.innerHTML = data[i].created;
						cell7.innerHTML = properties.departmentManager;
						cell8.innerHTML = properties.employees;
						cell9.innerHTML = properties.tasks;

						cell10.innerHTML = '<tr> <td> <input type="text" id="addEmployee" name="addEmployee" required class="Text1" placeholder="add employee"></td>  </tr>';
						cell11.innerHTML = '<tr> <td> <button class="btn btn-primary btn-sm" onclick="updateDepartment(this)" >Assign employee</button> </td> </tr>';

					}

				},
				error : function(e) {
				}

			});
}

function updateDepartment(el) {

	var myTable = document.getElementById("departmentTable");
	var x,y;
	for (var i = 1; i < myTable.rows.length; i++) {
		myTable.rows[i].onclick = function() {
			 x = this.cells[10].getElementsByClassName("Text1").item(0).value;
			 y = this.cells[1].innerHTML;
			 
				var action = {
						type:"assignEmployeeToDepartment",
						element:{
							id: y.split('#')[1],
							smartspace: y.split('#')[0]
						},
						player:{
							email:getQueryVariable('email') ,
							smartspace:getQueryVariable('usersmartspace') 
						},
						properties:{
							employee:x
						}
								
				}
				
				$
				.ajax({
					url : '/smartspace/actions',
					type : 'POST',
					dataType : 'json',
					contentType : 'application/json',
					success : function(data) {
						// $('#target').html(data.msg);

						document.location.href = "http://localhost:8087/departmentbar.html?"
								+ "username="
								+ getQueryVariable("username")
								+ "&usersmartspace="
								+ getQueryVariable("usersmartspace")
								+ "&email="
								+ getQueryVariable("email")
								+ "&role="
								+ getQueryVariable("role")
								+ "&points="
								+ getQueryVariable("points")+ "&avatar="+ getQueryVariable("avatar");

					},
					data : JSON.stringify(action)

				});				
			
			
		};
		
	}
	

}
