package smartspace.layout;

import java.util.HashMap;
import java.util.Map;

import smartspace.data.UserEntity;
import smartspace.data.UserRole;

public class UserBoundary {

	private Map<String, String> key;
	private String username;
	private String avatar;
	private String role;
	private long points;

	public UserBoundary() {
	}

	public UserBoundary(UserEntity user) {
		this.key = new HashMap<String, String>();
		this.key.put("smartspace", user.getUserSmartspace());
		this.key.put("email", user.getUserEmail());
		this.username = user.getUsername();
		this.avatar = user.getAvatar();

		if (user.getRole() != null)
			this.role = user.getRole().name();
		else
			this.role = null;

		this.points = user.getPoints();
	}

	public Map<String, String> getKey() {
		return key;
	}

	public void setKey(String userSmartspace, String userEmail) {
		this.key.put(userSmartspace, userEmail);
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

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public long getPoints() {
		return points;
	}

	public void setPoints(long points) {
		this.points = points;
	}

	public UserEntity convertToEntity() {
		UserEntity user = new UserEntity();

		if (this.key != null && this.key.get("smartspace") != null && this.key.get("email") != null
				&& !this.key.get("smartspace").trim().isEmpty() && !this.key.get("email").trim().isEmpty())
			user.setKey(this.key.get("smartspace") + "#" + this.key.get("email"));
		user.setAvatar(this.avatar);
		if(this.points != 0)
			user.setPoints(this.points);
		user.setUsername(this.username);
		if (this.role != null) {
			user.setRole(UserRole.valueOf(this.role));
		} else {
			user.setRole(null);
		}
		return user;
	}

	@Override
	public String toString() {
		return "UserBoundary [userSmartspace=" + this.key.get("smartspace") + ", userEmail=" + this.key.get("email")
				+ ", username=" + username + ", avatar=" + avatar + ", role=" + role + ", points=" + points + "]";
	}

}
