package br.cin.ufpe;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Counter {

	public static Set<String> loadHashes(String filePath)
			throws FileNotFoundException {

		Set<String> retorno = new HashSet<>();
		File hashFile = new File(filePath);
		Scanner scanner = new Scanner(hashFile);
		while (scanner.hasNextLine()) {
			String actualLine = scanner.nextLine();
			retorno.add(actualLine);
		}
		scanner.close();
		return retorno;
	}

	public static Set<Integer> countLines(String logPath, Set<String> hashes)
			throws FileNotFoundException {
		Set<Integer> numLines = new HashSet<>();
		File log = new File(logPath);
		Scanner scanner = new Scanner(log);
		while (scanner.hasNextLine()) {
			String actualLine = scanner.nextLine();
			if (!actualLine.isEmpty() && hashes.contains(actualLine)) {
				System.out.println(actualLine);
				while (scanner.hasNextLine()) {
					String content = scanner.nextLine();
					if (content.contains(".java")) {
						//System.out.println(content);
						String[] temp = content.split(" ");
						for (String string : temp) {
							if (Counter.isNumeric(string)) {
								Integer numLinesFile = Integer.valueOf(string);
								numLines.add(numLinesFile);
							}
						}
					} else if (content.contains("files changed")
							|| content.contains("file changed")) {
						break;
					}
				}
			}
		}
		scanner.close();
		return numLines;
	}

	public static Integer sumNumberofLines(Set<Integer> countLines) {
		Integer sum = 0;
		for (Integer i : countLines) {
			sum = sum + i;
		}
		return sum;
	}

	private static boolean isNumeric(String str) {
		try {
			Double.parseDouble(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// git command
		// git log --all --stat --format="%H" > gitblit.txt

		try {
			Set<String> hashes = Counter.loadHashes("openrefine-list.txt");
			Set<Integer> countLines = Counter.countLines("openrefine.txt", hashes);
			System.out.println(Counter.sumNumberofLines(countLines));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
}