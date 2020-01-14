package smartspace.data;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.HashMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "spring.profiles.active=default" })


public class ElementEntityUnitTests {
	

	
	@Test
	public void testSetAndGetElementSmartspace() throws Exception {
		// GIVEN a ElementEntity is available
		// AND a string
		ElementEntity ee = new ElementEntity();
		String expected = "testElementSmartspace";
		
		// WHEN we invoke elementSmartspace with the expected String
		ee.setElementSmartspace(expected);
		
		// THEN the elementSmartspace will equals the expected String
		assertThat(ee.getElementSmartspace()).usingDefaultComparator().isEqualTo(expected);

		ee.setElementSmartspace(null);
	}
	
	
	@Test
	public void testSetAndGetelementId() throws Exception {
		// GIVEN an elementEntity is available
		// AND a string
		ElementEntity ee = new ElementEntity();
		String expected = "testElementId";

		// WHEN we invoke setElementId with the expected String
		ee.setElementId(expected);

		// THEN the elementId will equals the expected String
		assertThat(ee.getElementId()).usingDefaultComparator().isEqualTo(expected);

		ee.setElementId(null);
	}
	
	
	@Test
	public void testSetAndGetelementLocation() throws Exception {
		// GIVEN an elementEntity is available
		// AND a location
		
		ElementEntity ee = new ElementEntity();
		Location location = new Location();
		location.setX(10.1);
		location.setY(10.1);

		
		// WHEN we invoke setLocation with the expected location
		ee.setLocation(location);
		
		// THEN the Location is the expected location
		assertThat(ee.getLocation().getX()).usingDefaultComparator().isEqualTo(location.getX());
		assertThat(ee.getLocation().getY()).usingDefaultComparator().isEqualTo(location.getY());
		ee.setLocation(null);

	}
	
	@Test
	public void testSetAndGetelementName() throws Exception {
		// GIVEN an elementEntity is available
		// AND a String
		
		ElementEntity ee = new ElementEntity();
		String expected = "testElementName";

		// WHEN we invoke setName with the expected String
		ee.setName(expected);

		// THEN the elementName will equals the expected String
		assertThat(ee.getName()).usingDefaultComparator().isEqualTo(expected);

		ee.setElementId(null);
	}
	
	@Test
	public void testSetAndGetelementType() throws Exception {
		// GIVEN an elementEntity is available
		// AND a String
		
		ElementEntity ee = new ElementEntity();
		String expected = "testElementType";

		// WHEN we invoke setType with the expected String
		ee.setType(expected);

		// THEN the elementType will equals the expected String
		assertThat(ee.getType()).usingDefaultComparator().isEqualTo(expected);

		ee.setElementId(null);
	}
	
	@Test
	public void testSetAndGetCreatorSmartspace() throws Exception {
		// GIVEN an elementEntity is available
		// AND a string
		
		ElementEntity ee = new ElementEntity();
		String expected = "testElementCreatorSmartspace";

		// WHEN we invoke setCreatorSmartspace with the expected String
		ee.setCreatorSmartspace(expected);

		// THEN the CreatorSmartspace will equals the expected String
		assertThat(ee.getCreatorSmartspace()).usingDefaultComparator().isEqualTo(expected);

		ee.setCreatorSmartspace(null);
	}
	
	
	
	@Test
	public void testSetAndGetCreatorEmail() throws Exception {
		// GIVEN an elementEntity is available
		// AND a string
		
		ElementEntity ee = new ElementEntity();
		String expected = "testElementCreatorEmail";

		// WHEN we invoke setCreatorEmail with the expected String
		ee.setCreatorEmail(expected);

		// THEN the CreatorEmail will equals the expected String
		assertThat(ee.getCreatorEmail()).usingDefaultComparator().isEqualTo(expected);

		ee.setCreatorEmail(null);
	}
	
	
	@Test
	public void testSetAndGetExpired() throws Exception {
		// GIVEN an elementEntity is available
		// AND a boolean
		
		ElementEntity ee = new ElementEntity();
		boolean expected = true;

		// WHEN we invoke setExpired with the expected boolean
		ee.setExpired(expected);

		// THEN the isExpired will equals the expected boolean
		assertThat(ee.getExpired()).usingDefaultComparator().isEqualTo(expected);

		ee.setExpired(false);	
		}
	
	@Test
	public void testSetAndGetDate() throws Exception {
		// GIVEN an elementEntity is available
		// AND a Date
		
		ElementEntity ee = new ElementEntity();
		Date expected = new Date();
		// WHEN we invoke setCreationTimeDate with the expected Date
		ee.setCreationTimestamp(expected);
		// THEN the getCreationTimeDate will equals the expected Date
		assertThat(ee.getCreationTimestamp()).usingDefaultComparator().isEqualTo(expected);

		ee.setCreationTimestamp(null);	
		}
	

	@Test
	public void testSetAndGetMoreAttributes() throws Exception {
		// GIVEN an elementEntity is available
		// AND a map

		ElementEntity ee = new ElementEntity();
		HashMap<String, Object>  expected = new HashMap<String, Object>();
		// WHEN we invoke setMoreAttributes with the expected <String, Object> 
		expected.put("amit", 1);
		ee.setMoreAttributes(expected);
		// THEN the getMoreAttributes will equals the expected <String, Object>
		assertThat(ee.getMoreAttributes()).usingDefaultComparator().isEqualTo(expected);

		expected.clear();
		ee.setMoreAttributes(null);
		}
	
}
