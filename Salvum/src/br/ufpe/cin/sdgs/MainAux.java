package br.ufpe.cin.sdgs;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.kit.joana.ifc.sdg.util.JavaMethodSignature;

public class MainAux {

	public static List<String> configureEntryMethods(Properties p) {
		String[] entries = p.getProperty("main").split(":");
		List<String> entryMethods = new ArrayList<String>();
		for (String meth : entries) {
			JavaMethodSignature entryMethod = JavaMethodSignature
					.fromString(meth);
			entryMethods.add(entryMethod.toBCString());
		}
		return entryMethods;
	}
}
