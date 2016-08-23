package br.ufpe.cin.analyses;

import java.io.IOException;
import java.util.List;

import edu.kit.joana.api.sdg.SDGProgram;
import edu.kit.joana.ifc.sdg.graph.SDGNodeTuple;

public class SDGLoader {

	public static void main(String[] args) {
		try {
			SDGProgram sdgProgram = SDGProgram
					.loadSDG("/Users/rodrigoandrade/Documents/workspaces/Doutorado"
							+ "/joana/Salvum/output/SDGFile.pdg");

			List<SDGNodeTuple> allCallSites = sdgProgram.getSDG()
					.getAllCallSites();
			for (SDGNodeTuple sdgNodeTuple : allCallSites) {
				System.out.println("primeiro: "
						+ sdgNodeTuple.getFirstNode().getBytecodeName() + " " + sdgNodeTuple.getFirstNode().getEr()
						+ " segundo: "
						+ sdgNodeTuple.getSecondNode().getBytecodeName() + " " + + sdgNodeTuple.getSecondNode().getEr());
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
