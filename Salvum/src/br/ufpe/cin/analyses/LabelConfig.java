package br.ufpe.cin.analyses;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

//#if FEATURE
//@import br.ufpe.cin.policy.PolicyFeature;
//#elif CONTRIBUTION
import br.ufpe.cin.policy.PolicyContribution;
//#elif CLAZZ
//@import br.ufpe.cin.policy.PolicyClazz;
//#endif

import com.ibm.wala.classLoader.IMethod;

import edu.kit.joana.api.IFCAnalysis;
import edu.kit.joana.api.lattice.BuiltinLattices;
import edu.kit.joana.api.sdg.SDGAttribute;
import edu.kit.joana.api.sdg.SDGClass;
import edu.kit.joana.api.sdg.SDGInstruction;
import edu.kit.joana.api.sdg.SDGMethod;
import edu.kit.joana.api.sdg.SDGProgram;
import edu.kit.joana.api.sdg.SDGProgramPart;

public class LabelConfig {

	public void labellingElements(List<SDGProgramPart> sources,
			List<SDGProgramPart> sinks, SDGProgram program, IFCAnalysis ana) {

		/** annotate sources and sinks */

		annotateSources(sources, program, ana);

		annotateSinks(sinks, program, ana);
	}

	private void annotateSources(List<SDGProgramPart> sources,
			SDGProgram program, IFCAnalysis ana) {
		for (SDGProgramPart source : sources) {
			if (source instanceof SDGMethod) {
				ana.addSourceAnnotation(program.getMethod(source.toString()),
						BuiltinLattices.STD_SECLEVEL_HIGH);
			} else if (source instanceof SDGAttribute) {
				ana.addSourceAnnotation(program.getPart(source.toString()),
						BuiltinLattices.STD_SECLEVEL_HIGH);
			} else if (source instanceof SDGInstruction) {
				Collection<SDGInstruction> instructions = program
						.getInstruction(
								source.getOwningMethod().getSignature(),
								((SDGInstruction) source).getBytecodeIndex());
				for (SDGInstruction sdgInstruction : instructions) {
					ana.addSourceAnnotation(sdgInstruction,
							BuiltinLattices.STD_SECLEVEL_HIGH);
				}
			} else {
				ana.addSourceAnnotation(source,
						BuiltinLattices.STD_SECLEVEL_HIGH);
			}
		}
	}

	private void annotateSinks(List<SDGProgramPart> sinks, SDGProgram program,
			IFCAnalysis ana) {
		for (SDGProgramPart sink : sinks) {
			if (sink instanceof SDGMethod) {
				ana.addSinkAnnotation(program.getMethod(sink.toString()),
						BuiltinLattices.STD_SECLEVEL_LOW);
			} else if (sink instanceof SDGAttribute) {
				ana.addSinkAnnotation(program.getPart(sink.toString()),
						BuiltinLattices.STD_SECLEVEL_LOW);
			} else if (sink instanceof SDGInstruction) {
				Collection<SDGInstruction> instructions = program
						.getInstruction(sink.getOwningMethod().getSignature(),
								((SDGInstruction) sink).getBytecodeIndex());
				for (SDGInstruction sdgInstruction : instructions) {
					ana.addSinkAnnotation(sdgInstruction,
							BuiltinLattices.STD_SECLEVEL_LOW);
				}
			} else {
				ana.addSinkAnnotation(sink, BuiltinLattices.STD_SECLEVEL_LOW);
			}
		}
	}

	// #if CONTRIBUTION
	public void prepareListsOfSourceAndSinksContribution(
			Collection<SDGClass> classes,
			Map<String, List<Integer>> mapClassesLineNumbers,
			PolicyContribution policy, List<SDGProgramPart> sources,
			List<SDGProgramPart> sinks) {
		for (SDGClass sdgClass : classes) {
			// foreach class da policy

			Map<String, Set<String>> elements = policy.getClazzAndElements();
			Set<String> clazzes = elements.keySet();
			for (String clazz : clazzes) {
				if (mapClassesLineNumbers.containsKey(sdgClass.toString())
						|| sdgClass.toString().contains(clazz)) {
					iterateOverAttributes(policy, sources, sinks, sdgClass,
							clazz);

					iterateOverMethods(mapClassesLineNumbers, policy, sources,
							sinks, sdgClass);
				}
			}
		}
	}

