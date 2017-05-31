package br.ufpe.cin.analyses;

import java.io.IOException;

import edu.kit.joana.api.sdg.SDGProgram;

public class SDGLoader {

	public static void main(String[] args) {
		try {
			if (args[0] == null || args[0].isEmpty()) {
				throw new IOException("Invalid sdg path");
			}

			SDGProgram sdgProgram = SDGProgram.loadSDG(args[0]);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
