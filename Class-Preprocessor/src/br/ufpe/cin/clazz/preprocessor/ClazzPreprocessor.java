package br.ufpe.cin.clazz.preprocessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClazzPreprocessor {

	private String meth;
	
	public ClazzPreprocessor(String sourceDirectory, String meth) {
		this.meth = meth;
		File[] files = new File(sourceDirectory).listFiles();
		try {
			ClazzSrcManager.getSrcManager().fillClassDirectories(files);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void execute() throws PreprocessorException, IOException {
		ClazzSrcManager manager = ClazzSrcManager.getSrcManager();

		if (manager.getSrcFiles() == null || manager.getSrcFiles().isEmpty()) {
			throw new PreprocessorException("Java files not found, check input");
		}

		preprocess();
	}

	private void preprocess() throws IOException, PreprocessorException {
		ClazzSrcManager manager = ClazzSrcManager.getSrcManager();
		List<String> srcFiles = manager.getSrcFiles();
		BufferedReader br = null;
		String methRegex = "(.*)" + (this.meth.trim().replaceAll("\\.", 
				Matcher.quoteReplacement("\\."))) + "(.*)";
		
		for (String srcFile : srcFiles) {
				br = new BufferedReader(new FileReader(srcFile));
				Pattern pattern = Pattern.compile(methRegex.toString(),
						Pattern.CASE_INSENSITIVE);

				int lineNumber = 0; // for counting the line number
				iteratingOverSrcLines(br, pattern, lineNumber, srcFile);
		}
		if (br != null) {
			br.close();
		}
	}

	private void iteratingOverSrcLines(BufferedReader br, Pattern pattern,
			int lineNumber, String srcFile) throws IOException,
			PreprocessorException {
		String line;
		String clazzName = this.formatClassName(srcFile);
		ClazzContextManager context = ClazzContextManager.getInstance();
		// reading line-by-line from input file
		while ((line = br.readLine()) != null) {
			lineNumber++;
			/**
			 * Creates a matcher that will match the given input against this
			 * pattern.
			 */
			if (clazzName.contains("Credentials") && lineNumber == 75) {
				System.out.println();
			}
			
			Matcher matcher = pattern.matcher(line);

			/**
			 * Matches the defined pattern with the current line
			 */
			if (matcher.matches()) {
				context.addInfo(clazzName, lineNumber);
			}
		}
	}

	private String formatClassName(String srcFile) {
		String className = null;
		// significa que comeca o diff de um novo arquivo
		if (srcFile != null && srcFile.contains(".java")
				&& srcFile.contains("/src")) {
			className = srcFile.substring(srcFile.indexOf("/src"),
					srcFile.indexOf(".java"));
			className = className.replace("/", ".");
			className = className.replace(".src.", "");
		}
		return className;
	}

	public static void main(String[] args) {
		ClazzPreprocessor cp = new ClazzPreprocessor(
				"/Users/rodrigoandrade/Documents/workspaces"
						+ "/Doutorado/opensource/gitblit/", "logger.error(");
		try {
			cp.execute();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (PreprocessorException e) {
			e.printStackTrace();
		}
		ClazzContextManager context = ClazzContextManager.getInstance();
		System.out.println(context.getMapClassLines());
	}

}
