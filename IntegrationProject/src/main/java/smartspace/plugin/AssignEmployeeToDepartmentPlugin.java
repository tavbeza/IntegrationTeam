package smartspace.plugin;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import smartspace.dao.AdvancedElementDao;
import smartspace.dao.AdvancedUserDao;
import smartspace.data.ActionEntity;
import smartspace.data.ElementEntity;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;

@Component
public class AssignEmployeeToDepartmentPlugin implements Plugin {

	private AdvancedElementDao<String> elementDao;
	private AdvancedUserDao<String> userDao;

	@Autowired
	public void setElementDao(AdvancedElementDao<String> elementDao) {
		this.elementDao = elementDao;
	}

	@Autowired
	public void setUserDao(AdvancedUserDao<String> userDao) {
		this.userDao = userDao;
	}

	@Override
	public ActionEntity process(ActionEntity action) {

		String departmentManagerKey = action.getPlayerSmartspace() + "#" + action.getPlayerEmail();
		String departmentKey = action.getElementSmartspace() + "#" + action.getElementId();
		
		if (!this.elementDao.readById(departmentKey).isPresent())
			throw new RuntimeException("this department does not exist in DB!");

		if (!this.elementDao.readById(departmentKey).get().getType().equals("Department"))
			throw new RuntimeException("this element type is not Department!");

		if (!this.userDao.readById(departmentManagerKey).get().getUsername().contains("-departmentManager"))
			throw new RuntimeException(
					"This user is not a department manager so he can not assign employee to department!");

		if (!action.getMoreAttributes().containsKey("employee"))
			throw new RuntimeException("There is no 'employee' in the action details!");

		String employeeKey = action.getMoreAttributes().get("employee").toString();

		if (!this.userDao.readById(employeeKey).isPresent())
			throw new RuntimeException("This employee is not exist in DB!");

		if (this.userDao.readById(employeeKey).get().getRole() != UserRole.PLAYER)
			throw new RuntimeException("This employee is not a player!");
		
		ElementEntity department = this.elementDao.readById(departmentKey).get();
		department.setKey(department.getKey());
		
		if(!department.getMoreAttributes().containsKey("departmentManager"))
			throw new RuntimeException("This depaertment does not has departmentManager!");
		
		if(!department.getMoreAttributes().get("departmentManager").toString().equals(departmentManagerKey))
			throw new RuntimeException("This depaertmentManager does not the manager of this department!");
		

		// update PLAYER 50 points
		UserEntity manager = this.userDao.readById(departmentManagerKey).get();
		manager.setKey(departmentManagerKey);
		manager.setPoints(manager.getPoints() + 50);
		this.userDao.update(manager);

		UserEntity employee = this.userDao.readById(employeeKey).get();
		employee.setKey(employeeKey);

		Map<String, Object> departmentMap = new HashMap<>();
		departmentMap = department.getMoreAttributes();
		if(departmentMap.containsKey("employees")) {
			String employees = departmentMap.get("employees").toString() + ", " + employee.getKey();
			departmentMap.put("employees", employees);
		}
		else {
			departmentMap.put("employees", employee.getKey());
		}
		department.setMoreAttributes(departmentMap);
		this.elementDao.update(department);

		return action;
	}

}