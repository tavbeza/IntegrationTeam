package smartspace;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.HashMap;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import smartspace.dao.UserDao;
import smartspace.dao.ActionDao;
import smartspace.dao.ElementDao;
import smartspace.data.UserEntity;
import smartspace.data.ActionEntity;
import smartspace.data.ElementEntity;
import smartspace.data.Location;
import smartspace.data.UserRole;
import smartspace.data.util.EntityFactory;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "spring.profiles.active=default" })
public class RdbUserEntityIntegrationTests {
	
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
	public void setActionrDao(ActionDao actionDao) {
		this.actionDao= actionDao;
	}
	
	@Autowired
	public void setFactory(EntityFactory factory) {
		this.factory = factory;
	}

	@After
	public void teardown() {
		this.userDao.deleteAll();
	}
	
	@Test
	public void testCreateSimpleUser() throws Exception{
		
		// GIVEN the database is clean
		// AND one element and one user
		ElementEntity element = 
			this.factory.createNewElement("Avi", "Task", new Location(1.0,1.0),
					new Date(), "tavb@gmail.com", "2019b.dana.zuka",false, new HashMap<>());
		element = this.elementDao.create(element);
		
		ActionEntity action =
				this.factory.createNewAction(element.getKey(), element.getCreatorSmartspace(), "type",
						new Date(), "tav@gmail", "playerSmartspace",new HashMap<>());
						
		// WHEN we create a new user and store it in DB
		String text = "Test";
		UserEntity user = 
			this.factory.createNewUser(action.getPlayerEmail(), action.getActionSmartspace(),
					"username", "avata", UserRole.PLAYER,1);
		user = this.userDao.create(user);
				
		// THEN the user is stored 
		assertThat(this.userDao.readAll().contains(user));
	}
}
	

