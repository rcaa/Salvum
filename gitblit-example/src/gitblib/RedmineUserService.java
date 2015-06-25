package gitblib;

// #if AUTH
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import com.google.gson.Gson;

public class RedmineUserService {

	// #if LOGGER
	private final Logger logger = LoggerFactory
			.getLogger(RedmineUserService.class);

	// #endif

	public UserModel authenticate(String username, char[] password) {
		String jsonString = null;
		try {
			// first attempt by username/password
			jsonString = getCurrentUserAsJson(username, password);
		} catch (Exception e1) {
			// #if LOGGER
			logger.warn("Failed to authenticate via username/password against Redmine");
			// #endif
			try {
				// second attempt is by apikey
				jsonString = getCurrentUserAsJson(null, password);
				username = null;
			} catch (Exception e2) {
				// #if LOGGER
				logger.error(
						"Failed to authenticate via apikey against Redmine", e2);
				// #endif
				return null;
			}
		}

		RedmineCurrent current = null;
		try {
			current = new Gson().fromJson(jsonString, RedmineCurrent.class);
		} catch (Exception e) {
			// #if LOGGER
			logger.error("Failed to deserialize Redmine json response: "
					+ jsonString, e);
			// #endif
			return null;
		}

		// executa outras operações e retorna um UserModel
		return new UserModel();

	}

	private String getCurrentUserAsJson(String username, char[] password)
			throws IOException {

		String url = "http://www.teste.com";
		if (!url.endsWith("/")) {
			url.concat("/");
		}
		HttpURLConnection http;
		if (username == null) {
			// apikey authentication
			String apiKey = String.valueOf(password);
			String apiUrl = url + "users/current.json?key=" + apiKey;
			http = (HttpURLConnection) ConnectionUtils.openConnection(apiUrl,
					null, null);
		} else {
			// username/password BASIC authentication
			String apiUrl = url + "users/current.json";
			http = (HttpURLConnection) ConnectionUtils.openConnection(apiUrl,
					username, password);
		}
		http.setRequestMethod("GET");
		http.connect();
		InputStreamReader reader = new InputStreamReader(http.getInputStream());
		return IOUtils.toString(reader);
	}

	private class RedmineCurrent {
		private class RedmineUser {
			public String login;
			public String firstname;
			public String lastname;
			public String mail;
		}
	}

	public static void main(String[] args) {
		RedmineUserService r = new RedmineUserService();
		UserModel u = new UserModel("rcaa", "123");

		r.authenticate(u.getUsername(), u.getPassword().toCharArray());
	}
}
// #endif
