package methods;

import java.io.IOException;
import java.lang.reflect.Method;

import testing.ClassMethodTest;

public class ClassMethod {

	public static void main(String args[]) throws IOException {

		ClassMethodTest t = new ClassMethodTest("val1", false);

		Method[] methods = t.getClass().getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			Class<?>[] parameterTypes = methods[i].getParameterTypes();
			String fullMethodName = methods[i].getDeclaringClass().getName()
					+ "." + methods[i].getName() + "(";
			fullMethodName = formatParameters(parameterTypes, fullMethodName);
			fullMethodName = fullMethodName + ")";
			fullMethodName = formatReturningType(methods, i, fullMethodName);
			System.out.println(fullMethodName);
		}
		// Constructor[] constructors = tClass.getDeclaredConstructors();
		// for (int i = 0; i < constructors.length; i++) {
		// System.out.println(constructors[i]);
		// }
	}

	private static String formatReturningType(Method[] methods, int i,
			String fullMethodName) {
		Class<?> returnType = methods[i].getReturnType();
		String returningTypeWithBars = returnType.getName().replace('.', '/');
		if (returningTypeWithBars.equals("void")) {
			fullMethodName = fullMethodName + "V";
		} else if (returnType.isArray() || returnType.isPrimitive()) {
			fullMethodName = fullMethodName + returningTypeWithBars;
		} else {
			fullMethodName = fullMethodName + "L" + returningTypeWithBars + ";";
		}
		return fullMethodName;
	}

	private static String formatParameters(Class<?>[] parameterTypes,
			String fullMethodName) {
		for (Class<?> paramType : parameterTypes) {
			String paramTypeNameWithBars = paramType.getName()
					.replace('.', '/');
			if (paramType.isArray() || paramType.isPrimitive()) {
				fullMethodName = fullMethodName + paramTypeNameWithBars;
			} else {
				fullMethodName = fullMethodName + "L" + paramTypeNameWithBars
						+ ";";
			}
		}
		return fullMethodName;
	}
}
