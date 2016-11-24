package br.ufpe.cin.preprocessor;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

public class ContributionPreprocessor {

	private String targetPathDirectory;
	private String parentCommitHash;
	private String currentCommitHash;
	private String diffFilePath;

	public ContributionPreprocessor(String sourceDirectory,
			String parentCommitHash, String currentCommitHash,
			String diffFilePath) {
		this.targetPathDirectory = sourceDirectory;
		this.parentCommitHash = parentCommitHash;
		this.currentCommitHash = currentCommitHash;
		this.diffFilePath = setDiffFilePath(currentCommitHash, diffFilePath);
	}

	public ContributionPreprocessor(Properties p, String currentCommitHash)
			throws IOException {
		this.targetPathDirectory = p.getProperty("targetPathDirectory");
		this.currentCommitHash = currentCommitHash;
		this.parentCommitHash = GitUtil.runParents(targetPathDirectory,
				currentCommitHash);
		this.diffFilePath = setDiffFilePath(currentCommitHash,
				p.getProperty("diffFilePath"));
	}

	public void preprocess() throws IOException {

		GitUtil.runDiffCommand(this.targetPathDirectory, this.parentCommitHash,
				this.currentCommitHash, this.diffFilePath);
		Path targetProjPath = GitUtil.loadDiffFile(this.diffFilePath,
				this.currentCommitHash);

		Scanner scanner = new Scanner(targetProjPath);

		ContextManagerContribution manager = ContextManagerContribution
				.getContext();

		String className = "";

		while (scanner.hasNextLine()) {
			String nextLine = scanner.nextLine();

			if (nextLine.startsWith(Tag.DIFF)) {
				className = formatClassName(nextLine);
				if (className == null) {
					// tenho que percorrer as linhas que nao sao interessantes
					// linha de arquivos que nao sao classes
					// percorrer linhas ate o proximo DIFF
					while (scanner.hasNextLine()) {
						String nextLineTemp = scanner.nextLine();
						if (nextLineTemp.contains(Tag.DIFF)) {
							className = formatClassName(nextLineTemp);
							break;
						}
					}
				}
			} else if (nextLine.startsWith(Tag.LINES)) {
				String[] linesAddition = obtainLineNumbers(nextLine, "+");
				Integer lineNumber = Integer.valueOf(linesAddition[0]);
				int totalChunk = Integer.valueOf(linesAddition[1]);

				// String[] linesRemoval = obtainLineNumbers(nextLine, "-");
				// int totalChunkRemoval = Integer.valueOf(linesRemoval[1]);
				//
				// int totalChunk = (totalChunkAddition > totalChunkRemoval) ?
				// totalChunkAddition
				// : totalChunkRemoval;

				int i = 0;

				while (i < totalChunk && scanner.hasNextLine()) {
					String posLine = scanner.nextLine();
					if (posLine.startsWith("+")) {
						// adicionar linha
						manager.addClassLinesInfo(className, lineNumber);
					}
					if (posLine.startsWith("-")) {
						lineNumber--;
						totalChunk++;
					}
					lineNumber++;
					i++;
				}
			}
		}
		scanner.close();

		GitUtil.checkoutCommitHash(this.targetPathDirectory,
				this.currentCommitHash);

		System.out.println(manager.getMapClassesLineNumbers().toString());
	}

	private String formatClassName(String nextLine) {
		String className = null;
		// significa que comeca o diff de um novo arquivo
		if (nextLine != null && nextLine.contains(".java")
				&& nextLine.contains("/src")) {
			className = nextLine.substring(nextLine.indexOf("/src"),
					nextLine.indexOf(".java"));
			className = className.replace("/", ".");
			className = className.replace(".src.", "");
		}
		return className;
	}

	private String[] obtainLineNumbers(String nextLine, String signal) {
		int indexOf;
		if (signal.equals("+")) {
			indexOf = nextLine.indexOf(" " + Tag.LINES);
		} else {
			indexOf = nextLine.indexOf(" +");
		}
		String substring = nextLine.substring(nextLine.indexOf(signal) + 1,
				indexOf);
		String[] lines = substring.split(",");
		return lines;
	}

	public static String setDiffFilePath(String currentCommitHash,
			String diffFilePath) {
		return diffFilePath + currentCommitHash.substring(0, 8) + ".txt";
	}

	/**
	 * 
	 * @param args
	 *            args[0] is the target project path; args[1] is the parent
	 *            commit hash; args[2] is the child commit hash; args[3] is the
	 *            diff file path
	 */
	public static void main(String[] args) {
		try {
			// test purposes
			Properties p = new Properties();
			FileInputStream in = new FileInputStream(
					"/Users/rodrigoandrade/Documents/workspaces/Doutorado/joana"
							+ "/Salvum/configFiles/openrefinelocal.properties");
			p.load(in);

			String[] hashes = { "533e7ba0dc83391e24ea77b5c4312fa0d364a659",
					"35b01fb33bd58760253f3c6c0524cdc50c81adc0",
					"1a6649e4f38334411003517fef020341c45ff399",
					"82ab9c28caeec3682c6850123f6ee0c5fc6c8a6b",
					"175f4a5319f082b9182908a13c1f775781a3ddde",
					"20220f7294ab6d18b1fe6575e49955fb0d1e48aa",
					"94e219042edf2b7cda5785f7c87af04e40d0672c",
					"85ffce60d2958c561f1e75982a618ea88980b30b",
					"9b2a506caada4ea6d580d2140184fef8caa7566c",
					"b3aae19568204e683dfea1ed0c1cbc7e766a0976",
					"cdda1edcf08976871f7455e6f7d869fe6aa9cdee",
					"f2c4e3ab486207412ff9ac8678f0237e3828d2ca" };
			
			System.out.println("Starting tests: ");
			for (String hash : hashes) {
				ContributionPreprocessor cp = new ContributionPreprocessor(p,
						hash);
				cp.preprocess();
				ContextManagerContribution contextContribution = ContextManagerContribution
						.getContext();
				Map<String, List<Integer>> mapClassesLineNumbers = contextContribution
						.getMapClassesLineNumbers();
				cp.preprocess();
				System.out.println("-----------------------------------------------------------");
				System.out.println("Starting new mapping for hash " + hash);
				System.out.println();
				System.out.println(mapClassesLineNumbers);
				System.out.println();
				System.out.println("-----------------------------------------------------------");
				contextContribution.clear();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
