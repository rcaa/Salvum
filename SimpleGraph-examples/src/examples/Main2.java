package examples;

public class Main2 {

	public static void main(String[] args) {
		String login = "rca";
		String pwd = "123";
		User u = new User(login, pwd);
		UserManager um = new UserManager();
		um.login(u);
	}
}
