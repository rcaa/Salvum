package br.ufpe.cin.analyses;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;

import br.ufpe.cin.mapping.LineMappingGenerator;
import br.ufpe.cin.mapping.MappingGenerator;
import br.ufpe.cin.policy.PolicyContribution;

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

public class ContributionIFCAnalysis {

	public static void main(String[] args) {

		// String propertiesPath =
		// "C:\\Doutorado\\workspace\\Salvum\\Salvum\\configFiles\\"
		// + "voldemort-local.properties";

		String propertiesPath = args[0];

		Properties p = getPropertiesFile(propertiesPath);

		try {
			System.out.println("Starting Contribution IFC Analysis...");
			ContributionIFCAnalysis m = new ContributionIFCAnalysis();
			m.run(p, propertiesPath);
			System.out.println("Ending Contribution IFC Analysis...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void run(Properties p, String propertiesPath) throws WalaException,
			IllegalArgumentException, IOException, CancelException,
			CoreException, UnsoundGraphException {

		String sdgsDirectoryPath = p.getProperty("sdgsDirectoryPath");
		File sdgsDirectory = new File(sdgsDirectoryPath);
		File[] sdgs = sdgsDirectory.listFiles();
		for (File sdg : sdgs) {
			if (sdg.isDirectory()) {
				continue;
			}
			System.out.println("Starting IFC for: " + sdg.getName());
			SDGProgram program = SDGProgram.loadSDG(sdg.getAbsolutePath());
			Collection<SDGClass> classes = program.getClasses();
			List<SDGProgramPart> sources = new ArrayList<SDGProgramPart>();
			List<SDGProgramPart> sinks = new ArrayList<SDGProgramPart>();
			LabelConfigContribution lconfig = new LabelConfigContribution();
			Path path = FileSystems.getDefault().getPath(
					p.getProperty("policyDirectory"));
			String sdgFileName = sdg.getName();
			String hash = sdgFileName.substring(sdgFileName.indexOf('-'),
					sdgFileName.indexOf('.'));
			PolicyContribution policy = new PolicyContribution(path, hash);

			Map<String, Set<Integer>> mapClassLines = MappingGenerator
					.loadMapping(p, sdg, "-contribution");
			
			Map<String, Integer> mapInstructionsLines = LineMappingGenerator
					.loadMapping(p, sdg);

			lconfig.prepareListsOfSourceAndSinksContribution(classes, mapClassLines,
					policy, sources, sinks, mapInstructionsLines);
			
			// rodo as analises
			IFCAnalysis ifc = new IFCAnalysis(program);
			lconfig.labellingElements(sources, sinks, program, ifc);
			Collection<? extends IViolation<SecurityNode>> result = ifc.doIFC();
			if (result == null || result.isEmpty()) {
				System.out.println("----No violations found----");
			}
			
			removeDuplicatedViolations(result);
			
			for (IViolation<SecurityNode> iViolation : result) {
				ClassifiedViolation sn = (ClassifiedViolation) iViolation;
				SecurityNode source = sn.getSource();
				SecurityNode sink = sn.getSink();
				if (policy.getOperator().equals("noflow")) {
					if (sn != null && sink != null && source != null
							&& sink.getBytecodeIndex() >= 0) {
						System.out.println("Illegal flow from "
								+ source.getBytecodeName() + " to "
								+ sink.getBytecodeName() + " at line "
								+ sink.getEr() + " in commit " + hash);
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
			System.out.println("Ending IFC for: " + sdg.getName());
		}
	}

	private static Properties getPropertiesFile(String propertiesPath) {
		Properties p = new Properties();
		FileInputStream in = null;
		try {
			in = new FileInputStream(propertiesPath);
			p.load(in);
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return p;
	}
	
	private void removeDuplicatedViolations(
			Collection<? extends IViolation<SecurityNode>> result) {
		List<String> stringViolation = new ArrayList<>();
		for (IViolation<SecurityNode> iViolation : result) {
			if (stringViolation.contains(iViolation.toString())) {
				result.remove(iViolation);
			} else {
				stringViolation.add(iViolation.toString());
			}
		}
	}
}
