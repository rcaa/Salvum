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

import org.eclipse.core.runtime.CoreException;

//#if CLAZZ
//@import br.ufpe.cin.policy.PolicyClazz;
//@import java.util.Set;
//@import br.ufpe.cin.clazz.preprocessor.ClazzContextManager;
//@import br.ufpe.cin.clazz.preprocessor.ClazzPreprocessor;
//@import br.ufpe.cin.clazz.preprocessor.PreprocessorException;
//@import br.ufpe.cin.policy.GitIntegration;
//#endif
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

//#if CONTRIBUTION
import br.ufpe.cin.preprocessor.ContextManagerContribution;
import br.ufpe.cin.preprocessor.ContributionPreprocessor;
import br.ufpe.cin.preprocessor.GitUtil;
import br.ufpe.cin.policy.PolicyContribution;

//#endif

public class Main {

	public static void main(String[] args) {

		// String propertiesPath =
		// "C:\\Doutorado\\workspace\\Salvum\\Salvum\\configFiles\\"
		// + "simpleContributionExampleEntryPoints.properties";
		
		String propertiesPath = args[0];
		
		Properties p = getPropertiesFile(propertiesPath);

		try {
			Main m = new Main();
			m.run(p, propertiesPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void run(Properties p, String propertiesPath) throws WalaException,
			IllegalArgumentException, IOException, UnsoundGraphException,
			CancelException, CoreException {

		// obtenho a policy
		Path path = FileSystems.getDefault().getPath(
				p.getProperty("policyDirectory"));
		// #if CONTRIBUTION
		List<String> hashes = PolicyContribution.findHashes(path,
				p.getProperty("targetPathDirectory"));
		System.out.println(hashes);

		if (hashes == null || hashes.isEmpty()) {
			throw new IOException("could not find hashes");
		}

		for (String hash : hashes) {

			// mandar ele ler outro arquivo de propriedade aqui
			String[] propertiesTemp = propertiesPath.split("\\.");
			String propertiesPathFinal = propertiesTemp[0] + hash + "."
					+ propertiesTemp[1];
			Properties propHash = getPropertiesFile(propertiesPathFinal);

			PolicyContribution policy = new PolicyContribution(path, hash);
			// Primeiro passo logico

			// preciso fazer um unzip do source do projeto aqui neste ponto

			ContributionPreprocessor cp = new ContributionPreprocessor(p,
					policy.getHash());
			cp.preprocess();
			ContextManagerContribution contextContribution = ContextManagerContribution
					.getContext();
			Map<String, List<Integer>> mapClassesLineNumbers = contextContribution
					.getMapClassesLineNumbers();
			if (mapClassesLineNumbers.isEmpty()) {
				continue;
			}

			// nao vamos tentar compilar automaticamente
			/*
			 * try { // copiar arquivos String sourceFiles =
			 * p.getProperty("nonexistentSourceFiles"); String targetFiles =
			 * p.getProperty("nonexistentTargetFiles"); if (sourceFiles != null
			 * && !sourceFiles.isEmpty() && targetFiles != null &&
			 * !targetFiles.isEmpty()) { FileUtil.copyFiles(sourceFiles,
			 * targetFiles); }
			 * 
			 * ProjectBuilder.compileProject(p); } catch (Exception e) {
			 * System.out.println(e.getMessage()); continue; }
			 */

			// #elif CLAZZ
			// @ PolicyClazz policy = new PolicyClazz(path);
			// @ String sourceDirectory = p.getProperty("targetPathDirectory");
			// @ ClazzPreprocessor cp = new ClazzPreprocessor(sourceDirectory,
			// @ policy.getMethodsAndArgs().keySet());
			// @ try {
			// @ cp.execute(sourceDirectory);
			// @ } catch (PreprocessorException e1) {
			// @ e1.printStackTrace();
			// @ }
			// @ ClazzContextManager context =
			// @ // ClazzContextManager.getInstance();
			// @ Map<String, Set<Integer>> mapClassLines =
			// @ // context.getMapClassLines();
			// @ System.out.println(mapClassLines);
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
			// JavaMethodSignature entryMethod =
			// JavaMethodSignature.fromString(p
			// .getProperty("main"));
			// JavaMethodSignature entryMethod = JavaMethodSignature
			// .mainMethodOfClass(p.getProperty("main"));
			SDGProgram program = null;
			try {
				System.out.println("Classpath: =================== " + propHash.getProperty("classpath"));
				program = ana.buildSDG(propHash.getProperty("classpath"),
						entryMethods,
						propHash.getProperty("thirdPartyLibsPath"));

			} catch (IllegalStateException e) {
				System.out.println(e.getMessage());
				// #if CONTRIBUTION
				continue;
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

			// #if CONTRIBUTION
			lconfig.prepareListsOfSourceAndSinksContribution(classes,
					mapClassesLineNumbers, policy, sources, sinks);
			// #elif CLAZZ
			// @ lconfig.prepareListsOfSourceAndSinks(classes, mapClassLines,
			// @ // policy,
			// @ sources, sinks);
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
						// #if CLAZZ
						// @ String filePath = "src"
						// @ + "/" + sink.getSource();
						// #endif
						System.out.println("Illegal flow from "
								+ source.getBytecodeName() + " to "
								+ sink.getBytecodeName() + " at line "
								+ sink.getEr()
								// #if CLAZZ
								// @ + " "
								// @ +
								// @ //
//@								// GitIntegration.gitBlame(p.getProperty("gitPath"),
								// @ sink.getEr(), filePath));
								// #elif CONTRIBUTION
								+ " in commit " + hash);
						// #endif
					}
				} else if (policy.getOperator().equals("noset")) {
					if (sn != null && source != null && sink != null
							&& source.getBytecodeIndex() >= 0) {
						System.out.println("Illegal set "
								+ sink.getBytecodeName() + " at "
								+ source.getBytecodeName() + " at line "
								+ source.getEr());
					}
				}
			}

			program = null;
			ifc = null;
			classes.clear();

			// #if CONTRIBUTION
			contextContribution.clear();
			mapClassesLineNumbers.clear();
			// #elif CLAZZ
			// @ context.clearMapping();
			// #endif
			sources.clear();
			sinks.clear();

			// #if CONTRIBUTION
		}
		GitUtil.checkoutCommitHash(p.getProperty("targetPathDirectory"),
				"master");
		// #endif
	}

	private static Properties getPropertiesFile(String propertiesPath) {
		Properties p = new Properties();
		FileInputStream in = null;
		try {
			in = new FileInputStream(propertiesPath);
			p.load(in);
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
		return p;
	}
}
