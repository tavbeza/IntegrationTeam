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

import smartspace.dao.ElementDao;
import smartspace.data.ElementEntity;
import smartspace.data.Location;

import smartspace.data.util.EntityFactory;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "spring.profiles.active=default" })
public class ElementKeyGenerationTests {

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
	public void tearDown() {
		this.elementDao.deleteAll();
	}

	@Test
	public void testCreateElementsAndVerifyUniqueKeys() throws Exception {
		// GIVEN clean database

		// WHEN I create 20 elements
				int size = 20;
				Set<String> keysSet = 
				  IntStream
					.range(1, size + 1) //Integer Stream
					.mapToObj(i->this.factory.createNewElement
							("element #" + i, 
									"task", new Location(1,2), 
									new Date(),
									"tav@gmail.com"," 2019b.dana.zuka",
									false,  new HashMap<>() )) // element Stream
					.map(this.elementDao::create)// element Stream
					.map(ElementEntity::getKey) // String Stream
					.collect(Collectors.toSet());
					
				// THEN they all have unique keys
				assertThat(keysSet)
					.hasSize(size);
			}
}
