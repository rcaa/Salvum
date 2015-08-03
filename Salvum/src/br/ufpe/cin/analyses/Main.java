package br.ufpe.cin.analyses;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
//#if FEATURE
//@import java.util.Set;
//@
//@import br.ufpe.cin.feature.preprocessor.ContextManager;
//@import br.ufpe.cin.feature.preprocessor.FeaturePreprocessor;
//@import br.ufpe.cin.feature.preprocessor.PreprocessorException;
//#endif
import br.ufpe.cin.policy.Policy;
//#if CONTRIBUTION
import br.ufpe.cin.preprocessor.ContextManagerContribution;
import br.ufpe.cin.preprocessor.ContributionPreprocessor;
//#endif

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

		//String propertiesPath = "/Users/rodrigoandrade/Documents/workspaces/Doutorado" +
		//		"/joana/Salvum/configFiles/simpleContributionExample.properties";
		String propertiesPath = args[0];

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
		Policy policy = new Policy(p.getProperty("policyDirectory"));

		setOutput(p, policy);
		
		// Primeiro passo logico

		// #if FEATURE
//@		try {
//@			String sourceDirectory = p.getProperty("sourceDirectory");
//@		    FeaturePreprocessor pp = new FeaturePreprocessor(sourceDirectory);
//@			pp.execute();
//@		} catch (PreprocessorException e) {
//@			e.printStackTrace();
//@		}
//@		 // mapeamento de features e linhas
//@		ContextManager context = ContextManager.getContext();
//@		Map<String, Map<String, Set<Integer>>> mapClassFeatures = context
//@		 .getMapClassFeatures();
		// #elif CONTRIBUTION
		ContributionPreprocessor cp = new ContributionPreprocessor(p,
				policy.getHash());
		cp.preprocess();
		ContextManagerContribution contextContribution = ContextManagerContribution
				.getContext();
		Map<String, List<Integer>> mapClassesLineNumbers = contextContribution
				.getMapClassesLineNumbers();
		// #endif
		// Segundo passo logico

		// montar o SDG graph
		AnalysisConfig ana = new AnalysisConfig();

		JavaMethodSignature entryMethod = JavaMethodSignature.fromString(p
				.getProperty("main"));
	//	JavaMethodSignature entryMethod = JavaMethodSignature.mainMethodOfClass(p.getProperty("main"));
		SDGProgram program = ana.buildSDG(p.getProperty("classpath"),
				entryMethod, p.getProperty("thirdPartyLibsPath"));

		// SDGProgram program =
		// ana.retrieveSDG("/Users/rodrigoandrade/Desktop/Saida_TYPE_BASED/SDGInformationFlow.pdg");
		// chamar o metodo pra carregar o grafo: ana.retrieveSDG(String path)
		Collection<SDGClass> classes = program.getClasses();

		// rotulo statements e expressions
		List<SDGProgramPart> sources = new ArrayList<SDGProgramPart>();
		List<SDGProgramPart> sinks = new ArrayList<SDGProgramPart>();
		LabelConfig lconfig = new LabelConfig();

		// #if FEATURE
//@		lconfig.prepareListsOfSourceAndSinks(classes, mapClassFeatures, policy,
//@		 sources, sinks);
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

	private void setOutput(Properties p, Policy policy)
			throws FileNotFoundException {
		String outputPath = p.getProperty("output")
				// #if FEATURE
//@				+policy.getFeature();
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
