package br.ufpe.cin.analyses;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import br.ufpe.cin.policy.Policy;
import br.ufpe.cin.preprocessor.ContextManagerContribution;
import br.ufpe.cin.preprocessor.ContributionPreprocessor;
import br.ufpe.cin.preprocessor.GitUtil;

import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.WalaException;
import com.ibm.wala.util.graph.GraphIntegrity.UnsoundGraphException;

import edu.kit.joana.api.IFCAnalysis;
import edu.kit.joana.api.sdg.SDGClass;
import edu.kit.joana.api.sdg.SDGProgram;
import edu.kit.joana.api.sdg.SDGProgramPart;
import edu.kit.joana.ifc.sdg.core.SecurityNode;
import edu.kit.joana.ifc.sdg.core.violations.IViolation;
import edu.kit.joana.ifc.sdg.util.JavaMethodSignature;
import gnu.trove.map.TObjectIntMap;

public class Main {

	public static void main(String[] args) {
		// Properties p = CommandLine.parse(args);

		Properties p = new Properties();

		 String propertiesPath =
		 "/Users/rodrigoandrade/Documents/workspaces/Doutorado" +
		 "/joana/Salvum/configFiles/simpleContributionExamplePaulo.properties";
		//String propertiesPath = args[0];

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

		try {
			Main m = new Main();
			m.run(p);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void run(Properties p) throws WalaException,
			IllegalArgumentException, IOException, UnsoundGraphException,
			CancelException {

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
			setOutput(p, policy);

			// Primeiro passo logico

			// #if FEATURE
			// @ try {
			// @ String sourceDirectory = p.getProperty("sourceDirectory");
			// @ FeaturePreprocessor pp = new
//@			// FeaturePreprocessor(sourceDirectory);
			// @ pp.execute();
			// @ } catch (PreprocessorException e) {
			// @ e.printStackTrace();
			// @ }
			// @ // mapeamento de features e linhas
			// @ ContextManager context = ContextManager.getContext();
			// @ Map<String, Map<String, Set<Integer>>> mapClassFeatures =
//@			// context
			// @ .getMapClassFeatures();
			// #elif CONTRIBUTION
			ContributionPreprocessor cp = new ContributionPreprocessor(p,
					policy.getHash());
			cp.preprocess();
			ContextManagerContribution contextContribution = ContextManagerContribution
					.getContext();
			Map<String, List<Integer>> mapClassesLineNumbers = contextContribution
					.getMapClassesLineNumbers();
			
			// compilacao tem que vir aqui
			// javac -d bin -sourcepath src -cp lib/lib1.jar;lib/lib2.jar src/com/example/Application.java
			compileProject(p);
			
			// #endif
			// Segundo passo logico

			// pegar o nome de todos os metodos de cada classe alterada acima

			// montar o SDG graph
			AnalysisConfig ana = new AnalysisConfig();

			JavaMethodSignature entryMethod = JavaMethodSignature.fromString(p
					.getProperty("main"));
			// JavaMethodSignature entryMethod =
			// JavaMethodSignature.mainMethodOfClass(p.getProperty("main"));
			SDGProgram program = ana.buildSDG(p.getProperty("classpath"),
					entryMethod, p.getProperty("thirdPartyLibsPath"));

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
//@			// policy,
			// @ sources, sinks);
			// #elif CONTRIBUTION
			lconfig.prepareListsOfSourceAndSinksContribution(classes,
					mapClassesLineNumbers, policy, sources, sinks);
			// #endif

			// rodo as analises
			IFCAnalysis ifc = new IFCAnalysis(program);
			lconfig.labellingElements(sources, sinks, program, ifc);
			Collection<? extends IViolation<SecurityNode>> result = ifc.doIFC();
			TObjectIntMap<IViolation<SDGProgramPart>> resultByProgramPart = ifc
					.groupByPPPart(result);

			System.out.println(resultByProgramPart);
		}
		GitUtil.checkoutCommitHash(p.getProperty("targetPathDirectory"), "master");
	}

	private void compileProject(Properties p)
			throws IOException {
//		Set<String> classesTemp = mapClassesLineNumbers.keySet();
//		Runtime rt = Runtime.getRuntime();
//		for (String clazz : classesTemp) {
//			String compileCommand = "javac -d " + p.getProperty("classpath") 
//					+ " -sourcepath " + p.getProperty("targetPathDirectory") + "src "
//					+ "-cp " + p.getProperty("thirdPartyLibsPath") + " " 
//					+ p.getProperty("targetPathDirectory") + "/src/" 
//					+ clazz.replace('.', '/') + ".java";
//			Process process = rt.exec(compileCommand);
//			GitUtil.createOutputCommandLine(process);
//		}
		
		// chamar o build.xml via ant
//		Runtime rt = Runtime.getRuntime();
//		String compileCommand = "ant -f " + p.getProperty("targetPathDirectory") + "build.xml";
//		Process process = rt.exec(compileCommand);
//		GitUtil.createOutputCommandLine(process);
		
		Project project = new Project();
		project.setProperty("java.home", "/Library/Java/JavaVirtualMachines/jdk1.7.0_67.jdk/Contents/Home/");
        File buildFile = new File(p.getProperty("targetPathDirectory") + "build.xml");
        project.setUserProperty("ant.file", buildFile.getAbsolutePath());
        project.init();
        ProjectHelper helper = ProjectHelper.getProjectHelper();
        project.addReference("ant.projectHelper", helper);
        helper.parse(project, buildFile);
        project.executeTarget(project.getDefaultTarget());
	}

	private void setOutput(Properties p, Policy policy)
			throws FileNotFoundException {
		String outputPath = p.getProperty("output")
		// #if FEATURE
		// @ +policy.getFeature();
				// #elif CONTRIBUTION
				+ policy.getHash().substring(0, 8);
		// #endif
		PrintStream out = new PrintStream(new FileOutputStream(outputPath
				+ "-output.txt"));
		PrintStream outST = new PrintStream(new FileOutputStream(outputPath
				+ "-outputerror.txt"));
		System.setOut(out);
		System.setErr(outST);
	}
}
