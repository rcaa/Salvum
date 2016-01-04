package br.ufpe.cin.util;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class ResultAnalyzer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		System.out.println("Illegal flow found: ");
		ResultAnalyzer.checkResultProperties("Illegal flow from");
		System.out.println("\nCompiled without error: ");
		ResultAnalyzer.checkResultProperties("Time needed:");
		System.out.println("\nMain method does not exist: ");
		ResultAnalyzer.checkResultProperties("Main method does not exist");
		System.out.println("\nDid not compile: ");
		ResultAnalyzer.checkResultProperties("Compile failed; see the compiler");
	}

	public static void checkResultProperties(String property) {
		try {
			File[] files = new File("/Users/rodrigoandrade/Desktop/output/")
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
	
}
