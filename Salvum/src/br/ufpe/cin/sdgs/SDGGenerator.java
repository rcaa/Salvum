package br.ufpe.cin.sdgs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import br.ufpe.cin.analyses.AnalysisConfig;
import br.ufpe.cin.mapping.LineMappingGenerator;
import br.ufpe.cin.util.FileUtil;
import br.ufpe.cin.util.ZipUtil;

import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.graph.GraphIntegrity.UnsoundGraphException;

import edu.kit.joana.api.sdg.SDGProgram;
import edu.kit.joana.ifc.sdg.graph.SDGSerializer;

public class SDGGenerator {

	public static void main(String[] args) {

		System.out.println("Starting SDG generator...");
		String zipPropPath = args[0];
		Properties zipProp = FileUtil.getPropertiesFile(zipPropPath);

		String zipDirectoryPath = zipProp.getProperty("zipDirectoryPath");
		String unzipedDirectory = zipProp.getProperty("unzipedDirectory");

		String projectPropPath = zipProp.getProperty("propFile");
		Properties projectProp = FileUtil.getPropertiesFile(projectPropPath);

		File zipDir = new File(zipDirectoryPath);
		if (zipDir.isDirectory()) {
			File[] zipFiles = zipDir.listFiles();
			for (File zipFile : zipFiles) {
				System.out.println("Running SDG generator for "
						+ zipFile.getName());
				List<String> sdgsNames = checkExistingSDGs(projectProp);
				try {
					if (zipFile.isDirectory()
							|| sdgsNames.contains(FilenameUtils
									.removeExtension(zipFile.getName()))) {
						continue;
					} else {

						ZipUtil.unzip(zipFile.getAbsolutePath(),
								unzipedDirectory);
						SDGProgram program = SDGGenerator.generateSDGFile(
								zipFile, projectProp);

						LineMappingGenerator.createLineMapping(program,
								zipFile, projectProp);

						FileUtils.deleteDirectory(new File(unzipedDirectory
								+ projectProp.getProperty("projectName")));
					}
				} catch (ClassHierarchyException | IOException
						| UnsoundGraphException | CancelException e) {
					e.printStackTrace();
				}
				System.out.println("Ending SDG generator for "
						+ zipFile.getName());
			}
		}
		System.out.println("Ending SDG generator.");
	}

	private static List<String> checkExistingSDGs(Properties projectProp) {
		List<String> sdgsNames = new ArrayList<>();
		File sdgDir = new File(projectProp.getProperty("sdgsDirectoryPath"));
		if (sdgDir != null && sdgDir.isDirectory()) {
			String sdgsDirNames[] = sdgDir.list();
			for (String existingSDG : sdgsDirNames) {
				sdgsNames.add(FilenameUtils.removeExtension(existingSDG));
			}
		}
		return sdgsNames;
	}

	private static SDGProgram generateSDGFile(File zipFile,
			Properties projectProp) throws ClassHierarchyException,
			IOException, UnsoundGraphException, CancelException {

		List<String> entryMethods = MainAux.configureEntryMethods(projectProp
				.getProperty("main"));
		String thirdPartyLibsPath = projectProp
				.getProperty("thirdPartyLibsPath");
		String sdgFilePath = projectProp.getProperty("sdgsDirectoryPath")
				+ FilenameUtils.removeExtension(zipFile.getName()) + ".pdg";
		String classPath = projectProp.getProperty("classPath");

		AnalysisConfig ana = new AnalysisConfig();
		SDGProgram program = ana.buildSDG(classPath, entryMethods,
				thirdPartyLibsPath);
		FileOutputStream sdgIO = new FileOutputStream(sdgFilePath);
		SDGSerializer.toPDGFormat(program.getSDG(), sdgIO);
		sdgIO.close();
		return program;
	}

}