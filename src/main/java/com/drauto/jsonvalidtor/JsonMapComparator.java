package com.drauto.jsonvalidtor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonMapComparator implements Comparator<JsonMap> {
    private static final Logger log = LoggerFactory.getLogger(JsonMapComparator.class);

    private List<String> listOfErrors = new ArrayList<String>();

    /**
     * Compare Json Maps based on
     * <li>size</li>
     * <li>Keys in (sorting is done internally)</li>
     * <li>Type of value</li>
     * <li>Content of value</li>
     *
     */
    @Override
    public int compare(JsonMap firstMap, JsonMap secondMap) {
        int compare = 0;
        if (firstMap.size() != secondMap.size()) {
            logAndAddToList(String.format("Size of first Map '%s', Size of second Map '%s'", firstMap.size(),
                    secondMap.size()));
            compare = firstMap.size() > secondMap.size() ? 1 : -1;
        }

        Set<String> unionSet = new HashSet<String>();

        Set<String> firstSet = firstMap.keySet();
        Set<String> secondSet = secondMap.keySet();
        unionSet.addAll(firstSet);
        unionSet.addAll(secondSet);

        compareKeySets(unionSet, firstSet, "First");
        unionSet.addAll(firstSet);
        compareKeySets(unionSet, secondSet, "Second");

        Set<String> intersect = new HashSet<String>(firstSet);
        intersect.retainAll(secondSet);

        for (String key : intersect) {
            compareValues(firstMap, secondMap, key);
        }

        if (listOfErrors.size() > 1) {
            return 1;
        }
        return compare;
    }

    /**
     * Compares type and content
     * 
     * @param firstMap  expected Map
     * @param secondMap actual Map
     * @param key       key to be validated
     */
    private void compareValues(JsonMap firstMap, JsonMap secondMap, String key) {
        Object firstValue = firstMap.get(key);
        Object secondValue = secondMap.get(key);

        String firstType = firstValue.getClass().getSimpleName();
        String secondType = secondValue.getClass().getSimpleName();

        if (!firstType.equals(secondType)) {
            logAndAddToList(
                    String.format("Key: '%s', Type mismatch for value, firstMap type = '%s', secondMap type = '%s'",
                            key, firstType, secondType));
        } else if (!firstValue.equals(secondValue)) {
            logAndAddToList(
                    String.format("Key: '%s', Data mismatch for value, firstMap value = %s, secondMap value = %s", key,
                            firstValue, secondValue));
        }

    }

    /**
     * Compare keys in the two KeySets
     */
    public void compareKeySets(Set<String> firstSet, Set<String> secondSet, String name) {
        firstSet.removeAll(secondSet);
        if (firstSet.size() != 0) {
            logAndAddToList(
                    "Missing keys in the " + name + " map " + (firstSet.size() == 1 ? "is " : "are ") + firstSet);
        }

    }

    /**
     * log and record failures
     */
    public void logAndAddToList(String message) {
        log.debug(message);
        listOfErrors.add(message);
    }

    /**
     * @return list of errors for reporting purpose
     */
    public List<String> getListOfErrors() {
        return listOfErrors;
    }

}
