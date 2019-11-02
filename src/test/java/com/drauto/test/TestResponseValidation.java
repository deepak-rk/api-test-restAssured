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
    public void responseValidation(String firstAPI, String secondAPI) {

        if (!(firstAPI != null && !firstAPI.isEmpty())) {
            Assert.fail("Test data for first API is null");
        }
        if (!(secondAPI != null && !secondAPI.isEmpty())) {
            Assert.fail("Test data for second API is null");
        }

        HttpClientRestAssured httpClientRestAssured = new HttpClientRestAssured();
        JsonMap expectedMap = httpClientRestAssured.getResponse(firstAPI);
        JsonMap actualMap = httpClientRestAssured.getResponse(secondAPI);
        List<String> errors = httpClientRestAssured.compareMaps(expectedMap, actualMap);
        for (String errorMessage : errors) {
            log.error(errorMessage);
            Reporter.log(errorMessage);
        }
        Assert.assertTrue(errors.size() == 0, "Validate Json");
    }

    @DataProvider(name = "apiUrls")
    public Object[][] apiDataPrivider() {
        Object[][] dataProvider;
        List<String> file1 = FileUtilities
                .returnFileAsListOfLines(System.getProperty("user.dir") + "/src/test/resources/File1.txt");
        List<String> file2 = FileUtilities
                .returnFileAsListOfLines(System.getProperty("user.dir") + "/src/test/resources/File2.txt");
        int size = file1.size() >= file2.size() ? file1.size() : file2.size();
        dataProvider = new Object[size][2];
        for (int i = 0; i < size; i++) {
            dataProvider[i][0] = file1.get(i).trim();
            dataProvider[i][1] = file2.get(i).trim();
        }
        return dataProvider;
    }

}
