package br.ufpe.cin.ant;

import java.io.IOException;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;

import br.ufpe.cin.util.ZipUtil;

public class ProjectBuilder {

	public void compileProject(Properties p) throws IOException, CoreException {

		// Project project = new Project();
		// File buildFile = new File(p.getProperty("targetPathDirectory")
		// + "build.xml");
		// project.setUserProperty("ant.file", buildFile.getAbsolutePath());
		// project.setProperty("java.home", p.getProperty("javahome"));
		// project.init();
		// ProjectHelper helper = ProjectHelper.getProjectHelper();
		// project.addReference("ant.projectHelper", helper);
		// helper.parse(project, buildFile);
		// String target = project.getDefaultTarget();
		// project.executeTarget(target);

		ZipUtil zu = new ZipUtil();
		zu.unzip(
				"/Users/rodrigoandrade/Desktop/bins-gitblit/Archive-75ebd391b8888.zip",
				p.getProperty("targetPathDirectory"));
	}

}
