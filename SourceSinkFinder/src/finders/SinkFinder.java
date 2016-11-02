package finders;

import java.io.FileNotFoundException;

public class SinkFinder extends Finder {

	public final String[] terms = { "setCookie", "log.severe", "logger.severe",
			"print", "log.warning", "logger.warning", "log.info", "logger.info" };

	@Override
	public void findTerms(String path, String[] terms)
			throws FileNotFoundException {
		super.findTerms(path, terms);
	}

	public static void main(String[] args) {
		try {
			SinkFinder sf = new SinkFinder();
			sf.findTerms("C:\\Doutorado\\workspace\\opensource\\"
					+ "SimpleContributionExample\\src\\", sf.terms);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
