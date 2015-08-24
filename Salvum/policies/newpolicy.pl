com.gitblit.models.UserModel {password} noflow AuthTask
	where AuthTask = {c | !c.message.contains("auth")}