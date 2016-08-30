package br.ufpe.cin.policy;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GitIntegration {
	//

	public static List<String> searchCommitHashesFromMessages(
			String targetPathDirectory, String message) throws IOException {

		List<String> commitHashes = new ArrayList<>();
		Runtime rt = Runtime.getRuntime();
		String[] command = new String[] { "git", "--git-dir",
				targetPathDirectory + ".git", "log", "-i", "--grep=" + message };
		iterateLog(commitHashes, rt, command);
		return commitHashes;
	}

	public static List<String> searchCommitHashesFromAuthor(
			String targetPathDirectory, String author) throws IOException {

		List<String> commitHashes = new ArrayList<>();

		Runtime rt = Runtime.getRuntime();
		String[] command = new String[] { "git", "--git-dir",
				targetPathDirectory + ".git", "log", "--author=" + author };
		// --committer
		iterateLog(commitHashes, rt, command);
		return commitHashes;
	}

	public static List<String> searchCommitHashesFromPackage(
			String targetPathDirectory, String pckage) throws IOException {

		List<String> commitHashes = new ArrayList<>();

		Runtime rt = Runtime.getRuntime();
		String[] command = new String[] { "git", "--git-dir",
				targetPathDirectory + ".git", "log", "--", ".",
				"\":(exclude)" + pckage, "\"" };
		iterateLog(commitHashes, rt, command);
		return commitHashes;
	}

	private static void iterateLog(List<String> commitHashes, Runtime rt,
			String[] command) throws IOException {
		Process process = rt.exec(command);

		Scanner scanner = new Scanner(new InputStreamReader(
				process.getInputStream()));

		while (scanner.hasNextLine()) {
			String nextLine = scanner.nextLine();
			if (nextLine.startsWith("commit ")) {
				String commitHash = nextLine.split(" ")[1];
				if (commitHash != null && !commitHash.isEmpty()) {
					commitHashes.add(commitHash);
				}
			}
		}

		iterateOnError(process);
	}
	
	//git --git-dir /Users/rodrigoandrade/Documents/workspaces/Doutorado/opensource/SimpleContributionExample/.git 
	// --work-tree=/Users/rodrigoandrade/Documents/workspaces/Doutorado/opensource/SimpleContributionExample/ 
	// blame -L 12,12 src/clazz/SysoExample.java

	public static String gitBlame(String targetPathDirectory,
			int lineNumber, String filePath) throws IOException {
		Runtime rt = Runtime.getRuntime();
		String[] command = new String[] { "git", "--git-dir",
				targetPathDirectory + ".git", "--work-tree=" + targetPathDirectory,
				"blame", "-L",
				lineNumber + "," + lineNumber, "-p", filePath };
		Process process = rt.exec(command);
		Scanner scanner = new Scanner(new InputStreamReader(
				process.getInputStream()));
		
		/* 2c910df5b67e2fabba5e03c1426c5b16867c26dd 12 13 1
		author Rodrigo Andrade
		author-mail <rodrigo_cardoso@hotmail.it>
		author-time 1462468511
		author-tz -0300
		committer Rodrigo Andrade
		committer-mail <rodrigo_cardoso@hotmail.it>
		committer-time 1462846462
		committer-tz -0300
		summary adding syso example
		filename src/clazz/SysoExample.java */
		String hash = "";
		String committer = "";
		int i = 0;
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (!line.isEmpty()) {
				if (i == 0) {
					String[] firstLine = line.split(" ");
					hash = firstLine[0];
				} else if (line.contains("committer ")) {
					committer = line.substring(10);
				}
			}
			i++;
		}
		if (!hash.isEmpty() && !committer.isEmpty()) {
			return "through " + hash + " and commited by " + committer;
		}
		iterateOnError(process);
		
		return null;
	}
	
	private static void iterateOnError(Process process) {
		Scanner stdError = new Scanner(new InputStreamReader(
				process.getErrorStream()));

		while (stdError.hasNextLine()) {
			System.out.println(stdError.nextLine());
		}
	}

	public static void main(String[] args) {
		try {
			List<String> hashes = GitIntegration
					.searchCommitHashesFromPackage(
							"/Users/rodrigoandrade/Documents/workspaces"
									+ "/Doutorado/opensource/gitblit/",
							"/Users/rodrigoandrade/Documents/workspaces/Doutorado/opensource/gitblit/src/main/java/com/gitblit/auth");
			for (String hash : hashes) {
				System.out.println(hash);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
