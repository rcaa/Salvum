model.User {password} noflow DangerousTask
	where DangerousTask = {c | c.message.contains("problematic code")}