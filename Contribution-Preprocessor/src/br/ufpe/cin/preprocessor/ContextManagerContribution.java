package br.ufpe.cin.preprocessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContextManagerContribution {

	private Map<String, List<Integer>> mapClassesLineNumbers;

	private final static ContextManagerContribution instance = new ContextManagerContribution();

	private ContextManagerContribution() {
		this.mapClassesLineNumbers = new HashMap<String, List<Integer>>();
	}

	public static ContextManagerContribution getContext() {
		return instance;
	}

	public Map<String, List<Integer>> getMapClassesLineNumbers() {
		return mapClassesLineNumbers;
	}

	public void addInfo(String className, List<Integer> linesAddedOrRemoved) {
		// List<Integer> lines = new ArrayList<>();
		// for (Integer integer : linesAddedOrRemoved) {
		// lines.add(integer);
		// }
		mapClassesLineNumbers.put(className, linesAddedOrRemoved);
	}

	public void addClassLinesInfo(String className, Integer lineAddedOrRemoved) {
		if (mapClassesLineNumbers.containsKey(className)) {
			List<Integer> listOldValues = mapClassesLineNumbers.get(className);
			listOldValues.add(lineAddedOrRemoved);
			mapClassesLineNumbers.put(className, listOldValues);
		} else {
			List<Integer> listLineNumbers = new ArrayList<Integer>();
			listLineNumbers.add(lineAddedOrRemoved);

			mapClassesLineNumbers.put(className, listLineNumbers);
		}
	}

}