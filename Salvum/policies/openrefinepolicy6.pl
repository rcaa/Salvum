com.google.refine.oauth.Credentials {token,secret} noflow UntrustingMemberTask
	where UntrustingMemberTask = {c | c.message.contains("allow people with freeq keys to load data")}