package br.ufpe.cin.mapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;

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
				lineMapping.put(sdgMethod.toString(), lineNumber);
			}
		}
		registerMapping(zipFile, projectProp, lineMapping);
	}

	private static void registerMapping(File zipFile, Properties projectProp,
			Map<String, Integer> mapInstructionsLines)
			throws FileNotFoundException, IOException {
		String mappingName = projectProp.getProperty("lineMappingsPath")
				+ FilenameUtils.removeExtension(zipFile.getName());
		FileOutputStream fos = new FileOutputStream(mappingName);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(mapInstructionsLines);
		oos.close();
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Integer> loadMapping(Properties p, File mapping)
			throws FileNotFoundException, IOException {
		FileInputStream fis = new FileInputStream(
				p.getProperty("lineMappingsPath")
						+ FilenameUtils.removeExtension(mapping.getName()));
		ObjectInputStream ois = new ObjectInputStream(fis);
		Map<String, Integer> mapInstructionsLines = null;
		try {
			mapInstructionsLines = (Map<String, Integer>) ois.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		ois.close();
		return mapInstructionsLines;
	}
}
