package smartspace.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.h2.engine.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import smartspace.dao.AdvancedUserDao;
import smartspace.data.UserEntity;
import smartspace.data.UserRole;

@Service
public class UserServiceImpl implements UserService {
	private AdvancedUserDao<String> users;
	
	@Autowired	
	public UserServiceImpl(AdvancedUserDao<String> users) {
		this.users = users;
	}
	
	@Override
	@Transactional
	public UserEntity newUser(UserEntity user) {
		if (validate(user)) {
			return this.users.create(user);
		}else {
			throw new RuntimeException(" bad user form");
		}
	}
	
	
	private boolean validate(UserEntity userEntity) {
		return userEntity.getUserEmail() != null &&
				userEntity.getUsername() != null &&
				userEntity.getRole() != null &&
				userEntity.getAvatar() != null;
	}

	@Override
	public List<UserEntity> getUsers(int size, int page) {
		return this.users
				.readAll(size, page);
	}
	
	@Override
	public List<UserEntity> getUsers(String sortBy, int size, int page) {
		return this.users
				.readAll(sortBy, size, page);
	}

	@Override
	public Optional<UserEntity> getUserByKey(String key) {
		return users.readById(key);
	}
	
	public List<UserEntity> getUsingPagination(String key, int page, int size) {
		if(!this.users.readById(key).isPresent())
			throw new RuntimeException("The user does not exist in DB!");
		if(this.users.readById(key).get().getRole() != UserRole.ADMIN)
			throw new RuntimeException("This user is not an admin!\n");
		
		List<UserEntity> usersList = new ArrayList<>();
		usersList = this.users.readAll(size, page);
		for(UserEntity user : usersList)
			user.setKey(user.getKey());
		return usersList;
	}

	@Override
	public List<UserEntity> importUsers(UserEntity[] users, String key) {
		if(!this.users.readById(key).isPresent())
			throw new RuntimeException("The user does not exist in DB!");
		if(this.users.readById(key).get().getRole() != UserRole.ADMIN)
			throw new RuntimeException("This user is not an admin!\n");
		
		List<UserEntity> usersList = new ArrayList<>();
		for(UserEntity user : users) {
			if(user.getUserSmartspace().equals("2019B.dana.zuka"))
				throw new RuntimeException("You are trying to import users from your own project-can't do that");
			else
				usersList.add(this.users.create(user));
		}
		
		return usersList;
	}
	
	@Override
	public UserEntity login(String key) {
		Optional<UserEntity> user = this.users.readById(key);
		if(!user.isPresent())
			throw new RuntimeException("user login failed, there is no such user in the DB");
		user.get().setKey(key);
		return user.get();
	}
	
	@Override
	public void update(UserEntity userEntity, String key) {
		this.users.readById(key).orElseThrow(() -> new RuntimeException("update user failed, there is no such user in the DB"));
		
		userEntity.setKey(key);
		this.users.update(userEntity);
	}

}
