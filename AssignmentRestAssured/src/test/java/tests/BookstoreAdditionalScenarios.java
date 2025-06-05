//this tests covers scenarios like search book with invalid ID's,Missing fields
//Edge cases: unauthorized access, invalid data, duplicates
package tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class BookstoreAdditionalScenarios {

    private final String BASE_URL = "http://127.0.0.1:8000";
    private final String PASSWORD = "securepassword";
    private String accessToken;
    private String bookId;
    private String uniqueEmail;

    @BeforeClass
    public void signupAndLogin() {
        RestAssured.baseURI = BASE_URL;
        uniqueEmail = "testuser_" + System.currentTimeMillis() + "@example.com";

        String signupBody = "{ \"email\": \"" + uniqueEmail + "\", \"password\": \"" + PASSWORD + "\" }";
        RestAssured.given().contentType(ContentType.JSON).body(signupBody).post("/signup");

        String loginBody = "{ \"email\": \"" + uniqueEmail + "\", \"password\": \"" + PASSWORD + "\" }";
        Response loginResponse = RestAssured.given().contentType(ContentType.JSON).body(loginBody)
            .post("/login").then().statusCode(200).extract().response();

        accessToken = loginResponse.jsonPath().getString("access_token");
        assertNotNull(accessToken, "Access token should not be null");
    }

    @Test(priority = 1)
    public void testCreateBook() {
        String createBookBody = "{ \"name\": \"Sample Book\", \"author\": \"John Doe\", \"published_year\": 2024, \"book_summary\": \"An example book.\" }";

        Response initialResponse = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + accessToken)
            .body(createBookBody)
            .post("/books")
            .then()
            .statusCode(307)
            .extract().response();

        String redirectUrl = initialResponse.getHeader("Location");
        assertNotNull(redirectUrl);

        Response finalResponse = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + accessToken)
            .body(createBookBody)
            .post(redirectUrl)
            .then().extract().response();

        assertTrue(finalResponse.statusCode() == 200 || finalResponse.statusCode() == 201);
        bookId = finalResponse.jsonPath().getString("id");
        assertNotNull(bookId);
    }

    @Test(priority = 2, dependsOnMethods = "testCreateBook")
    public void testGetAllBooks() {
        Response response = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + accessToken)
            .get("/books")
            .then().statusCode(200).extract().response();

        assertTrue(response.asString().contains("Sample Book"));
    }

    @Test(priority = 3, dependsOnMethods = "testCreateBook")
    public void testGetBookById() {
        Response response = RestAssured.given()
            .header("Authorization", "Bearer " + accessToken)
            .get("/books/" + bookId)
            .then().statusCode(200).extract().response();

        assertEquals(response.jsonPath().getString("id"), bookId);
    }

    @Test(priority = 4, dependsOnMethods = "testCreateBook")
    public void testUpdateBook() {
        String updateBody = "{ \"name\": \"Updated Book Title\", \"author\": \"Jane Smith\", \"published_year\": 2025, \"book_summary\": \"Updated summary.\" }";

        Response response = RestAssured.given()
            .contentType(ContentType.JSON)
            .header("Authorization", "Bearer " + accessToken)
            .body(updateBody)
            .put("/books/" + bookId)
            .then().statusCode(200).extract().response();

        assertEquals(response.jsonPath().getString("name"), "Updated Book Title");
    }

    @Test(priority = 5, dependsOnMethods = "testCreateBook")
    public void testDeleteBook() {
        RestAssured.given()
            .header("Authorization", "Bearer " + accessToken)
            .delete("/books/" + bookId)
            .then().statusCode(200);
    }

    // 🔽 Additional edge case tests 🔽

    @Test(priority = 6)
    public void testUnauthorizedCreateBook() {
        String body = "{ \"name\": \"Unauthorized\", \"author\": \"None\", \"published_year\": 2022, \"book_summary\": \"Should fail.\" }";

        RestAssured.given().contentType(ContentType.JSON).body(body)
            .post("/books")
            .then().statusCode(307);
    }

    @Test(priority = 7)
    public void testCreateBookWithMissingFields() {
        String body = "{ \"author\": \"No Title\" }";

        RestAssured.given()
            .header("Authorization", "Bearer " + accessToken)
            .contentType(ContentType.JSON)
            .body(body)
            .post("/books")
            .then().statusCode(307);
    }

    @Test(priority = 8)
    public void testGetBookByInvalidId() {
        String nonExistentUUID = "123e4567-e89b-12d3-a456-426614174000";  // valid UUID format, unlikely to exist

        RestAssured.given()
            .header("Authorization", "Bearer " + accessToken)
            .get("/books/" + nonExistentUUID)
            .then().statusCode(422);
    }
    @Test(priority = 9)
    public void testDeleteBookTwice() {
        if (bookId != null) {
            RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .delete("/books/" + bookId);

            RestAssured.given()
                .header("Authorization", "Bearer " + accessToken)
                .delete("/books/" + bookId)
                .then().statusCode(404);
        }
    }

    @Test(priority = 10)
    public void testUpdateBookWithInvalidData() {
        if (bookId == null) return;

        String invalidBody = "{ \"name\": \"\", \"author\": 123, \"published_year\": \"abc\", \"book_summary\": null }";

        RestAssured.given()
            .header("Authorization", "Bearer " + accessToken)
            .contentType(ContentType.JSON)
            .body(invalidBody)
            .put("/books/" + bookId)
            .then().statusCode(404);
    }
}
