package smartspace;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

import smartspace.dao.ElementDao;
import smartspace.dao.UserDao;
import smartspace.data.ElementEntity;
import smartspace.data.Location;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;
import smartspace.layout.ElementBoundary;

import smartspace.logic.ElementService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "spring.profiles.active=default" })
public class ElementRestControllerTests {

	private int port;
	private String baseUrl;
	private RestTemplate restTemplate;
	private ElementDao<String> elementDao;
	private ElementService elementService;
	private UserDao<String> userDao;
	
	@Autowired
	public void setElementService(ElementService elementService) {
		this.elementService = elementService;
	}

	@Autowired
	public void setElementDao(ElementDao<String> elementDao,UserDao<String> userDao) {
		this.elementDao = elementDao;
		this.userDao = userDao;
	}

	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}

	@PostConstruct
	public void init() {
		this.baseUrl = "http://localhost:" + port + "/smartspace/";
		this.restTemplate = new RestTemplate();
	}

	@After
	public void tearDown() {
		this.elementDao.deleteAll();
		this.userDao.deleteAll();
	}

	@Test
	public void testCreateNewElementManagerORplayer() throws Exception {
		// Given the database is clean
		UserEntity user= new UserEntity();
		user.setRole(UserRole.MANAGER);
		user.setUserEmail("tav@com");
		user.setAvatar("monkey");
		user.setPoints(13);
		user.setUsername("matan");
		user.setUserSmartspace("userSmartspace");
		user.setKey(user.getUserSmartspace()+"#"+user.getUserEmail());
		
		user =userDao.create(user);
		
		HashMap<String, String> keyHashMap = new HashMap<String, String>();
		keyHashMap.put("id", "12");
		keyHashMap.put("smartspace", "smartspacename");

		HashMap<String, String> creatorHashMap = new HashMap<String, String>();
		creatorHashMap.put("email", "email");
		creatorHashMap.put("smartspace", "smartspacename");

		Map<String, Double> latlngHashMap = new HashMap<String, Double>();
		latlngHashMap.put("lat", 32.1);
		latlngHashMap.put("lng", 84.8);

		// when i create new element and read it
		ElementBoundary[] elements =new ElementBoundary[1];

		ElementBoundary newElement = new ElementBoundary();
		newElement.setKey(keyHashMap);
		newElement.setName("name");
		newElement.setElementType("type");
		newElement.setCreator(creatorHashMap);
		newElement.setLatlng(latlngHashMap);
		newElement.setCreated(new Date());
		newElement.setExpired(false);
		newElement.setElementProperties(new HashMap<>());
		elements[0]=newElement;
		
		ElementBoundary response = this.restTemplate.postForObject(
				this.baseUrl + "/elements/{managerSmartspace}/{managerEmail}", newElement, ElementBoundary.class,
				user.getUserSmartspace(), user.getUserEmail());
		// THEN the database contains 1 element
		// AND the returned element is similar to the element in the database
		assertThat(this.elementDao.readAll()).hasSize(1).usingElementComparatorOnFields("key")
				.containsExactly(response.convertToEntity());

	}

	
	
	
	@Test
	public void testImportElementsToDataBase() throws Exception {
		// GIVEN the database is clean
		UserEntity user= new UserEntity();
		user.setRole(UserRole.ADMIN);
		user.setUserEmail("tav@com");
		user.setAvatar("monkey");
		user.setPoints(13);
		user.setUsername("matan");
		user.setUserSmartspace("userSmartspace");
		user.setKey(user.getUserSmartspace()+"#"+user.getUserEmail());
		
		user =userDao.create(user);
		
		HashMap<String, String> keyHashMap = new HashMap<String, String>();
		keyHashMap.put("id", "12");
		keyHashMap.put("smartspace", "smartspacename");

		HashMap<String, String> creatorHashMap = new HashMap<String, String>();
		creatorHashMap.put("email", "email");
		creatorHashMap.put("smartspace", "smartspacename");

		Map<String, Double> latlngHashMap = new HashMap<String, Double>();
		latlngHashMap.put("lat", 32.1);
		latlngHashMap.put("lng", 84.8);

		// WHEN I import array of 1 ElementBoundaries
		ElementBoundary[] elements =new ElementBoundary[1];

		ElementBoundary newElement = new ElementBoundary();
		newElement.setKey(keyHashMap);
		newElement.setName("name");
		newElement.setElementType("type");
		newElement.setCreator(creatorHashMap);
		newElement.setLatlng(latlngHashMap);
		newElement.setCreated(new Date());
		newElement.setExpired(false);
		newElement.setElementProperties(new HashMap<>());
		elements[0]=newElement;

		ElementBoundary[] response = this.restTemplate.postForObject(
				this.baseUrl + "admin/elements/{adminSmartspace}/{adminEmail}", elements, ElementBoundary[].class,
				user.getUserSmartspace(), user.getUserEmail());

		// THEN the database contains 1 element
		// AND the returned element is similar to the element in the database
		assertThat(this.elementDao.readAll())
		.hasSize(1).
		usingElementComparatorOnFields("key")
				.containsExactly(response[0].convertToEntity());

	}


