package br.ufpe.cin.analyses;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;

import br.ufpe.cin.clazz.preprocessor.ClazzContextManager;
import br.ufpe.cin.clazz.preprocessor.ClazzPreprocessor;
import br.ufpe.cin.clazz.preprocessor.PreprocessorException;
import br.ufpe.cin.policy.GitIntegration;
import br.ufpe.cin.policy.PolicyClazz;

import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.WalaException;
import com.ibm.wala.util.graph.GraphIntegrity.UnsoundGraphException;

import edu.kit.joana.api.IFCAnalysis;
import edu.kit.joana.api.sdg.SDGAttribute;
import edu.kit.joana.api.sdg.SDGClass;
import edu.kit.joana.api.sdg.SDGMethod;
import edu.kit.joana.api.sdg.SDGProgram;
import edu.kit.joana.api.sdg.SDGProgramPart;
import edu.kit.joana.ifc.sdg.core.SecurityNode;
import edu.kit.joana.ifc.sdg.core.violations.ClassifiedViolation;
import edu.kit.joana.ifc.sdg.core.violations.IViolation;
import edu.kit.joana.ifc.sdg.graph.SDG;
import edu.kit.joana.ifc.sdg.util.JavaMethodSignature;
//#if FEATURE
//@import java.util.Set;
//#endif
//#if FEATURE
//@import br.ufpe.cin.feature.preprocessor.ContextManager;
//@import br.ufpe.cin.feature.preprocessor.FeaturePreprocessor;
//@import br.ufpe.cin.feature.preprocessor.PreprocessorException;
//@import br.ufpe.cin.policy.PolicyFeature;
//#elif CONTRIBUTION
//@import br.ufpe.cin.preprocessor.ContextManagerContribution;
//@import br.ufpe.cin.preprocessor.ContributionPreprocessor;
//@import br.ufpe.cin.preprocessor.GitUtil;
//@import br.ufpe.cin.ant.ProjectBuilder;
//@import br.ufpe.cin.policy.Policy;
//@import br.ufpe.cin.util.FileUtil;
//#elif CLAZZ
//#endif

public class Main {

