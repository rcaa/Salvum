package model;

import java.util.ArrayList;
import java.util.List;

public class User {

	private String login;
	private String passwordHash;
	private static final List<User> users = new ArrayList<User>();
	
	static {
		User u1 = new User("rcaa", "hash1");
		User u2 = new User("phmb", "hash2");
		User u3 = new User("lmt", "hash3");
		users.add(u1);
		users.add(u2);
		users.add(u3);
	}
	
	public User() {
	}
	
	public User(String login, String passwordHash) {
		super();
		this.login = login;
		this.passwordHash = passwordHash;
	}
	
	public void registerUser(User user) {
		if (user != null) {
			users.add(user);
		}
	}
	
	public String getLogin() {
		return login;
	}
	
	public void setLogin(String login) {
		this.login = login;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
}