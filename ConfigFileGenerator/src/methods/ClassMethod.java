package methods;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class ClassMethod {

	public static void main(String args[]) throws IOException,
			ClassNotFoundException, SecurityException, InstantiationException,
			IllegalAccessException {

		//String filePath = "C:\\Doutorado\\workspace\\Salvum\\Salvum\\src\\br\\ufpe\\cin\\analyses\\Main.java";
		//String className = "br.ufpe.cin.analyses.Main";

		String filePath = args[0];
		File file = new File(filePath);
		URL url = file.toURI().toURL();
		URLClassLoader classLoader = new URLClassLoader(new URL[] {url});
		String className = args[1];

		Class<?> classDefinition = classLoader.loadClass(className);
		classLoader.close();
				

		Method[] methods = classDefinition.newInstance().getClass()
				.getDeclaredMethods();
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
