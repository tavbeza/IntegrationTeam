package smartspace.dao;

import java.util.List;
import java.util.Optional;
import smartspace.data.UserEntity;
	public interface UserDao<UserKey> {

	
	public UserEntity create(UserEntity user);
	
	public Optional<UserEntity> readById(UserKey key);
	
	public List<UserEntity> readAll();
	
	public void update(UserEntity user);
		
	public void deleteAll();

	
}
