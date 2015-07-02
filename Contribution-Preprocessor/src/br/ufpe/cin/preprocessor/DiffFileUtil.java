package br.ufpe.cin.preprocessor;

//#if CONTRIBUTION
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DiffFileUtil {

	public static void runDiffCommand(String targetPathDirectory,
			String parentCommitHash, String childCommitHash, String diffFilePath)
			throws IOException {
		Runtime rt = Runtime.getRuntime();
		String[] gitDiffCommands = {
				"bash",
				"-c",
				"git --git-dir " + targetPathDirectory + ".git diff "
						+ parentCommitHash + " " + childCommitHash + " > "
						+ diffFilePath };
		rt.exec(gitDiffCommands);
	}

	public static Path loadDiffFile(String diffFilePath) throws IOException {
		return Paths.get(diffFilePath);
	}

	public static void deleteDiffFile(Path targetProjPath) throws IOException {
		Files.delete(targetProjPath);
	}

	public static String runParents(String targetPathDirectory,
			String commitHash) throws IOException {
		Runtime rt = Runtime.getRuntime();
		String[] gitDiffCommands = {
				"bash",
				"-c",
				"git --git-dir " + targetPathDirectory
						+ ".git rev-list --parents -n 1  " + commitHash };
		Process process = rt.exec(gitDiffCommands);
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
		System.out
				.println("Here is the standard error of the command (if any):\n");
		s = stdError.readLine();
		if (s != null) {
			return s;
		}
		return null;
	}

}
// #endif