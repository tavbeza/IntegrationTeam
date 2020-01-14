package smartspace.data;

import org.springframework.data.mongodb.core.mapping.Document;

//after change to MongoDB
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

@Document(collection = "USERS")
public class UserEntity implements SmartspaceEntity<String> {
	
	@Transient
	private String userSmartspace;
	
	@Transient
	private String userEmail;
	
	private String username;
	private String avatar;
	private UserRole role;
	private long points;
	// mongoDB requirement this attribute
	private String key;

	public UserEntity() {
	}
	
	public UserEntity(String userEmail) {
		this.userEmail = userEmail;
	}
	
	public UserEntity(String userEmail, String username, String avatar, UserRole role, long points) {
		super();
		this.userEmail = userEmail;
		this.username = username;
		this.avatar = avatar;
		this.role = role;
		this.points = points;
	}

	public UserEntity(String userSmartspace, String userEmail, String username, String avatar, UserRole role,
			long points) {
		super();
		this.userSmartspace = userSmartspace;
		this.userEmail = userEmail;
		this.username = username;
		this.avatar = avatar;
		this.role = role;
		this.points = points;
	}

	public String getUserSmartspace() {
		return userSmartspace;
	}

	public void setUserSmartspace(String userSmartspace) {
		this.userSmartspace = userSmartspace;
	}
	
	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public long getPoints() {
		return points;
	}

	public void setPoints(long points) {
		this.points = points;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	@Id
	public String getKey() {
		return this.key;
	}

	@Override
	public void setKey(String key) {
		this.userSmartspace = key.split("#")[0];
		this.userEmail = key.split("#")[1];
		this.key = key;
	}

	@Override
	public String toString() {
		return "UserEntity [userSmartspace=" + userSmartspace + ", userEmail=" + userEmail + ", username=" + username
				+ ", avatar=" + avatar + ", role=" + role + ", points=" + points + "]";
	}
	
	
}
