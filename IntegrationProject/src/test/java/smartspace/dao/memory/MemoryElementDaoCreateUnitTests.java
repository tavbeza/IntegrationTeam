package smartspace.dao.memory;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import smartspace.data.ElementEntity;

public class MemoryElementDaoCreateUnitTests {
	
	@Test
	public void testCreateWithValidElement() throws Exception{
		
		// GIVEN MemoryElementDao is initialized
		String smartspace = "2019B.dana.zuka";
		MemoryElementDao dao = new MemoryElementDao(); 
		dao.setSmartspace(smartspace);
		
		// WHEN create with Valid ElementEntity
		ElementEntity newElementEntity = new ElementEntity();
		ElementEntity createdElement = dao.create(newElementEntity);
		
		// THEN the returned element has a valid key
		// AND the dao is added with the element
		// AND no exception is thrown
		
		assertThat(createdElement.getKey())
		.isNotNull()
		.startsWith(smartspace);
		
		assertThat(dao.readAll())
		.contains(newElementEntity);

	}
	
	@Test
	public void testCreateTwoElements() throws Exception{
		// GIVEN MemoryElementDao is initialized
		String smartspace = "2019B.dana.zuka";
		MemoryElementDao dao = new MemoryElementDao(); 
		dao.setSmartspace(smartspace);
		
		// WHEN I create 2 elements
		List<ElementEntity> list = 
			Stream.of("test", "test2") // String Stream
			.map(element->new ElementEntity()) // ElementEntity Stream
			.map(element->dao.create(element)) // ElementEntity Stream
			.collect(Collectors.toList());
		
		// THEN the dao contains exactly 2 elements
		// AND the elements' Keys are different
		assertThat(dao.readAll())
			.hasSize(2)
			.containsExactlyElementsOf(list);
		assertThat(list.get(0))
			.usingComparatorForFields((String k1, String k2)->k1.compareTo(k2), "key")
			.isNotEqualTo(list.get(1));
	}
	
	
	
	
	
	
	
}
