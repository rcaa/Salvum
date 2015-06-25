package gitblib;
//#if LOGGER
public class Logger {

	public void warn(String string) {
		System.out.println(string);
	}

	public void error(String string, Exception e2) {
		System.out.println(string + " " + e2.getMessage());
	}
}
//#endif
