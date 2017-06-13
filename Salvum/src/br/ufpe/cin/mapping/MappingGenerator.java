package br.ufpe.cin.mapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;
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
					Map<String, Set<Integer>> mapClassLines = null;
					if (args.length > 1 && args[1] != null
							&& !args[1].isEmpty()
							&& args[1].equals("contribution")) {
						Path policyPath = FileSystems.getDefault().getPath(
								projectProp.getProperty("policyDirectory"));
						List<String> hashes = PolicyContribution.findHashes(
								policyPath,
								projectProp.getProperty("targetPathDirectory"));
						String zipFileName = zipFile.getName();
						String hash = zipFileName.substring(
								zipFileName.indexOf('-'),
								zipFileName.indexOf('.'));
						if (hashes.contains(hash)) {
							mapClassLines = preprocessMappingContribution(
									projectProp, hash);
						} else {
							continue;
						}
					} else {
						mapClassLines = preprocessMappingClazz(projectProp);
					}
					registerMapping(zipFile, projectProp, mapClassLines);
					System.out.println(mapClassLines);
					FileUtils.deleteDirectory(new File(unzipedDirectory
							+ projectProp.getProperty("projectName")));
					mapClassLines.clear();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
			cp.execute(sourceDirectory);
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
			Map<String, Set<Integer>> mapClassLines)
			throws FileNotFoundException, IOException {
		String mappingName = projectProp.getProperty("mappingsPath")
				+ FilenameUtils.removeExtension(zipFile.getName());
		FileOutputStream fos = new FileOutputStream(mappingName);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(mapClassLines);
		oos.close();
		fos.close();
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Set<Integer>> loadMapping(Properties p, File sdg)
			throws FileNotFoundException, IOException {
		FileInputStream fis = new FileInputStream(p.getProperty("mappingsPath")
				+ FilenameUtils.removeExtension(sdg.getName()));
		ObjectInputStream ois = new ObjectInputStream(fis);
		Map<String, Set<Integer>> mapClassLines = null;
		try {
			mapClassLines = (Map<String, Set<Integer>>) ois.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		fis.close();
		ois.close();
		return mapClassLines;
	}
}
