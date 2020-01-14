package smartspace;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.TreeSet;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import smartspace.dao.UserDao;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;
import smartspace.data.util.EntityFactory;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "spring.profiles.active=default" })
public class UserKeyGenerationTests {

	private UserDao<String> userDao;
	private EntityFactory factory;

	@Autowired
	public void setUserDao(UserDao<String> userDao) {
		this.userDao = userDao;
	}

	@Autowired
	public void setFactory(EntityFactory factory) {
		this.factory = factory;
	}

	@After
	public void tearDown() {
		this.userDao.deleteAll();
	}

	@Test
	public void testCreateUsersAndVerifyUniqueKeys() throws Exception {
		// GIVEN clean database

		// WHEN I create 2 users
		int size = 2;

		UserEntity userOne = this.factory.createNewUser("Tst1@Afeka", "2019b.dana.zuka", "shalev", "afeka", UserRole.ADMIN,
				100);
		UserEntity userTwo = this.factory.createNewUser("Tst2@Afeka", "2019b.dana.zuka", "shalev", "afeka", UserRole.ADMIN,
				100);
		
		TreeSet<String> KeysSet = new TreeSet<String>();
		KeysSet.add(userOne.getKey());
		KeysSet.add(userTwo.getKey());

		// THEN they all have unique keys
		assertThat(KeysSet).hasSize(size);
	}

}
