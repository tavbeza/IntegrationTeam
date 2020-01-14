package smartspace.layout;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import smartspace.data.UserEntity;
import smartspace.logic.UserService;

@RestController
public class UserController {

	private UserService userService;

	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}

	@RequestMapping(
			path = "/smartspace/admin/users/{adminSmartspace}/{adminEmail}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary[] exportUsers(
			@PathVariable("adminSmartspace") String adminSmartspace,
			@PathVariable("adminEmail") String adminEmail,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size) {
		return this.userService.getUsingPagination(adminSmartspace + "#" + adminEmail, page, size)
				.stream()
				.map(UserBoundary::new)
				.collect(Collectors.toList())
				.toArray(new UserBoundary[0]);
	}

	@RequestMapping(
			path = "/smartspace/admin/users/{adminSmartspace}/{adminEmail}",
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary[] importUsers(
			@RequestBody UserBoundary[] userBoundaryArray,
			@PathVariable("adminSmartspace") String adminSmartspace,
			@PathVariable("adminEmail") String adminEmail) {
		UserEntity[] userEntityArray = new UserEntity[userBoundaryArray.length];
		for (int i = 0; i < userEntityArray.length; i++) {
			userEntityArray[i] = userBoundaryArray[i].convertToEntity();
		}
		return this.userService.importUsers(userEntityArray, adminSmartspace + "#" + adminEmail)
				.stream()
				.map(UserBoundary::new)
				.collect(Collectors.toList())
				.toArray(new UserBoundary[0]);
	}

	@RequestMapping(
			path = "/smartspace/users",
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary createNewUser(
			@RequestBody NewUserForm userForm) {
		return new UserBoundary(this.userService.newUser(userForm.convertToEntity()));
	}

	@RequestMapping(
			path = "/smartspace/users/login/{userSmartspace}/{userEmail}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary login(
			@PathVariable("userSmartspace") String userSmartspace,
			@PathVariable("userEmail") String userEmail) {
		return new UserBoundary(this.userService.login(userSmartspace + "#" + userEmail));
	}

	@RequestMapping(
			path = "/smartspace/users/login/{userSmartspace}/{userEmail}",
			method = RequestMethod.PUT,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public void update(
			@PathVariable("userSmartspace") String userSmartspace,
			@PathVariable("userEmail") String userEmail, @RequestBody UserBoundary user) {
		this.userService.update(user.convertToEntity(), userSmartspace + "#" + userEmail);
	}

}
