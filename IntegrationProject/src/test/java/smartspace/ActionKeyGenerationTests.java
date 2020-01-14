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
import smartspace.data.ActionEntity;
import smartspace.data.util.EntityFactory;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "spring.profiles.active=default" })
public class ActionKeyGenerationTests {

	private ActionDao actionDao;
	private EntityFactory factory;

	@Autowired
	public void setActionDao(ActionDao actionDao) {
		this.actionDao = actionDao;
	}

	@Autowired
	public void setFactory(EntityFactory factory) {
		this.factory = factory;
	}

	@After
	public void tearDown() {
		this.actionDao.deleteAll();
	}

	@Test
	public void testCreateActionsAndVerifyUniqueKeys() throws Exception {
		// GIVEN clean database

		
		// WHEN I create 20 Action
		int size = 20;
		Set<String> keysSet = 
				IntStream
				.range(1, size + 1) //Integer Stream
				.mapToObj(i->this.factory.createNewAction("Task #" + i,
						"2019b.dana.zuka", "addTask",
						new Date(), "tavb@gmail.com", "manger", 
						new HashMap<>())
						) // ActionEntity Stream
				.map(this.actionDao::create)// ActionEntity Stream
				.map(ActionEntity::getKey) // String Stream
				.collect(Collectors.toSet());

		// THEN they all have unique keys
		assertThat(keysSet)
		.hasSize(size);
	}
		
		
}
