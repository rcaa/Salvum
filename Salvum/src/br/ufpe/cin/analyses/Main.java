package br.ufpe.cin.analyses;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
//#if FEATURE
import java.util.Set;
//#endif
import org.eclipse.core.runtime.CoreException;

//#if FEATURE
import br.ufpe.cin.feature.preprocessor.ContextManager;
import br.ufpe.cin.feature.preprocessor.FeaturePreprocessor;
import br.ufpe.cin.feature.preprocessor.PreprocessorException;
//#elif CONTRIBUTION
//@import br.ufpe.cin.preprocessor.ContextManagerContribution;
//@import br.ufpe.cin.preprocessor.ContributionPreprocessor;
//@import br.ufpe.cin.preprocessor.GitUtil;
//@import br.ufpe.cin.ant.ProjectBuilder;
//@import br.ufpe.cin.policy.Policy;
//@import br.ufpe.cin.util.FileUtil;
//#endif
import br.ufpe.cin.policy.PolicyFeature;

import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.WalaException;
import com.ibm.wala.util.graph.GraphIntegrity.UnsoundGraphException;

import edu.kit.joana.api.IFCAnalysis;
import edu.kit.joana.api.sdg.SDGClass;
import edu.kit.joana.api.sdg.SDGProgram;
import edu.kit.joana.api.sdg.SDGProgramPart;
import edu.kit.joana.ifc.sdg.core.SecurityNode;
import edu.kit.joana.ifc.sdg.core.violations.ClassifiedViolation;
import edu.kit.joana.ifc.sdg.core.violations.IViolation;
import edu.kit.joana.ifc.sdg.util.JavaMethodSignature;

public class Main {

