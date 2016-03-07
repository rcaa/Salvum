/**
 * This file is part of the Joana IFC project. It is developed at the
 * Programming Paradigms Group of the Karlsruhe Institute of Technology.
 *
 * For further details on licensing please read the information at
 * http://joana.ipd.kit.edu or contact the authors.
 */
package edu.kit.joana.api.example;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.NullProgressMonitor;
import com.ibm.wala.util.graph.GraphIntegrity.UnsoundGraphException;

import edu.kit.joana.api.sdg.SDGConfig;
import edu.kit.joana.api.sdg.SDGProgram;
import edu.kit.joana.ifc.sdg.graph.SDGSerializer;
import edu.kit.joana.ifc.sdg.util.JavaMethodSignature;
import edu.kit.joana.util.Stubs;
import edu.kit.joana.wala.core.SDGBuilder.ExceptionAnalysis;
import edu.kit.joana.wala.core.SDGBuilder.PointsToPrecision;
/**
 * TODO: @author Add your name here.
 */
public class HSQLDBExample {

	public static void main(String[] args) throws ClassHierarchyException, IOException, UnsoundGraphException,
			CancelException {

		// #if HOME
		 PrintStream out = new PrintStream(new FileOutputStream("/Users/rodrigoandrade/Documents/workspaces/Doutorado/joana/joana.api/output.txt"));
		 PrintStream outST = new PrintStream(new FileOutputStream("/Users/rodrigoandrade/Documents/workspaces/Doutorado/joana/joana.api/stackTrace.txt"));
		// #else
//@		PrintStream out = new PrintStream(new FileOutputStream("/home/local/CIN/rcaa2/experimentOutput/output.txt"));
//@		PrintStream outST = new PrintStream(new FileOutputStream("/home/local/CIN/rcaa2/experimentOutput/stackTrace.txt"));
		// #endif
		System.setOut(out);
		System.setErr(outST);

		/**
		 * the class path is either a directory or a jar containing all the
		 * classes of the program which you want to analyze
		 */
		// #if HOME
		 String classPath = "/Users/rodrigoandrade/Documents/workspaces/Doutorado/opensource/HSQLDB/bin";
		// #else
//@		String classPath = "/home/local/CIN/rcaa2/opensource/HSQLDB/bin";
		// #endif

		/**
		 * the entry method is the main method which starts the program you want
		 * to analyze
		 */
		JavaMethodSignature entryMethod = JavaMethodSignature.mainMethodOfClass("org.hsqldb.util.DatabaseManager");
	//	JavaMethodSignature entryMethod = JavaMethodSignature
		//		.fromString("com.gitblit.wicket.pages.RootPage.loginUser(Lcom/gitblit/models/UserModel;)V");
				//.fromString("com.gitblit.manager.AuthenticationManager.authenticate(Ljavax/servlet/http/HttpServletRequest;Z)Lcom/gitblit/models/UserModel;");

		/**
		 * For multi-threaded programs, it is currently neccessary to use the
		 * jdk 1.4 stubs
		 */
		SDGConfig config = new SDGConfig(classPath, entryMethod.toBCString(), Stubs.JRE_15);

		/**
		 * adiciona libs necessarias (dependencias)
		 */
		// #if HOME
		
		 config.setThirdPartyLibsPath("/Users/rodrigoandrade/Documents/workspaces/Doutorado/opensource/HSQLDB/lib/ant.jar");
		 
		 
		 config.setThirdPartyLibsPath("/Users/rodrigoandrade/Documents/workspaces/Doutorado/opensource/HSQLDB/lib/servlet-2_3-fcs-classfiles.jar");
		// #else
//@
//@		config.setThirdPartyLibsPath("/home/local/CIN/rcaa2/opensource/HSQLDB/lib/ant.jar");
//@		config.setThirdPartyLibsPath("/home/local/CIN/rcaa2/opensource/HSQLDB/lib/servlet-2_3-fcs-classfiles.jar");
		// #endif

		/**
		 * compute interference edges to model dependencies between threads (set
		 * to false if your program does not use threads)
		 */
		config.setComputeInterferences(false);

		/**
		 * additional MHP analysis to prune interference edges (does not matter
		 * for programs without multiple threads)
		 */
		//config.setMhpType(MHPType.SIMPLE);

		/**
		 * precision of the used points-to analysis - INSTANCE_BASED is a good
		 * value for simple examples
		 */
		config.setPointsToPrecision(PointsToPrecision.OBJECT_SENSITIVE);

		/**
		 * exception analysis is used to detect exceptional control-flow which
		 * cannot happen
		 */
		config.setExceptionAnalysis(ExceptionAnalysis.IGNORE_ALL);

		/** build the PDG */
		SDGProgram program = SDGProgram.createSDGProgram(config, System.out, new NullProgressMonitor());

		/** optional: save PDG to disk */
		// #if HOME
		 SDGSerializer.toPDGFormat(program.getSDG(), new FileOutputStream(
		 "/Users/rodrigoandrade/Desktop/SDGInformationFlow.pdg"));
		// #else
//@		SDGSerializer.toPDGFormat(program.getSDG(), new FileOutputStream(
//@				"/home/local/CIN/rcaa2/experimentOutput/SDGInformationFlow.pdg"));
		// #endif

		// IFCAnalysis ana = new IFCAnalysis(program);
		// /** annotate sources and sinks */
		// // for example: fields
		// ana.addSourceAnnotation(program.getPart("com.gitblit.models.UserModel.password"),
		// BuiltinLattices.STD_SECLEVEL_HIGH);
		// ana.addSinkAnnotation(
		// program.getMethod("com.gitblit.manager.AuthenticationManager.setCookie(Ljavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/HttpServletResponse;Lcom/gitblit/models/UserModel;)V"),
		// BuiltinLattices.STD_SECLEVEL_LOW);
		//
		// /** run the analysis */
		// Collection<? extends IViolation<SecurityNode>> result = ana.doIFC();
		// TObjectIntMap<IViolation<SDGProgramPart>> resultByProgramPart =
		// ana.groupByPPPart(result);
		// System.out.println(resultByProgramPart);
		// /** do something with result */
	}
}
