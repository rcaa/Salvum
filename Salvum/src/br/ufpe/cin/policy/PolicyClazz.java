package br.ufpe.cin.policy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PolicyClazz {

	private Map<String, Set<String>> clazzAndElements;
	private String operator;
	private List<String> methods;

	public PolicyClazz(String constraint) {
		// X509Utils {CA_Config} noflow Log
		// where Log = {logger.error(..)}
		this.clazzAndElements = new HashMap<String, Set<String>>();
		this.methods = new ArrayList<String>();
		if (constraint.contains("noflow")) {
			String[] elements = constraint.split("noflow");
			String firstPart = elements[0];
			String[] classAndElements = firstPart.split(", ");
			for (String string : classAndElements) {
				String[] temp = string.split(" ");
				String clazz = temp[0];
				Set<String> classElements = retreiveProgramElements(temp[1]);
				this.clazzAndElements.put(clazz, classElements);
			}
			this.operator = "noflow";
			String secondPart = elements[1];
			String methodsStr = secondPart.substring(secondPart.indexOf("{") + 1,
					secondPart.indexOf("}"));
			String[] methods = methodsStr.split(",");
			for (String meth : methods) {
				this.methods.add(meth);
			}
		} else if (constraint.contains("noset")) {
		}
	}

	private Set<String> retreiveProgramElements(String element) {
		Set<String> elements = new HashSet<>();
		String programElements = element.substring(1, element.length() - 1);
		String[] listOfProgramElements = programElements.split(",");
		for (String programElement : listOfProgramElements) {
			elements.add(programElement);
		}
		return elements;
	}

	public Set<String> getSensitiveResources(String clazz) {
		Set<String> sR = this.clazzAndElements.get(clazz);
		Set<String> sesitiveResources = new HashSet<>();
		for (String programElement : sR) {
			sesitiveResources.add(clazz + "." + programElement);
		}
		return sesitiveResources;
	}

	public String getOperator() {
		return operator;
	}

	public Map<String, Set<String>> getClazzAndElements() {
		return clazzAndElements;
	}

	public List<String> getMethods() {
		return methods;
	}
}
