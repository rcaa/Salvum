package br.ufpe.cin.policy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PolicyContribution {

	private String hash;
	private String taskName;

	private String operator;
	private Map<String, Set<String>> clazzAndElements;
	private Set<String> securePackages;

	public PolicyContribution(Path path, String hash) throws IOException {
		this.clazzAndElements = new HashMap<String, Set<String>>();
		if (path.toString().endsWith(".pl")) {
			String constraint = new String(Files.readAllBytes(path));
			if (constraint.contains("noflow")) {
				String[] elements = constraint.split("noflow");
				String firstPart = elements[0];
				String[] classAndElements = firstPart.split(", ");
				for (String string : classAndElements) {
					String[] temp = string.split(" ");
					String clazz = temp[0];
					if (temp.length > 1 && (temp[1] != null)) {
						Set<String> classElements = retreiveProgramElements(temp[1]);
						this.clazzAndElements.put(clazz, classElements);
					} else if (temp[0].contains("Contribution")) {
						Set<String> classElements = new HashSet<>();
						this.clazzAndElements.put(clazz, classElements);
						String securePackages = elements[1].substring(
								elements[1].indexOf("{") + 1,
								elements[1].indexOf("}"));
						String[] securePackagesArray = securePackages.split(",");
						this.securePackages = new HashSet<>();
						for (String securePackage : securePackagesArray) {
							this.securePackages.add(securePackage);
						}
					}

				}
				this.operator = "noflow";

				// this.taskName = elements[3].substring(0,
				// elements[3].indexOf("\n"));
				this.hash = hash;
			} else if (constraint.contains("noset")) {
				// String[] elements = policy.split(" ");
				// this.taskName = elements[0].substring(0,
				// elements[0].indexOf("\n"));
				// this.hash = hash;
				// this.setOperator(elements[1]);
				// this.clazz = elements[2];
				// retreiveProgramElements(elements[3]);
			}
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

	public static List<String> findHashes(Path path, String gitPath)
			throws IOException {
		String constraint = new String(Files.readAllBytes(path));
		String term = constraint.substring(constraint.indexOf('"') + 1,
				constraint.lastIndexOf('"'));
		if (constraint.contains("c | c.message.contains")) {
			return GitIntegration.searchCommitHashesFromMessages(gitPath, term);
		} else if (constraint.contains("c | c.author")) {
			return GitIntegration.searchCommitHashesFromAuthor(gitPath, term);
		} else if (constraint.contains("c | !c.package")) {
			return GitIntegration.searchCommitHashesFromPackage(gitPath, term);
		}
		return null;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
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

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Map<String, Set<String>> getClazzAndElements() {
		return clazzAndElements;
	}

	public Set<String> getSecurePackages() {
		return securePackages;
	}

	public void setSecurePackages(Set<String> securePackages) {
		this.securePackages = securePackages;
	}
}