@Test
public void testGetElementssUsingPagination() throws Exception {
	// GIVEN the database contains 38 elements
			// AND the database contains 1 admin user
			UserEntity admin = new UserEntity("tav@mail", "username", "avatar", UserRole.ADMIN, 10);
			admin = userDao.create(admin);
			
			int totalSize = 38;
			
			List<ElementEntity> all = 
					IntStream.range(1, totalSize + 1)
					.mapToObj(i -> "element@mail" + i)
					.map(email -> new ElementEntity("name", "type", new Location(1,2), new Date(), email, 
							"creatorSmartspace", false, new HashMap<String, Object>()))
					.map(this.elementDao::create).collect(Collectors.toList());

			List<ElementBoundary> lastNine =
					all.stream()
					.skip(30)
					.map(ElementBoundary::new )
					.collect(Collectors.toList());

			// WHEN I getElements using page #3 of size 10
			int size = 10;
			int page = 3;
			ElementBoundary[] results =
					this.restTemplate.
					getForObject(this.baseUrl + "admin/elements/{adminSmartspace}/{adminEmail}?page={page}&size={size}",
							ElementBoundary[].class, "2019B.dana.zuka", "tav@mail", page, size);
			
			// THEN the response contains 8 user
			assertThat(results).usingElementComparatorOnFields("key").containsExactlyElementsOf(lastNine);
			
}


@Test
public void testUpdateElement() throws Exception {

	// GIVEN i have empty database
	UserEntity user= new UserEntity();
	user.setRole(UserRole.MANAGER);
	user.setUserEmail("tav@com");
	user.setAvatar("monkey");
	user.setPoints(13);
	user.setUsername("matan");
	user.setUserSmartspace("userSmartspace");
	user.setKey(user.getUserSmartspace()+"#"+user.getUserEmail());
	
	user =userDao.create(user);
	
	HashMap<String, String> keyHashMap = new HashMap<String, String>();
	keyHashMap.put("id", "12");
	keyHashMap.put("smartspace", "smartspacename");

	HashMap<String, String> creatorHashMap = new HashMap<String, String>();
	creatorHashMap.put("email", "email");
	creatorHashMap.put("smartspace", "smartspacename");

	Map<String, Double> latlngHashMap = new HashMap<String, Double>();
	latlngHashMap.put("lat", 32.1);
	latlngHashMap.put("lng", 84.8);

	ElementBoundary newElement = new ElementBoundary();
	newElement.setKey(keyHashMap);
	newElement.setName("name");
	newElement.setElementType("type");
	newElement.setCreator(creatorHashMap);
	newElement.setLatlng(latlngHashMap);
	newElement.setCreated(new Date());
	newElement.setExpired(false);
	newElement.setElementProperties(new HashMap<>());
	
	
	// WHEN i add new element and try to update his details

	ElementEntity elementEntity= newElement.convertToEntity();
	this.elementDao.create(elementEntity);

	System.err.println("+++++++++++++++++++++++"+elementEntity.getElementId());
	ElementBoundary updated = newElement;
	newElement.setName("othername");
	newElement.setElementType("othertype");
	updated.setKey(null);

	// when i create new element and read it


	this.restTemplate.put(
			this.baseUrl + "elements/{managerSmartspace}/{managerEmail}/{elementSmartspace}/{elementId}",
			updated,
			user.getUserSmartspace(),
			user.getUserEmail(),
			elementEntity.getElementSmartspace(),
			elementEntity.getElementId());
	
	// THEN the details are updated
	assertThat(this.elementDao.readById(elementEntity.getKey())).isPresent().get().extracting("name")
			.containsExactly(updated.getName());

	assertThat(this.elementDao.readById(elementEntity.getKey())).isPresent().get().extracting("type")
			.containsExactly(updated.getElementType());


}


