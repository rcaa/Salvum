package br.ufpe.cin.policy;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class Policy {

	//#if FEATURE
	private String feature;
	//#elif CONTRIBUTION
//@	private String hash;
//@	private String taskName;
	//#endif
	private String operator;
	private String clazz;
	private Set<String> programElements;

	public Policy(String policyDirectory) {
		Path path = FileSystems.getDefault().getPath(policyDirectory);
		try {
			String policy = new String(Files.readAllBytes(path));
			if (policy.contains("noflow")) {
				String[] elements = policy.split(" ");
				this.clazz = elements[0];
				retreiveProgramElements(elements[1]);
				this.setOperator(elements[2]);
				//#if FEATURE
				this.feature = elements[3];
				//#elif CONTRIBUTION
//@				this.taskName = elements[3].substring(0, elements[3].indexOf("\n"));
//@				this.hash = elements[6].substring(1, elements[6].length() - 1);
				//#endif
			} else if (policy.contains("noset")) {
				String[] elements = policy.split(" ");
				//#if FEATURE
				this.feature = elements[0];
				//#elif CONTRIBUTION
//@				this.taskName = elements[0].substring(0, elements[0].indexOf("\n"));
//@				this.hash = elements[6].substring(1, elements[6].length() - 1);
				//#endif
				this.setOperator(elements[1]);
				this.clazz = elements[2];
				retreiveProgramElements(elements[3]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void retreiveProgramElements(String element) {
		this.programElements = new HashSet<>();
		String programElements = element.substring(1,
				element.length() - 1);
		String[] listOfProgramElements = programElements.split(",");
		for (String programElement : listOfProgramElements) {
			this.getProgramElements().add(programElement);
		}
	}

	//#if FEATURE
	public String getFeature() {
		return feature;
	}

	public void setFeature(String feature) {
		this.feature = feature;
	}
	//#elif CONTRIBUTION
//@	public String getHash() {
//@		return hash;
//@	}
//@
//@	public void setHash(String hash) {
//@		this.hash = hash;
//@	}
//@
//@	public String getTaskName() {
//@		return taskName;
//@	}
//@
//@	public void setTaskName(String taskName) {
//@		this.taskName = taskName;
//@	}
	//#endif

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public Set<String> getSensitiveResources() {
		Set<String> sesitiveResources = new HashSet<>();
		for (String programElement : programElements) {
			sesitiveResources.add(clazz + "." + programElement);
		}
		return sesitiveResources;
	}

	@Override
	public String toString() {
		String elements = "";
		int i = 0;
		for (String programElement : programElements) {
			if (i != programElements.size()) {
				elements = elements + programElement + ",";
			}
			i++;
		}
		
		return this.clazz + " {" + elements + "}" + " "
				+ this.getOperator() + " " + 
				//#if FEATURE
				this.feature + ";";
				//#elif CONTRIBUTION
//@				taskName + " where " + taskName + " = " + "{" + hash + "}";
				//#endif
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Set<String> getProgramElements() {
		return programElements;
	}

	public void setProgramElements(Set<String> programElements) {
		this.programElements = programElements;
	}
}
