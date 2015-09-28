package br.ufpe.cin.ant;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.eclipse.core.runtime.CoreException;

public class ProjectBuilder {
	
	public void compileProject(Properties p) throws IOException, CoreException {
		// Set<String> classesTemp = mapClassesLineNumbers.keySet();
		// Runtime rt = Runtime.getRuntime();
		// for (String clazz : classesTemp) {
		// String compileCommand = "javac -d " + p.getProperty("classpath")
		// + " -sourcepath " + p.getProperty("targetPathDirectory") + "src "
		// + "-cp " + p.getProperty("thirdPartyLibsPath") + " "
		// + p.getProperty("targetPathDirectory") + "/src/"
		// + clazz.replace('.', '/') + ".java";
		// Process process = rt.exec(compileCommand);
		// GitUtil.createOutputCommandLine(process);
		// }

		// chamar o build.xml via ant
		// Runtime rt = Runtime.getRuntime();
		// String compileCommand = "ant -f " +
		// p.getProperty("targetPathDirectory") + "build.xml";
		// Process process = rt.exec(compileCommand);
		// GitUtil.createOutputCommandLine(process);

		Project project = new Project();
		// project.setProperty("java.home", p.getProperty("javahome"));
		File buildFile = new File(p.getProperty("targetPathDirectory")
				+ "build.xml");
		project.setUserProperty("ant.file", buildFile.getAbsolutePath());
		project.setProperty("java.home", p.getProperty("javahome"));
		project.init();
		ProjectHelper helper = ProjectHelper.getProjectHelper();
		project.addReference("ant.projectHelper", helper);
		helper.parse(project, buildFile);
		String target = project.getDefaultTarget();
		project.executeTarget(target);

//		AntRunner runner = new AntRunner();
//		runner.setBuildFileLocation(p.getProperty("targetPathDirectory")
//				+ "build.xml");
//		runner.setArguments("-Dmessage=Building -verbose");
//		runner.run();
	}

}
