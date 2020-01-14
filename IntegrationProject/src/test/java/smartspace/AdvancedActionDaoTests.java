package smartspace;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import smartspace.dao.AdvancedActionDao;
import smartspace.data.ActionEntity;
import smartspace.data.util.EntityFactory;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties= {"spring.profiles.active=default"})
public class AdvancedActionDaoTests {
	
	private AdvancedActionDao actionDao;
	private EntityFactory factory;
	
	@Autowired
	public void setActionDao(AdvancedActionDao messageDao) {
		this.actionDao = messageDao;
	}

	@Autowired
	public void setFactory(EntityFactory factory) {
		this.factory = factory;
	}
	
	@Before
	public void setup() {
		actionDao.deleteAll();
	}
	
	@After
	public void tearDown() {
		//this.actionDao.deleteAll();
	}
	
	@Test
	public void testCreate() throws Exception {
		ActionEntity temp = new ActionEntity();
		actionDao.create(temp);
	}
	
	/*
	@Test
	public void testReadAllWithPagination() throws Exception {
		// GIVEN the database contains only 20 actions
		IntStream.range(0, 20)
			.mapToObj(i->this.factory.createNewAction("Task #" + i, "2019b.dana.zuka", 
					"updated task description", new Date(), 
					"player" + i + "@2019b.dana.zuka.com", "2019b.dana.zuka", 
					new HashMap<String, Object>()))
			.forEach(this.actionDao::create);
		
		// WHEN I read 3 actions from page 6
		List<ActionEntity> actual = this.actionDao.readAll(3, 6);
		
		// THEN I receive 2 messages
		assertThat(actual)
			.hasSize(2);
	}
	*/
	
	/*
	@Test
	public void testReadAllWithPagination() throws Exception{
		// GIVEN the database contains only 20 actions
		IntStream.range(0, 20)
			.mapToObj(i->this.factory.createNewAction("Task #" + i, "2019b.dana.zuka", 
					"updated task description", new Date(), 
					"player" + i + "@2019b.dana.zuka.com", "2019b.dana.zuka", 
					new HashMap<String, Object>()))
			.forEach(this.actionDao::create);
		
		// WHEN I read 3 actions from page 6
		List<ActionEntity> actual = this.actionDao.readAll(3, 6);
		
		// THEN I receive 2 messages
		assertThat(actual)
			.hasSize(2);
	}
	
	
	@Test
	public void testReadAllWithPaginationAndSortByElementId() throws Exception{
		// GIVEN the database contains only 10 actions 
		IntStream.range(0, 10)
		.mapToObj(i->this.factory.createNewAction("Task #" + i, "2019b.dana.zuka", 
				"updated task description", new Date(), 
				"player" + i + "@2019b.dana.zuka.com", "2019b.dana.zuka", 
				new HashMap<String, Object>()))
		.forEach(this.actionDao::create);
		
		// WHEN I read 2 actions from page 3 and sorting by elementId
		List<ActionEntity> actual = this.actionDao.readAll("elementId", 2, 3);
		
		// THEN I receive actions with elementId containing: "6","7"
		assertThat(actual)
			.usingElementComparatorOnFields("elementId")
			.containsExactly(
					factory.createNewAction("Task #6", null, null, null, null, null, null),
					factory.createNewAction("Task #7", null, null, null, null, null, null));
	}
	
	@Test
	public void testReadAllWithPaginationFromTheStartAndSortByElementId() throws Exception{
		// GIVEN the database contains only 10 actions 
		IntStream.range(0, 10)
			.mapToObj(i->this.factory.createNewAction("Task #" + i, "2019b.dana.zuka", 
					"updated task description", new Date(), 
					"player" + i + "@2019b.dana.zuka.com", "2019b.dana.zuka", 
					new HashMap<String, Object>()))
			.forEach(this.actionDao::create);
		
		// WHEN I read 3 messages from page 0 and sorting by elementId
		List<ActionEntity> actual = this.actionDao.readAll("elementId", 3, 0);
		
		// THEN I receive messages with text containing: "0","1","2"
		assertThat(actual)
			.usingElementComparatorOnFields("elementId")
			.containsExactly(
					factory.createNewAction("Task #0", null, null, null, null, null, null),
					factory.createNewAction("Task #1", null, null, null, null, null, null),
					factory.createNewAction("Task #2", null, null, null, null, null, null));
	}
	
	@Test
	public void testReadActionsByElementIdPattern() throws Exception{
		// GIVEN the database contains 4 actions containing "ask"
		// AND the database contains 40 messages that do not contain "ask"
		Map<String, Object> details = new HashMap<>();
		details.put("demo", 12);
		
		Stream.of("ask", "Task", "ask123", "abaskc")
			.map(elementId->this.factory.createNewAction(
					elementId, "2019b.dana.zuka", 
					"updated task description", new Date(), 
					"player@2019b.dana.zuka.com", "2019b.dana.zuka", 
					details))
			.forEach(this.actionDao::create);
		
		IntStream.range(0, 40)
		.mapToObj(i->this.factory.createNewAction("Department #" + i, "2019b.dana.zuka", 
				"updated task description", new Date(), 
				"player" + i + "@2019b.dana.zuka.com", "2019b.dana.zuka", 
				new HashMap<String, Object>()))
		.forEach(this.actionDao::create);
		
		// WHEN I read messages by text pattern "abc" of up to 3 messages skipping the first 3
		List<ActionEntity> result = this.actionDao.readActionsByIdPattern("ask", 3, 1);
		
		// THEN we receive 1 message
		assertThat(result)
			.hasSize(1);
		
		assertThat(result.get(0).getElementId())
			.containsSequence("ask");

	}
	
	@Test
	public void testReadActionsWithAvailableFromInRange() throws Exception{
		// GIVEN the database contain 16 actions from yesterday
		// AND the database contain 5 actions from now
		Date yesterday = new Date(System.currentTimeMillis() - 1000*3600*24);
		IntStream.range(5, 5+16)
		.mapToObj(i->this.factory.createNewAction("Task #" + i, "2019b.dana.zuka", 
				"updated task description", new Date(), 
				"player" + i + "@2019b.dana.zuka.com", "2019b.dana.zuka", 
				new HashMap<String, Object>()))
		.peek(msg->
			msg.setCreationTimestamp(yesterday)
		)
		.forEach(this.actionDao::create);

		IntStream.range(0, 5)
		.mapToObj(i->this.factory.createNewAction("Department #" + i, "2019b.dana.zuka", 
				"updated task description", new Date(), 
				"player" + i + "@2019b.dana.zuka.com", "2019b.dana.zuka", 
				new HashMap<String, Object>()))
		.forEach(this.actionDao::create);
		
		// WHEN I read 5 messages created between two days ago and one hour ago with skipping first 3 pages
		Date twoDaysAgo = new Date(System.currentTimeMillis() - 48*3600000);
		Date oneHourAgo = new Date(System.currentTimeMillis() - 3600000);
		List<ActionEntity> list = this.actionDao
				.readActionsWithAvailableFromInRange(
						twoDaysAgo, oneHourAgo, 5, 3);
		
		// THEN I receive 1 message
		assertThat(list)
			.hasSize(1);
	}
	*/
	
}
