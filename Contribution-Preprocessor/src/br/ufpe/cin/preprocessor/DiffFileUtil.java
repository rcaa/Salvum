package br.ufpe.cin.preprocessor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DiffFileUtil {

	public static void runDiffCommand(String targetProjectPath,
			String parentCommitHash, String childCommitHash, String diffFilePath) throws IOException {
		Runtime rt = Runtime.getRuntime();
		String[] gitDiffCommands = {
				"bash",
				"-c",
				"git --git-dir " + targetProjectPath + ".git diff "
						+ parentCommitHash + " " + childCommitHash
						+ " > " + diffFilePath };
		rt.exec(gitDiffCommands);
	}
	
	public static Path loadDiffFile(String diffFilePath) throws IOException {
		return Paths.get(diffFilePath);
	}

	public static void deleteDiffFile(Path targetProjPath) throws IOException {
		Files.delete(targetProjPath);
	}
	
}
