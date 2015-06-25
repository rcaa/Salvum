package records;

//https://android.googlesource.com/platform/frameworks/base/+/27f592d/core/java/android/os/AsyncResult.java

//#if BASE
public class AsyncResult {

	public Object result;

	public AsyncResult() {
		// result armazena o imsi do aparelho celular
		// IMSI:310150123456789
		// MCC 310 USA
		// MNC 150 AT&T Mobility
		// MSIN 123456789
		result = "310150123456789";
	}
}
//#endif
