package br.ufpe.cin.analyses;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.ufpe.cin.util.LibFilterUtil;

import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.NullProgressMonitor;
import com.ibm.wala.util.graph.GraphIntegrity.UnsoundGraphException;

import edu.kit.joana.api.sdg.SDGConfig;
import edu.kit.joana.api.sdg.SDGProgram;
import edu.kit.joana.ifc.sdg.graph.SDG;
import edu.kit.joana.ifc.sdg.mhpoptimization.MHPType;
import edu.kit.joana.util.Stubs;
import edu.kit.joana.wala.core.SDGBuilder.ExceptionAnalysis;
import edu.kit.joana.wala.core.SDGBuilder.FieldPropagation;
import edu.kit.joana.wala.core.SDGBuilder.PointsToPrecision;

public class AnalysisConfig {

	public SDGProgram retrieveSDG(String path) throws IOException {
		return new SDGProgram(SDG.readFrom(path));
	}

	public SDGProgram buildSDG(String classPath, List<String> entryMethods,
			String thirdPartyLibsPath) throws ClassHierarchyException,
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
		SDGConfig config = new SDGConfig(classPath, null, Stubs.JRE_14);

		config.setEntryMethods(entryMethods);

		/**
		 * compute interference edges to model dependencies between threads (set
		 * to false if your program does not use threads)
		 */
		config.setComputeInterferences(true);

		/**
		 * additional MHP analysis to prune interference edges (does not matter
		 * for programs without multiple threads)
		 */
		config.setMhpType(MHPType.SIMPLE);

		/**
		 * precision of the used points-to analysis - INSTANCE_BASED is a good
		 * value for simple examples
		 */
		config.setPointsToPrecision(PointsToPrecision.INSTANCE_BASED);

		/**
		 * exception analysis is used to detect exceptional control-flow which
		 * cannot happen
		 */
		config.setExceptionAnalysis(ExceptionAnalysis.INTERPROC);

		config.setFieldPropagation(FieldPropagation.OBJ_GRAPH);

		String libsPath = prepareLibsPath(thirdPartyLibsPath);

		config.setThirdPartyLibsPath(libsPath);

		/** build the PDG */
		SDGProgram program = SDGProgram.createSDGProgram(config, System.out,
				new NullProgressMonitor());

		/** optional: save PDG to disk */
		// SDGSerializer.toPDGFormat(program.getSDG(), new FileOutputStream(
		// "/Users/rodrigoandrade/Dropbox/Temp/SDGDirect.pdg"));
		return program;
	}

	private String prepareLibsPath(String thirdPartyLibsPath)
			throws IOException {
		String libsPath = "";
		if (thirdPartyLibsPath != null && !thirdPartyLibsPath.isEmpty()) {
			thirdPartyLibsPath = includeSubfolders(thirdPartyLibsPath);
			if (thirdPartyLibsPath.contains("%")) {
				String[] paths = thirdPartyLibsPath.split("%");
				for (int i = 0; i < paths.length; i++) {
					String temp = paths[i];
					if (i == (paths.length - 1)) {
						libsPath += iterateFiles(temp, libsPath);
					} else {
						String filePaths = iterateFiles(temp, libsPath);
						libsPath += filePaths.isEmpty() ? ""
								: (filePaths + ":");
					}
				}
			} else {
				libsPath = iterateFiles(thirdPartyLibsPath, libsPath);
			}
		}
		return libsPath;
	}

	private String includeSubfolders(String thirdPartyLibsPath)
			throws IOException {
		String thirdPartyLibsPathTobeUsed = "";
		if (thirdPartyLibsPath.contains("*")) {
			// using a collecting parameter
			List<String> jarPathsDirectories = new ArrayList<String>();
			walk(jarPathsDirectories, thirdPartyLibsPath.substring(0,
					thirdPartyLibsPath.length() - 1));
			thirdPartyLibsPathTobeUsed = createThirdLibsPath(
					thirdPartyLibsPathTobeUsed, jarPathsDirectories);
			return thirdPartyLibsPathTobeUsed;
		}
		return thirdPartyLibsPath;

	}

	private String createThirdLibsPath(String thirdPartyLibsPathTobeUsed,
			List<String> jarPathsList) {
		for (String jar : jarPathsList) {
			if (thirdPartyLibsPathTobeUsed.equals("")) {
				thirdPartyLibsPathTobeUsed = jar;
			} else {
				thirdPartyLibsPathTobeUsed = thirdPartyLibsPathTobeUsed + "%"
						+ jar;
			}
		}
		return thirdPartyLibsPathTobeUsed;
	}

	private void walk(List<String> jarPathsList, String path)
			throws IOException {
		File root = new File(path);
		File[] list = root.listFiles();

		if (list == null) {
			return;
		}
		for (File f : list) {
			if (f.isDirectory()) {
				if (containsJar(f)) {
					jarPathsList.add(f.getCanonicalPath());
				}
				walk(jarPathsList, f.getCanonicalPath());
			}
		}
	}

	private String iterateFiles(String thirdPartyLibsPath, String libsPath)
			throws IOException {
		File[] files = new File(thirdPartyLibsPath)
				.listFiles(new LibFilterUtil());
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (i == (files.length - 1)) {
				libsPath += file.getCanonicalPath();
			} else {
				libsPath += file.getCanonicalPath() + ":";
			}
		}
		return libsPath;
	}

	private boolean containsJar(File file) {
		String[] listFiles = file.list();
		if (listFiles != null) {
			for (String f : listFiles) {
				if (f.endsWith(".jar") || f.endsWith(".zip")) {
					return true;
				}
			}
		}
		return false;
	}
}
