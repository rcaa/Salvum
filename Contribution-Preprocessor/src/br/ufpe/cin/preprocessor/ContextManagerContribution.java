package br.ufpe.cin.preprocessor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ContextManagerContribution {

	private Map<String, Set<Integer>> mapClassesLineNumbers;

	private final static ContextManagerContribution instance = new ContextManagerContribution();

	private ContextManagerContribution() {
		this.mapClassesLineNumbers = new HashMap<String, Set<Integer>>();
	}

	public static ContextManagerContribution getContext() {
		return instance;
	}

	public Map<String, Set<Integer>> getMapClassesLineNumbers() {
		return mapClassesLineNumbers;
	}

	public void addInfo(String className, Set<Integer> linesAddedOrRemoved) {
		// List<Integer> lines = new ArrayList<>();
		// for (Integer integer : linesAddedOrRemoved) {
		// lines.add(integer);
		// }
		mapClassesLineNumbers.put(className, linesAddedOrRemoved);
	}

	public void addClassLinesInfo(String className, Integer lineAddedOrRemoved) {
		if (mapClassesLineNumbers.containsKey(className)) {
			Set<Integer> listOldValues = mapClassesLineNumbers.get(className);
			listOldValues.add(lineAddedOrRemoved);
			mapClassesLineNumbers.put(className, listOldValues);
		} else {
			Set<Integer> listLineNumbers = new HashSet<Integer>();
			listLineNumbers.add(lineAddedOrRemoved);

			mapClassesLineNumbers.put(className, listLineNumbers);
		}
	}
	
	public void clear() {
		this.mapClassesLineNumbers.clear();
	}

}