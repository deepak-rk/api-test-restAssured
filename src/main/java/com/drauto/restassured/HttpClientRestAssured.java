package com.drauto.restassured;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drauto.jsonvalidtor.JsonMap;
import com.drauto.jsonvalidtor.JsonMapComparator;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class HttpClientRestAssured {

    private static final Logger log = LoggerFactory.getLogger(HttpClientRestAssured.class);

    public static void main(String[] args) {
//
//        JsonMap expectedMap = getResponse("https://reqres.in/api/users/1");
//        JsonMap actualMap = getResponse("https://reqres.in/api/users?page=3");
//        System.out.println(compareMaps(expectedMap, actualMap));

    }

    /**
     * @param uRI Path of the API
     * @return response as a flattened json Map
     */
    public JsonMap getResponse(String uRI) {
        RequestSpecification requestSpecification = RestAssured.given();

        String[] urISplit = uRI.split("\\?");
        requestSpecification = requestSpecification.urlEncodingEnabled(true).baseUri(urISplit[0]);
        if (urISplit.length > 1) {
            log.debug("Constructing queryParam");
            Map<String, String> queryParam = getQueryParams(urISplit[1]);
            requestSpecification = requestSpecification.queryParams(queryParam);
        }

        Response response = requestSpecification.get();
        int httpStatus = response.getStatusCode();
        // success
        if (httpStatus != 200) {
            log.error("Expected status = 200 , Actual status = " + httpStatus);
        }

        // response
        String responseString = response.getBody().asString();
        if (!(responseString != null && !responseString.isEmpty())) {
            log.error("No response found for the request");
        }
        return new JsonMap(responseString);

    }

    public Map<String, String> getQueryParams(String queryParam) {
        String[] queryParamArr = queryParam.split("&");
        Map<String, String> map = new HashMap<String, String>();

        for (int i = 0; i < queryParamArr.length; i++) {
            int index = queryParamArr[i].indexOf("=");
            String paramKey = queryParamArr[i].substring(0, index);
            String paramValue = queryParamArr[i].substring(index + 1);
            map.put(paramKey, paramValue);

        }
        return map;
    }

    /**
     * JsonComparator will compare the two maps and list the errors
     */
    public List<String> compareMaps(JsonMap firstJsonMap, JsonMap secondJsonMap) {
        JsonMapComparator jsonMapComparator = new JsonMapComparator();
        jsonMapComparator.compare(firstJsonMap, secondJsonMap);
        return jsonMapComparator.getListOfErrors();
    }

}
