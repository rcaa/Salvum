package br.ufpe.cin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

//#if CONTRIBUTION
//@import java.io.IOException;
//@import java.nio.file.FileSystems;
//@import java.nio.file.Files;
//@import java.nio.file.Path;
//@import java.nio.file.StandardCopyOption;
//@
//@
//@import br.ufpe.cin.policy.PolicyContribution;
//@import java.util.Properties;
//@import java.io.PrintStream;
//@import java.io.FileNotFoundException;
//@import java.io.FileOutputStream;
//@
//@
//#endif
public class FileUtil {
	// #if CONTRIBUTION
	// @
	// @ public static void copyFiles(String srcFiles, String tgtFiles)
	// @ throws IOException {
	// @ String[] sourceFiles = srcFiles.split(":");
	// @ String[] targetFiles = tgtFiles.split(":");
	// @ int i = 0;
	// @
	// @ while (i < sourceFiles.length && i < sourceFiles.length) {
	// @ Path source = FileSystems.getDefault().getPath(sourceFiles[i]);
	// @ Path dest = FileSystems.getDefault().getPath(targetFiles[i]);
	// @ Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
	// @ i++;
	// @ }
	// @ }
	// @
	// @ public static void setOutput(Properties p, PolicyContribution policy)
	// @ throws FileNotFoundException {
	// @
	// @ String outputPath = p.getProperty("output");
	// @
	// @ if (policy != null) {
	// @ outputPath = outputPath + policy.getHash().substring(0, 8);
	// @ }
	// @
	// @ PrintStream out = new PrintStream(new FileOutputStream(outputPath
	// @ + "-output.txt"));
	// @ PrintStream outST = new PrintStream(new FileOutputStream(outputPath
	// @ + "-outputerror.txt"));
	// @ System.setOut(out);
	// @ System.setErr(outST);
	// @ }
	// #endif

	public static Properties getPropertiesFile(String propertiesPath) {
		Properties p = new Properties();
		FileInputStream in = null;
		try {
			in = new FileInputStream(propertiesPath);
			p.load(in);
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return p;
	}

	public static void copyFile(File source, File dest) {
		try {
			FileUtils.copyFile(source, dest);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void copyPropFiles(String source, List<String> destFiles) {
		File propFile = new File(source);
		for (String destProp : destFiles) {
			File destFile = new File(destProp);
			copyFile(propFile, destFile);
		}
	}

	public static void main(String[] args) {
		File zipDir = new File("C:\\Doutorado\\workspace\\opensource");
		File[] zipFiles = zipDir.listFiles();
		List<String> destFiles = new ArrayList<>();
		for (File file : zipFiles) {
			destFiles
					.add("C:\\Doutorado\\workspace\\Salvum\\Salvum\\configFiles\\"
							+ FilenameUtils.removeExtension(file.getName())
							+ ".properties");
		}
		FileUtil.copyPropFiles(
				"C:\\Doutorado\\workspace\\Salvum\\Salvum\\configFiles"
						+ "\\voldemortSDG.properties", destFiles);
	}
}
