package br.ufpe.cin.preprocessor;

//#if CONTRIBUTION
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GitUtil {

	public static void runDiffCommand(String targetPathDirectory,
			String parentCommitHash, String currentCommitHash, String diffFilePath)
			throws IOException {
		Runtime rt = Runtime.getRuntime();
		String[] gitDiffCommands = {
				"bash",
				"-c",
				Tag.GIT_DIR + targetPathDirectory + ".git diff "
						+ parentCommitHash + " " + currentCommitHash + " > "
						+ diffFilePath };
		rt.exec(gitDiffCommands);
	}

	public static Path loadDiffFile(String diffFilePath, String currentCommitHash) throws IOException {
		return Paths.get(diffFilePath);
	}

//	public static void deleteDiffFile(Path targetProjPath) throws IOException {
//		Files.delete(targetProjPath);
//	}

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
			return s.split(" ")[1];
		}

		BufferedReader stdError = new BufferedReader(new InputStreamReader(
				process.getErrorStream()));

		// read any errors from the attempted command
		
		s = stdError.readLine();
		if (s != null) {
			return s;
		}
		return null;
	}
	
	public void checkoutCommitHash(String currentCommitHash) throws IOException {
		Runtime rt = Runtime.getRuntime();
		String[] gitCheckoutCommand = {
				"bash",
				"-c",
				Tag.GIT_DIR + "checkout " + currentCommitHash };
		rt.exec(gitCheckoutCommand);
	}
}
// #endif