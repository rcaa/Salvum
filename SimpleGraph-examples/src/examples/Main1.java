package examples;

public class Main1 {
	
	
	public void printInt() {
		System.out.println(0);
	}
	
	public static void main(String[] args) {
		int x;
		int y;
		x = 10;
		if (x < 1234) {
			Main1 m = new Main1();
			m.printInt();
		}
		y = x;
		System.out.println(y);
	}
}