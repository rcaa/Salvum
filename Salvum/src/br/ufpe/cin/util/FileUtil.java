package br.ufpe.cin.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

import br.ufpe.cin.policy.Policy;

public class FileUtil {

	public static void copyFiles(String srcFiles, String tgtFiles)
			throws IOException {
		String[] sourceFiles = srcFiles.split(":");
		String[] targetFiles = tgtFiles.split(":");
		int i = 0;

		while (i < sourceFiles.length && i < sourceFiles.length) {
			Path source = FileSystems.getDefault().getPath(sourceFiles[i]);
			Path dest = FileSystems.getDefault().getPath(targetFiles[i]);
			Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
			i++;
		}
	}

	public static void setOutput(Properties p, Policy policy)
			throws FileNotFoundException {

		String outputPath = p.getProperty("output");

		// #if FEATURE
		// @ +policy.getFeature();
		// #elif CONTRIBUTION
		if (policy != null) {
			outputPath = outputPath + policy.getHash().substring(0, 8);
		}
		// #endif
		PrintStream out = new PrintStream(new FileOutputStream(outputPath
				+ "-output.txt"));
		PrintStream outST = new PrintStream(new FileOutputStream(outputPath
				+ "-outputerror.txt"));
		System.setOut(out);
		System.setErr(outST);
	}
}
