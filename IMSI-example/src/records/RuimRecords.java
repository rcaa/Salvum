package records;

// https://android.googlesource.com/platform/frameworks/opt/telephony/+/dbb088ab146d2fcb435e75e4ca70f2ff7d00d2c7/src/java/com/android/internal/telephony/cdma/RuimRecords.java

//#if BASE
public class RuimRecords extends IccRecords {

	private String mImsi;
	private AsyncResult ar;

	public RuimRecords(AsyncResult ar) {
		this.ar = ar;
	}

	public void eventGetIMSIDone() {
		mImsi = (String) ar.result;
		// IMSI (MCC+MNC+MSIN) is at least 6 digits, but not more
		// than 15 (and usually 15).
		if (mImsi != null && (mImsi.length() < 6 || mImsi.length() > 15)) {
			// #if LOG
			loge("invalid IMSI " + mImsi);
			// #endif
			mImsi = null;
		}
		// nesse ponto ocorre um falso positivo! Os seis primeiros numeros de um
		// IMSI s‹o irrelevantes, pois representam o modelo e origem do aparelho
		// celular. Milhoes possuem os seis primeiros digitos iguais!
		//#if LOG
		log("IMSI: " + mImsi.substring(0, 6) + "xxxxxxxxx");
		//#endif
	}

	public static void main(String[] args) {
		AsyncResult ar = new AsyncResult();
		RuimRecords rr = new RuimRecords(ar);
		rr.eventGetIMSIDone();
	}

	// #if LOG
	@Override
	protected void log(String s) {
		Log.d("CDMA", "[RuimRecords] " + s);
	}

	@Override
	protected void loge(String s) {
		Log.e("CDMA", "[RuimRecords] " + s);
	}
	// #endif
}
// #endif
