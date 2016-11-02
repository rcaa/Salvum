package finders;

import java.io.FileNotFoundException;

public class SourceFinder extends Finder {

	public final String[] terms = { "password", "pwd", "email", "secret",
			"locality", "imei", "address", "attachment", "cookie", "token",
			"oauthProvider", "passwordHash" };

	@Override
	public void findTerms(String path, String[] terms)
			throws FileNotFoundException {
		super.findTerms(path, terms);
	}

	public static void main(String[] args) {
		try {
			SourceFinder sf = new SourceFinder();
			sf.findTerms("C:\\Doutorado\\workspace\\opensource\\"
					+ "SimpleContributionExample\\src\\" , sf.terms);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