@Test
public void testGetSpecificElement() {

	// GIVEN an empty database
	UserEntity user= new UserEntity();
	user.setRole(UserRole.PLAYER);
	user.setUserEmail("tav@com");
	user.setAvatar("monkey");
	user.setPoints(13);
	user.setUsername("matan");
	user.setUserSmartspace("userSmartspace");
	user.setKey(user.getUserSmartspace()+"#"+user.getUserEmail());
	
	user =userDao.create(user);
	
	HashMap<String, String> keyHashMap = new HashMap<String, String>();
	keyHashMap.put("id", "12");
	keyHashMap.put("smartspace", "smartspacename");

	HashMap<String, String> creatorHashMap = new HashMap<String, String>();
	creatorHashMap.put("email", "email");
	creatorHashMap.put("smartspace", "smartspacename");

	Map<String, Double> latlngHashMap = new HashMap<String, Double>();
	latlngHashMap.put("lat", 32.1);
	latlngHashMap.put("lng", 84.8);
	// WHEN i create an element and read it from database
	ElementBoundary newElement = new ElementBoundary();
	newElement.setKey(keyHashMap);
	newElement.setName("name");
	newElement.setElementType("type");
	newElement.setCreator(creatorHashMap);
	newElement.setLatlng(latlngHashMap);
	newElement.setCreated(new Date());
	newElement.setExpired(false);
	newElement.setElementProperties(new HashMap<>());
	// when i create new element and read it

	ElementEntity entity = newElement.convertToEntity();
	this.elementDao.create(entity);

	ElementBoundary returnElement = this.restTemplate.getForObject(
			this.baseUrl + "elements/{userSmartspace}/{userEmail}/{elementSmartspace}/{elementId}",
			ElementBoundary.class, user.getUserSmartspace(), user.getUserEmail(),
			entity.getElementSmartspace(), entity.getElementId());
	// THEN i will get the element i wanted

	assertThat(entity).isNotNull().extracting
	("elementSmartspace").containsExactly(returnElement.getKey().get("smartspace"));
	
	assertThat(entity).isNotNull().extracting
	("elementId").containsExactly(returnElement.getKey().get("id"));
	}


@Test
public void testGetAllElementsByName() throws Exception {
	// GIVEN i the database contains 2 elements with name "a"
	//and 2 elements without "a"
	List<ElementEntity> all = 
			  Stream.of("tav", "dor", "matan",
					"guy")
				.map(elem->new  ElementEntity(
						elem, 
						"type", 
						new Location(), 
						new Date(),
						"email", 
						"smartspace", 
						false, 
						new HashMap<String, Object>()))							
				.map(this.elementDao::create)
				.collect(Collectors.toList());
			
	
			List<ElementBoundary> elementsWithPattern = 
					all
					.stream()
					.filter(msg->msg.getName().contains("dor"))
					.map(ElementBoundary::new)
					.collect(Collectors.toList());
	// WHEN I getElements using Name using page #0 of size 100 with name "a"
			
			
			int size = 100;
			int page = 0;
			
			UserEntity playerUser = new UserEntity();
			playerUser.setRole(UserRole.PLAYER);
			playerUser.setUserEmail("test email");
			playerUser = userDao.create(playerUser);
			
			String name="dor";
			String search="name";
			
			ElementBoundary[] results = 
			  this.restTemplate
				.getForObject(
						this.baseUrl + "/elements/{userSmartspace}/{userEmail}"+
				"?search={search}&value={name}&page={page}&size={size}", 
						ElementBoundary[].class, 
						playerUser.getUserSmartspace(), playerUser.getUserEmail(),
						search,name, page,size);
			
			for (int i=0;i<results.length;i++) {
				System.err.println(results[i].getName());
			}
			// THEN the response contains 3 elements with "dor" text pattern
			assertThat(results)
				.usingElementComparatorOnFields("name")
				.containsExactlyInAnyOrderElementsOf(elementsWithPattern);
	

}



