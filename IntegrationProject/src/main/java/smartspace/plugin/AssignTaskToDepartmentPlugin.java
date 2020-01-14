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

@Component
public class AssignTaskToDepartmentPlugin implements Plugin {
	
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

		String playerKey = action.getPlayerSmartspace() + "#" + action.getPlayerEmail();
		String taskKey = action.getElementSmartspace() + "#" + action.getElementId();
		
		if(!action.getMoreAttributes().containsKey("department"))
			throw new RuntimeException("There is no 'department' in the action details!");
		
		String departmentKey = action.getMoreAttributes().get("department").toString();
		
		ElementEntity task = this.elementDao.readById(taskKey).get();
		task.setKey(taskKey);
		
		if(!this.elementDao.readById(departmentKey).isPresent())
			throw new RuntimeException("This department is not exist!");
		
		ElementEntity department = this.elementDao.readById(departmentKey).get();
		department.setKey(departmentKey);
		
		if(!task.getType().equals("Task"))
			throw new RuntimeException("this element type is not Task!");
		
		if(!department.getType().equals("Department"))
			throw new RuntimeException("this element type is not department! (in action details)");
		
		if(!this.userDao.readById(playerKey).get().getUsername().contains("-departmentManager"))
			throw new RuntimeException("This user is not a department manager so he can not assign task to department!");
			
		if(!department.getMoreAttributes().containsKey("departmentManager"))
			throw new RuntimeException("This department element does not contains a departmentManager!");
		
		if(!department.getMoreAttributes().get("departmentManager").equals(playerKey))
			throw new RuntimeException("This departmentManager is not the manager of this department!");
		
		// update PLAYER 50 points 
		UserEntity theUser = this.userDao.readById(playerKey).get();
		theUser.setKey(playerKey);
		theUser.setPoints(theUser.getPoints() + 50);
		this.userDao.update(theUser);		
		
		// Update the department in Task element
		ElementEntity updateTask = new ElementEntity();
		updateTask.setKey(taskKey);
		Map<String, Object> taskMap = new HashMap<>();
		taskMap = task.getMoreAttributes();
		taskMap.put("department", departmentKey);
		updateTask.setMoreAttributes(taskMap);
		this.elementDao.update(updateTask);
		
		// Insert the task into the task list in Department element
		ElementEntity updateDepartment = new ElementEntity();
		updateDepartment.setKey(departmentKey);
		Map<String, Object> departmentMap = new HashMap<>();
		departmentMap = department.getMoreAttributes();
		if(departmentMap.containsKey("tasks")) {
			String tasks = departmentMap.get("tasks").toString() + ", " + task.getKey();
			departmentMap.put("tasks", tasks);
		}
		else {
			departmentMap.put("tasks", task.getKey());
		}
		updateDepartment.setMoreAttributes(departmentMap);
		this.elementDao.update(updateDepartment);
		
		return action;
	}

}
