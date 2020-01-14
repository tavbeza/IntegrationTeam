package smartspace.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartspace.aop.PerformanceMonitor;
import smartspace.dao.AdvancedElementDao;
import smartspace.dao.AdvancedUserDao;
import smartspace.data.ElementEntity;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;

@Service
public class ElementServiceImpl implements ElementService {

	private AdvancedElementDao<String> elements;
	private AdvancedUserDao<String> users;

	@Autowired
	public ElementServiceImpl(AdvancedElementDao<String> elements, AdvancedUserDao<String> users) {
		this.elements = elements;
		this.users = users;
	}

	@Transactional
	@Override
	@PerformanceMonitor
	public ElementEntity writeElement(ElementEntity elementEntity) {
		if (validate(elementEntity)) {
			elementEntity.setCreationTimestamp(new Date());
			return this.elements.create(elementEntity);
		} else {
			throw new RuntimeException("invalid element input");
		}
	}

	private boolean validate(ElementEntity elementEntity) {
		return
		elementEntity.getName() != null && elementEntity.getType() != null &&
		// elementEntity.getCreationTimestamp() != null &&
				elementEntity.getCreatorSmartspace() != null && elementEntity.getCreatorEmail() != null
				&& elementEntity.getMoreAttributes() != null
				&& String.valueOf(elementEntity.getLocation().getX()) != null
				&& String.valueOf(elementEntity.getLocation().getY()) != null;
	}

	@Override
	@PerformanceMonitor
	public List<ElementEntity> getElements(int size, int page) {
		/*List<ElementEntity> elementArrayEntity = new ArrayList<ElementEntity>();
		elementArrayEntity = this.elements.readAll(size, page);
		for (ElementEntity entity : elementArrayEntity) {
			entity.setKey(entity.getKey());
		}
		return elementArrayEntity;*/
		return this.elements.readAll(size, page);
	}

	@Override
	@PerformanceMonitor
	public List<ElementEntity> getElements(String sortBy, int size, int page) {
		return this.elements.readAll(sortBy, size, page);
	}

	@Override
	@PerformanceMonitor
	public List<ElementEntity> getElementsByPattern(String pattern, String sortBy, int size, int page) {
		switch (sortBy) {
		case "elementSmartspace":
		case "elementId":
		case "location":
		case "name":
		case "type":
		case "creationTimestamp":
		case "expired":
		case "creatorSmartspace":
		case "creatorEmail":
			break;

		default:
			throw new RuntimeException("illegal sortBy value: " + sortBy);
		}
		return this.elements.readElementByTextPattern(pattern, sortBy, size, page);
	}

	@Override
	@PerformanceMonitor
	public void deleteByKey(String key) {
		this.elements.deleteByKey(key);
	}

	@Override
	public List<ElementEntity> importElements(List<ElementEntity> elementsEntity, String key) {
		this.users.readById(key).orElseThrow(() -> new RuntimeException("This user does not exist in the DB"));
		if (this.users.readById(key).get().getRole() != UserRole.ADMIN)
			throw new RuntimeException("This user is not an admin!\n");

		List<ElementEntity> elementArrayEntity = new ArrayList<ElementEntity>();
		for (ElementEntity entity : elementsEntity) {
			if (entity.getElementSmartspace().equals("2019B.dana.zuka"))
				throw new RuntimeException("You are trying to import elements from your own project-can't do that");
			else
				elementArrayEntity.add(this.elements.create(entity));
		}
		return elementArrayEntity;

	}

	@Override
	public List<ElementEntity> ExportElements(int size, int page, String key) {
		this.users.readById(key).orElseThrow(() -> new RuntimeException("This user does not exist in the DB"));
		if (this.users.readById(key).get().getRole() != UserRole.ADMIN)
			throw new RuntimeException("This user is not an admin!\n");
		
		List<ElementEntity> elementsList = new ArrayList<>();
		elementsList = this.elements.readAll(size, page);
		for(ElementEntity element : elementsList)
			element.setKey(element.getKey());
		return elementsList;
	}

