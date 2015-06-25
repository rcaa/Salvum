package gitblib;

public class UserModel {

	// https://github.com/gitblit/gitblit/blob/3e0c6ca8a65bd4b076cac1451c9cdfde4be1d4b8/src/main/java/com/gitblit/models/UserModel.java

	private String username;
	private String password;

	public UserModel() {
	}

	public UserModel(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}
}
