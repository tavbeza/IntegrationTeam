package smartspace.dao.memory;

import org.junit.Test;
import java.util.List;
import smartspace.data.ElementEntity;
import static org.assertj.core.api.Assertions.assertThat;

public class MemoryElementDaoDeleteAllUnitTests {
	
	@Test
	public void testElementEntityDeleteAll() throws Exception {
		
		// GIVEN a Element is available
		MemoryElementDao dao = new MemoryElementDao();
		// AND  user entities are added to the dao
		ElementEntity elementEntity = new ElementEntity();
		ElementEntity elementEntity2 = new ElementEntity();
		dao.create(elementEntity);
		dao.create(elementEntity2);
		
		// AND delete all elements from dao
		dao.deleteAll();
		
		// WHEN invoking readAll on the dao
		List<ElementEntity> result = dao.readAll();
		
		// THEN the dao is empty
		assertThat(result)
		.isEmpty();	
	}
}
