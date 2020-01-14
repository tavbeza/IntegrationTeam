package smartspace;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import smartspace.dao.AdvancedUserDao;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;
import smartspace.data.util.EntityFactory;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = { "spring.profiles.active=default" })
public class AdvancedUserDaoTests {

	private AdvancedUserDao<String> usersDao;
	private EntityFactory factory;

	@Autowired
	public void setUsersDao(AdvancedUserDao<String> usersDao) {
		this.usersDao = usersDao;
	}

	@Autowired
	public void setFactory(EntityFactory factory) {
		this.factory = factory;
	}

	@After
	public void tearDown() {
		this.usersDao.deleteAll();
	}

	@Test
	public void testReadAll() throws Exception {

		// GIVEN the database contains only 20 users
		IntStream.range(0, 20).mapToObj(
				i -> this.factory.createNewUser("userEmail" + i, "2019b.dana.zuka", "tst", "afk", UserRole.PLAYER, 100))
				.forEach(this.usersDao::create);

		// WHEN I read all the users
		List<UserEntity> actual = this.usersDao.readAll();

		// THEN I receive 20 users
		assertThat(actual).hasSize(20);

	}

	@Test
	public void testReadAllWithPagination() throws Exception {

		// GIVEN the database contains 20 users
		IntStream.range(0, 20).mapToObj(
				i -> this.factory.createNewUser("Afeka" + i, "2019b.dana.zuka", "sh", "afk", UserRole.PLAYER, 100))
				.forEach(this.usersDao::create);

		// WHEN I read 3 users from page 0
		List<UserEntity> actual = this.usersDao.readAll(3, 0);

		// THEN I receive 3 users
		assertThat(actual).hasSize(3);
	}

}
