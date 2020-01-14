package smartspace.layout;

import smartspace.data.UserEntity;
import smartspace.data.UserRole;

public class NewUserForm {

	private String email;
	private String role;
	private String username;
	private String avatar;
	
	public NewUserForm() {
	}
	
	public NewUserForm (UserEntity entity) {
		this.email = entity.getUserEmail();
		this.role = entity.getRole().name();
		this.username = entity.getUsername();
		this.avatar = entity.getAvatar();
	}

	public String getAvatar() {
		return avatar;
	}
	public String getEmail() {
		return email;
	}
	public String getRole() {
		return role;
	}
	public String getUsername() {
		return username;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	public UserEntity convertToEntity() {
		UserEntity user = new UserEntity();

		user.setUserEmail(this.email);
		user.setAvatar(this.avatar);
		user.setUsername(this.username);
		if(this.role != null) {
			user.setRole(UserRole.valueOf(this.role));
		}
		else {
			user.setRole(null);
		}
		
		return user;
	}
	
}
