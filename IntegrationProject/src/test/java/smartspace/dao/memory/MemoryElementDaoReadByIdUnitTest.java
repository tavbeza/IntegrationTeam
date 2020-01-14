package smartspace.dao.memory;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Test;

import smartspace.data.ElementEntity;

public class MemoryElementDaoReadByIdUnitTest {
	
	@Test
	public void testReadByIdWithValidElement() throws Exception{
	
		// GIVEN MemoryElementDao is initialized
		String smartspace = "2019B.dana.zuka";
		MemoryElementDao dao = new MemoryElementDao(); 
		dao.setSmartspace(smartspace);
		
		// WHEN create valid ElementEntity
		ElementEntity element = new ElementEntity();
		element = dao.create(element);
		// AND read the element into elementOp
		Optional<ElementEntity> elementOp = dao.readById(element.getKey());
		
		//THEN can read the element from dao by Id
		assertThat(elementOp.isPresent());
		assertThat(elementOp.get().getKey().equals(element.getKey()));

		dao.deleteAll();		
	}
	
}
