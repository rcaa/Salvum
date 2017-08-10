package br.ufpe.cin.generator;

import java.io.IOException;
import java.util.List;

import br.ufpe.cin.analyses.AnalysisConfig;
import br.ufpe.cin.sdgs.MainAux;

import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.NullProgressMonitor;
import com.ibm.wala.util.graph.GraphIntegrity.UnsoundGraphException;

import edu.kit.joana.api.sdg.SDGConfig;
import edu.kit.joana.api.sdg.SDGProgram;
import edu.kit.joana.wala.core.SDGBuilder.CGResult;

public class CallGraphGenerator {

	public static void main(String[] args) {
		CallGraphGenerator cgg = new CallGraphGenerator();
		
		String classPath = "C:\\Doutorado\\workspace\\opensource\\TesteAuthentication\\bin\\";
		String thirdPartyLibsPath = "";
		String entryPoints = "view.Main.main([Ljava/lang/String;)V";
		List<String> entryMethods = MainAux.configureEntryMethods(entryPoints);
		
		try {
			cgg.buildCallGraphOnly(classPath, entryMethods, thirdPartyLibsPath);
		} catch (ClassHierarchyException | IOException | UnsoundGraphException
				| CancelException e) {
			e.printStackTrace();
		}
		
	}

	public CGResult buildCallGraphOnly(String classPath,
			List<String> entryMethods, String thirdPartyLibsPath)
			throws IOException, ClassHierarchyException, UnsoundGraphException,
			CancelException {
		SDGConfig config = AnalysisConfig.configureAnalysis(classPath, entryMethods,
				thirdPartyLibsPath);
		return SDGProgram.createCallGraphOnly(config, System.out,
				new NullProgressMonitor());
	}
}