@Test
public void testGetAllElementsByType() throws Exception {
	// GIVEN i the database contains 2 elements with type "type"
	//and 3 elements type not "type"
	List<ElementEntity> all = 
			  Stream.of("type", "type", "noType",
					"noType", "justtype")
				.map(elem->new  ElementEntity(
						"name", 
						elem, 
						new Location(), 
						new Date(),
						"email", 
						"smartspace", 
						false, 
						new HashMap<String, Object>()))							
				.map(this.elementDao::create)
				.collect(Collectors.toList());
			
	
			List<ElementBoundary> elementsWithPattern = 
					all
					.stream()
					.filter(el->el.getType().contains("justtype"))
					.map(ElementBoundary::new)
					.collect(Collectors.toList());
	// WHEN I getElements using Name using page #0 of size 100 with name "da"
			
			
			int size = 100;
			int page = 0;
			//////////////////////////////////////////////
			
			UserEntity playerUser = new UserEntity();
			playerUser.setRole(UserRole.PLAYER);
			playerUser.setUserEmail("test email");
			playerUser = userDao.create(playerUser);
			
			String name="justtype";
			String search="type";
			ElementBoundary[] results = 
			  this.restTemplate
				.getForObject(
						this.baseUrl + "/elements/{userSmartspace}/{userEmail}"+
				"?search={search}&value={name}&page={page}&size={size}", 
						ElementBoundary[].class, 
						playerUser.getUserSmartspace(), playerUser.getUserEmail(),
						search,name, page,size);
			
			
			// THEN the response contains 3 elements with "justtype" text pattern
			assertThat(results)
				.usingElementComparatorOnFields("elementType")
				.containsExactlyInAnyOrderElementsOf(elementsWithPattern);
	
}



@Test
public void tesetGetAllElementsByDistance()throws Exception{
	// GIVEN i the database contains 2 elements with location in range
			//and 3 elements not in range
			List<ElementEntity> all = 
					  Stream.of(new Location(0.5,0.5), new Location(2,1), new Location(2.5,1.5),
							  new Location(3,3), new Location(10,10))
						.map(elem->new  ElementEntity(
								"name", 
								"type", 
								elem, 
								new Date(),
								"email", 
								"smartspace", 
								false, 
								new HashMap<String, Object>()))							
						.map(this.elementDao::create)
						.collect(Collectors.toList());
					
			double distance=4;
					List<ElementBoundary> elementsWithLocation = 
							all
							.stream()
							.filter(msg->msg.getLocation().getX()>=distance-2)
							.filter(msg->msg.getLocation().getX()<distance+2)
							.filter(msg->msg.getLocation().getY()>=distance-3)
							.filter(msg->msg.getLocation().getY()<distance+3)
							.map(ElementBoundary::new)
							.collect(Collectors.toList());
			// WHEN I getElements using Name using page #0 of size 100 with name "da"
					
						
					int size = 100;
					int page = 0;
					
					UserEntity playerUser = new UserEntity();
					playerUser.setRole(UserRole.PLAYER);
					playerUser.setUserEmail("test email");
					playerUser = userDao.create(playerUser);
					
					double x=2;
					double y=3;
					String search="location";
					ElementBoundary[] results = 
					  this.restTemplate
						.getForObject(
								this.baseUrl + "/elements/{userSmartspace}/{userEmail}"+
						"?search={search}&x{x}&y={y}&distance={distance}&page={page}&size={size}", 
								ElementBoundary[].class, 
								playerUser.getUserSmartspace(), playerUser.getUserEmail(),
								search,x,y,distance, page,size);
					
					List<Double> Xs= new ArrayList<>();
					List<Double> Ys=new ArrayList<>();
					
					for (int i=0;i<results.length;i++) {
						Xs.add(results[i].getLatlng().get("lat"));
					}
					for (int i=0;i<results.length;i++) {
						Ys.add(results[i].getLatlng().get("lng"));
					}
					// THEN X and Y in the right range
					for (int i=0;i<Xs.size();i++) {
						assertThat(Xs.get(i)).isLessThan(Xs.get(i)+distance);
						assertThat(Xs.get(i)).isGreaterThanOrEqualTo(Xs.get(i)-distance);

					}
					
					for (int i=0;i<Ys.size();i++) {
						assertThat(Ys.get(i)).isLessThan(Ys.get(i)+distance);
						assertThat(Ys.get(i)).isGreaterThanOrEqualTo(Ys.get(i)-distance);

					}
			
			
			
		}





}
