package br.ufpe.cin.preprocessor;

import java.io.IOException;
import java.nio.file.Path;
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
		// test purposes with gitblit
		String projectPath = "/Users/rodrigoandrade/Documents/workspaces/Doutorado/opensource/SimpleContributionExample/";
		String parentCommit = "cd05cb8fba6621b64799735da179e6794c810bb3";
		String currentCommitHash = "d61da68d24a09d308ef1a5ab020219de65d029a3";
		String diffFile = "/Users/rodrigoandrade/Documents/workspaces/Doutorado/joana/Contribution-Preprocessor/diffFiles/";

		try {
			// test purposes with gitblit
			ContributionPreprocessor cp = new ContributionPreprocessor(
					projectPath, parentCommit, currentCommitHash, diffFile);
			cp.preprocess();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
