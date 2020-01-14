package smartspace;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import smartspace.dao.AdvancedElementDao;
import smartspace.data.ElementEntity;
import smartspace.data.Location;
import smartspace.data.util.EntityFactory;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties= {"spring.profiles.active=default"})
public class AdvancedElementDaoTests {
	private AdvancedElementDao<String> elementDao;
	private EntityFactory factory;
	
	@Autowired
	public void setElementDao(AdvancedElementDao<String> elementDao) {
		this.elementDao = elementDao;
	}

	@Autowired
	public void setFactory(EntityFactory factory) {
		this.factory = factory;
	}
	
	@After
	public void tearDown() {
		this.elementDao.deleteAll();
	}
	
	@Test
	public void testReadAllWithPagination() throws Exception{
		// GIVEN the database contains only 20 elements 
		IntStream.range(0, 20)
			.mapToObj(i->this.factory.createNewElement("text", "Task", new Location(1.0,1.0),
					new Date(), "tavb@gmail.com", "2019b.dana.zuka",false, new HashMap<>()))
			.forEach(this.elementDao::create);
		
		// WHEN I read 3 elements from page 6
		List<ElementEntity> actual = this.elementDao.readAll(3, 6);
		
		// THEN I receive 2 elements
		assertThat(actual)
			.hasSize(2);
	}
	
	@Test
	public void testReadAllWithPaginationAndSortByText() throws Exception{
		// GIVEN the database contains only 10 elements 
		IntStream.range(0, 10)
			.mapToObj(i->this.factory.createNewElement("text #"+i, "Task",
					new Location(1.0,1.0),
					new Date(), 
					"tavb@gmail.com",
					"2019b.dana.zuka",
					false, 
					new HashMap<>()))
			.forEach(this.elementDao::create);
		
		// WHEN I read 2 elements from page 3 and sorting by text
		List<ElementEntity> actual = this.elementDao.readAll("name", 2, 3);
		
		// THEN I receive elements with text containig: "6","7"
		assertThat(actual)
			.usingElementComparatorOnFields("name")
			.containsExactly(
					factory.createNewElement("text #6",  "Task",
							new Location(1.0,1.0),
							new Date(), 
							"tavb@gmail.com",
							"2019b.dana.zuka",
							false, 
							new HashMap<>()),
					factory.createNewElement("text #7",  "Task",
							new Location(1.0,1.0),
							new Date(), 
							"tavb@gmail.com",
							"2019b.dana.zuka",
							false, 
							new HashMap<>()));
	}

	@Test
	public void testReadAllWithPaginationFromTheStartAndSortByText() throws Exception{
		// GIVEN the database contains only 10 elements 
		IntStream.range(0, 10)
			.mapToObj(i->this.factory.createNewElement("text #"+i, "Task",
					new Location(1.0,1.0),
					new Date(), 
					"tavb@gmail.com",
					"2019b.dana.zuka",
					false, 
					new HashMap<>()))
			.forEach(this.elementDao::create);
		
		// WHEN I read 3 elements from page 0 and sorting by text
		List<ElementEntity> actual = this.elementDao.readAll("name", 3, 0);
		
		// THEN I receive elements with text containig: "0","1","2"
		assertThat(actual)
			.usingElementComparatorOnFields("name")
			.containsExactly(
					factory.createNewElement("text #0",  "Task",
							new Location(1.0,1.0),
							new Date(), 
							"tavb@gmail.com",
							"2019b.dana.zuka",
							false, 
							new HashMap<>()),
					factory.createNewElement("text #1", "Task",
							new Location(1.0,1.0),
							new Date(), 
							"tavb@gmail.com",
							"2019b.dana.zuka",
							false, 
							new HashMap<>()),
					factory.createNewElement("text #2", "Task",
							new Location(1.0,1.0),
							new Date(), 
							"tavb@gmail.com",
							"2019b.dana.zuka",
							false, 
							new HashMap<>()));
	}

	@Test
	public void testReadElementsByTextPattern() throws Exception{
		// GIVEN the database contains 4 elements containing "abc"
		
		// AND the database contains 40 elements that do not contain "abc"
		Map<String, Object> details = new HashMap<>();
		details.put("demo", 12);
		
		Stream.of("abc", "xabcz", "abc123", "ababcc")
			.map(text->this.factory.createNewElement(text, "Task",
					new Location(1.0,1.0),
					new Date(), 
					"tavb@gmail.com",
					"2019b.dana.zuka",
					false, 
					details))
			.forEach(this.elementDao::create);
		
		IntStream.range(0, 40)
		.mapToObj(i->this.factory.createNewElement("text", "Task",
				new Location(1.0,1.0),
				new Date(), 
				"tavb@gmail.com",
				"2019b.dana.zuka",
				false, 
				new HashMap<String, Object>()))
		.forEach(this.elementDao::create);
		
		// WHEN I read elements by name pattern "abc" of up to 3 elements skipping the first 3
		List<ElementEntity> result = this.elementDao.readElementByTextPattern("abc", 3, 1);
		
		// THEN we receive 1 element
		assertThat(result)
			.hasSize(1);
		
		assertThat(result.get(0).getName())
			.containsSequence("abc");

	}
	
}
