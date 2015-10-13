package br.ufpe.cin.util;

import java.io.File;
import java.io.FileFilter;

public class LibFilterUtil implements FileFilter {

	private final String jarExtension = "jar";

	@Override
	public boolean accept(File file) {
		if (file.getName().toLowerCase().endsWith(jarExtension)) {
			return true;
		}
		return false;
	}
}
