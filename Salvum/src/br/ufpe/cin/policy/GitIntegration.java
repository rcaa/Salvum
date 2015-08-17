package br.ufpe.cin.policy;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GitIntegration {

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
		iterateLog(commitHashes, rt, command);
		return commitHashes;
	}

	public static List<String> searchCommitHashesFromFile(
			String targetPathDirectory, String file) throws IOException {

		List<String> commitHashes = new ArrayList<>();

		Runtime rt = Runtime.getRuntime();
		String[] command = new String[] {"git", "--git-dir",
				targetPathDirectory + ".git", "log", "-i", "--", file };
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

		Scanner stdError = new Scanner(new InputStreamReader(
				process.getErrorStream()));

		while (stdError.hasNextLine()) {
			System.out.println(stdError.nextLine());
		}
	}

	public static void main(String[] args) {
		try {
			List<String> hashes = GitIntegration
					.searchCommitHashesFromAuthor(
							"/Users/rodrigoandrade/Documents/workspaces"
									+ "/Doutorado/opensource/SimpleContributionExample/",
							"rodrigo_cardoso@hotmail.it");
			for (String hash : hashes) {
				System.out.println(hash);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
