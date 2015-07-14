package examples;

public class UserManager {

	public boolean login(User user) {
		if (user.getLogin().equals("rca") && user.getPwd().equals("pwd")) {
			Log.printLogin(user);
			return true;
		} else {
			Log.printError("error");
			return false;
		}
	}
}
