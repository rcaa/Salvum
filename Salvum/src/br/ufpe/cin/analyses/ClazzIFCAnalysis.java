package br.ufpe.cin.analyses;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;

import br.ufpe.cin.policy.GitIntegration;
import br.ufpe.cin.policy.PolicyClazz;
import br.ufpe.cin.util.FileUtil;

import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.WalaException;
import com.ibm.wala.util.graph.GraphIntegrity.UnsoundGraphException;

import edu.kit.joana.api.IFCAnalysis;
import edu.kit.joana.api.sdg.SDGClass;
import edu.kit.joana.api.sdg.SDGProgram;
import edu.kit.joana.api.sdg.SDGProgramPart;
import edu.kit.joana.ifc.sdg.core.SecurityNode;
import edu.kit.joana.ifc.sdg.core.violations.ClassifiedViolation;
import edu.kit.joana.ifc.sdg.core.violations.IViolation;

public class ClazzIFCAnalysis {

	public static void main(String[] args) {

		// String propertiesPath =
		// "C:\\Doutorado\\workspace\\Salvum\\Salvum\\configFiles\\"
		// + "voldemort-local.properties";

		String propertiesPath = args[0];

		Properties p = FileUtil.getPropertiesFile(propertiesPath);

		try {
			ClazzIFCAnalysis m = new ClazzIFCAnalysis();
			m.run(p, propertiesPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "unchecked" })
	private void run(Properties p, String propertiesPath) throws WalaException,
			IllegalArgumentException, IOException, UnsoundGraphException,
			CancelException, CoreException {

		// obtenho a policy

		// Segundo passo logico
		AnalysisConfig ana = new AnalysisConfig();

		// JavaMethodSignature entryMethod =
		// JavaMethodSignature.fromString(p
		// .getProperty("main"));
		// JavaMethodSignature entryMethod = JavaMethodSignature
		// .mainMethodOfClass(p.getProperty("main"));

		/*
		 * SDGProgram program = null; try { program =
		 * ana.buildSDG(p.getProperty("classpath"), entryMethods,
		 * p.getProperty("thirdPartyLibsPath"));
		 * 
		 * } catch (IllegalStateException e) {
		 * System.out.println(e.getMessage()); }
		 */

		// get sdg

		String sdgsDirectoryPath = p.getProperty("sdgsDirectoryPath");
		File sdgsDirectory = new File(sdgsDirectoryPath);
		File[] sdgs = sdgsDirectory.listFiles();
		for (File sdg : sdgs) {

			SDGProgram program = ana.retrieveSDG(sdg.getAbsolutePath());

			// save sdg
			// SDGSerializer.toPDGFormat(program.getSDG(),
			// new
			// FileOutputStream("/home/local/CIN/rcaa2/contributionExperiments/joana/SDGFile.pdg"));

			Collection<SDGClass> classes = program.getClasses();

			// rotulo statements e expressions
			List<SDGProgramPart> sources = new ArrayList<SDGProgramPart>();
			List<SDGProgramPart> sinks = new ArrayList<SDGProgramPart>();
			LabelConfigClazz lconfig = new LabelConfigClazz();

			Path path = FileSystems.getDefault().getPath(
					p.getProperty("policyDirectory"));
			PolicyClazz policy = new PolicyClazz(path);

			FileInputStream fis = new FileInputStream("map.ser");
			ObjectInputStream ois = new ObjectInputStream(fis);
			Map<String,Set<Integer>> mapClassLines = null;
			try {
				mapClassLines = (Map<String,Set<Integer>>) ois.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			ois.close();

			lconfig.prepareListsOfSourceAndSinks(classes, mapClassLines,
					policy, sources, sinks);

			// rodo as analises
			IFCAnalysis ifc = new IFCAnalysis(program);
			lconfig.labellingElements(sources, sinks, program, ifc);
			Collection<? extends IViolation<SecurityNode>> result = ifc.doIFC();
			for (IViolation<SecurityNode> iViolation : result) {
				ClassifiedViolation sn = (ClassifiedViolation) iViolation;
				SecurityNode source = sn.getSource();
				SecurityNode sink = sn.getSink();
				if (policy.getOperator().equals("noflow")) {
					if (sn != null && sink != null && source != null
							&& sink.getBytecodeIndex() >= 0) {
						String filePath = "src" + "/" + sink.getSource();
						System.out.println("Illegal flow from "
								+ source.getBytecodeName()
								+ " to "
								+ sink.getBytecodeName()
								+ " at line "
								+ sink.getEr()
								+ " "
								+ GitIntegration.gitBlame(
										p.getProperty("gitPath"), sink.getEr(),
										filePath));
					}
				} else if (policy.getOperator().equals("noset")) {
					if (sn != null && source != null && sink != null
							&& source.getBytecodeIndex() >= 0) {
						System.out.println("Illegal set "
								+ sink.getBytecodeName() + " at "
								+ source.getBytecodeName() + " at line "
								+ source.getEr());
					}
				}
			}

			program = null;
			ifc = null;
			classes.clear();

			sources.clear();
			sinks.clear();
			System.out
					.println("---------------------------------END-----------------------------------");
		}

	}
}
