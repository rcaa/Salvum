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

import org.eclipse.core.runtime.CoreException;

import br.ufpe.cin.ant.ProjectBuilder;
import br.ufpe.cin.policy.Policy;
import br.ufpe.cin.preprocessor.ContextManagerContribution;
import br.ufpe.cin.preprocessor.ContributionPreprocessor;
import br.ufpe.cin.preprocessor.GitUtil;
import br.ufpe.cin.util.FileUtil;

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

		// String propertiesPath =
		// "/Users/rodrigoandrade/Documents/workspaces/Doutorado"
		// + "/joana/Salvum/configFiles/simpleContributionExample.properties";
		String propertiesPath = args[0];

		FileInputStream in = null;
		try {
			in = new FileInputStream(propertiesPath);
			p.load(in);
			FileUtil.setOutput(p, null);
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
		List<String> hashes = Policy.findHashes(policyText,
				p.getProperty("targetPathDirectory"));
		System.out.println(hashes);

		if (hashes == null || hashes.isEmpty()) {
			throw new IOException("could not find hashes");
		}

		for (String hash : hashes) {
			Policy policy = new Policy(policyText, hash);
			FileUtil.setOutput(p, policy);

			// Primeiro passo logico

			// #if FEATURE
			// @ try {
			// @ String sourceDirectory = p.getProperty("sourceDirectory");
			// @ FeaturePreprocessor pp = new
			// @ // FeaturePreprocessor(sourceDirectory);
			// @ pp.execute();
			// @ } catch (PreprocessorException e) {
			// @ e.printStackTrace();
			// @ }
			// @ // mapeamento de features e linhas
			// @ ContextManager context = ContextManager.getContext();
			// @ Map<String, Map<String, Set<Integer>>> mapClassFeatures =
			// @ // context
			// @ .getMapClassFeatures();
			// #elif CONTRIBUTION
			ContributionPreprocessor cp = new ContributionPreprocessor(p,
					policy.getHash());
			cp.preprocess();
			ContextManagerContribution contextContribution = ContextManagerContribution
					.getContext();
			Map<String, List<Integer>> mapClassesLineNumbers = contextContribution
					.getMapClassesLineNumbers();
			if (mapClassesLineNumbers.isEmpty()) {
//				// significa que nao tem classes java alteradas
//
//				// remover diff file e outputs
//				Path diffFiles = FileSystems.getDefault().getPath(
//						ContributionPreprocessor.setDiffFilePath(hash,
//								p.getProperty("diffFilePath")));
//				System.out.println(diffFiles);
//				String output = p.getProperty("output")
//						+ policy.getHash().substring(0, 8);
//				Path deletedOutput = FileSystems.getDefault().getPath(
//						output + "-output.txt");
//				System.out.println(deletedOutput);
//				Path deletedOutputError = FileSystems.getDefault().getPath(
//						output + "-outputerror.txt");
//				System.out.println(deletedOutputError);
//				Files.deleteIfExists(diffFiles);
//				Files.deleteIfExists(deletedOutput);
//				Files.deleteIfExists(deletedOutputError);
				continue;
			}

			// compilacao tem que vir aqui
			// javac -d bin -sourcepath src -cp lib/lib1.jar;lib/lib2.jar
			// src/com/example/Application.java

			try {
				// copiar arquivos
				String sourceFiles = p.getProperty("nonexistentSourceFiles");
				String targetFiles = p.getProperty("nonexistentTargetFiles");
				if (sourceFiles != null && !sourceFiles.isEmpty()
						&& targetFiles != null && !targetFiles.isEmpty()) {
					FileUtil.copyFiles(sourceFiles, targetFiles);
				}

				ProjectBuilder.compileProject(p, hash);
			} catch (Exception e) {
				System.out.println(e.getMessage());
				continue;
			}

			// #endif
			// Segundo passo logico

			// pegar o nome de todos os metodos de cada classe alterada acima

			// montar o SDG graph
			AnalysisConfig ana = new AnalysisConfig();

			JavaMethodSignature entryMethod = JavaMethodSignature.fromString(p
					.getProperty("main"));
			// JavaMethodSignature entryMethod = JavaMethodSignature
			// .mainMethodOfClass(p.getProperty("main"));
			SDGProgram program = null;
			try {
				program = ana.buildSDG(p.getProperty("classpath"), entryMethod,
						p.getProperty("thirdPartyLibsPath"));
			} catch (IllegalStateException e) {
				if (e.getMessage().contains("main([Ljava/lang/String")) {
					System.out.println("Main method does not exist "
							+ "in this project version");
					continue;
				}
			}

			// SDGProgram program =
			// ana.retrieveSDG("/Users/rodrigoandrade/Desktop/Saida_TYPE_BASED/SDGInformationFlow.pdg");
			// chamar o metodo pra carregar o grafo: ana.retrieveSDG(String
			// path)
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
			lconfig.prepareListsOfSourceAndSinksContribution(classes,
					mapClassesLineNumbers, policy, sources, sinks);
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
							+ sink.getBytecodeName() + " at line "
							// usar o mapeamento aqui pra pegar linha de codigo
							+ sink.getBytecodeIndex() + " in commit " + hash);
				}
			}

			program = null;
			ifc = null;
			classes.clear();
			contextContribution.clear();
			mapClassesLineNumbers.clear();
			sources.clear();
			sinks.clear();
		}
		GitUtil.checkoutCommitHash(p.getProperty("targetPathDirectory"),
				"master");
	}
}
