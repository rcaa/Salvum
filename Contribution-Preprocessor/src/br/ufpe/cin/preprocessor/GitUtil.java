package br.ufpe.cin.preprocessor;

//#if CONTRIBUTION
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GitUtil {

	public static void runDiffCommand(String targetPathDirectory,
			String parentCommitHash, String currentCommitHash,
			String diffFilePath) throws IOException {
		Runtime rt = Runtime.getRuntime();
		String[] gitDiffCommands = {
				"bash",
				"-c",
				Tag.GIT_DIR + targetPathDirectory + ".git diff "
						+ parentCommitHash + " " + currentCommitHash + " -- '*.java'"};
		Process process = rt.exec(gitDiffCommands);

		BufferedWriter bw = createDiffFile(diffFilePath);

		writeDiffFile(process, bw);

		writeErrorsDiffFile(process, bw);

		bw.close();
	}

	// public static void deleteDiffFile(Path targetProjPath) throws IOException
	// {
	// Files.delete(targetProjPath);
	// }

	public static String runParents(String targetPathDirectory,
			String currentCommitHash) throws IOException {
		Runtime rt = Runtime.getRuntime();
		String[] gitParentCommitCommand = {
				"bash",
				"-c",
				Tag.GIT_DIR + targetPathDirectory
						+ ".git rev-list --parents -n 1  " + currentCommitHash };
		Process process = rt.exec(gitParentCommitCommand);
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(
				process.getInputStream()));

		// read the output from the command
		String s = stdInput.readLine();
		if (s != null) {
			stdInput.close();
			return s.split(" ")[1];
		}

		BufferedReader stdError = new BufferedReader(new InputStreamReader(
				process.getErrorStream()));

		// read any errors from the attempted command

		s = stdError.readLine();
		if (s != null) {
			stdError.close();
			return s;
		}
		throw new IOException("wrong diff file entries");
	}

	public static void checkoutCommitHash(String targetPathDirectory,
			String currentCommitHash) throws IOException {
		Runtime rt = Runtime.getRuntime();

		String exect = Tag.GIT_DIR + targetPathDirectory + ".git "
				+ "checkout -f " + currentCommitHash;
		Process process = rt.exec(exect);

		createOutputCommandLine(process);
	}

	public static void createOutputCommandLine(Process process)
			throws IOException {
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(
				process.getInputStream()));
		String line = "";
		while ((line = stdInput.readLine()) != null) {
			System.out.println(line);
		}
		stdInput.close();

		BufferedReader stdError = new BufferedReader(new InputStreamReader(
				process.getErrorStream()));
		String line2 = "";
		while ((line2 = stdError.readLine()) != null) {
			System.out.println(line2);
		}
		stdError.close();
	}

	private static void writeErrorsDiffFile(Process process, BufferedWriter bw)
			throws IOException {
		String line = "";
		BufferedReader stdError = new BufferedReader(new InputStreamReader(
				process.getErrorStream()));

		// read any errors from the attempted command

		while ((line = stdError.readLine()) != null) {
			bw.write(line + "\n");
		}
		stdError.close();
	}

	private static void writeDiffFile(Process process, BufferedWriter bw)
			throws IOException {
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(
				process.getInputStream()));

		// read the output from the command
		String line = "";
		while ((line = stdInput.readLine()) != null) {
			bw.write(line + "\n");
		}
		stdInput.close();
	}

	private static BufferedWriter createDiffFile(String diffFilePath)
			throws IOException {
		File file = new File(diffFilePath);
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		return bw;
	}

	public static Path loadDiffFile(String diffFilePath,
			String currentCommitHash) throws IOException {
		return Paths.get(diffFilePath);
	}
}
// #endif