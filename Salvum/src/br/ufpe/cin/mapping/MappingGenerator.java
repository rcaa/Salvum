package br.ufpe.cin.mapping;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import br.ufpe.cin.clazz.preprocessor.ClazzContextManager;
import br.ufpe.cin.clazz.preprocessor.ClazzPreprocessor;
import br.ufpe.cin.clazz.preprocessor.PreprocessorException;
import br.ufpe.cin.policy.PolicyClazz;
import br.ufpe.cin.policy.PolicyContribution;
import br.ufpe.cin.preprocessor.ContextManagerContribution;
import br.ufpe.cin.preprocessor.ContributionPreprocessor;
import br.ufpe.cin.util.FileUtil;
import br.ufpe.cin.util.ZipUtil;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class MappingGenerator {

	public static void main(String[] args) {
		if (args[0] == null || args[0].isEmpty()) {
			System.out.println("Invalid zip property path");
			return;
		}
		String zipPropPath = args[0];
		Properties zipProp = FileUtil.getPropertiesFile(zipPropPath);
		String zipDirectoryPath = zipProp.getProperty("zipDirectoryPath");
		String unzipedDirectory = zipProp.getProperty("unzipedDirectory");
		File zipDir = new File(zipDirectoryPath);
		if (zipDir.isDirectory()) {
			try {
				File[] zipFiles = zipDir.listFiles();
				for (File zipFile : zipFiles) {
					if (zipFile.isDirectory()) {
						continue;
					}
					ZipUtil.unzip(zipFile.getAbsolutePath(), unzipedDirectory);
					String projectPropPath = zipProp.getProperty("propFile");
					Properties projectProp = FileUtil
							.getPropertiesFile(projectPropPath);

					createMapping(args, unzipedDirectory, zipFile, projectProp);

					FileUtils.deleteDirectory(new File(unzipedDirectory
							+ projectProp.getProperty("projectName")));

				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void createMapping(String[] args, String unzipedDirectory,
			File zipFile, Properties projectProp) throws IOException,
			FileNotFoundException {
		Map<String, Set<Integer>> mapClassLines = null;
		if (args.length > 1 && args[1] != null && !args[1].isEmpty()
				&& args[1].equals("contribution")) {
			// Path policyPath = FileSystems.getDefault().getPath(
			// projectProp.getProperty("policyDirectory"));
			// List<String> hashes = PolicyContribution.findHashes(
			// policyPath,
			// projectProp.getProperty("targetPathDirectory"));
			String zipFileName = zipFile.getName();
			String hash = zipFileName.substring(zipFileName.indexOf('-') + 1,
					zipFileName.indexOf('.'));
			// comparo as hashes dos zips com as hashes resultantes
			// da policy
			// if (hashes.contains(hash)) {
			mapClassLines = preprocessMappingContribution(projectProp, hash);
			registerMapping(zipFile, projectProp, mapClassLines, "-contribution");
			// } else {
			// continue;
			// }
		} else {
			mapClassLines = preprocessMappingClazz(projectProp);
			registerMapping(zipFile, projectProp, mapClassLines, "-clazz");
		}
		
		mapClassLines.clear();
	}

	private static Map<String, Set<Integer>> preprocessMappingClazz(
			Properties projectProp) throws IOException {
		Path path = FileSystems.getDefault().getPath(
				projectProp.getProperty("policyDirectory"));
		PolicyClazz policy = new PolicyClazz(path);
		String sourceDirectory = projectProp.getProperty("targetPathDirectory");
		ClazzPreprocessor cp = new ClazzPreprocessor(sourceDirectory, policy
				.getMethodsAndArgs().keySet());
		try {
			cp.execute(projectProp);
		} catch (PreprocessorException e1) {
			e1.printStackTrace();
		}
		return ClazzContextManager.getInstance().getMapClassLines();
	}

	private static Map<String, Set<Integer>> preprocessMappingContribution(
			Properties projectProp, String hash) throws IOException {
		Path path = FileSystems.getDefault().getPath(
				projectProp.getProperty("policyDirectory"));
		PolicyContribution policy = new PolicyContribution(path, hash);
		ContributionPreprocessor cp = new ContributionPreprocessor(projectProp,
				policy.getHash());
		cp.preprocess();
		return ContextManagerContribution.getContext()
				.getMapClassesLineNumbers();
	}

	private static void registerMapping(File zipFile, Properties projectProp,
			Map<String, Set<Integer>> mapClassLines, String flag)
			throws FileNotFoundException, IOException {
		String mappingName = projectProp.getProperty("mappingsPath")
				+ FilenameUtils.removeExtension(zipFile.getName()) + flag + ".json";
		File f = new File(mappingName);
		if (!f.exists()) {
			Gson gson = new Gson();
			Type mapType = new TypeToken<HashMap<String, Set<Integer>>>() {
			}.getType();
			JsonWriter writer = new JsonWriter(new FileWriter(mappingName));
			gson.toJson(mapClassLines, mapType, writer);
			writer.close();
		} else {
			System.out
					.println("Mapping already exist for " + zipFile.getName());
		}
	}

	public static Map<String, Set<Integer>> loadMapping(Properties p, File sdg, String flag)
			throws FileNotFoundException, IOException {
		String mappingPath = p.getProperty("mappingsPath")
				+ FilenameUtils.removeExtension(sdg.getName()) + flag + ".json";
		Map<String, Set<Integer>> mapClassLines = null;
		Gson gson = new Gson();
		JsonReader reader = new JsonReader(new FileReader(mappingPath));
		mapClassLines = gson.fromJson(reader,
				new TypeToken<HashMap<String, Set<Integer>>>() {
				}.getType());
		reader.close();
		return mapClassLines;
	}
}
