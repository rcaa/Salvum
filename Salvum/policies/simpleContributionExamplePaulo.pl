others.C {x} noflow DangerousTask
	where DangerousTask = {c | !c.message.contains("problem")}