	public static void main(String[] args) {
		// Properties p = CommandLine.parse(args);
		Properties p = new Properties();

		String propertiesPath =
		"/Users/rodrigoandrade/Documents/workspaces/Doutorado"
		+ "/joana/Salvum/configFiles/shriramExample.properties";
		//String propertiesPath = args[0];

		FileInputStream in = null;
		try {
			in = new FileInputStream(propertiesPath);
			p.load(in);
			// #if FEATURE

			// #elif CONTRIBUTION
			// @ FileUtil.setOutput(p, null);
			// #endif
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		try {
			Main m = new Main();
			m.run(p);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void run(Properties p) throws WalaException,
			IllegalArgumentException, IOException, UnsoundGraphException,
			CancelException, CoreException {

		// obtenho a policy
		Path path = FileSystems.getDefault().getPath(
				p.getProperty("policyDirectory"));
		String policyText = new String(Files.readAllBytes(path));
		// #if CONTRIBUTION
		// @ List<String> hashes = Policy.findHashes(policyText,
		// @ p.getProperty("targetPathDirectory"));
		// @ System.out.println(hashes);
		// @
		// @ if (hashes == null || hashes.isEmpty()) {
		// @ throw new IOException("could not find hashes");
		// @ }
		// @
		// @ for (String hash : hashes) {
		// @ Policy policy = new Policy(policyText, hash);
		// @ FileUtil.setOutput(p, policy);
		// #endif

		PolicyFeature policy = new PolicyFeature(policyText);

		// Primeiro passo logico

		// #if FEATURE
		try {
			String sourceDirectory = p.getProperty("targetPathDirectory");
			FeaturePreprocessor pp = new FeaturePreprocessor(sourceDirectory, policy.getFeatureName());
			pp.execute();
		} catch (PreprocessorException e) {
			e.printStackTrace();
		}
		// mapeamento de features e linhas
		ContextManager contextFeature = ContextManager.getContext();
		Map<String, Map<String, Set<Integer>>> mapClassFeatures = contextFeature
				.getMapClassFeatures();
		// #elif CONTRIBUTION
		// @ ContributionPreprocessor cp = new ContributionPreprocessor(p,
		// @ policy.getHash());
		// @ cp.preprocess();
		// @ ContextManagerContribution contextContribution =
		// @ // ContextManagerContribution
		// @ .getContext();
		// @ Map<String, List<Integer>> mapClassesLineNumbers =
		// @ // contextContribution
		// @ .getMapClassesLineNumbers();
		// @ if (mapClassesLineNumbers.isEmpty()) {
		// @ continue;
		// @ }
		// @
		// @ try {
		// @ // copiar arquivos
		// @ String sourceFiles = p.getProperty("nonexistentSourceFiles");
		// @ String targetFiles = p.getProperty("nonexistentTargetFiles");
		// @ if (sourceFiles != null && !sourceFiles.isEmpty()
		// @ && targetFiles != null && !targetFiles.isEmpty()) {
		// @ FileUtil.copyFiles(sourceFiles, targetFiles);
		// @ }
		// @
		// @ ProjectBuilder.compileProject(p, hash);
		// @ } catch (Exception e) {
		// @ System.out.println(e.getMessage());
		// @ continue;
		// @ }
		// @
		// #endif
		// Segundo passo logico
		AnalysisConfig ana = new AnalysisConfig();

		//JavaMethodSignature entryMethod = JavaMethodSignature.fromString(p
		//		.getProperty("main"));
		 JavaMethodSignature entryMethod = JavaMethodSignature
		 .mainMethodOfClass(p.getProperty("main"));
		SDGProgram program = null;
		try {
			program = ana.buildSDG(p.getProperty("classpath"), entryMethod,
					p.getProperty("thirdPartyLibsPath"));
		} catch (IllegalStateException e) {
			if (e.getMessage().contains("main([Ljava/lang/String")) {
				System.out.println("Main method does not exist "
						+ "in this project version");
			} else {
				System.out.println(e.getMessage());
			}
			// #if CONTRIBUTION
			// @ continue;
			// #endif
		}

		// SDGProgram program =
		// ana.retrieveSDG("/Users/rodrigoandrade/Desktop/Saida_TYPE_BASED/SDGInformationFlow.pdg");

		Collection<SDGClass> classes = program.getClasses();

		// rotulo statements e expressions
		List<SDGProgramPart> sources = new ArrayList<SDGProgramPart>();
		List<SDGProgramPart> sinks = new ArrayList<SDGProgramPart>();
		LabelConfig lconfig = new LabelConfig();

		// #if FEATURE
		lconfig.prepareListsOfSourceAndSinks(classes, mapClassFeatures, policy,
				sources, sinks);
		// #elif CONTRIBUTION
		// @ lconfig.prepareListsOfSourceAndSinksContribution(classes,
		// @ mapClassesLineNumbers, policy, sources, sinks);
		// #endif

		// rodo as analises
		IFCAnalysis ifc = new IFCAnalysis(program);
		lconfig.labellingElements(sources, sinks, program, ifc);
		Collection<? extends IViolation<SecurityNode>> result = ifc.doIFC();
		for (IViolation<SecurityNode> iViolation : result) {
			ClassifiedViolation sn = (ClassifiedViolation) iViolation;
			SecurityNode source = sn.getSource();
			SecurityNode sink = sn.getSink();
			if (sn != null && sn.getSink() != null
					&& sink.getBytecodeIndex() >= 0) {
				System.out.println("Illegal flow from "
						+ source.getBytecodeName() + " to "
						+ sink.getBytecodeName() + " at line " + sink.getEr()
				// #if FEATURE
						);
				// #elif CONTRIBUTION
				// @ + " in commit " + hash);
				// #endif
			}
		}

		program = null;
		ifc = null;
		classes.clear();

		// #if FEATURE
		contextFeature.clearAll();
		contextFeature.getMapClassFeatures().clear();
		// #elif CONTRIBUTION
		// @ contextContribution.clear();
		// @ mapClassesLineNumbers.clear();
		// #endif
		sources.clear();
		sinks.clear();

		// #if CONTRIBUTION
		// @ }
		// @ GitUtil.checkoutCommitHash(p.getProperty("targetPathDirectory"),
		// @ "master");
		// #endif
	}
}
