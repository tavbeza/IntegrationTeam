package smartspace;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import smartspace.dao.ElementDao;
import smartspace.data.util.EntityFactory;
import smartspace.data.ElementEntity;
import smartspace.data.Location;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "spring.profiles.active=default" })
public class ElementEntiyDaoDemoIntegrationTests {

	private ElementDao<String> elementDao;
	private EntityFactory factory;

	@Autowired
	public void setElementDao(ElementDao<String> elementDao) {
		this.elementDao = elementDao;
	}

	@Autowired
	public void setFactory(EntityFactory factory) {
		this.factory = factory;
	}

	@After
	public void teardown() {
		this.elementDao.deleteAll();
	}

	@Test
	public void createManyElementsTests() throws Exception {
		// GIVEN we have a clean DAO
		// AND we have a factory

		// WHEN I create 20 elements
		List<ElementEntity> allTasks = IntStream.range(1, 21) // int Stream
				.mapToObj(num -> "task #" + num) // String Stream
				.map(task -> // ElementEntity Stream
				this.factory.createNewElement("task1", "Task", new Location(1.0, 1.0), new Date(), "tavb@gmail.com",
						"2019b.dana.zuka", false, new HashMap<>()))
				.map(this.elementDao::create) // ElementEntity Stream
				.collect(Collectors.toList());

		// check for failed ( for failed do // on line "this.elementDao.create(e);" )
		/*
		 * ElementEntity e = this.factory.createNewElement("task1", "Task", new
		 * Location(1.0,1.0), new Date(), "tavb@gmail.com", "2019b.dana.zuka",false, new
		 * HashMap<>()); this.elementDao.create(e); allTasks.add(e);
		 */

		// THEN the DAO contains 20 elements
		assertThat(this.elementDao.readAll().containsAll(allTasks));
	}

	@Test
	public void testCreateUpdateReadByKeyDeleteAllReadAll() throws Exception {
		// GIVEN we have a DAO
		// AND we have a factory

		// WHEN I create a new element
		// AND I update the element
		// AND I read the element by key
		// AND I delete all
		// AND I read all
		String[] cleaningEmployees = { "2019b.dana.zuka@mail1", "2019b.dana.zuka@mail2", "2019b.dana.zuka@mail3" };
		Map<String, Object> moreAttributes = new HashMap<>();
		moreAttributes.put("departmentName", "clean");
		moreAttributes.put("taskDescribe", "clean the...");
		moreAttributes.put("priority", 1);
		moreAttributes.put("status", enumStatus.IN_PROGRESS);
		moreAttributes.put("employeesAssigned", Arrays.asList(cleaningEmployees));
		ElementEntity task1 = this.factory.createNewElement("task1", "Task", new Location(1.0, 1.0), new Date(),
				"tavb@gmail.com", "2019b.dana.zuka", false, moreAttributes);

		task1 = elementDao.create(task1);
		ElementEntity initTask = new ElementEntity();
		initTask.setKey(task1.getKey());

		Map<String, Object> updatedDetails = new HashMap<>(task1.getMoreAttributes());
		updatedDetails.put("gender", "male");
		updatedDetails.put("age", 32);

		ElementEntity update = new ElementEntity();
		update.setKey(task1.getKey());
		update.setMoreAttributes(updatedDetails);
		this.elementDao.update(update);

		Optional<ElementEntity> elementOp = this.elementDao.readById(task1.getKey());

		this.elementDao.deleteAll();

		List<ElementEntity> listAfterDeletion = this.elementDao.readAll();

		// THEN the initially generated element key is not null
		// AND the element read using key is present
		// AND the key of the element read is not null
		// AND the list after deletion is empty

		assertThat(initTask.getKey()).isNotNull();
		assertThat(elementOp).isPresent().get().extracting("key").doesNotContain((String) null);
		assertThat(listAfterDeletion).isEmpty();

	}


	@Test
	public void addElementDeleteAndCheck() throws Exception {
		// GIVEN we have a DAO

		// WHEN we create element 
		// AND update 
		ElementEntity em = this.factory.createNewElement("tst", "Test", new Location(1.0, 1.0), new Date(),
				"sh@gmail.com", "2019b.dana.zuka", false, new HashMap<>());
		elementDao.create(em);
		this.elementDao.deleteAll();
		
		// THAN the DOA empty
		assertThat(this.elementDao.readAll().isEmpty());
	}

}
