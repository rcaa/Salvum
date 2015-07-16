package br.ufpe.cin.analyses;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import br.ufpe.cin.policy.Policy;

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
		// ana.addSourceAnnotation(program.getPart("model.User.passwordHash"),
		// BuiltinLattices.STD_SECLEVEL_HIGH);
		// ana.addSinkAnnotation(program.getMethod("util.Log.loggingAction(Ljava/lang/Object;)V"),
		// BuiltinLattices.STD_SECLEVEL_LOW);
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

	
	//#if FEATURE
//@	public void prepareListsOfSourceAndSinks(Collection<SDGClass> classes,
//@			Map<String, Map<String, Set<Integer>>> mapClassFeatures,
//@			Policy policy, List<SDGProgramPart> sources,
//@			List<SDGProgramPart> sinks) {
//@		for (SDGClass sdgClass : classes) {
//@			if (!mapClassFeatures.containsKey(sdgClass.toString())) {
//@				continue;
//@			}
//@
//@			// por enquanto so marca atributo como source
//@			for (SDGAttribute sdgAttribute : sdgClass.getAttributes()) {
//@				if (sdgAttribute.toString().equals(
//@						policy.getSensitiveResource())) {
//@					if (policy.getOperator().equals("noflow")) {
//@						sources.add(sdgAttribute);
//@					} else if (policy.getOperator().equals("noset")) {
//@						sinks.add(sdgAttribute);
//@					}
//@				}
//@			}
//@
//@			// por enquanto so marca instrucao de metodo como sink
//@			for (SDGMethod sdgMethod : sdgClass.getMethods()) {
//@				IMethod meth = sdgMethod.getMethod();
//@				List<SDGInstruction> methodInstructions = sdgMethod
//@						.getInstructions();
//@				for (SDGInstruction sdgInstruction : methodInstructions) {
//@					Map<String, Set<Integer>> mapFeatures = mapClassFeatures
//@							.get(sdgClass.toString());
//@					Set<Integer> lineNumbers = mapFeatures.get(policy
//@							.getFeature());
//@
//@					Integer sourceLine = meth.getLineNumber(sdgInstruction
//@							.getBytecodeIndex());
//@
//@					if (lineNumbers != null && lineNumbers.contains(sourceLine)) {
//@						if (policy.getOperator().equals("noflow")) {
//@							sinks.add(sdgInstruction);
//@						} else if (policy.getOperator().equals("noset")) {
//@							sources.add(sdgInstruction);
//@						}
//@
//@					}
//@				}
//@			}
//@		}
//@	}
	//#elif CONTRIBUTION
	public void prepareListsOfSourceAndSinksContribution(Collection<SDGClass> classes,
			Map<String, List<Integer>> mapClassesLineNumbers,
			Policy policy, List<SDGProgramPart> sources,
			List<SDGProgramPart> sinks) {
		for (SDGClass sdgClass : classes) {
			if (sdgClass.toString().contains("UserModel")) {
				System.out.println(policy.getClazz());
			}
			if (mapClassesLineNumbers.containsKey(sdgClass.toString()) || sdgClass.toString().contains(policy.getClazz())) {
				iterateOverAttributes(policy, sources, sinks, sdgClass);

				iterateOverMethods(mapClassesLineNumbers, policy, sources, sinks,
						sdgClass);
			}

			
		}
	}

	private void iterateOverMethods(
			Map<String, List<Integer>> mapClassesLineNumbers, Policy policy,
			List<SDGProgramPart> sources, List<SDGProgramPart> sinks,
			SDGClass sdgClass) {
		// por enquanto so marca instrucao de metodo como sink
		for (SDGMethod sdgMethod : sdgClass.getMethods()) {
			IMethod meth = sdgMethod.getMethod();
			List<SDGInstruction> methodInstructions = sdgMethod
					.getInstructions();
			for (SDGInstruction sdgInstruction : methodInstructions) {
				
				
				
				List<Integer> lineNumbers = mapClassesLineNumbers.get(sdgClass.toString());

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

	private void iterateOverAttributes(Policy policy,
			List<SDGProgramPart> sources, List<SDGProgramPart> sinks,
			SDGClass sdgClass) {
		// por enquanto so marca atributo como source
		for (SDGAttribute sdgAttribute : sdgClass.getAttributes()) {
			if (sdgAttribute.toString().equals(
					policy.getSensitiveResource())) {
				if (policy.getOperator().equals("noflow")) {
					sources.add(sdgAttribute);
				} else if (policy.getOperator().equals("noset")) {
					sinks.add(sdgAttribute);
				}
			}
		}
	}
	
	//#endif
}
