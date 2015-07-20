package context;

public class Main {

	public static void main(String[] args) {
		C c = new C();
		D d = new D();
		C c2 = new C();
		c.fun1();
		d.fun2();
		c2.fun1();
	}
}