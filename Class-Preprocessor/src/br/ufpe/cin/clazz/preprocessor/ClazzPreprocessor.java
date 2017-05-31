package br.ufpe.cin.clazz.preprocessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClazzPreprocessor {

	private static final String CLASSES_SOURCE_PATH = "/src/java";
	private static final String JAVA_CLASSES_EXT = ".java";

	private Set<String> meths;

	public ClazzPreprocessor(String sourceDirectory, Set<String> meths) {
		this.meths = meths;
		File[] files = new File(sourceDirectory).listFiles();
		try {
			ClazzSrcManager.getSrcManager().fillClassDirectories(files);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void execute(String targetPathDirectory)
			throws PreprocessorException, IOException {
		ClazzSrcManager manager = ClazzSrcManager.getSrcManager();

		if (manager.getSrcFiles() == null || manager.getSrcFiles().isEmpty()) {
			throw new PreprocessorException("Java files not found, check input");
		}

		preprocess(targetPathDirectory);
	}

	private void preprocess(String targetPathDirectory) throws IOException,
			PreprocessorException {
		ClazzSrcManager manager = ClazzSrcManager.getSrcManager();
		List<String> srcFiles = manager.getSrcFiles();
		BufferedReader br = null;
		for (String meth : meths) {
			String methRegex = "(.*)"
					+ (meth.trim().replaceAll("\\.",
							Matcher.quoteReplacement("\\."))) + "(.*)";

			for (String srcFile : srcFiles) {
				FileReader fileReader = null;
				try {
					fileReader = new FileReader(srcFile);
				} catch (FileNotFoundException e) {
					continue;
				}
				br = new BufferedReader(fileReader);
				Pattern pattern = Pattern.compile(methRegex.toString(),
						Pattern.CASE_INSENSITIVE);

				int lineNumber = 0; // for counting the line number
				iteratingOverSrcLines(br, pattern, lineNumber, srcFile,
						targetPathDirectory);
			}
		}
		if (br != null) {
			br.close();
		}
	}

	private void iteratingOverSrcLines(BufferedReader br, Pattern pattern,
			int lineNumber, String srcFile, String targetPathDirectory)
			throws IOException, PreprocessorException {
		String line;
		String clazzName = this.formatClassName(srcFile, targetPathDirectory);
		ClazzContextManager context = ClazzContextManager.getInstance();
		// reading line-by-line from input file
		while ((line = br.readLine()) != null) {
			if (clazzName == null) {
				continue;
			}
			lineNumber++;
			/**
			 * Creates a matcher that will match the given input against this
			 * pattern.
			 */
			Matcher matcher = pattern.matcher(line);

			/**
			 * Matches the defined pattern with the current line
			 */
			if (matcher.matches()) {
				context.addInfo(clazzName, lineNumber);
			}
		}
	}

	public String formatClassName(String srcFile, String targetPathDirectory) {

		String classSourcePath = targetPathDirectory.substring(
				targetPathDirectory.indexOf("/src"),
				targetPathDirectory.length() - 1);
		String className = null;
		// significa que comeca o diff de um novo arquivo
		if (srcFile != null && srcFile.contains(JAVA_CLASSES_EXT)
				&& srcFile.contains(CLASSES_SOURCE_PATH)) {
			// dependendo de onde o codigo fonte esteja, pode ser necessario
			// alterar o path
			className = srcFile.substring(srcFile.indexOf(classSourcePath),
					srcFile.indexOf(JAVA_CLASSES_EXT));
			className = className.replace("/", ".");
			// dependendo de onde o codigo fonte esteja, pode ser necessario
			// alterar o path
			String classSourcePathDot = classSourcePath.replace('/', '.') + ".";
			className = className.replace(classSourcePathDot, "");
		}
		return className;
	}

	public static void main(String[] args) {
		String targetPathDirectory = "/home/local/CIN/rcaa2/contributionExperiments/casestudies/teammates/src/main/java/";
		String classSourcePath = targetPathDirectory.substring(
				targetPathDirectory.indexOf("/src"),
				targetPathDirectory.length() - 1);
		System.out.println(classSourcePath);
		/*
		 * try { cp.execute(); } catch (IOException e) { e.printStackTrace(); }
		 * catch (PreprocessorException e) { e.printStackTrace(); }
		 * ClazzContextManager context = ClazzContextManager.getInstance();
		 * System.out.println(context.getMapClassLines());
		 */
	}
}
