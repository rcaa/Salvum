package testing;

public class ClassMethodTest {

	private String string1;
	private boolean boolean1;
	private String privateDude;
	private String protectedDude;

	public ClassMethodTest() {
	}

	public ClassMethodTest(String string1, boolean boolean1) {
		this.string1 = string1;
		this.boolean1 = boolean1;
	}

	private String getPrivateDude() {
		return privateDude;
	}

	private void setPrivateDude(String privateDude) {
		this.privateDude = privateDude;
	}

	protected String getProtectedDude() {
		return protectedDude;
	}

	protected void setProtectedDude(String protectedDude, String testing) {
		this.protectedDude = protectedDude;
	}

	public boolean isBoolean1() {
		return boolean1;
	}

	public void setBoolean1(boolean boolean1) {
		this.boolean1 = boolean1;
	}

	public String getString1() {
		return string1;
	}

	public void setString1(String string1) {
		this.string1 = string1;
	}
	
	public static void main(String[] args) {
		
	}
}
