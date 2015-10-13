com.gitblit.servlet.GitblitContext {goSettings} noflow AuthTask
	where AuthTask = {c | c.message.contains("Prevent double authentication for the same public key")}