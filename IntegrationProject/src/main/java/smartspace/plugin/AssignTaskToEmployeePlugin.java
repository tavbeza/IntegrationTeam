package smartspace.plugin;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import smartspace.enumStatus;
import smartspace.dao.AdvancedElementDao;
import smartspace.dao.AdvancedUserDao;
import smartspace.data.ActionEntity;
import smartspace.data.ElementEntity;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;

@Component
public class AssignTaskToEmployeePlugin implements Plugin {

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
		String taskKey = action.getElementSmartspace() + "#" + action.getElementId();

		ElementEntity task = this.elementDao.readById(taskKey).get();
		task.setKey(taskKey);
		
		UserEntity departmentManager = this.userDao.readById(departmentManagerKey).get();
		departmentManager.setKey(departmentManagerKey);
		
		if(!task.getMoreAttributes().containsKey("department"))
			throw new RuntimeException("This task is not assign to department!");
		
		ElementEntity department = this.elementDao.readById(task.getMoreAttributes().get("department").toString()).get();
		department.setKey(task.getMoreAttributes().get("department").toString());

		if (!action.getMoreAttributes().containsKey("employee"))
			throw new RuntimeException("This action does not contains a employee!");

		String employeeKey = action.getMoreAttributes().get("employee").toString();
		if (!this.userDao.readById(employeeKey).isPresent())
			throw new RuntimeException("This employee does not exist!");
		
		if(this.userDao.readById(employeeKey).get().getRole() != UserRole.PLAYER)
			throw new RuntimeException("Only players can perform tasks!");

		if (!this.userDao.readById(departmentManagerKey).get().getUsername().contains("-departmentManager"))
			throw new RuntimeException("This user is not a department manager so he can not assign task to department!");

		if (!task.getType().equals("Task"))
			throw new RuntimeException("this element type is not Task!");
		
		if(!department.getMoreAttributes().get("departmentManager").toString().equals(departmentManagerKey))
			throw new RuntimeException("This departmentManger is not assign to this department!");
		
		if(!department.getMoreAttributes().containsKey("employees"))
			throw new RuntimeException("There is no employees in this department!");
		
		if(!department.getMoreAttributes().get("employees").toString().contains(employeeKey))
			throw new RuntimeException("This employee is not assign to this department!");
		
		if(!department.getMoreAttributes().containsKey("tasks"))
			throw new RuntimeException("There is no tasks in this department!");
		
		if(!department.getMoreAttributes().get("tasks").toString().contains(taskKey))
			throw new RuntimeException("This task is not assign to this department!");
		
		// update PLAYER 50 points 
		departmentManager.setPoints(departmentManager.getPoints() + 50);
		this.userDao.update(departmentManager);
		
		Map<String, Object> taskMap = new HashMap<>();
		taskMap=task.getMoreAttributes();
		taskMap.put("performBy", employeeKey);
		taskMap.put("status", enumStatus.TO_DO);
		task.setMoreAttributes(taskMap);
		this.elementDao.update(task);
		return action;

	}

}
