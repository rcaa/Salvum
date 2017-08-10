package br.ufpe.cin.sdgs;

import java.util.ArrayList;
import java.util.List;

import edu.kit.joana.ifc.sdg.util.JavaMethodSignature;

public class MainAux {

	public static List<String> configureEntryMethods(String entryPoints) {
		String[] entries = entryPoints.split(":");
		List<String> entryMethods = new ArrayList<String>();
		for (String meth : entries) {
			JavaMethodSignature entryMethod = JavaMethodSignature
					.fromString(meth);
			entryMethods.add(entryMethod.toBCString());
		}
		return entryMethods;
	}
}
