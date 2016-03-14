package br.ufpe.cin.clazz.preprocessor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClazzContextManager {

	private Map<String, Set<Integer>> mapClassLines;

	private static final ClazzContextManager instance = new ClazzContextManager();

	private ClazzContextManager() {
		this.mapClassLines = new HashMap<String, Set<Integer>>();
	}

	public static ClazzContextManager getInstance() {
		return instance;
	}

	public void addInfo(String clazz, Integer lineNumber) {
		if (getMapClassLines().containsKey(clazz)) {
			Set<Integer> setOldValues = getMapClassLines().get(clazz);
			setOldValues.add(lineNumber);
			getMapClassLines().put(clazz, setOldValues);
		} else {
			Set<Integer> setLineNumbers = new HashSet<Integer>();
			setLineNumbers.add(lineNumber);
			getMapClassLines().put(clazz, setLineNumbers);
		}
	}
	
	public void clearMapping() {
		this.getMapClassLines().clear();
	}

	public Map<String, Set<Integer>> getMapClassLines() {
		return mapClassLines;
	}
}