	public static void main(String[] args) {
		// Properties p = CommandLine.parse(args);
		Properties p = new Properties();

		//String propertiesPath = "C:\\Doutorado\\workspace\\Salvum\\Salvum\\configFiles\\"
		//+ "simpleContributionExampleEntryPoints.properties";
		String propertiesPath = args[0];

		FileInputStream in = null;
		try {
			in = new FileInputStream(propertiesPath);
			p.load(in);
			// #if FEATURE
			// @
			// #elif CONTRIBUTION
			// @ FileUtil.setOutput(p, null);
			// #elif CLAZZ

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

		// Primeiro passo logico

		// #if FEATURE
		// @ PolicyFeature policy = new PolicyFeature(policyText);
		// @ try {
		// @ String sourceDirectory = p.getProperty("targetPathDirectory");
		// @ FeaturePreprocessor pp = new
		// @ // FeaturePreprocessor(sourceDirectory,
		// @ policy.getFeatureName());
		// @ pp.execute();
		// @ } catch (PreprocessorException e) {
		// @ e.printStackTrace();
		// @ }
		// @ // mapeamento de features e linhas
		// @ ContextManager contextFeature = ContextManager.getContext();
		// @ Map<String, Map<String, Set<Integer>>> mapClassFeatures =
		// @ // contextFeature
		// @ .getMapClassFeatures();
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
		// @ ProjectBuilder.compileProject(p);
		// @ } catch (Exception e) {
		// @ System.out.println(e.getMessage());
		// @ continue;
		// @ }
		// @
		// #elif CLAZZ
		PolicyClazz policy = new PolicyClazz(path);
		String sourceDirectory = p.getProperty("targetPathDirectory");
		ClazzPreprocessor cp = new ClazzPreprocessor(sourceDirectory,
				policy.getMethods());
		try {
			cp.execute();
		} catch (PreprocessorException e1) {
			e1.printStackTrace();
		}
		ClazzContextManager context = ClazzContextManager.getInstance();
		Map<String, Set<Integer>> mapClassLines = context.getMapClassLines();
		System.out.println(mapClassLines);
		// #endif
		// Segundo passo logico
		AnalysisConfig ana = new AnalysisConfig();

		String[] entries = p.getProperty("main").split(":");
		List<String> entryMethods = new ArrayList<String>();
		for (String meth : entries) {
			JavaMethodSignature entryMethod = JavaMethodSignature
					.fromString(meth);
			entryMethods.add(entryMethod.toBCString());
		}
		// JavaMethodSignature entryMethod = JavaMethodSignature.fromString(p
		// .getProperty("main"));
		// JavaMethodSignature entryMethod = JavaMethodSignature
		// .mainMethodOfClass(p.getProperty("main"));
		SDGProgram program = null;
		try {
			program = ana.buildSDG(p.getProperty("classpath"), entryMethods,
					p.getProperty("thirdPartyLibsPath"));

			/*
			 * DEBUG
			 * 
			 * Collection<SDGAttribute> allAttributes =
			 * program.getAllAttributes(); for (SDGAttribute sdgAttribute :
			 * allAttributes) { System.out.println(sdgAttribute); }
			 * Collection<SDGMethod> allMethods = program.getAllMethods(); for
			 * (SDGMethod sdgMethod : allMethods) {
			 * System.out.println(sdgMethod); }
			 */

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

		// get sdg
		// SDGProgram program =
		// ana.retrieveSDG("/home/local/CIN/rcaa2/contributionExperiments/joana/SDGFile.pdg");

		// save sdg
		// SDGSerializer.toPDGFormat(program.getSDG(),
		// new
		// FileOutputStream("/home/local/CIN/rcaa2/contributionExperiments/joana/SDGFile.pdg"));

		Collection<SDGClass> classes = program.getClasses();

		// rotulo statements e expressions
		List<SDGProgramPart> sources = new ArrayList<SDGProgramPart>();
		List<SDGProgramPart> sinks = new ArrayList<SDGProgramPart>();
		LabelConfig lconfig = new LabelConfig();

		// #if FEATURE
		// @ lconfig.prepareListsOfSourceAndSinks(classes, mapClassFeatures,
		// @ // policy,
		// @ sources, sinks);
		// #elif CONTRIBUTION
		// @ lconfig.prepareListsOfSourceAndSinksContribution(classes,
		// @ mapClassesLineNumbers, policy, sources, sinks);
		// #elif CLAZZ
		lconfig.prepareListsOfSourceAndSinks(classes, mapClassLines, policy,
				sources, sinks);
		// #endif

		// rodo as analises
		IFCAnalysis ifc = new IFCAnalysis(program);
		lconfig.labellingElements(sources, sinks, program, ifc);
		Collection<? extends IViolation<SecurityNode>> result = ifc.doIFC();
		for (IViolation<SecurityNode> iViolation : result) {
			ClassifiedViolation sn = (ClassifiedViolation) iViolation;
			SecurityNode source = sn.getSource();
			SecurityNode sink = sn.getSink();
			if (policy.getOperator().equals("noflow")) {
				if (sn != null && sink != null && source != null
						&& sink.getBytecodeIndex() >= 0) {
					String filePath = ClazzPreprocessor.CLASSES_SOURCE_PATH_JAVA_MAIN
							+ "/" + sink.getSource();
					System.out.println("Illegal flow from "
							+ source.getBytecodeName()
							+ " to "
							+ sink.getBytecodeName()
							+ " at line "
							+ sink.getEr()
							// #if FEATURE
							// @ );
							// #elif CLAZZ
							+ " "
							+ GitIntegration.gitBlame(p.getProperty("gitPath"),
									sink.getEr(), filePath));
					// #elif CONTRIBUTION
					// @ + " in commit " + hash);
					// #endif
				}
			} else if (policy.getOperator().equals("noset")) {
				if (sn != null && source != null && sink != null
						&& source.getBytecodeIndex() >= 0) {
					System.out.println("Illegal set "
							// #if FEATURE
							// @ + "from feature " + policy.getFeatureName()
							// @ // + " on "
							// #endif
							+ sink.getBytecodeName() + " at "
							+ source.getBytecodeName() + " at line "
							+ source.getEr());
				}
			}
		}

		program = null;
		ifc = null;
		classes.clear();

		// #if FEATURE
		// @ contextFeature.clearAll();
		// @ contextFeature.getMapClassFeatures().clear();
		// #elif CONTRIBUTION
		// @ contextContribution.clear();
		// @ mapClassesLineNumbers.clear();
		// #elif CLAZZ
		context.clearMapping();
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
