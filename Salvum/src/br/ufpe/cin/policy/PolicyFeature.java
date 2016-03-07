package br.ufpe.cin.policy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PolicyFeature {

	private String featureName;
	private Map<String, Set<String>> clazzAndElements;
	private String operator;

	public PolicyFeature(String constraint) {
		// Text {text} noflow Find where Find = {loc | "#if FIND" - loc -
		// "#endif"}
		this.clazzAndElements = new HashMap<String, Set<String>>();
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
			this.featureName = elements[1].substring(1,
					elements[1].indexOf("\n"));
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

	public String getFeatureName() {
		return featureName;
	}

	public String getOperator() {
		return operator;
	}
	
	public Map<String, Set<String>> getClazzAndElements() {
		return clazzAndElements;
	}
}
