package finders;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Finder {

	public void findTerms(String path, String[] terms)
			throws FileNotFoundException {

		File[] files = new File(path).listFiles();
		List<File> javaFiles = new ArrayList<>();

		searchFiles(files, javaFiles);

		for (File javaFile : javaFiles) {
			Scanner scanner = new Scanner(javaFile);
			int lineNumber = 1;
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				for (String term : terms) {
					if (line.contains(term)) {
						System.out.println("File: "
								+ javaFile.getAbsolutePath() + " Line: "
								+ lineNumber);
					}
				}
				lineNumber++;
			}
			scanner.close();
		}
	}

	private void searchFiles(File[] files, List<File> javaFiles) {
		for (File file : files) {
			if (file.isDirectory()) {
				searchFiles(file.listFiles(), javaFiles);
			} else if (file.getAbsolutePath().endsWith(".java")) {
				javaFiles.add(file);
			}
		}
	}
}
