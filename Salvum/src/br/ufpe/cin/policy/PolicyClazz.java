package br.ufpe.cin.policy;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class PolicyClazz {

	private Map<String, Set<String>> clazzAndElements;
	private String operator;
	private Map<String, Set<String>> methodsAndArgs;

	public PolicyClazz(Path path) throws IOException {
		this.clazzAndElements = new HashMap<String, Set<String>>();
		this.methodsAndArgs = new HashMap<String, Set<String>>();
		if (path.toString().endsWith(".pl")) {
			// ainda recebe texto
			String constraint = new String(Files.readAllBytes(path));
			// X509Utils {CA_Config} noflow Log
			// where Log = {logger.error(..)}
			if (constraint.contains("noflow")) {
				String[] elements = constraint.split("noflow");
				String firstPart = elements[0];
				String[] classAndElements = firstPart.split(", ");
				for (String string : classAndElements) {
					String[] temp = string.split(" ");
					String clazz = temp[0];
					System.out.println("Classe: " + clazz);
					Set<String> classElements = retreiveProgramElements(temp[1]);
					this.clazzAndElements.put(clazz, classElements);
				}
				this.operator = "noflow";
				String secondPart = elements[1];
				String methodsStr = secondPart.substring(
						secondPart.indexOf("{") + 1, secondPart.indexOf("}"));
				String[] methods = methodsStr.split(",");
				for (String meth : methods) {
					// TODO tenho que alterar esse null depois
					this.methodsAndArgs.put(meth, null);
				}
			} else if (constraint.contains("noset")) {

			}
		} else if (path.toString().endsWith(".json")) {
			JSONParser parser = new JSONParser();
			try {
				Object obj = parser.parse(new FileReader(path.toString()));
				JSONObject jsonObject = (JSONObject) obj;

				JSONObject module = (JSONObject) jsonObject.get("module");

				// identifiers como metodos
				if (module.containsKey("identifiers")) {
					JSONArray identifiers = (JSONArray) module
							.get("identifiers");
					for (Object ident : identifiers) {
						// TODO tenho que alterar esse null depois
						this.methodsAndArgs.put(ident.toString().trim(), null);
					}
				}
				if (module.containsKey("identifier")
						&& module.containsKey("method")
						&& module.containsKey("arguments")) {
					//String identifier = (String) module.get("identifier");
					String meth = (String) module.get("method");
					JSONArray arguments = (JSONArray) module.get("arguments");
					Set<String> args = new HashSet<>();
					for (Object argument : arguments) {
						args.add(argument.toString().trim());
					}
					this.methodsAndArgs.put(meth.trim(), args);
				}
				String construct = (String) jsonObject.get("construct");
				this.operator = construct;
				JSONArray classes = (JSONArray) jsonObject.get("classes");
				for (Object clazz : classes) {
					JSONObject jsonObjectClazz = (JSONObject) clazz;
					String class_name = (String) jsonObjectClazz
							.get("class-name");
					JSONArray fields = (JSONArray) jsonObjectClazz
							.get("fields");
					Set<String> classElements = new HashSet<>();
					for (Object field : fields) {
						classElements.add(field.toString().trim());
					}
					this.clazzAndElements.put(class_name.trim(), classElements);
				}
			} catch (ParseException e) {
				e.printStackTrace();
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

	public Map<String, Set<String>> getMethodsAndArgs() {
		return methodsAndArgs;
	}

	public static void main(String[] args) {
		Path path = Paths
				.get("C:\\Doutorado\\workspace\\Salvum\\Salvum\\policies\\TA-clazz.pl");
		try {
			PolicyClazz pc = new PolicyClazz(path);
			System.out.println("Class e elements: " + pc.getClazzAndElements());
			System.out.println("Methods e args: " + pc.getMethodsAndArgs());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
