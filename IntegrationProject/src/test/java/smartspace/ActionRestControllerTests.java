package smartspace;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.PostConstruct;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import smartspace.dao.ActionDao;
import smartspace.dao.AdvancedElementDao;
import smartspace.dao.AdvancedUserDao;
import smartspace.data.ActionEntity;
import smartspace.data.ElementEntity;
import smartspace.data.Location;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;
import smartspace.layout.ActionBoundary;
import smartspace.logic.ActionService;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties= {"spring.profiles.active=default"})

public class ActionRestControllerTests {

	private int port;
	private String baseUrl;
	private RestTemplate restTemplate;
	private ActionDao actionDao;
	private AdvancedElementDao<String> elementDao;
	private ActionService actionService;
	private AdvancedUserDao<String> userDao;

	@Autowired
	public void setActionService(ActionService actionService, AdvancedUserDao<String> userDao, AdvancedElementDao<String> elementDao) {
		this.actionService = actionService;
		this.userDao = userDao;
		this.elementDao = elementDao;
	}


	@Autowired
	public void setActionDao(ActionDao actionDao) {
		this.actionDao = actionDao;
	}

	
	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}

	@PostConstruct
	public void init(){
		this.baseUrl = "http://localhost:" + port + "/smartspace";
		this.restTemplate = new RestTemplate();
	}

	@After
	public void tearDown() {
		this.actionDao.deleteAll();
		this.userDao.deleteAll();
	}

	@Test
	public void testImportAction() {
		// GIVEN the database contains only 1 admin
		UserEntity user = new UserEntity();
		user.setRole(UserRole.ADMIN);
		user.setUserEmail("matan@mail.com");
		user.setAvatar("monkey");
		user.setPoints(13);
		user.setUsername("matan");
		user = userDao.create(user);
		
		// WHEN i import action from other smartspace
		
		ActionBoundary[] ActionArray = new ActionBoundary[1];
		
		ActionBoundary newAction = new ActionBoundary();

		Map<String, String> player = new HashMap<String, String>();
		player.put("smartspace", "playerSmartspace");
		player.put("email", "emailSmartspace");
		newAction.setPlayer(player);
		
		Map<String, String> element = new HashMap<String, String>();
		element.put("id", "12");
		element.put("smartspace", "elementSmartspace");
		newAction.setElement(element);
		
		Map<String, String> key = new HashMap<String, String>();		
		key.put("smartspace", "actionSmartspace");
		key.put("id", "10");
		newAction.setActionKey(key);
		
		newAction.setType("TypeTest");
		newAction.setProperties(new HashMap<String, Object>());
		
		ActionArray[0] = newAction;
		
		ActionBoundary[] response =
				this.restTemplate
					.postForObject(
					this.baseUrl + "/admin/actions/{adminSmartspace}/{adminEmail}", 
					ActionArray, 
					ActionBoundary[].class, user.getUserSmartspace(), user.getUserEmail());
		
		// THEN the database contain the new imported action
		assertThat(this.actionDao.readAll())
			.hasSize(1);
			
		assertThat(response)
			.containsExactly(response[0]);
		
			assertThat(this.actionDao.readAll())
				.usingElementComparatorOnFields("key")
				.contains(response[0].convertToEntity());
		
	}
	
	
	@Test
	public void testExportActionsUsingPagination() throws Exception {
		// GIVEN the database contains 38 elements
		
		// AND the database contains 1 admin user
		UserEntity user = new UserEntity();
		user.setRole(UserRole.ADMIN);
		user.setUserEmail("matan@mail.com");
		user.setAvatar("monkey");
		user.setPoints(13);
		user.setUsername("matan");
		user = userDao.create(user);
		
		int totalSize = 38;
//		ActionEntity a = new ActionEntity(elementSmartspace, elementId, playerSmartspace,
//				playerEmail, actionType, creationTimestamp, moreAttributes)
		List<ActionEntity> all = 
				IntStream.range(1, totalSize + 1)
				.mapToObj(i -> "element@mail" + i)
				.map(email -> new ActionEntity("elementSmartspace", "12", "playerSmartspace", "playerEmail", "typeTest", 
						new Date(), new HashMap<String, Object>()))
				.map(this.actionDao::create).collect(Collectors.toList());

		List<ActionBoundary> lastNine =
				all.stream()
				.skip(30)
				.map(ActionBoundary::new )
				.collect(Collectors.toList());

		// WHEN I getElements using page #3 of size 10
		int size = 10;
		int page = 3;
		ActionBoundary[] results =
				this.restTemplate.
				getForObject(this.baseUrl + "/admin/actions/{adminSmartspace}/{adminEmail}?page={page}&size={size}",
						ActionBoundary[].class, "2019B.dana.zuka", "matan@mail.com", page, size);
		
		// THEN the response contains 8 user
		
		assertThat(results)
		.hasSize(8);
		
	}
	
	@Test
	public void testInvokeAnACtion() throws Exception{
		//GIVEN the database is empty
		
		//WHEN I create a new action 
	
		UserEntity playerUser = new UserEntity();
		playerUser.setRole(UserRole.PLAYER);
		playerUser.setUserEmail("player@mail");
		playerUser = userDao.create(playerUser);
		
		ActionBoundary newAction = new ActionBoundary();
		
		Map<String, String> player = new HashMap<String, String>();
		player.put("smartspace", "2019B.dana.zuka");
		player.put("email", "player@mail");
		newAction.setPlayer(player);
		
		ElementEntity elementEn = new ElementEntity("name", "type", new Location(1, 1), new Date(), "creatorEmail", "2019B.dana.zuka", false, new HashMap<String, Object>());
		elementEn = elementDao.create(elementEn);
		
		Map<String, String> element = new HashMap<String, String>();
		element.put("id", elementEn.getElementId());
		element.put("smartspace", elementEn.getElementSmartspace());
		newAction.setElement(element);
		
		/*Map<String, String> key = new HashMap<String, String>();		
		key.put("smartspace", "actionSmartspace");
		key.put("id", "10");
		newAction.setActionKey(key);*/
		newAction.setActionKey(null);
		newAction.setType("echo");
		newAction.setProperties(new HashMap<String, Object>());
		
		ActionBoundary response = this.restTemplate
				.postForObject(
						this.baseUrl+"/actions",
						newAction, 
						ActionBoundary.class);
		
			// THEN the database contains 1 message
			// AND the returned message is similar to the message in the database
			assertThat(
					this.actionDao.readAll())
				.hasSize(1)
			.usingElementComparatorOnFields("key")
			.containsExactly(response.convertToEntity());
	}
	
	}
