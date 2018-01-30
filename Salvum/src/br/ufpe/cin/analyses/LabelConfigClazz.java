package br.ufpe.cin.analyses;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.ufpe.cin.policy.PolicyClazz;
import edu.kit.joana.api.IFCAnalysis;
import edu.kit.joana.api.annotations.IFCAnnotation;
import edu.kit.joana.api.lattice.BuiltinLattices;
import edu.kit.joana.api.sdg.SDGAttribute;
import edu.kit.joana.api.sdg.SDGClass;
import edu.kit.joana.api.sdg.SDGInstruction;
import edu.kit.joana.api.sdg.SDGMethod;
import edu.kit.joana.api.sdg.SDGProgram;
import edu.kit.joana.api.sdg.SDGProgramPart;
//#if FEATURE
//@import br.ufpe.cin.policy.PolicyFeature;
//#elif CONTRIBUTION
//@import br.ufpe.cin.policy.PolicyContribution;
//#elif CLAZZ
//#endif

public class LabelConfigClazz {

	public void labellingElements(List<SDGProgramPart> sources,
			List<SDGProgramPart> sinks, List<SDGProgramPart> declassifications,
			SDGProgram program, IFCAnalysis ana) {

		/** annotate sources and sinks */

		annotateSources(sources, program, ana);

		annotateSinks(sinks, program, ana);

		annotateDeclassification(declassifications, program, ana);
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
			i++;
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

	private void annotateDeclassification(
			List<SDGProgramPart> declassifications, SDGProgram program,
			IFCAnalysis ana) {
		for (SDGProgramPart declass : declassifications) {
			if (declass instanceof SDGMethod) {
				ana.addDeclassification(program.getMethod(declass.toString()),
						BuiltinLattices.STD_SECLEVEL_HIGH,
						BuiltinLattices.STD_SECLEVEL_LOW);
			} else if (declass instanceof SDGAttribute) {
				ana.addDeclassification(program.getPart(declass.toString()),
						BuiltinLattices.STD_SECLEVEL_HIGH,
						BuiltinLattices.STD_SECLEVEL_LOW);
			} else if (declass instanceof SDGInstruction) {
				Collection<SDGInstruction> instructions = program
						.getInstruction(declass.getOwningMethod()
								.getSignature(), ((SDGInstruction) declass)
								.getBytecodeIndex());
				for (SDGInstruction sdgInstruction : instructions) {
					ana.addDeclassification(sdgInstruction,
							BuiltinLattices.STD_SECLEVEL_HIGH,
							BuiltinLattices.STD_SECLEVEL_LOW);
				}
			} else {
				ana.addDeclassification(declass,
						BuiltinLattices.STD_SECLEVEL_HIGH,
						BuiltinLattices.STD_SECLEVEL_LOW);
			}
		}
	}

	public void prepareListsOfSourceAndSinks(Collection<SDGClass> classes,
			Map<String, Set<Integer>> mapClassLines, PolicyClazz policy,
			List<SDGProgramPart> sources, List<SDGProgramPart> sinks,
			List<SDGProgramPart> declassifications,
			Map<String, Integer> mapInstructionsLines) {
		for (SDGClass sdgClass : classes) {
			Map<String, Set<String>> elements = policy.getClazzAndElements();
			Set<String> clazzes = elements.keySet();
			if (clazzes.contains(sdgClass.toString())) {
				labelSource(policy, sources, sinks, sdgClass, clazzes);
			}
			if (mapClassLines.containsKey(sdgClass.toString())) {
				// por enquanto so marca instrucao de metodo como sink
				labelSink(mapClassLines, policy, sources, sinks, sdgClass,
						mapInstructionsLines);
			}
			List<String> declassList = policy.getDeclassifications();
			if (declassList != null && !declassList.isEmpty()) {
				// por enquanto so marca instrucao de metodo como
				// declassification
				labelDeclassification(policy, declassifications, sdgClass);
			}
		}
	}

	private void labelSource(PolicyClazz policy, List<SDGProgramPart> sources,
			List<SDGProgramPart> sinks, SDGClass sdgClass, Set<String> clazzes) {
		for (String clazz : clazzes) {
			// por enquanto so marca atributo como source
			for (SDGAttribute sdgAttribute : sdgClass.getAttributes()) {
				Set<String> sensitiveResources = policy
						.getSensitiveResources(clazz);
				for (String sensitiveResource : sensitiveResources) {
					if (sdgAttribute.toString().equals(
							sensitiveResource.toString())) {
						if (policy.getOperator().equals("noflow")) {
							System.out.println("source to be labelled: "
									+ sdgAttribute);
							sources.add(sdgAttribute);
						} else if (policy.getOperator().equals("noset")) {
							sinks.add(sdgAttribute);
						}
					}
				}
			}
		}
	}

	private void labelSink(Map<String, Set<Integer>> mapClassLines,
			PolicyClazz policy, List<SDGProgramPart> sources,
			List<SDGProgramPart> sinks, SDGClass sdgClass,
			Map<String, Integer> mapInstructionsLines) {
		for (SDGMethod sdgMethod : sdgClass.getMethods()) {
			// IMethod meth = sdgMethod.getMethod();
			List<SDGInstruction> methodInstructions = sdgMethod
					.getInstructions();
			for (SDGInstruction sdgInstruction : methodInstructions) {
				Set<Integer> lineNumbers = mapClassLines.get(sdgClass
						.toString());
				// sourceLine = null;
				// = meth.getLineNumber(sdgInstruction
				// .getBytecodeIndex());
				Integer sourceLine = null;
				Set<String> instructionsSet = mapInstructionsLines.keySet();
				for (String sdgIns : instructionsSet) {
					if (sdgIns.equals(sdgInstruction.toString())) {
						sourceLine = mapInstructionsLines.get(sdgIns);
						break;
					}
				}

				if (lineNumbers != null && lineNumbers.contains(sourceLine)) {
					if (policy.getOperator().equals("noflow")) {
						System.out.println("sink to be labelled: "
								+ sdgInstruction);
						sinks.add(sdgInstruction);
					} else if (policy.getOperator().equals("noset")) {
						sources.add(sdgInstruction);
					}

				}
			}
		}
	}

	private void labelDeclassification(PolicyClazz policy,
			List<SDGProgramPart> declassifications, SDGClass sdgClass) {
		for (SDGMethod sdgMethod : sdgClass.getMethods()) {
			List<SDGInstruction> methodInstructions = sdgMethod
					.getInstructions();
			for (SDGInstruction sdgInstruction : methodInstructions) {
				String instructionStr = sdgInstruction.toString();

				for (String declass : policy.getDeclassifications()) {
					if (instructionStr.contains(declass)) {
						System.out.println("declassification to be labelled: "
								+ sdgInstruction);
						declassifications.add(sdgInstruction);
					}
				}
			}
		}
	}

}
