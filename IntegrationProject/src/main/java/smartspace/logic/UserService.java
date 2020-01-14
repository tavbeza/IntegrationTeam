package smartspace.logic;

import java.util.List;
import java.util.Optional;

import smartspace.data.UserEntity;

public interface UserService {
	
	public List<UserEntity> getUsingPagination(String key, int page, int size);
	
	public List<UserEntity> importUsers(UserEntity[] users, String key);
	
	public UserEntity newUser(UserEntity user);
	
	public UserEntity login(String key);
	
	public void update(UserEntity userEntity, String key);

	public List<UserEntity> getUsers(int size, int page);
	
	public List<UserEntity> getUsers(String sortBy, int size, int page);
	
	public Optional<UserEntity> getUserByKey(String key);

}