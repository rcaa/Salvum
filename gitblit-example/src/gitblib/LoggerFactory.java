package gitblib;

// #if LOGGER
public class LoggerFactory {

	private LoggerFactory() {
	}

	public static Logger getLogger(Class<RedmineUserService> class1) {
		return new Logger();
	}

}
// #endif