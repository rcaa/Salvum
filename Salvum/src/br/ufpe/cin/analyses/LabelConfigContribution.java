package br.ufpe.cin.analyses;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ClassUtils;

import br.ufpe.cin.policy.PolicyContribution;
import edu.kit.joana.api.IFCAnalysis;
import edu.kit.joana.api.lattice.BuiltinLattices;
import edu.kit.joana.api.sdg.SDGAttribute;
import edu.kit.joana.api.sdg.SDGClass;
import edu.kit.joana.api.sdg.SDGInstruction;
import edu.kit.joana.api.sdg.SDGMethod;
import edu.kit.joana.api.sdg.SDGProgram;
import edu.kit.joana.api.sdg.SDGProgramPart;

public class LabelConfigContribution {

	public void labellingElements(List<SDGProgramPart> sources,
			List<SDGProgramPart> sinks, SDGProgram program, IFCAnalysis ana) {

		/** annotate sources and sinks */

		annotateSources(sources, program, ana);

		annotateSinks(sinks, program, ana);
	}

	private void annotateSources(List<SDGProgramPart> sources,
			SDGProgram program, IFCAnalysis ana) {
		System.out.println("List of sources: ");
		int i = 1;
		for (SDGProgramPart source : sources) {
			System.out.println(i + "- " + source);
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
		i++;
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

	public void prepareListsOfSourceAndSinksContribution(
			Collection<SDGClass> classes,
			Map<String, Set<Integer>> mapClassesLineNumbers,
			PolicyContribution policy, List<SDGProgramPart> sources,
			List<SDGProgramPart> sinks,
			Map<String, Integer> mapInstructionsLines) {
		for (SDGClass sdgClass : classes) {
			// foreach class da policy

			Map<String, Set<String>> elements = policy.getClazzAndElements();
			Set<String> clazzes = elements.keySet();
			for (String clazz : clazzes) {
				// fazer uma condicao a mais para passar todas as classes de um
				// pacote
				if (policy.getClazzAndElements().containsKey("Contribution")
						&& (sdgClass.toString().contains(
								ClassUtils.getPackageCanonicalName(clazz)) || clazz
								.contains(ClassUtils
										.getPackageCanonicalName(sdgClass
												.toString())))) {
					iterateOverAttributes(policy, sources, sinks, sdgClass,
							clazz, mapClassesLineNumbers);

					iterateOverMethods(mapClassesLineNumbers, policy, sources,
							sinks, sdgClass, mapInstructionsLines);
				} else if (mapClassesLineNumbers.containsKey(sdgClass
						.toString()) || sdgClass.toString().contains(clazz)) {
					iterateOverAttributes(policy, sources, sinks, sdgClass,
							clazz, mapClassesLineNumbers);

					iterateOverMethods(mapClassesLineNumbers, policy, sources,
							sinks, sdgClass, mapInstructionsLines);
				}
			}
		}
		//TODO degub somente
		for (SDGProgramPart sdgProgramPart : sources) {
			System.out.println("Source: " + sdgProgramPart);
		}
		for (SDGProgramPart sdgProgramPart : sinks) {
			System.out.println("Sink: " + sdgProgramPart);
		}
	}

	private void iterateOverMethods(
			Map<String, Set<Integer>> mapClassesLineNumbers,
			PolicyContribution policy, List<SDGProgramPart> sources,
			List<SDGProgramPart> sinks, SDGClass sdgClass,
			Map<String, Integer> mapInstructionsLines) {
		// por enquanto so marca instrucao de metodo como sink
		for (SDGMethod sdgMethod : sdgClass.getMethods()) {
			List<SDGInstruction> methodInstructions = sdgMethod
					.getInstructions();
			for (SDGInstruction sdgInstruction : methodInstructions) {
				Set<Integer> lineNumbers = mapClassesLineNumbers.get(sdgClass
						.toString());

				Integer sourceLine = null;
				Set<String> instructionsSet = mapInstructionsLines.keySet();
				for (String sdgIns : instructionsSet) {
					if (sdgIns.equals(sdgInstruction.toString())) {
						sourceLine = mapInstructionsLines.get(sdgIns);
						break;
					}
				}

				if (lineNumbers != null
						&& lineNumbers.contains(sourceLine)
						&& !policy.getClazzAndElements().containsKey(
								"Contribution")) {
					if (policy.getOperator().equals("noflow")) {
						sinks.add(sdgInstruction);
					} else if (policy.getOperator().equals("noset")) {
						sources.add(sdgInstruction);
					}
				} else if (policy.getClazzAndElements().containsKey(
						"Contribution")) {
					// casos da policy considerando pacotes
					if (lineNumbers != null
							&& lineNumbers.contains(sourceLine)) {
						// esta no diff
						sources.add(sdgInstruction);
					} else if (sdgClass.toString().contains(
							policy.getSecurePackage())) {
						// esta em determinado pacote
						sinks.add(sdgInstruction);
					}
				}
			}
		}
	}

	private void iterateOverAttributes(PolicyContribution policy,
			List<SDGProgramPart> sources, List<SDGProgramPart> sinks,
			SDGClass sdgClass, String clazz,
			Map<String, Set<Integer>> mapClassesLineNumbers) {
		// por enquanto so marca atributo como source
		for (SDGAttribute sdgAttribute : sdgClass.getAttributes()) {

			Set<String> sensitiveResources = policy
					.getSensitiveResources(clazz);
			for (String sensitiveResource : sensitiveResources) {
				if (sdgAttribute.toString().equals(sensitiveResource)
						&& !policy.getClazzAndElements().containsKey(
								"Contribution")) {
					if (policy.getOperator().equals("noflow")) {
						sources.add(sdgAttribute);
					} else if (policy.getOperator().equals("noset")) {
						sinks.add(sdgAttribute);
					}
				} else if (policy.getClazzAndElements().containsKey(
						"Contribution")) {
					// casos da policy considerando pacotes
					if (sdgClass.toString().contains(
							policy.getSecurePackage())) {
						// esta em determinado pacote
						sinks.add(sdgAttribute);
					} else {
						// esta no diff
						sources.add(sdgAttribute);
					}
				}
			}
		}
	}
}