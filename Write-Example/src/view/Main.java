package view;

import model.User;
import controller.UserController;

public class Main {

	public static void main(String[] args) {
		UserController uc = new UserController();
		User user = new User("rcaa", "hash1");
		uc.registerUser(user);
	}
}