package controller;

//#if LOGGING
import util.Log;
//#endif
//#if USER
import model.User;

public class UserController {

 public User search(String login) throws Exception {
  for (User user : User.list()) {
   if (user != null && user.getLogin().equals(login)) {
    //#if LOGGING
	//Log.loggingAction(user);
	user.setPasswordHash("teste");
	//#endif
	return user;
   }
  }
  throw new Exception("User not found!");
 }
}
//#endif
