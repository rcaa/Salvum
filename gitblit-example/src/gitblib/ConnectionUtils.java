package gitblib;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

//https://github.com/gitblit/gitblit/blob/3e0c6ca8a65bd4b076cac1451c9cdfde4be1d4b8/src/main/java/com/gitblit/utils/ConnectionUtils.java

public class ConnectionUtils {

	public static URLConnection openConnection(String url, String username,
			char[] password) throws IOException {
		URL urlObject = new URL(url);
		URLConnection conn = urlObject.openConnection();
		// setAuthorization(conn, username, password);
		conn.setUseCaches(false);
		conn.setDoOutput(true);
//		if (conn instanceof HttpsURLConnection) {
//			HttpsURLConnection secureConn = (HttpsURLConnection) conn;
//			secureConn.setSSLSocketFactory(SSL_CONTEXT.getSocketFactory());
//			secureConn.setHostnameVerifier(HOSTNAME_VERIFIER);
//		}
		return conn;
	}
}
