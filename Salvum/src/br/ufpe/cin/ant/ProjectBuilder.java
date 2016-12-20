package br.ufpe.cin.ant;

// #if CONTRIBUTION
import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;

//#endif

public class ProjectBuilder {
//#if CONTRIBUTION

	public static void compileProject(Properties p) throws IOException,
			CoreException {

		// para OpenRefine
		// removeOldFiles(p);

		Runtime rt = Runtime.getRuntime();
		String[] command = new String[] { "ant", "-buildfile", p.getProperty("build"), "clean", "compile" };
		rt.exec(command);

		// Project project = new Project();
		// File buildFile = new File(p.getProperty("targetPathDirectory")
		// + p.getProperty("buildScriptPath"));
		// project.setUserProperty("ant.file", buildFile.getAbsolutePath());
		// project.setProperty("java.home", p.getProperty("javahome"));
		// project.init();
		// ProjectHelper helper = ProjectHelper.getProjectHelper();
		// project.addReference("ant.projectHelper", helper);
		// helper.parse(project, buildFile);
		//
		// String target = project.getDefaultTarget();
		// project.executeTarget(target);

		// gitblit
		// ZipUtil zu = new ZipUtil();
		// zu.unzip(p.getProperty("zips") + "Archive-" + commitHash + ".zip",
		// p.getProperty("targetPathDirectory"));
	}

	private static void removeOldFiles(Properties p) throws IOException {
		File buildDirectory = new File(p.getProperty("classpath"));
		purgeDirectory(buildDirectory);
	}

	private static void purgeDirectory(File dir) {
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				purgeDirectory(file);
			}
			file.delete();
		}
	}
	// #endif
}

