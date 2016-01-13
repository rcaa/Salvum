package br.ufpe.cin.util;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class ResultAnalyzer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		ResultAnalyzer.diffAnalyzer();
		System.out.println("\nIllegal flow found in these files: ");
		ResultAnalyzer.checkResultProperties("Illegal flow from");
		System.out.println("\nCompiled without error in these files: ");
		ResultAnalyzer.checkResultProperties("Time needed:");
		System.out.println("\nEntry point does not exist in these files: ");
		ResultAnalyzer.checkResultProperties("Main method does not exist");
		System.out.println("\nDid not compile in these files: ");
		ResultAnalyzer
				.checkResultProperties("Compile failed; see the compiler");
	}

	public static void checkResultProperties(String property) {
		try {
			File[] files = new File("/Users/rodrigoandrade/Desktop/openrefine/")
					.listFiles();
			int i = 0;
			for (File file : files) {
				Scanner scan = new Scanner(file);
				while (scan.hasNextLine()) {
					String nextLine = scan.nextLine();
					if (nextLine.contains(property)) {
						System.out.println(file.getCanonicalPath());
						i++;
						break;
					}
				}
				scan.close();
			}
			System.out.println("Number of files: " + i);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void diffAnalyzer() {
		File[] files = new File("/Users/rodrigoandrade/Desktop/diffFiles/")
				.listFiles();
		int i = 0;
		int j = 0;
		for (File file : files) {
			if (file.length() > 0) {
				i++;
			} else {
				j++;
			} 
		}
		System.out.println("Number of diff files containing java changes: " + i);
		System.out.println("Number of diff files not containing java changes: " + j);
	}
}
