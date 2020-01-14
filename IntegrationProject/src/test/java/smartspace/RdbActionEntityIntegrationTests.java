package smartspace;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import smartspace.dao.ActionDao;
import smartspace.dao.ElementDao;
import smartspace.dao.UserDao;
import smartspace.data.ActionEntity;
import smartspace.data.ElementEntity;
import smartspace.data.Location;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;
import smartspace.data.util.EntityFactory;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "spring.profiles.active=default" })
public class RdbActionEntityIntegrationTests {
	
	private ElementDao<String> elementDao;
	private ActionDao actionDao;
	private UserDao<String> userDao;
	private EntityFactory factory;
	
	@Autowired
	public void setUserDao(UserDao<String> userDao) {
		this.userDao = userDao;
	}
	
	@Autowired
	public void setElementDao(ElementDao<String> elementDao) {
		this.elementDao = elementDao;
	}
	
	@Autowired
	public void setActionDao(ActionDao actionDao) {
		this.actionDao = actionDao;
	}
	
	@Autowired
	public void setFactory(EntityFactory factory) {
		this.factory = factory;
	}

	@After
	public void teardown() {
		this.actionDao.deleteAll();
	}
	
	@Test
	public void testCreateSimpleAction() throws Exception{
		
		// GIVEN the database is clean
		// AND one element and one user
		ElementEntity element = 
			this.factory.createNewElement("Avi", "Task", new Location(1.0,1.0),
					new Date(), "tavb@gmail.com", "2019b.dana.zuka",false, new HashMap<>());
		element = this.elementDao.create(element);
		
		UserEntity user =
				this.factory.createNewUser("bibi@king", "2019b.dana.zuka", "bibi", "king", UserRole.MANAGER, 10);
		user = this.userDao.create(user);
		
		// WHEN we create a new action and store it in DB
		String text = "Test";
		ActionEntity action = 
			this.factory.createNewAction(element.getKey(), element.getElementSmartspace(), text, new Date(), user.getUserEmail(), 
					user.getUserSmartspace(), new HashMap<>());
		action = this.actionDao.create(action);
				
		// THEN the action is stored 
		assertThat(this.actionDao.readAll().contains(action));
	}
	
	@Test
	public void testCreateActionAndVerifyUniqueKeys () throws Exception{
		// GIVEN clean database 

		// WHEN I create 30 Tasks
		int size = 30;
		Set<String> keysSet = 
				IntStream
				.range(1, size + 1) //Integer Stream
				.mapToObj(i->this.factory
				.createNewAction("elementId",  "elementSmartspace",  "actionType",
					new Date(), "playerguy@gmail.com",  "playerSmartspace",new HashMap<>()))
				.map(this.actionDao::create)// 
				.map(ActionEntity::getKey) // String Stream
				.collect(Collectors.toSet());

		// THEN they all have unique keys
		assertThat(keysSet)
		.hasSize(size);
	}
}
	

