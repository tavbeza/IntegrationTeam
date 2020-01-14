package smartspace.dao.memory;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import smartspace.data.ElementEntity;

public class MemoryElementDaoDeleteUnitTests {

	@Test
	public void testElementEntityDeleteAll() throws Exception {

		// GIVEN a Element is available
		MemoryElementDao dao = new MemoryElementDao();
		// AND user entities are added to the dao
		ElementEntity elementEntity = new ElementEntity();
		ElementEntity elementEntity2 = new ElementEntity();
		dao.create(elementEntity);
		dao.create(elementEntity2);
		
		// AND delete an element from dao
		dao.delete(elementEntity);
		
		// WHEN invoking readAll on the dao
		List<ElementEntity> result = dao.readAll();

		// THEN the dao contains exactly elementEntity2
		assertThat(result).containsExactly(elementEntity2);
				
	}
	
}
