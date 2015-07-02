package br.ufpe.cin.analyses;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import br.ufpe.cin.policy.Policy;
//#if CONTRIBUTION
import br.ufpe.cin.preprocessor.ContributionPreprocessor;
import br.ufpe.cin.preprocessor.ContextManagerContribution;
//#endif
//#if FEATURE
//@import br.ufpe.cin.preprocessor.ContextManager;
//@import br.ufpe.cin.preprocessor.ContextManagerContribution;
//@import br.ufpe.cin.preprocessor.PreprocessorException;
//@import br.ufpe.cin.preprocessor.FeaturePreprocessor;
//#endif

import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.WalaException;
import com.ibm.wala.util.graph.GraphIntegrity.UnsoundGraphException;
import com.ibm.wala.util.io.CommandLine;

import edu.kit.joana.api.IFCAnalysis;
import edu.kit.joana.api.sdg.SDGClass;
import edu.kit.joana.api.sdg.SDGProgram;
import edu.kit.joana.api.sdg.SDGProgramPart;
import edu.kit.joana.ifc.sdg.core.SecurityNode;
import edu.kit.joana.ifc.sdg.core.violations.IViolation;
import edu.kit.joana.ifc.sdg.util.JavaMethodSignature;
import gnu.trove.map.TObjectIntMap;

public class MainAnalysis {

	public static void main(String[] args) {
		Properties p = CommandLine.parse(args);
		try {
			MainAnalysis m = new MainAnalysis();
			m.run(p);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void run(Properties p) throws WalaException,
			IllegalArgumentException, IOException, UnsoundGraphException,
			CancelException {

		// Primeiro passo logico
		
		//#if FEATURE
//@		preprocessFeature(p);
		//#elif CONTRIBUTION
		ContributionPreprocessor cp = new ContributionPreprocessor(p);
		cp.preprocess();
		//#endif
		// Segundo passo logico

		JavaMethodSignature entryMethod = JavaMethodSignature
				.mainMethodOfClass(p.getProperty("main"));

		//#if FEATURE
//@		// mapeamento de features e linhas
//@		ContextManager context = ContextManager.getContext();
//@		Map<String, Map<String, Set<Integer>>> mapClassFeatures = context
//@				.getMapClassFeatures();
		//#elif CONTRIBUTION
		ContextManagerContribution contextContribution = ContextManagerContribution.getContext();
		Map<String, List<Integer>> mapClassesLineNumbers = contextContribution.getMapClassesLineNumbers();
		//#endif

		// obtenho a policy
		Policy policy = new Policy(p.getProperty("policyDirectory"));

		// montar o SDG graph
		IFCAnalysisConfig ana = new IFCAnalysisConfig();
		SDGProgram program = ana.prepareAnalysis(p.getProperty("classpath"),
				entryMethod);
		Collection<SDGClass> classes = program.getClasses();

		// rotulo statements e expressions
		List<SDGProgramPart> sources = new ArrayList<SDGProgramPart>();
		List<SDGProgramPart> sinks = new ArrayList<SDGProgramPart>();
		//#if FEATURE
//@		ana.prepareListsOfSourceAndSinks(classes, mapClassFeatures, policy,
//@				sources, sinks);
		//#elif CONTRIBUTION
		ana.prepareListsOfSourceAndSinksContribution(classes, mapClassesLineNumbers, policy,
			sources, sinks);
		//#endif

		// rodo as analises
		IFCAnalysis ifc = ana.runAnalysis(sources, sinks, program);
		Collection<? extends IViolation<SecurityNode>> result = ifc.doIFC();
		TObjectIntMap<IViolation<SDGProgramPart>> resultByProgramPart = ifc
				.groupByPPPart(result);
		System.out.println(resultByProgramPart);
	}

	//#if FEATURE
//@	private void preprocessFeature(Properties p) {
//@		try {
//@			String sourceDirectory = p.getProperty("sourceDirectory");
//@			FeaturePreprocessor pp = new FeaturePreprocessor(sourceDirectory);
//@			pp.execute();
//@		} catch (PreprocessorException e) {
//@			e.printStackTrace();
//@		}
//@	}
	//#elif CONTRIBUTION
	//#endif
}
