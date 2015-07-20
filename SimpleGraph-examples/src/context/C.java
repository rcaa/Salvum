package context;

public class C extends S {
	
	void fun1() {
		Object a1 = new A1();
		Object b1 = id2(a1);
	}
	
	public Object id2(Object o) {
		return super.id2(o);
	}
}