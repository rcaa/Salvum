package typebasedLimitation;

public class Main {

	public int publicOutput = 0;
	public boolean confidential = true;
	
	public static void main(String[] args) {
		Main m = new Main();
		
		if (m.confidential) {
			m.publicOutput = 87;
		} else {
			m.publicOutput = 40;
		}
		m.publicOutput = 2008;
		m.print(m.publicOutput);
	}

	private void print(int publicOutput2) {
		System.out.println(publicOutput2);
		
	}
	
}
