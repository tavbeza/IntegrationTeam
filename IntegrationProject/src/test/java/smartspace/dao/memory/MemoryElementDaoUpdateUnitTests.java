package smartspace.dao.memory;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import smartspace.data.ElementEntity;

public class MemoryElementDaoUpdateUnitTests {

	@Test
	public void testElementEntityUpdate() throws Exception {

		// GIVEN a dao is available
		MemoryElementDao dao = new MemoryElementDao();
		String smartspace = "smartspace";
		dao.setSmartspace(smartspace);
		// AND a element entity is added to the dao
		ElementEntity elementEntity = new ElementEntity();
		elementEntity = dao.create(elementEntity);
		ElementEntity updateEntity = new ElementEntity();

		// WHEN updating one or more of elementEntity's attributes
		// AND invoking update on the dao with rvUser

		updateEntity.setName("table");
		updateEntity.setExpired(true);
		updateEntity.setKey(elementEntity.getKey());
		dao.update(updateEntity);

		// THEN the user entity with rvUser's key is updated in the dao
		assertThat(updateEntity).isNotNull().isEqualToComparingFieldByField(elementEntity);

		dao.deleteAll();
	}

}
