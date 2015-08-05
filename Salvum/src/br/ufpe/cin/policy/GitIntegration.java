package br.ufpe.cin.policy;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import br.ufpe.cin.preprocessor.Tag;

public class GitIntegration {

	public static List<String> searchCommitHashesFromMessages(
			String targetPathDirectory, String message) throws IOException {
		Runtime rt = Runtime.getRuntime();
		String command = Tag.GIT_DIR + targetPathDirectory
				+ ".git log --grep=" + message;
		System.out.println(command);
		Process process = rt.exec(command);

		Scanner scanner = new Scanner(new InputStreamReader(
				process.getInputStream()));

		List<String> commitHashes = new ArrayList<>();

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
		return commitHashes;
	}

	public static void main(String[] args) {
		try {
			List<String> hashes = GitIntegration
					.searchCommitHashesFromMessages(
							"/Users/rodrigoandrade/Documents/workspaces"
									+ "/Doutorado/opensource/SimpleContributionExample/",
							"adding");
			for (String hash : hashes) {
				System.out.println(hash);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
