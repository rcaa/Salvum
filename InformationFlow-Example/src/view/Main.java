package view;

import controller.UserController;

public class Main {

	public static void main(String[] args) {
		UserController uc = new UserController();
		try {
			uc.search("rcaa");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}