package records;

//https://android.googlesource.com/platform/frameworks/base/+/56a2301/core/java/android/util/Log.java
//#if LOG
public class Log {

	public static void d(String tag, String msg) {
		System.out.println("--------DEBUG--------" + tag + " " + msg);
	}

	public static void e(String tag, String msg) {
		System.out.println("--------ERROR--------" + tag + " " + msg);
	}
}
//#endif
