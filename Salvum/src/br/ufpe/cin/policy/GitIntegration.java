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
				lineNumber + "," + lineNumber, filePath };
		Process process = rt.exec(command);
		Scanner scanner = new Scanner(new InputStreamReader(
				process.getInputStream()));
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (!line.isEmpty()) {
				String[] elements = line.split(" ");
				// 2625a31c (Rodrigo Andrade 2016-05-05 14:15:11 -0300 12) 		System.out.println(this.password);
				String output = " through " + elements[0] 
						+ " and commited by " + elements[1].substring(1);
				int i = 2;
				while (!(elements[i].matches("\\d{4}-\\d{2}-\\d{2}") 
						&& elements[i].length() == 10)) {
					output += elements[i];
					i++;
				}
				return output;
			}
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
