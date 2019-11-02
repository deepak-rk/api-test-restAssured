package com.drauto.jsonvalidtor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;

/**
 * This class is a wrapper class over map, which converts a {@link JSONObject}
 * into a flattened map (in case of nested Objects)
 * 
 * @author Deepak-rk
 *
 */
public class JsonMap {
    private static final Logger log = LoggerFactory.getLogger(JsonMap.class);
    private static final String DEFAULT_KEY = "id";
    protected String primaryKey = DEFAULT_KEY;
    private Map<String, Object> map = new HashMap<String, Object>();

    public JsonMap(String json) {
        JsonNode jsonNode = createJsonNode(json);
        createMap("", jsonNode);
        map = new TreeMap<String, Object>(map);

        Iterator<Entry<String, Object>> sortedIt = map.entrySet().iterator();
        while (sortedIt.hasNext()) {
            Entry<String, Object> pair = sortedIt.next();
            log.debug(pair.getKey() + ":" + pair.getValue());
        }

    }

    public JsonMap(String json, String primaryKey) {
        this(json);
        this.primaryKey = primaryKey;
    }

    /**
     * This method will create a flattened Map Based
     * <li>Case 1: Inner Json Object - call the recursive function
     * <li>Case 2: Json Value node
     * <li>Case 3: Json Array - step1 sort json array, step2 add to map
     * <li>Case 4: Unknown Type
     * 
     * 
     */
    public void createMap(String path, JsonNode jsonNode) {

        // Case 1
        if (jsonNode.isObject()) {
            log.debug("Node is an Object");
            ObjectNode node = (ObjectNode) jsonNode;
            Iterator<Map.Entry<String, JsonNode>> iter = node.fields();
            while (iter.hasNext()) {
                Map.Entry<String, JsonNode> entry = iter.next();
                String appender = path != null && path.isEmpty() ? "." : "";
                String childPath = path + entry.getKey() + appender;
                createMap(childPath, entry.getValue());

            }

        }
        // Case 2
        else if (jsonNode.isValueNode()) {
            ValueNode valueNode = (ValueNode) jsonNode;
            map.put(path, valueNode);
        }

        // Case 3
        else if (jsonNode.isArray()) {
            log.debug("iterating over Json Array");
            JSONArray sortedArray = sortJsonArray(jsonNode);
            jsonNode = createJsonNode(sortedArray.toJSONString());
            ArrayNode arrayNode = (ArrayNode) jsonNode;
            for (int j = 0; j < arrayNode.size(); j++) {
                createMap(path + "[" + j + "]", arrayNode.get(j));
            }
            // Case 4
        } else {
            log.error("Uknown type for jsonNode - " + jsonNode.toString());
        }

    }

    /**
     * This method will return sorted {@link JSONArray} based on the PRIMARY_KEY
     * <b>OR</b> on the basis of first key
     */
    @SuppressWarnings("unchecked")
    private JSONArray sortJsonArray(JsonNode jsonNode) {
        JSONParser jsonParser = new JSONParser();
        JSONArray jsonArray = null;
        try {
            jsonArray = (JSONArray) jsonParser.parse(jsonNode.toString());
        } catch (ParseException e) {
            log.error("Exception occured while parsing", e);
            return null;
        }

        List<JSONObject> jsonList = new ArrayList<JSONObject>();
        for (int i = 0; i < jsonArray.size(); i++) {
            jsonList.add((JSONObject) jsonArray.get(i));
        }

        Collections.sort(jsonList, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject firstJson, JSONObject secondJson) {
                int compare = 0;
                try {
                    Object firstValue;
                    Object secondValue;

                    firstValue = firstJson.get(primaryKey);
                    if (firstValue == null) {
                        log.warn("Primary key '{}' not found , sorting on the basis of firstKey", primaryKey);
                        Set<String> setOfKeys = firstJson.keySet();
                        Iterator<String> iter = setOfKeys.iterator();
                        String defaultKey = iter.next();
                        firstValue = firstJson.get(defaultKey);
                        secondValue = secondJson.get(defaultKey);

                    } else {
                        secondValue = secondJson.get(primaryKey);
                    }
                    if (!(firstValue instanceof Comparable && secondValue instanceof Comparable)) {
                        log.warn("Data cannot be compared , first value is of Type '{}', second value is of Type '{}'",
                                firstValue.getClass().getName(), secondValue.getClass().getName());
                    }
                    compare = firstValue.toString().compareTo(secondValue.toString());
                } catch (Exception e) {
                    log.error("Exception occured ", e);
                }
                return compare;
            }
        });

        JSONArray sortedJsonArray = new JSONArray();
        for (int i = 0; i < jsonList.size(); i++) {
            JSONObject jsonObject = (JSONObject) jsonList.get(i);
            sortedJsonArray.add(jsonObject);
        }

        return sortedJsonArray;
    }

    /**
     * @param json Json text
     * @return {@link JsonNode} based on the jsonText
     */
    private static JsonNode createJsonNode(String json) {
        JsonNode jsonNode = null;
        try {
            jsonNode = new ObjectMapper().readTree(json);
        } catch (JsonMappingException e) {
            log.error("Mapping exception occured", e);

        } catch (JsonProcessingException e) {
            log.error("Processing exception occured", e);
        }
        if (jsonNode == null) {
            log.error("Cannot Process Json");
        }
        return jsonNode;
    }

    public int size() {
        return map.size();
    }

    public Set<String> keySet() {
        return map.keySet();
    }

    public Object get(String key) {
        return map.get(key);
    }

}
