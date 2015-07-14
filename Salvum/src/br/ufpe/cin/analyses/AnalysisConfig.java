package br.ufpe.cin.analyses;

import java.io.IOException;

import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.NullProgressMonitor;
import com.ibm.wala.util.graph.GraphIntegrity.UnsoundGraphException;

import edu.kit.joana.api.sdg.SDGConfig;
import edu.kit.joana.api.sdg.SDGProgram;
import edu.kit.joana.ifc.sdg.util.JavaMethodSignature;
import edu.kit.joana.util.Stubs;
import edu.kit.joana.wala.core.SDGBuilder.ExceptionAnalysis;
import edu.kit.joana.wala.core.SDGBuilder.PointsToPrecision;

public class AnalysisConfig {

	public SDGProgram prepareAnalysis(String classPath,
			JavaMethodSignature entryMethod) throws ClassHierarchyException,
			IOException, UnsoundGraphException, CancelException {
		/**
		 * the class path is either a directory or a jar containing all the
		 * classes of the program which you want to analyze
		 */
		// String classPath =
		// "/Users/rodrigoandrade/Documents/workspaces/Doutorado/wala/SimpleExamples/bin/";

		/**
		 * the entry method is the main method which starts the program you want
		 * to analyze
		 */
		// JavaMethodSignature entryMethod = JavaMethodSignature
		// .mainMethodOfClass("Main");

		/**
		 * For multi-threaded programs, it is currently necessary to use the jdk
		 * 1.4 stubs
		 */
		SDGConfig config = new SDGConfig(classPath, entryMethod.toBCString(),
				Stubs.JRE_15);

		/**
		 * compute interference edges to model dependencies between threads (set
		 * to false if your program does not use threads)
		 */
		config.setComputeInterferences(false);

		/**
		 * additional MHP analysis to prune interference edges (does not matter
		 * for programs without multiple threads)
		 */
		// config.setMhpType(MHPType.PRECISE);

		/**
		 * precision of the used points-to analysis - INSTANCE_BASED is a good
		 * value for simple examples
		 */
		config.setPointsToPrecision(PointsToPrecision.TYPE_BASED);

		/**
		 * exception analysis is used to detect exceptional control-flow which
		 * cannot happen
		 */
		config.setExceptionAnalysis(ExceptionAnalysis.INTERPROC);
		
		
		
		config.setThirdPartyLibsPath("/Users/rodrigoandrade/Documents/workspaces/Doutorado/opensource/gitblit/ext/wicket-1.4.21.jar");
		 
		 
		config.setThirdPartyLibsPath("/Users/rodrigoandrade/Documents/workspaces/Doutorado/opensource/gitblit/ext/javax.servlet-api-3.1.0.jar");
		

		/** build the PDG */
		SDGProgram program = SDGProgram.createSDGProgram(config, System.out,
				new NullProgressMonitor());

		/** optional: save PDG to disk */
		// SDGSerializer.toPDGFormat(program.getSDG(), new FileOutputStream(
		// "/Users/rodrigoandrade/Dropbox/Temp/SDGDirect.pdg"));
		return program;
	}
}
