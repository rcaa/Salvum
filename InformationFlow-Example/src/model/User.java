package model;

//#if USER
public class User {

	private String login;
	private String passwordHash;
	
	public User() {}
	
	public User(String login, String passwordHash) {
		super();
		this.login = login;
		this.passwordHash = passwordHash;
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

	@Override
	public String toString() {
		return "User: " + this.login + " " + this.passwordHash;
	}

	public static User[] list() {
		User u1 = new User("rcaa", "hash1");
		User u2 = new User("phmb", "hash2");
		User u3 = new User("lmt", "hash3");
		return new User[] {u1, u2, u3};
	}
}
//#endif