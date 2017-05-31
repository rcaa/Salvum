package br.ufpe.cin.clazz.preprocessor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClazzSrcManager {

	private List<String> srcFiles; // input file paths

	private final static ClazzSrcManager instance = new ClazzSrcManager();

	private ClazzSrcManager() {
		this.srcFiles = new ArrayList<>();
	}

	public static ClazzSrcManager getSrcManager() {
		return instance;
	}

	public List<String> getSrcFiles() {
		return srcFiles;
	}

	/**
	 * Searches for files to have preprocessor tags mapped
	 * 
	 * @param files
	 * @throws IOException
	 */
	public void fillClassDirectories(File[] files) throws IOException {
		for (File file : files) {
			if (file != null && file.isDirectory()) {
				fillClassDirectories(file.listFiles());
			} else {
				// caso deseje-se considerar outros tipos de arquivos, eh so
				// adicionar a terminacao
				if (file.getName().endsWith(".java")) {
					srcFiles.add(file.getCanonicalPath());
				}
			}
		}

	}

}
