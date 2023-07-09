package com.amused.assignment.rest;

import amused.assignment.utils.ExcelReader;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import static io.restassured.RestAssured.given;


public class TestLaptopCRUDOperations {

    String dataFileName = "DataSheet.xlsx";
    RequestSpecification rs;

    @DataProvider(name = "allLaptopData")
    public Object[][] allObjectsDataProvider() throws Exception {
        return ExcelReader.getAllCellValuesInARow(dataFileName, "Sheet1", 0);
    }

    @DataProvider(name = "addLaptopData")
    public Object[][] addObjectDataProvider() throws Exception {
        return ExcelReader.getAllCellValuesInARow(dataFileName, "Sheet2", 0);
    }

    @DataProvider(name = "updateLaptopData")
    public Object[][] updateObjectDataProvider() throws Exception {
        return ExcelReader.getAllCellValuesInARow(dataFileName, "Sheet3", 1);
    }

    @DataProvider(name = "deleteLaptopData")
    public Object[][] deleteObjectDataProvider() throws Exception {
        return ExcelReader.getAllCellValuesInARow(dataFileName, "Sheet4", 0);
    }


    @BeforeClass
    @Parameters("baseUri")
    public void beforeTest(String baseUri) {
        rs = new RequestSpecBuilder()
                .setBaseUri(baseUri)
                .addHeader("Content-Type", "application/json")
                .build();
    }

    @Test(priority = 0, dataProvider = "allLaptopData", description = "Verify viewing all laptops")
    public void getAllObjectsTest(Object[] phoneNames) {

        Response response =
                given()
                        .filter(new AllureRestAssured())
                        .spec(rs)
                        .when()
                        .get("objects")
                        .then()
                        .statusCode(200)
                        .extract().response();

        JSONArray jsonArray = new JSONArray(response.body().asString());

        SoftAssert softAssert = new SoftAssert();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            String name = jsonObject.get("name").toString();
            softAssert.assertEquals(phoneNames[i].toString(), name);
        }
        softAssert.assertAll();

    }

    @Test(priority = 1, dataProvider = "addLaptopData", description = "Verify adding a laptop")
    public void addObjectsTest(Object[] values) {

        Response response = addLaptop(values);

        JSONObject jsonObject = new JSONObject(response.body().asString());
        String name = jsonObject.get("name").toString();
        Assert.assertEquals(name, values[1]);

    }

    @Test(priority = 2, dataProvider = "updateLaptopData", description = "Verify updating a laptop")
    public void updateObjectsTest(Object[] values) {

        String body = "{\n" +
                "   \"name\": \"" + values[0] + "\",\n" +
                "   \"data\": {\n" +
                "      \"year\": " + values[1] + ",\n" +
                "      \"price\": " + values[2] + ",\n" +
                "      \"CPU model\":\"" + values[3] + "\",\n" +
                "      \"Hard disk size\": \"" + values[4] + "\" \n" +
                "   }\n" +
                "}";

        Response response =
                given()
                        .filter(new AllureRestAssured())
                        .spec(rs)
                        .when()
                        .body(body)
                        .post("objects")
                        .then()
                        .statusCode(200)
                        .extract().response();

        String newLaptopId = response.jsonPath().getString("id");

        String editedBody = "{\n" +
                "   \"name\": \"" + values[5] + "\",\n" +
                "   \"data\": {\n" +
                "      \"year\": " + values[6] + ",\n" +
                "      \"price\": " + values[7] + ",\n" +
                "      \"CPU model\": \"" + values[8] + "\",\n" +
                "      \"Hard disk size\": \"" + values[9] + "\",\n" +
                "      \"color\": \"" + values[10] + "\"\n" +
                "   }\n" +
                "}";

        Response editedResponse =
                given()
                        .filter(new AllureRestAssured())
                        .spec(rs)
                        .when()
                        .body(editedBody)
                        .pathParam("objectId", newLaptopId)
                        .put("objects/{objectId}")
                        .then()
                        .statusCode(200)
                        .extract().response();

        JSONObject jsonObject = new JSONObject(editedResponse.body().asString());

        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(jsonObject.has("updatedAt"));
        softAssert.assertEquals(jsonObject.get("name").toString(), values[5]);

        JSONObject dataObject = (JSONObject) jsonObject.get("data");
        softAssert.assertEquals(dataObject.get("year").toString(), values[6]);
        softAssert.assertEquals(dataObject.get("price").toString(), values[7]);
        softAssert.assertEquals(dataObject.get("CPU model").toString(), values[8]);
        softAssert.assertEquals(dataObject.get("Hard disk size").toString(), values[9]);
        softAssert.assertEquals(dataObject.get("color").toString(), values[10]);

        softAssert.assertAll();


    }

    @Test(priority = 3, dataProvider = "deleteLaptopData", description = "Verify laptop deletion from the list")
    public void deleteObjectTest(Object[] values) {
        Response response1 = addLaptop(values);
        String newLaptopId = response1.jsonPath().getString("id");

        Response response2 =
                given()
                        .filter(new AllureRestAssured())
                        .spec(rs)
                        .when()
                        .pathParam("objectId", newLaptopId)
                        .delete("objects/{objectId}")
                        .then()
                        .statusCode(200)
                        .extract().response();

        String message = response2.jsonPath().getString("message");
        Assert.assertEquals(message, "Object with id = " + newLaptopId + " has been deleted.");
    }


    // Common method to call object addition to object list
    private Response addLaptop(Object[] values) {
        return given()
                .filter(new AllureRestAssured())
                .spec(rs)
                .when()
                .body(values[0])
//                .log().all()
                .post("objects")
                .then()
//                .log().all()
                .statusCode(200)
                .extract().response();
    }
}
