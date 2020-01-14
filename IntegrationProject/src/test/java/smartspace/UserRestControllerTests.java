package smartspace;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.PostConstruct;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import smartspace.dao.UserDao;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;
import smartspace.layout.NewUserForm;
import smartspace.layout.UserBoundary;
import smartspace.logic.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "spring.profiles.active=default" })
public class UserRestControllerTests {

	private int port;
	private String baseUrl;
	private RestTemplate restTemplate;
	private UserDao<String> userDao;
	private UserService userService;

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	@Autowired
	public void setUserDao(UserDao<String> userDao) {
		this.userDao = userDao;
	}

	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}

	@PostConstruct
	public void init() {
		this.baseUrl = "http://localhost:" + port + "/smartspace";
		this.restTemplate = new RestTemplate();
	}

	@After
	public void tearDown() {
		this.userDao.deleteAll();
	}

	@Test
	public void testExportUsingPagination() throws Exception {
		// GIVEN the database contains 38 Users
		// AND the database contains 1 admin user
		UserEntity admin = new UserEntity("tav@mail", "username", "avatar", UserRole.ADMIN, 10);
		admin = userDao.create(admin);
		
		int totalSize = 38;
		
		List<UserEntity> all = 
				IntStream.range(1, totalSize + 1)
				.mapToObj(i -> "User@mail" + i)
				.map(email -> new UserEntity(email, "username", "avatar", UserRole.PLAYER, 10))
				.map(this.userDao::create).collect(Collectors.toList());

		List<UserBoundary> lastNine =
				all.stream()
				.skip(29)
				.map(UserBoundary::new)
				.collect(Collectors.toList());

		// WHEN I getUser using page #3 of size 10
		int size = 10;
		int page = 3;
		UserBoundary[] results =
				this.restTemplate.
				getForObject(this.baseUrl + "/admin/users/{adminSmartspace}/{adminEmail}?page={page}&size={size}",
						UserBoundary[].class, "2019B.dana.zuka", "tav@mail", page, size);
		
		// THEN the response contains 8 user
		assertThat(results).usingElementComparatorOnFields("key").containsExactlyElementsOf(lastNine);
	}
	
	@Test
	public void testImportUsers() throws Exception {
		// GIVEN the database contains an admin user
		UserEntity admin = new UserEntity("tav@mail", "username", "avatar", UserRole.ADMIN, 10);
		admin = userDao.create(admin);
		
		// WHEN I POST one user from local smartspace
		// and POST two user with external smartspace
		
		UserEntity localUser = new UserEntity("local@mail", "local", "avatar", UserRole.PLAYER, 5);
		localUser = userDao.create(localUser);
		UserEntity externalUser1 = new UserEntity("externalSmartspce", "external@mail1", "external1", "avatar", UserRole.PLAYER, 5);
		UserEntity externalUser2 = new UserEntity("externalSmartspce", "external@mail2", "external2", "avatar", UserRole.PLAYER, 7);
		
		UserBoundary localBoundary[]  = new UserBoundary[1]; 
		UserBoundary externalBoundary[]  = new UserBoundary[2]; 
		
		localBoundary[0] = new UserBoundary(localUser);
		externalBoundary[0] = new UserBoundary(externalUser1);
		externalBoundary[1] = new UserBoundary(externalUser2);
		
		try {
			UserBoundary[] wrongBoundary =
					this.restTemplate
						.postForObject(
						this.baseUrl + "/admin/users/{adminSmartspace}/{adminEmail}",
						localBoundary,
						UserBoundary[].class,
						"2019B.dana.zuka",
						"tav@mail");
		} catch(Exception e) {}
		
		UserBoundary[] validBoundary =
				this.restTemplate
					.postForObject(
							this.baseUrl + "/admin/users/{adminSmartspace}/{adminEmail}",
							externalBoundary,
							UserBoundary[].class,
							"2019B.dana.zuka",
							"tav@mail");
		
		// THEN the database contains the external and admin user only 
		// and Post method throws an exception 
		assertThat(this.userDao.readAll()).hasSize(4);
		
		assertThat(this.userDao
				.readAll().get(0)).isEqualToComparingFieldByField(admin);
		
		assertThat(this.userDao
				.readAll().get(2)).isEqualToComparingFieldByField(externalUser1);
	}
	
	@Test
	public void testPostNewUser() throws Exception{
		// GIVEN the database is empty

		// WHEN I POST new user with new user form
		UserEntity user = new UserEntity("tav@mail", "username", "avatar", UserRole.MANAGER, 0);
		
		NewUserForm userForm = new NewUserForm(user);
		
		this.restTemplate
				.postForObject(
				this.baseUrl + "/users",
				userForm,
				UserBoundary.class);
		
		// THEN the database contains a single user
		// AND this user's email , avatar ,role and username fields
		// are exactly the same as the fields in userForm
		// AND his smartspace field is the same as the local project's smartspace
		
		List<UserEntity> rv = this.userDao.readAll();
		assertThat(rv)
			.hasSize(1);
		
		assertThat(rv.get(0)).isNotNull()
			.extracting("userEmail", "username", "avatar", "role", "userSmartspace", "points")
			.containsExactly(user.getUserEmail(), user.getUsername(), user.getAvatar(), user.getRole(),
					"2019B.dana.zuka", user.getPoints());
	}
	
	@Test
	public void testGetLoginWithValidUser() throws Exception{
		// GIVEN the user database contains manager
		
		UserEntity user = new UserEntity("tav@mail", "username", "avatar", UserRole.MANAGER, 0);
		this.userDao.create(user);
		
		// WHEN i login with manager's smartspace and email 		
		UserBoundary result = 
			this.restTemplate
			.getForObject(
					this.baseUrl + "/users/login/{userSmartspace}/{userEmail}",
					UserBoundary.class,
					user.getUserSmartspace() , user.getUserEmail());
		
		// THEN the login will retrieve the user's details.
		assertThat(result).isNotNull().isEqualToComparingFieldByField(new UserBoundary(user));
	}
	
	@Test
	public void testPostNewUserAndLogin() throws Exception{
		// GIVEN the user database is empty

		// WHEN I POST new user with new user form
		UserEntity user = new UserEntity("tav@mail", "username", "avatar", UserRole.MANAGER, 0);
		user.setUserSmartspace(null);
		
		NewUserForm userForm = new NewUserForm(user);
		
		this.restTemplate
			.postForObject(
					this.baseUrl + "/users", 
					userForm, 
					NewUserForm.class);
		
		// And I login with user's smartspace and email 		
		UserBoundary result = 
			this.restTemplate
			.getForObject(
					this.baseUrl + "/users/login/{userSmartspace}/{userEmail}",
					UserBoundary.class,
					"2019B.dana.zuka" , user.getUserEmail());
		
		// THEN the database contains a single user
		// AND this user's email , avatar ,role and username fields
		// are exactly the same as the fields in userForm
		// AND his smartspace field is the same as the local project's smartspace
		// AND the login will retrieve the user's details.
				
		List<UserEntity> rv = this.userDao.readAll();
		assertThat(rv)
			.hasSize(1);
		
		assertThat(result.convertToEntity()).isNotNull()
		.extracting("userEmail", "username", "avatar", "role", "userSmartspace", "points")
		.containsExactly(user.getUserEmail(), user.getUsername(), user.getAvatar(), user.getRole(),
				"2019B.dana.zuka", user.getPoints());
			
	}
	
	@Test
	public void testPutUpdateWithUserInDatabase() throws Exception{
		// GIVEN the user database contains only manager
		
		UserEntity user = new UserEntity("tav@mail", "username", "avatar", UserRole.MANAGER, 0);
		this.userDao.create(user);
		
		// WHEN I update manager's details with updetedUser details using PUT 
		
		UserEntity updatedUser = new UserEntity();
		updatedUser.setAvatar("updateAvatar");
		updatedUser.setUsername("updateUsername");
		
			this.restTemplate
			.put(this.baseUrl + "/users/login/{userSmartspace}/{userEmail}",
					new UserBoundary(updatedUser),
					user.getUserSmartspace(),
					user.getUserEmail());

		
		// THEN the user in the database will have details exactly like updatedUser except for their points.
		assertThat(this.userDao.readAll().get(0)).isNotNull()
			.extracting("username", "avatar")
			.containsExactly(updatedUser.getUsername(), updatedUser.getAvatar());
	}

	/*@Test
	public void testWriteUser() throws Exception {
		// GIVEN the database is clean

		// WHEN I crate new User
		UserBoundary newUser = new UserBoundary();

		newUser.setUsername("username");
		newUser.setAvatar("avatar");
		newUser.setPoints(10);
		newUser.setRole(UserRole.PLAYER.name());
		newUser.setUserEmail("jane@gmail.com");

		UserBoundary response = this.restTemplate.postForObject(this.baseUrl, newUser, UserBoundary.class);

		// THEN the database contains 1 user
		// AND the returned user is similar to the user in the database
		assertThat(this.userDao.readAll()).hasSize(1).usingElementComparatorOnFields("username")
				.containsExactly(response.convertToEntity());

	}

	@Test
	public void testWriteUserAndValidateReturnedKey() throws Exception {
		// GIVEN the database is clean

		// WHEN I post a new User
		UserBoundary newUser = new UserBoundary();
		newUser.setUsername("username");
		newUser.setAvatar("avatar");
		newUser.setPoints(10);
		newUser.setRole(UserRole.PLAYER.name());
		newUser.setUserEmail("jane@gmail.com");

		UserBoundary response = this.restTemplate.postForObject(this.baseUrl, newUser, UserBoundary.class);

		// THEN the returned User json contains a key
		assertThat(response.getUserSmartspace() + response.getUserEmail()).isNotNull().isNotBlank().isNotEmpty();

	}

	@Test
	public void testGetUsersUsingPagination() throws Exception {
		// GIVEN the database contains 38 Users
		int totalSize = 38;
		// @SuppressWarnings("unchecked")//back again to here
		List<UserEntity> all = IntStream.range(1, totalSize + 1).mapToObj(i -> "User@mail" + i)
				.map(name -> new UserEntity(name, "username", "avatar", UserRole.PLAYER, 10))
				.map(this.userService::writeUser).collect(Collectors.toList());

		List<UserBoundary> lastEight = all.stream().skip(30).map(UserBoundary::new).collect(Collectors.toList());

		// WHEN I getUser using page #3 of size 10
		int size = 10;
		int page = 3;
		UserBoundary[] results = this.restTemplate.getForObject(this.baseUrl + "?size={size}&page={page}",
				UserBoundary[].class, size, page);

		// THEN the response contains 8 user
		assertThat(results).usingElementComparatorOnFields("userEmail").containsExactlyElementsOf(lastEight);
	}

	@Test
	public void testGetUsersUsingPaginationWithNoResult() throws Exception {
		// GIVEN the database contains 30 users
		int totalSize = 30;

		IntStream.range(1, totalSize + 1).mapToObj(i -> "user@mail" + i)
				.map(name -> new UserEntity(name, "username", "avatar", UserRole.PLAYER, 10))
				.map(this.userService::writeUser).collect(Collectors.toList());

		// WHEN I getUser using page #3 of size 10
		int size = 10;
		int page = 3;
		UserBoundary[] results = this.restTemplate.getForObject(this.baseUrl + "?size={size}&page={page}",
				UserBoundary[].class, size, page);

		// THEN the response contains no users
		assertThat(results).isEmpty();
	}

	@Test
	public void testGetUsersUsingPaginationOfFirstPage() throws Exception {
		// GIVEN the database contains 38 users
		int totalSize = 38;

		IntStream.range(1, totalSize + 1).mapToObj(i -> "user@mail" + i)
				.map(name -> new UserEntity(name, "username", "avatar", UserRole.PLAYER, 10))
				.map(this.userService::writeUser).collect(Collectors.toList());

		// WHEN I getUser using page #0 of size 100
		int size = 100;
		int page = 0;
		UserBoundary[] results = this.restTemplate.getForObject(this.baseUrl + "?size={size}&page={page}",
				UserBoundary[].class, size, page);

		// THEN the response contains 38 User
		assertThat(results).hasSize(totalSize);
	}

	@Test
	public void testGetUsersWithPattern() throws Exception {
		// GIVEN the database contains 3 user with the name "abc"
		// AND the database contains 2 more user without the name "abc"

		String pattern = "abc";

		List<UserEntity> all = Stream.of("abc@mail", "abxyzabc@mail", "xyabczz@mail", "urjmdl@mail", "ababbac@mail")
				.map(name -> new UserEntity(name, "username", "avatar", UserRole.PLAYER, 10))
				.map(this.userService::writeUser).collect(Collectors.toList());

		List<UserBoundary> userWithPattern = all.stream().filter(act -> act.getUserEmail().contains(pattern))
				.map(UserBoundary::new).collect(Collectors.toList());

		// WHEN I getUser using pattern using page #0 of size 100 with pattern "abc"
		int size = 100;
		int page = 0;
		UserBoundary[] results = this.restTemplate.getForObject(
				this.baseUrl + "/{pattern}/{sortBy}?size={size}&page={page}", UserBoundary[].class, pattern, "key",
				size, page);

		// THEN the response contains 3 Users with "abc" text pattern
		assertThat(results).usingElementComparatorOnFields("userEmail")
				.containsExactlyInAnyOrderElementsOf(userWithPattern);
	}
*/
}