	@Override
	public ElementEntity createElement(ElementEntity entity, String managerKey) {
		this.users.readById(managerKey).orElseThrow(() -> new RuntimeException("This user does not exist in the DB"));
		if (this.users.readById(managerKey).get().getRole() != UserRole.MANAGER)
			throw new RuntimeException("This user is not a manager!\n");

		// update MANAGER 100 points
		UserEntity manager = this.users.readById(managerKey).get();
		manager.setPoints(manager.getPoints() + 100);
		this.users.update(manager);
		
		if(entity.getMoreAttributes().containsKey("departmentManager"))
		{
			String departmentManagerKey = entity.getMoreAttributes().get("departmentManager").toString();
			if(!this.users.readById(departmentManagerKey).isPresent())
				throw new RuntimeException("This departmentManager does not exist in the DB");
			if(!this.users.readById(departmentManagerKey).get().getUsername().contains("departmentManager"))
				throw new RuntimeException("This user does not departmentManager");
		}

		entity.setCreatorEmail(managerKey.split("#")[1]);
		entity.setCreatorSmartspace(managerKey.split("#")[0]);
		return this.elements.create(entity);

	}

	@Override
	public void update(ElementEntity entity, String elementKey, String managerKey) {
		this.users.readById(managerKey).orElseThrow(() -> new RuntimeException("This user does not exist in the DB"));
		if (this.users.readById(managerKey).get().getRole() != UserRole.MANAGER)
			throw new RuntimeException("This user is not a manager!\n");

		this.elements.readById(elementKey).orElseThrow(() -> new RuntimeException("This element does not exist in the DB"));
		entity.setKey(elementKey);
		this.elements.update(entity);

		// update MANAGER 100 points
		UserEntity theUser = this.users.readById(managerKey).get();
		theUser.setPoints(theUser.getPoints() + 100);
		this.users.update(theUser);

	}

	@Override
	public ElementEntity returnElement(String elementSmartspace, String elementId, String key) {
		this.users.readById(key).orElseThrow(() -> new RuntimeException("user login failed, there is no such user in the DB"));

		this.elements.readById(elementSmartspace + '#' + elementId).orElseThrow(() -> new RuntimeException("user login failed, there is no such user in the DB"));
		if (this.elements.readById(elementSmartspace + '#' + elementId).get().getExpired() == true)
			throw new RuntimeException("This element is not expired! \n");
		ElementEntity element = this.elements.readById(elementSmartspace + "#" + elementId).get();
		element.setKey(element.getKey());
		return element;

	}

	@Override
	public List<ElementEntity> getElementsByName(String value, int size, int page, String key) {
		this.users.readById(key)
				.orElseThrow(() -> new RuntimeException("user login failed, there is no such user in the DB"));
		
		List<ElementEntity> allElements = new ArrayList<>();
		allElements = this.elements.readElementsByName(value, size, page);
		for(ElementEntity element : allElements)
			element.setKey(element.getKey());
		return allElements;
	}

	@Override
	public List<ElementEntity> getElementsByType(String value, int size, int page, String key) {
		this.users.readById(key)
				.orElseThrow(() -> new RuntimeException("user login failed, there is no such user in the DB"));
		
		List<ElementEntity> allElements = new ArrayList<>();
		allElements = this.elements.readElementsByType(value, size, page);
		for(ElementEntity element : allElements)
			element.setKey(element.getKey());
		return allElements;
	}

	@Override
	public List<ElementEntity> getElementsByDistance(double x, double y, double distance, int size, int page,
			String key) {

		this.users.readById(key)
				.orElseThrow(() -> new RuntimeException("user login failed, there is no such user in the DB"));

		List<ElementEntity> allElements = new ArrayList<>();
		allElements = this.elements.readElementsByDistance(x, y, distance, size, page);
		for(ElementEntity element : allElements)
			element.setKey(element.getKey());
		return allElements;
	
	}

	@Override
	public List<ElementEntity> getElements(int size, int page, String key) {
		this.users.readById(key).orElseThrow(() -> new RuntimeException("user login failed, there is no such user in the DB"));
		List<ElementEntity> allElements = new ArrayList<>();
		allElements = this.elements.readAll(size, page);
		for(ElementEntity element : allElements)
			element.setKey(element.getKey());
		return allElements;
	}

}
