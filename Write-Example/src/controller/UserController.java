package controller;

import util.Log;
import model.User;

public class UserController {

	public void registerUser(User user) {
		
		long initialTime = System.nanoTime();
		
		// problema principal
		user.setPasswordHash("hacked");
		
		if (user != null) {
			user.registerUser(user);	
		}
		
		long totalNanoTime = System.nanoTime() - initialTime;
		Log.loggingAction("Total operation nano time: " + totalNanoTime);
	}
}