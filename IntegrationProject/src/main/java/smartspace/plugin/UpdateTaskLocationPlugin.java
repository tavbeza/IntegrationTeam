package smartspace.plugin;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import smartspace.dao.AdvancedElementDao;
import smartspace.dao.AdvancedUserDao;
import smartspace.data.ActionEntity;
import smartspace.data.ElementEntity;
import smartspace.data.Location;
import smartspace.data.UserEntity;

@Component
public class UpdateTaskLocationPlugin  implements Plugin {

	private AdvancedUserDao<String> userDao;
	private AdvancedElementDao<String> elementDao;
	
	@Autowired
	public void setElementDao(AdvancedElementDao<String> elementDao) {
		this.elementDao=elementDao;
	}
	
	@Autowired
	public void setUserDao(AdvancedUserDao<String> userDao) {
		this.userDao = userDao;
	}
	
	@Override
	public ActionEntity process(ActionEntity action) {
				
		String playerKey = action.getPlayerSmartspace() + "#" + action.getPlayerEmail();
		String taskKey = action.getElementSmartspace() + "#" + action.getElementId();		
		
		if(!action.getMoreAttributes().containsKey("x"))
			throw new RuntimeException("There is no 'x' in the action details!");
		if(!action.getMoreAttributes().containsKey("y"))
			throw new RuntimeException("There is no 'y' in the action details!");
		
		ElementEntity task = this.elementDao.readById(taskKey).get();//my task
		task.setKey(taskKey);

		if(!task.getType().equals("Task"))
			throw new RuntimeException("this element type is not Task!");
		
		if(!this.elementDao.readById(taskKey).get().getMoreAttributes().containsKey("department"))
			throw new RuntimeException("this task does not contain department");
		
		String departmentKey = this.elementDao.readById(taskKey)
				.get().getMoreAttributes().get("department").toString();
	
		if(!this.userDao.readById(playerKey).get().getUsername().contains("-departmentManager"))
			throw new RuntimeException("This user is not a department manager so he can not change location!");	

		
		if(!this.elementDao.readById(departmentKey).isPresent()) 
			throw new RuntimeException("this department does not exist in db");
		
		
		if(!this.elementDao.readById(departmentKey).get().getMoreAttributes().containsKey("departmentManager"))
			throw new RuntimeException("this department does not have any department Manger");
		
		
		if(!this.elementDao.readById(departmentKey).get()
				.getMoreAttributes().get("departmentManager").toString().equals(playerKey))
			throw new RuntimeException("This department Manager is not the manager of this department");
					
		// update PLAYER 50 points 
		UserEntity theUser = this.userDao.readById(playerKey).get();
		theUser.setKey(playerKey);
		theUser.setPoints(theUser.getPoints() + 50);
		this.userDao.update(theUser);
		
		// Update the task location	
		Location taskLoc = new Location();
		taskLoc.setX(Double.parseDouble(action.getMoreAttributes().get("x").toString()));
		taskLoc.setY(Double.parseDouble(action.getMoreAttributes().get("y").toString()));
		task.setLocation(taskLoc);
		this.elementDao.update(task);
			
		return action;
	}



	
}
