package br.ufpe.cin.util;

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
//#if CONTRIBUTION
//@
//@	public static void copyFiles(String srcFiles, String tgtFiles)
//@			throws IOException {
//@		String[] sourceFiles = srcFiles.split(":");
//@		String[] targetFiles = tgtFiles.split(":");
//@		int i = 0;
//@
//@		while (i < sourceFiles.length && i < sourceFiles.length) {
//@			Path source = FileSystems.getDefault().getPath(sourceFiles[i]);
//@			Path dest = FileSystems.getDefault().getPath(targetFiles[i]);
//@			Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
//@			i++;
//@		}
//@	}
//@
//@ public static void setOutput(Properties p, PolicyContribution policy)
//@ throws FileNotFoundException {
//@
//@ String outputPath = p.getProperty("output");
//@
//@ if (policy != null) {
//@ outputPath = outputPath + policy.getHash().substring(0, 8);
//@ }
//@
//@ PrintStream out = new PrintStream(new FileOutputStream(outputPath
//@ + "-output.txt"));
//@ PrintStream outST = new PrintStream(new FileOutputStream(outputPath
//@ + "-outputerror.txt"));
//@ System.setOut(out);
//@ System.setErr(outST);
//@ }
//#endif
}
