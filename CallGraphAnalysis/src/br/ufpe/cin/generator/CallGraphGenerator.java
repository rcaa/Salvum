package br.ufpe.cin.generator;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import br.ufpe.cin.analyses.AnalysisConfig;
import br.ufpe.cin.sdgs.MainAux;
import br.ufpe.cin.util.CombinationGenerator;
import br.ufpe.cin.util.FileUtil;

import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.NullProgressMonitor;
import com.ibm.wala.util.graph.GraphIntegrity.UnsoundGraphException;

import edu.kit.joana.api.sdg.SDGConfig;
import edu.kit.joana.api.sdg.SDGProgram;
import edu.kit.joana.wala.core.SDGBuilder.CGResult;

public class CallGraphGenerator {

	public static void main(String[] args) {
		if (args[0] != null && !args[0].isEmpty()) {
			String propertiesPath = args[0];
			Properties propFile = FileUtil.getPropertiesFile(propertiesPath);
			CallGraphGenerator cgg = new CallGraphGenerator();

			String classPath = propFile.getProperty("classPath");
			String thirdPartyLibsPath = propFile.getProperty("thirdPartyLibsPath");
			String entryPoints = propFile.getProperty("main");
			List<String> entryMethods = MainAux
					.configureEntryMethods(entryPoints);
			try {
				List<String> allEntryPointsCombination = CombinationGenerator.run(entryMethods);
				for (String entryPointsTemp : allEntryPointsCombination) {
					System.out.println("Running combination: " + entryPointsTemp);
					cgg.buildCallGraphOnly(classPath, Arrays.asList(entryPointsTemp.split(":")),
							thirdPartyLibsPath);
				}
			} catch (ClassHierarchyException | IOException
					| UnsoundGraphException | CancelException e) {
				e.printStackTrace();
			}
		}
	}

	public CGResult buildCallGraphOnly(String classPath,
			List<String> entryMethods, String thirdPartyLibsPath)
			throws IOException, ClassHierarchyException, UnsoundGraphException,
			CancelException {
		SDGConfig config = AnalysisConfig.configureAnalysis(classPath,
				entryMethods, thirdPartyLibsPath);
		return SDGProgram.createCallGraphOnly(config, System.out,
				new NullProgressMonitor());
	}
}
