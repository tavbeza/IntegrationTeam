package smartspace.dao.memory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.beans.factory.annotation.Value;
import smartspace.dao.UserDao;
import smartspace.data.UserEntity;

// @Repository
public class MemoryUserDao implements UserDao<String> {

	private Map<String, UserEntity> memory;
	private AtomicLong serial;
	private String smartspace;
	
	public MemoryUserDao() {
		this.memory = Collections.synchronizedSortedMap(new TreeMap<>());
		this.serial = new AtomicLong(1L);
	}
	
	@Value("${smartspace.name:smartspace}")
	public void setSmartspace(String smartspace) {
		this.smartspace = smartspace;
	}

	@Override
	public UserEntity create(UserEntity user) {
		user.setKey(smartspace + "#" + serial.getAndIncrement());
		user.setUserSmartspace(smartspace);
		this.memory.put(user.getKey(), user);
		return user;
	}

	@Override
	public Optional<UserEntity> readById(String key) {
		UserEntity user = this.memory.get(key);
		if (user != null) {
			return Optional.of(user);
		} else {
			return Optional.empty();
		}
	}

	@Override
	public List<UserEntity> readAll() {
		return new ArrayList<>(this.memory.values());
	}

	@Override
	public void update(UserEntity updateUser) {

		UserEntity existing = this.readById(updateUser.getKey())
				.orElseThrow(() -> new RuntimeException("no element entity with key: " + updateUser.getKey()));

		if(updateUser.getAvatar() != null) {
			existing.setAvatar(updateUser.getAvatar());
		}
		
		if(updateUser.getRole() != null) {
			existing.setRole(updateUser.getRole());
		}
		
		if(updateUser.getUserEmail() != null) {
			existing.setUserEmail(updateUser.getUserEmail());
		}
		
		if(updateUser.getUsername() != null) {
			existing.setUsername(updateUser.getUsername());
		}
		
		if(updateUser.getUserSmartspace() != null) {
			existing.setUserSmartspace(updateUser.getUserSmartspace());
		}
		
		existing.setPoints(updateUser.getPoints());
	}

	@Override
	public void deleteAll() {
		this.memory.clear();
	}

}
