package smartspace.logic;

import static org.hamcrest.CoreMatchers.nullValue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import smartspace.dao.AdvancedActionDao;
import smartspace.dao.AdvancedElementDao;
import smartspace.dao.AdvancedUserDao;
import smartspace.data.ActionEntity;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;
import smartspace.plugin.Plugin;

@Service
public class ActionServiceImpl implements ActionService{
	
	private AdvancedActionDao actions;
	private AdvancedUserDao<String> users;
	private AdvancedElementDao<String> elements;
	private ApplicationContext ctx;
	
	@Autowired	
	public ActionServiceImpl(AdvancedActionDao actions, AdvancedUserDao<String> users, AdvancedElementDao<String> elements, ApplicationContext ctx) {
		this.actions = actions;
		this.users = users;
		this.elements = elements;
		this.ctx = ctx;
	}

	@Override
	public ActionEntity writeAction(ActionEntity actionEntity) {
		if (validate(actionEntity)) {
			actionEntity.setCreationTimestamp(new Date());
			return this.actions
				.create(actionEntity);
		}else {
			throw new RuntimeException("invalid action input");
		}
	}
	
	private boolean validate(ActionEntity actionEntity) {
		return 
				actionEntity.getElementSmartspace() != null &&
				actionEntity.getElementId() != null &&
				actionEntity.getPlayerSmartspace() != null &&
				actionEntity.getPlayerEmail() != null &&
				actionEntity.getActionType() != null&&
				actionEntity.getMoreAttributes()!=nullValue();
	}


	@Override
	public List<ActionEntity> getActions(int size, int page) {
		return this.actions
				.readAll(size, page);
	}
	
	@Override
	public List<ActionEntity> getActions(String sortBy, int size, int page) {
		return this.actions
				.readAll(sortBy, size, page);
	}

	@Override
	public List<ActionEntity> getActionsByPattern(String pattern, String sortBy, int size, int page) {
		
		switch (sortBy) {
		case "elementSmartspace":
		case "elementId":
		case "playerSmartspace":
		case "playerEmail":
		case "actionType":
		case "creationTimestamp":
		case "moreAttributes":
		break;
		
		default:
			throw new RuntimeException("illegal sortBy value: " + sortBy);
		}
		return this.actions.readActionsByIdPattern(
						pattern,
						sortBy,
						size, page);
	}

	public AdvancedActionDao getActions() {
		return actions;
	}

	public void setActions(AdvancedActionDao actions) {
		this.actions = actions;
	}

	@Override
	public void update(ActionEntity entity) {
		this.actions.update(entity);
	}

	@Override
	public void deleteByKey(String key) {
		this.actions.deleteByKey(key);
		
	}

	@Override
	public List<ActionEntity> importActions(ActionEntity[] actionEntityArray, String key) {
		if(this.users.readById(key).get().getRole() != UserRole.ADMIN)
			throw new RuntimeException("This user is not an admin!\n");
		
		List<ActionEntity> actualActionEntities = new ArrayList<ActionEntity>();
		for(ActionEntity actionEntity : actionEntityArray) {
			if(actionEntity.getActionSmartspace().equals("2019B.dana.zuka")) {
				throw new RuntimeException("this imported action is from our smartspace\n");
			}else {
				actualActionEntities.add(this.actions.create(actionEntity));
			}
		}
		return actualActionEntities;
	}

	@Override
	public List<ActionEntity> exportActions(int size, int page, String key) {
		if(this.users.readById(key).get().getRole() != UserRole.ADMIN)
			throw new RuntimeException("This user is not an admin!\n");
		
		List<ActionEntity> allActions = new ArrayList<ActionEntity>();
		allActions = this.actions.readAll(size, page);
		for(ActionEntity action : allActions)
			action.setKey(action.getKey());
		return allActions;		
	}
	
	private boolean validateAction(ActionEntity entity) {
		return entity.getActionType() != null &&
				!entity.getActionType().trim().isEmpty() &&
				entity.getElementId()!= null &&
				!entity.getElementId().trim().isEmpty() &&
				entity.getElementSmartspace()!= null &&
				!entity.getElementSmartspace().trim().isEmpty() &&
				entity.getPlayerEmail() != null&&
				!entity.getPlayerEmail().trim().isEmpty()&&
				entity.getPlayerSmartspace()!=null&&
				!entity.getPlayerSmartspace().trim().isEmpty() &&
				entity.getMoreAttributes()!=null;
	}

	@Override
	public ActionEntity invokeAnAction(ActionEntity action) {
		
		Optional<UserEntity> user = this.users.readById(action.getPlayerSmartspace()+  "#" + action.getPlayerEmail());
		if (user.isPresent() == false || user.get().getRole()!= UserRole.PLAYER)
			throw new RuntimeException("Only players are allowed to perform this action!");
		
		String elementKey = action.getElementSmartspace() + "#" + action.getElementId();
		this.elements.readById(elementKey).orElseThrow(() -> new RuntimeException("This element does not exist in the DB!"));
		
		if(this.elements.readById(elementKey).get().getExpired())
			throw new RuntimeException("This element is expired!");
				
		if(validateAction(action) != false) {
			try {
				String type = action.getActionType();
				String className =
						"smartspace.plugin." 
						+ type.toUpperCase().charAt(0) 
						+ type.substring(1, type.length())
						+ "Plugin";
				Class<?> theClass = Class.forName(className);
				Plugin plugin = (Plugin) this.ctx.getBean(theClass);
				
				action.setCreationTimestamp(new Date());
				action = plugin.process(action);
				return this.actions.create(action);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		else {
			throw new RuntimeException("illegal action");
		}
	}

}
