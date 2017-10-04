package br.ufpe.cin.mapping;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import edu.kit.joana.api.sdg.SDGInstruction;
import edu.kit.joana.api.sdg.SDGMethod;
import edu.kit.joana.api.sdg.SDGProgram;

public class LineMappingGenerator {

	public static void createLineMapping(SDGProgram program, File zipFile,
			Properties projectProp) throws FileNotFoundException, IOException {
		Map<String, Integer> lineMapping = new HashMap<>();
		
		Collection<SDGMethod> allMethods = program.getAllMethods();
		for (SDGMethod sdgMethod : allMethods) {
			List<SDGInstruction> instructions = sdgMethod.getInstructions();
			for (SDGInstruction sdgInstruction : instructions) {
				int lineNumber = sdgMethod.getMethod().getLineNumber(
						sdgInstruction.getBytecodeIndex());
				lineMapping.put(sdgInstruction.toString(), lineNumber);
			}
		}
		registerMapping(zipFile, projectProp, lineMapping);
	}

	private static void registerMapping(File zipFile, Properties projectProp,
			Map<String, Integer> mapInstructionsLines)
			throws FileNotFoundException, IOException {
		String mappingName = projectProp.getProperty("lineMappingsPath")
				+ FilenameUtils.removeExtension(zipFile.getName()) + ".json";
		File f = new File(mappingName);
		if (!f.exists()) {
			System.out.println("Generating Line Mapping for contribution");
			Gson gson = new Gson();
			Type mapType = new TypeToken<HashMap<String, Integer>>() {
			}.getType();
			JsonWriter writer = new JsonWriter(new FileWriter(mappingName));
			gson.toJson(mapInstructionsLines, mapType, writer);
			writer.close();
			System.out.println("Line mapping generated");
		} else {
			System.out.println("Line Mapping already exist for "
					+ zipFile.getName());
		}

	}

	public static Map<String, Integer> loadMapping(Properties p, File mapping)
			throws FileNotFoundException, IOException {
		String mappingPath = p.getProperty("lineMappingsPath")
				+ FilenameUtils.removeExtension(mapping.getName()) + ".json";
		Map<String, Integer> mapInstructionsLines = null;

		Gson gson = new Gson();
		JsonReader reader = new JsonReader(new FileReader(mappingPath));
		mapInstructionsLines = gson.fromJson(reader,
				new TypeToken<HashMap<String, Integer>>() {
				}.getType());
		reader.close();
		return mapInstructionsLines;
	}
}
