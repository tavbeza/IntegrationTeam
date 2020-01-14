package smartspace.dao.rdb;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import smartspace.dao.AdvancedUserDao;
import smartspace.data.UserEntity;

@Repository
public class RdbUserDao implements AdvancedUserDao<String> {

	private UserCrud userCrud;
	private String smartspace;

	@Autowired
	public RdbUserDao(UserCrud userCrud) {
		super();
		this.userCrud = userCrud;
	}

	@Value("${smartspace.name:smartspace}")
	public void setSmartspace(String smartspace) {
		this.smartspace = smartspace;
	}

	@Override
	@Transactional
	public UserEntity create(UserEntity user) {
		if (user.getUserSmartspace() != null && user.getUserEmail() != null)
			user.setKey(user.getUserSmartspace() + "#" + user.getUserEmail());
		else
			user.setKey(smartspace + "#" + user.getUserEmail());

		// SQL: INSERT
		if (!this.userCrud.existsById(user.getKey())) {
			return this.userCrud.save(user);
		} else {
			throw new RuntimeException("user already exists with key: " + user.getKey());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<UserEntity> readById(String key) {
		// SQL: SELECT
		return this.userCrud.findById(key);
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserEntity> readAll() {
		List<UserEntity> rv = new ArrayList<>();
		// SQL: SELECT
		this.userCrud.findAll().forEach(rv::add);
		return rv;
	}

	@Override
	@Transactional
	public void update(UserEntity user) {
		UserEntity existing = this.readById(user.getKey())
				.orElseThrow(() -> new RuntimeException("no user entity with key: " + user.getKey()));

		if (user.getAvatar() != null) {
			existing.setAvatar(user.getAvatar());
		}
		if (user.getRole() != null) {
			existing.setRole(user.getRole());
		}
		if (user.getUserEmail() != null) {
			existing.setUserEmail(user.getUserEmail());
		}
		if (user.getUsername() != null) {
			existing.setUsername(user.getUsername());
		}
		if (user.getUserSmartspace() != null) {
			existing.setUserSmartspace(user.getUserSmartspace());
		}
		if (user.getPoints() != 0)
			existing.setPoints(user.getPoints());
		// SQL: UPDATE
		this.userCrud.save(existing);
	}

	@Override
	@Transactional
	public void deleteAll() {
		// SQL: DELETE
		this.userCrud.deleteAll();
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserEntity> readAll(int size, int page) {
		return this.userCrud.findAll(PageRequest.of(page, size)).getContent();
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserEntity> readAll(String sortBy, int size, int page) {
		return this.userCrud.findAll(PageRequest.of(page, size, Direction.ASC, sortBy)).getContent();
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserEntity> getUsersByKey(String key, int page, int size) {
		return this.userCrud.findAllByKeyLike("%" + key + "%", PageRequest.of(page, size));
	}

}
