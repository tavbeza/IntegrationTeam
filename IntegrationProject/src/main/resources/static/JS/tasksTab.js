


function taskForm() {
	var name1 = document.getElementById("taskname").value;
	var lat1 = document.getElementById("lat").value;
	var lng1 = document.getElementById("lng").value;

	var task = {
		name : name1,
		latlng : {
			lat : lat1,
			lng : lng1
		},
		expired : false,
		elementType : "Task",
		elementProperties : {
			status: "TO_DO"
		}
	}

	$.ajax({
		url : '/smartspace/elements/' + getQueryVariable("usersmartspace")
				+ '/' + getQueryVariable("email"),
		type : 'POST',
		dataType : 'json',
		contentType : 'application/json',
		success : function(data) {
			// $('#target').html(data.msg);

			console.log(data);
			document.location.href = "http://localhost:8087/tasksbar.html?"
					+ "username=" + getQueryVariable("username")
					+ "&usersmartspace=" + getQueryVariable("usersmartspace")
					+ "&email=" + getQueryVariable("email") + "&role="
					+ getQueryVariable("role") + "&points="
					+ getQueryVariable("points")
					+ "&avatar="+ getQueryVariable("avatar");

		},
		data : JSON.stringify(task)

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
	var table = document.getElementById("taskTable");

	$
			.ajax({
				url : '/smartspace/elements/'
						+ getQueryVariable('usersmartspace') + '/'
						+ getQueryVariable('email') + '?search=type&value=Task',
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
						var cell7 = row.insertCell(7);
						var cell8 = row.insertCell(8);
						var cell9 = row.insertCell(9);
						var cell10 = row.insertCell(10);

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

						cell7.innerHTML = properties.department;

						cell8.innerHTML = properties.performBy;

						cell9.innerHTML = properties.status;
						cell10.innerHTML = '<tr> <td> <button class="btn btn-primary btn-sm" onclick="updateTask(this)">Update</button> </td> </tr>';

					}

				},
				error : function(e) {
				}

			});
}

function updateTask(el) {

	var myTable = document.getElementById("taskTable");
	var dep, emp, stat;
	for (var i = 1; i < myTable.rows.length; i++) {
		myTable.rows[i].onclick = function() {
			dep = document.getElementById("department");
			emp = document.getElementById("performBy");
			stat = document.getElementById("status-choice");

			if (dep != null) {

				var action1 = {
					type : "assignTaskToDepartment",
					element : {
						id : this.cells[1].innerHTML.split('#')[1],
						smartspace : this.cells[1].innerHTML.split('#')[0]
					},
					player : {
						email : getQueryVariable('email'),
						smartspace : getQueryVariable('usersmartspace')
					},
					properties : {
						department : dep.value
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

								document.location.href = "http://localhost:8087/tasksbar.html?"
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
							data : JSON.stringify(action1)

						});

			}

			if (emp != null) {

				var action2 = {
					type : "assignTaskToEmployee",
					element : {
						id : this.cells[1].innerHTML.split('#')[1],
						smartspace : this.cells[1].innerHTML.split('#')[0]
					},
					player : {
						email : getQueryVariable('email'),
						smartspace : getQueryVariable('usersmartspace')
					},
					properties : {
						employee : emp.value
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

								document.location.href = "http://localhost:8087/tasksbar.html?"
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
							data : JSON.stringify(action2)

						});

			}

			if (stat.value != "Choose") {

				var action3 = {

					type : "updateTaskStatus",
					element : {
						id : this.cells[1].innerHTML.split('#')[1],
						smartspace : this.cells[1].innerHTML.split('#')[0]
					},
					player : {
						email : getQueryVariable('email'),
						smartspace : getQueryVariable('usersmartspace')
					},
					properties : {
						status : stat.value
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

								document.location.href = "http://localhost:8087/tasksbar.html?"
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
							data : JSON.stringify(action3)

						});

			}
		};
	}
}

