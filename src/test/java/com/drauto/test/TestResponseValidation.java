package com.drauto.test;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.drauto.jsonvalidtor.JsonMap;
import com.drauto.restassured.HttpClientRestAssured;
import com.drauto.utilities.FileUtilities;

public class TestResponseValidation {

    private static final Logger log = LoggerFactory.getLogger(TestResponseValidation.class);

    @Test(dataProvider = "apiUrls")
    public void responseValidationTest(String firstAPI, String secondAPI) {
        testResponse(firstAPI, secondAPI);
    }

    @Test(dataProvider = "apiUrlsParallel")
    public void responseValidationTestParallel(String firstAPI, String secondAPI) {
        testResponse(firstAPI, secondAPI);
    }

    private void testResponse(String firstAPI, String secondAPI) {
        if (!(firstAPI != null && !firstAPI.isEmpty())) {
            Assert.fail("Test data for first API is null");
        }
        if (!(secondAPI != null && !secondAPI.isEmpty())) {
            Assert.fail("Test data for second API is null");
        }

        HttpClientRestAssured httpClientRestAssured = new HttpClientRestAssured();
        JsonMap expectedMap = httpClientRestAssured.getResponse(firstAPI);
        if (expectedMap == null) {
            Assert.fail("Expected Map cannot be null");

        }
        JsonMap actualMap = httpClientRestAssured.getResponse(secondAPI);
        if (actualMap == null) {
            Assert.fail("Actual Map cannot be null");

        }
        List<String> errors = httpClientRestAssured.compareMaps(expectedMap, actualMap);
        String response = "equals";
        for (String errorMessage : errors) {
            response = "not equals";
            log.error(errorMessage);
            Reporter.log(errorMessage);
        }
        String logMessage = String.format("%s %s %s", firstAPI, response, secondAPI);
        log.info(logMessage);
        Reporter.log(logMessage);
        Assert.assertTrue(errors.size() == 0, "Validate Json");
    }

    @DataProvider(name = "apiUrls")
    public Object[][] apiDataPrivider() {
        return dataProvider("File1", "File2");
    }

    @DataProvider(name = "apiUrlsParallel", parallel = true)
    public Object[][] apiDataPrividerParallel() {
        return dataProvider("File1", "File2");
    }

    private Object[][] dataProvider(String file1, String file2) {
        Object[][] dataProvider;
        List<String> list1 = FileUtilities
                .returnFileAsListOfLines(System.getProperty("user.dir") + "/src/test/resources/" + file1 + ".txt");
        List<String> list2 = FileUtilities
                .returnFileAsListOfLines(System.getProperty("user.dir") + "/src/test/resources/" + file2 + ".txt");
        int size = list1.size() >= list2.size() ? list1.size() : list2.size();
        dataProvider = new Object[size][2];
        for (int i = 0; i < size; i++) {
            dataProvider[i][0] = list1.get(i).trim();
            dataProvider[i][1] = list2.get(i).trim();
        }
        return dataProvider;
    }

}
