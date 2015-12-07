package br.ufpe.cin.ant;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.eclipse.core.runtime.CoreException;

public class ProjectBuilder {

	public void compileProject(Properties p, String commitHash)
			throws IOException, CoreException {

		 Project project = new Project();
		 File buildFile = new File(p.getProperty("targetPathDirectory")
		 + p.getProperty("buildScriptPath"));
		 project.setUserProperty("ant.file", buildFile.getAbsolutePath());
		 project.setProperty("java.home", p.getProperty("javahome"));
		 project.init();
		 ProjectHelper helper = ProjectHelper.getProjectHelper();
		 project.addReference("ant.projectHelper", helper);
		 helper.parse(project, buildFile);
		 String target = project.getDefaultTarget();
		 project.executeTarget(target);

		
		// gitblit
//		ZipUtil zu = new ZipUtil();
//		zu.unzip(p.getProperty("zips") + "Archive-" + commitHash + ".zip",
//				p.getProperty("targetPathDirectory"));
	}
}
