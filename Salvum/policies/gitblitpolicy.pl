com.gitblit.models.UserModel {password,cookie} noflow RefactoringTask
	where RefactoringTask = {c | c.message.contains("Remove Wicket references from")}