	private void iterateOverMethods(
			Map<String, List<Integer>> mapClassesLineNumbers,
			PolicyContribution policy, List<SDGProgramPart> sources,
			List<SDGProgramPart> sinks, SDGClass sdgClass) {
		// por enquanto so marca instrucao de metodo como sink
		for (SDGMethod sdgMethod : sdgClass.getMethods()) {
			IMethod meth = sdgMethod.getMethod();
			List<SDGInstruction> methodInstructions = sdgMethod
					.getInstructions();
			for (SDGInstruction sdgInstruction : methodInstructions) {
				List<Integer> lineNumbers = mapClassesLineNumbers.get(sdgClass
						.toString());

				Integer sourceLine = meth.getLineNumber(sdgInstruction
						.getBytecodeIndex());

				if (lineNumbers != null && lineNumbers.contains(sourceLine)) {
					if (policy.getOperator().equals("noflow")) {
						sinks.add(sdgInstruction);
					} else if (policy.getOperator().equals("noset")) {
						sources.add(sdgInstruction);
					}

				}
			}
		}
	}

	private void iterateOverAttributes(PolicyContribution policy,
			List<SDGProgramPart> sources, List<SDGProgramPart> sinks,
			SDGClass sdgClass, String clazz) {
		// por enquanto so marca atributo como source
		for (SDGAttribute sdgAttribute : sdgClass.getAttributes()) {

			Set<String> sensitiveResources = policy
					.getSensitiveResources(clazz);
			for (String sensitiveResource : sensitiveResources) {
				if (sdgAttribute.toString().equals(sensitiveResource)) {
					if (policy.getOperator().equals("noflow")) {
						sources.add(sdgAttribute);
					} else if (policy.getOperator().equals("noset")) {
						sinks.add(sdgAttribute);
					}
				}
			}

		}
	}

	// #elif CLAZZ
//@	 public void prepareListsOfSourceAndSinks(Collection<SDGClass> classes,
//@	 Map<String, Set<Integer>> mapClassLines, PolicyClazz policy,
//@	 List<SDGProgramPart> sources, List<SDGProgramPart> sinks) {
//@	 for (SDGClass sdgClass : classes) {
//@	 Map<String, Set<String>> elements = policy.getClazzAndElements();
//@	 Set<String> clazzes = elements.keySet();
//@	 if (clazzes.contains(sdgClass.toString())) {
//@	 labelSource(policy, sources, sinks, sdgClass, clazzes);
//@	 }
//@	 if (mapClassLines.containsKey(sdgClass.toString())) {
//@	 // por enquanto so marca instrucao de metodo como sink
//@	 labelSink(mapClassLines, policy, sources, sinks, sdgClass);
//@	 }
//@	 }
//@	 }
//@	
//@	
//@	 private void labelSource(PolicyClazz policy, List<SDGProgramPart>
//@	sources,
//@	 List<SDGProgramPart> sinks, SDGClass sdgClass, Set<String> clazzes) {
//@	 for (String clazz : clazzes) {
//@	 // por enquanto so marca atributo como source
//@	 for (SDGAttribute sdgAttribute : sdgClass.getAttributes()) {
//@	 Set<String> sensitiveResources = policy
//@	 .getSensitiveResources(clazz);
//@	 for (String sensitiveResource : sensitiveResources) {
//@	 if (sdgAttribute.toString().equals(sensitiveResource)) {
//@	 if (policy.getOperator().equals("noflow")) {
//@	 sources.add(sdgAttribute);
//@	 } else if (policy.getOperator().equals("noset")) {
//@	 sinks.add(sdgAttribute);
//@	 }
//@	 }
//@	 }
//@	 }
//@	 }
//@	 }
//@	
//@	 private void labelSink(Map<String, Set<Integer>> mapClassLines,
//@	 PolicyClazz policy, List<SDGProgramPart> sources,
//@	 List<SDGProgramPart> sinks, SDGClass sdgClass) {
//@	 for (SDGMethod sdgMethod : sdgClass.getMethods()) {
//@	 IMethod meth = sdgMethod.getMethod();
//@	 List<SDGInstruction> methodInstructions = sdgMethod
//@	 .getInstructions();
//@	 for (SDGInstruction sdgInstruction : methodInstructions) {
//@	 Set<Integer> lineNumbers = mapClassLines.get(sdgClass
//@	 .toString());
//@	
//@	 Integer sourceLine = meth.getLineNumber(sdgInstruction
//@	 .getBytecodeIndex());
//@	
//@	 if (lineNumbers != null && lineNumbers.contains(sourceLine)) {
//@	 if (policy.getOperator().equals("noflow")) {
//@	 sinks.add(sdgInstruction);
//@	 } else if (policy.getOperator().equals("noset")) {
//@	 sources.add(sdgInstruction);
//@	 }
//@	
//@	 }
//@	 }
//@	 }
//@	 }
	// #endif
}
