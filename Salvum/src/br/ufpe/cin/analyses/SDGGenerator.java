package br.ufpe.cin.analyses;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.graph.GraphIntegrity.UnsoundGraphException;

import edu.kit.joana.api.sdg.SDGProgram;
import edu.kit.joana.ifc.sdg.graph.SDGSerializer;

public class SDGGenerator {

	public static void main(String[] args) {

		String zipPropPath = args[0];
		Properties zipProp = FileUtil.getPropertiesFile(zipPropPath);

		String zipDirectoryPath = zipProp.getProperty("zipDirectoryPath");
		String unzipedDirectory = zipProp.getProperty("unzipedDirectory");
		File zipDir = new File(zipDirectoryPath);
		if (zipDir.isDirectory()) {
			File[] zipFiles = zipDir.listFiles();
			for (File zipFile : zipFiles) {
				try {
					ZipUtil.unzip(zipFile.getAbsolutePath(), unzipedDirectory);

					String projectPropPath = zipProp.getProperty("propFile");
					Properties projectProp = FileUtil
							.getPropertiesFile(projectPropPath);

					SDGGenerator.generateSDGFile(zipFile, projectProp);

					Map<String, Set<Integer>> mapClassLines = null;
					if (args[1] != null && !args[1].isEmpty()
							&& args[1].equals("contribution")) {
						Path policyPath = FileSystems.getDefault().getPath(
								projectProp.getProperty("policyDirectory"));
						List<String> hashes = PolicyContribution.findHashes(
								policyPath,
								projectProp.getProperty("targetPathDirectory"));
						// voldemort-1d15aa8564727ad26abf60a98dedf93430303b1e.zip
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
				} catch (ClassHierarchyException | IOException
						| UnsoundGraphException | CancelException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void generateSDGFile(File zipFile, Properties projectProp)
			throws ClassHierarchyException, IOException, UnsoundGraphException,
			CancelException {

		List<String> entryMethods = MainAux.configureEntryMethods(projectProp);
		String thirdPartyLibsPath = projectProp
				.getProperty("thirdPartyLibsPath");
		String sdgFilePath = projectProp.getProperty("outputPath")
				+ FilenameUtils.removeExtension(zipFile.getName()) + ".pdg";
		String classPath = projectProp.getProperty("classPath");

		AnalysisConfig ana = new AnalysisConfig();
		SDGProgram program = ana.buildSDG(classPath, entryMethods,
				thirdPartyLibsPath);
		SDGSerializer.toPDGFormat(program.getSDG(), new FileOutputStream(
				sdgFilePath));
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
		ClazzContextManager context = ClazzContextManager.getInstance();
		Map<String, Set<Integer>> mapClassLines = context.getMapClassLines();
		return mapClassLines;
	}

	private static Map<String, Set<Integer>> preprocessMappingContribution(
			Properties projectProp, String hash) throws IOException {
		Path path = FileSystems.getDefault().getPath(
				projectProp.getProperty("policyDirectory"));
		PolicyContribution policy = new PolicyContribution(path, hash);
		ContributionPreprocessor cp = new ContributionPreprocessor(projectProp,
				policy.getHash());
		cp.preprocess();
		ContextManagerContribution contextContribution = ContextManagerContribution
				.getContext();
		Map<String, Set<Integer>> mapClassesLineNumbers = contextContribution
				.getMapClassesLineNumbers();
		return mapClassesLineNumbers;
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
	}
}