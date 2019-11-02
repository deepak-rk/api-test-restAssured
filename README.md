# API Response Validator

A testing framework to compare responses from two APIs. Validation is done for JSON responses of any structure. The responses are converted into flattened Map which are then compared against each other.The Map is nothing but a key value representation of JSONPath and Jsonvalue which are in sorted manner

###Sample 

data.[0]avatar:"https://s3.amazonaws.com/uifaces/faces/twitter/russoedu/128.jpg"<br />
data.[0]email:"byron.fields@reqres.in"<br />
data.[0]first_name:"Byron"<br />
data.[0]id:10<br />
data.[0]last_name:"Fields"<br />
data.[1]avatar:"https://s3.amazonaws.com/uifaces/faces/twitter/mrmoiree/128.jpg"<br />
data.[1]email:"george.edwards@reqres.in"<br />
data.[1]first_name:"George"<br />
data.[1]id:11<br />
data.[1]last_name:"Edwards"<br />
data.[2]avatar:"https://s3.amazonaws.com/uifaces/faces/twitter/hebertialmeida/128.jpg"<br />
data.[2]email:"rachel.howell@reqres.in"<br />
data.[2]first_name:"Rachel"<br />
data.[2]id:12<br />
data.[2]last_name:"Howell"<br />
per_page.:6<br />
total.:12<br />
total_pages.:2<br />


## Getting Started

There are two maven projects. First one is utilities which includes JSON, files, excel utils etc.
Second project is the actual test project which includes the JSONMap, JsonComparator, HTTP calls(Using RESTAssured) and TestNG.

### Prerequisites

1. Eclipse
2. JDK 1.8
3. Maven Plugin for eclipse
4. TestNG Plugin for eclipse

### Libraries used

1. Simple JSON object
2. RestAssured for API utilities
3. TestNG for test suite management

### Installing

1. Extract the folder
2. Import drauto-utilities-commons project
3. Execute "mvn clean install test" command for this project
4. Repeat steps 2 and 3 for "api-response-validator" project


## Running the tests

1. File1.txt and File2.txt are the test data for this test suite. You can modify these files with your respective test Data
2. validatorTestSuite.xml at the root directory is the TestNG suite. you can execute the test by running it using testNG plugin
3. validatorTestSuiteParallel.xml is the same test suite which has parallel execution capability


## Author

* **Deepak Radhakrishnan** - (https://github.com/deepak-rk)
