package br.ufpe.cin.policy;

// #if CONTRIBUTION
//@
//@import java.io.IOException;
//@import java.util.HashMap;
//@import java.util.HashSet;
//@import java.util.List;
//@import java.util.Map;
//@import java.util.Set;
//@
//#endif
public class Policy {
//#if CONTRIBUTION
//@
//@
//@	private String hash;
//@	private String taskName;
//@
//@	private String operator;
//@	private Map<String, Set<String>> clazzAndElements;
//@
//@	public Policy(String policy, String hash) {
//@
//@		this.clazzAndElements = new HashMap<String, Set<String>>();
//@		if (policy.contains("noflow")) {
//@
//@			String[] elements = policy.split("noflow");
//@			String firstPart = elements[0];
//@			String[] classAndElements = firstPart.split(", ");
//@			for (String string : classAndElements) {
//@				String[] temp = string.split(" ");
//@				String clazz = temp[0];
//@				Set<String> classElements = retreiveProgramElements(temp[1]);
//@				this.getClazzAndElements().put(clazz, classElements);
//@			}
//@			this.operator = "noflow";
//@
//@			// this.taskName = elements[3].substring(0,
//@			// elements[3].indexOf("\n"));
//@			this.hash = hash;
//@		} else if (policy.contains("noset")) {
//@			// String[] elements = policy.split(" ");
//@			// this.taskName = elements[0].substring(0,
//@			// elements[0].indexOf("\n"));
//@			// this.hash = hash;
//@			// this.setOperator(elements[1]);
//@			// this.clazz = elements[2];
//@			// retreiveProgramElements(elements[3]);
//@		}
//@	}
//@
//@	private Set<String> retreiveProgramElements(String element) {
//@		Set<String> elements = new HashSet<>();
//@		String programElements = element.substring(1, element.length() - 1);
//@		String[] listOfProgramElements = programElements.split(",");
//@		for (String programElement : listOfProgramElements) {
//@			elements.add(programElement);
//@		}
//@		return elements;
//@	}
//@
//@	public static List<String> findHashes(String policy,
//@			String targetPathDirectory) throws IOException {
//@		String term = policy.substring(policy.indexOf('"') + 1,
//@				policy.lastIndexOf('"'));
//@		if (policy.contains("c | c.message.contains")) {
//@			return GitIntegration.searchCommitHashesFromMessages(
//@					targetPathDirectory, term);
//@		} else if (policy.contains("c | c.author")) {
//@			return GitIntegration.searchCommitHashesFromAuthor(
//@					targetPathDirectory, term);
//@		} else if (policy.contains("c | !c.package")) {
//@			return GitIntegration.searchCommitHashesFromPackage(
//@					targetPathDirectory, term);
//@		}
//@		return null;
//@	}
//@
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
//@
//@
//@	public Set<String> getSensitiveResources(String clazz) {
//@		Set<String> sR = this.clazzAndElements.get(clazz);
//@		Set<String> sesitiveResources = new HashSet<>();
//@		for (String programElement : sR) {
//@			sesitiveResources.add(clazz + "." + programElement);
//@		}
//@		return sesitiveResources;
//@	}
//@
//@	public String getOperator() {
//@		return operator;
//@	}
//@
//@	public void setOperator(String operator) {
//@		this.operator = operator;
//@	}
//@
//@	public Map<String, Set<String>> getClazzAndElements() {
//@		return clazzAndElements;
//@	}
	// #endif
}

