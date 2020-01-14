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

@Component
public class UpdateTaskStatusPlugin implements Plugin{
	
	private AdvancedElementDao<String> elementDao;
	private AdvancedUserDao<String> userDao;
	
	@Autowired
	public void setElementDao(AdvancedElementDao<String> elementDao) {
		this.elementDao=elementDao;
	}
	
	@Autowired
	public void setUserDao(AdvancedUserDao<String> userDao) {
		this.userDao = userDao;
	}
	
	@Override
	public ActionEntity process(ActionEntity actionStatusChangeEntity) {
		String playerKey= actionStatusChangeEntity.getPlayerSmartspace() + "#" + actionStatusChangeEntity.getPlayerEmail();
		String taskKey= actionStatusChangeEntity.getElementSmartspace() + "#" + actionStatusChangeEntity.getElementId();
	
		
		if(!elementDao.readById(taskKey).get().getType().equals("Task"))
			throw new RuntimeException("this element type is not Task!");
		
		if(!elementDao.readById(taskKey).get().getMoreAttributes().get("performBy").equals(playerKey))
			throw new RuntimeException("This Task is not assign to this player so this player cant change the status!");
		
		if(!actionStatusChangeEntity.getMoreAttributes().containsKey("status"))
			throw new RuntimeException("There is no 'status' in this action!");
		
		// update PLAYER 50 points 
		UserEntity theUser = this.userDao.readById(playerKey).get();
		theUser.setKey(playerKey);
		theUser.setPoints(theUser.getPoints() + 50);
		this.userDao.update(theUser);
		
		enumStatus status = enumStatus.valueOf(
				actionStatusChangeEntity.getMoreAttributes()
				.get("status").toString());
		
		ElementEntity updateElement = new ElementEntity();
		updateElement.setKey(taskKey);
		Map<String, Object> elementMap = new HashMap<>();
		elementMap = elementDao.readById(taskKey).get().getMoreAttributes();
		elementMap.put("status", status);
		updateElement.setMoreAttributes(elementMap);
		this.elementDao.update(updateElement);
		
		Map<String, Object> actionMap = new HashMap<>();
		actionMap = actionStatusChangeEntity.getMoreAttributes();
		actionMap.put("status", status);
		
		actionStatusChangeEntity.setMoreAttributes(actionMap);
		
		return actionStatusChangeEntity;
	}

	
}
