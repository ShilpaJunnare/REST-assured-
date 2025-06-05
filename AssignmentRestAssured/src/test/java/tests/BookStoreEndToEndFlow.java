
//This tests covers CRUD operations on the BookStore
package tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class BookStoreEndToEndFlow {

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

        Response signupResponse = RestAssured
            .given()
                .contentType(ContentType.JSON)
                .body(signupBody)
            .when()
                .post("/signup");

        System.out.println("Signup Response Status: " + signupResponse.statusCode());
        System.out.println("Signup Response Body: " + signupResponse.asString());

        assertTrue(signupResponse.statusCode() == 200 || signupResponse.statusCode() == 201,
            "Expected status 200 or 201 but was " + signupResponse.statusCode());

        String loginBody = "{ \"email\": \"" + uniqueEmail + "\", \"password\": \"" + PASSWORD + "\" }";

        Response loginResponse = RestAssured
            .given()
                .contentType(ContentType.JSON)
                .body(loginBody)
            .when()
                .post("/login")
            .then()
                .statusCode(200)
                .extract()
                .response();

        accessToken = loginResponse.jsonPath().getString("access_token");
        String tokenType = loginResponse.jsonPath().getString("token_type");

        System.out.println("Login Response: " + loginResponse.asString());
        System.out.println("Access Token: " + accessToken);

        assertNotNull(accessToken, "Access token should not be null");
        assertEquals(tokenType.toLowerCase(), "bearer", "Token type should be bearer");
    }

    @Test(priority = 1)
    public void testCreateBook() {
        assertNotNull(accessToken, "Access token must be available for authenticated requests");

        String createBookBody = "{\n" +
                "  \"name\": \"Sample Book\",\n" +
                "  \"author\": \"John Doe\",\n" +
                "  \"published_year\": 2024,\n" +
                "  \"book_summary\": \"An example book.\"\n" +
                "}";

        // Step 1: Send POST request that results in 307 redirect
        Response initialResponse = RestAssured
            .given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(createBookBody)
            .when()
                .post("/books")
            .then()
                .statusCode(307)  // Expect redirect here
                .extract()
                .response();

        System.out.println("Initial Create Book Response (Redirect): " + initialResponse.asString());

        // Step 2: Get the redirect URL from the Location header
        String redirectUrl = initialResponse.getHeader("Location");
        System.out.println("Redirect URL: " + redirectUrl);
        assertNotNull(redirectUrl, "Redirect URL must not be null");

        Response finalResponse = RestAssured
        	    .given()
        	        .contentType(ContentType.JSON)
        	        .header("Authorization", "Bearer " + accessToken)
        	        .body(createBookBody)
        	    .when()
        	        .post(redirectUrl)
        	    .then()
        	        .extract()
        	        .response();

        	int statusCode = finalResponse.statusCode();
        	System.out.println("Final Create Book Response status: " + statusCode);
        	System.out.println("Final Create Book Response body: " + finalResponse.asString());

        	assertTrue(statusCode == 200 || statusCode == 201, "Expected status code 200 or 201 but was " + statusCode);

        	bookId = finalResponse.jsonPath().getString("id");
        	assertNotNull(bookId, "Created book ID should not be null");
    }
 

    @Test(priority = 2, dependsOnMethods = "testCreateBook")
    public void testGetAllBooks() {
        assertNotNull(accessToken);

        Response response = RestAssured
            .given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
            .when()
                .get("/books")
            .then()
                .statusCode(200)
                .extract()
                .response();

        System.out.println("Get All Books Response: " + response.asString());

        assertTrue(!response.asString().isEmpty());
    }

    @Test(priority = 3, dependsOnMethods = "testCreateBook")
    public void testFindBookById() {
        assertNotNull(accessToken);
        assertNotNull(bookId);

        Response response = RestAssured
            .given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
            .when()
                .get("/books/" + bookId)
            .then()
                .statusCode(200)
                .extract()
                .response();

        System.out.println("Find Book by ID Response: " + response.asString());

        assertEquals(response.jsonPath().getString("id"), bookId);
    }

    @Test(priority = 4, dependsOnMethods = "testCreateBook")
    public void testUpdateBook() {
        assertNotNull(accessToken, "Access token must be available");
        assertNotNull(bookId, "bookId must be available to update the book");

        System.out.println("Updating book with ID: " + bookId);

        String updateBody = "{\n" +
                "  \"name\": \"Updated Book Title\",\n" +
                "  \"author\": \"Updated Author Name\",\n" +
                "  \"published_year\": 2025,\n" +
                "  \"book_summary\": \"Updated summary.\"\n" +
                "}";

        Response response = RestAssured
            .given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessToken)
                .body(updateBody)
            .when()
                .put("/books/" + bookId)
            .then()
                .statusCode(200)
                .extract()
                .response();

        System.out.println("Update Book Response: " + response.asString());

        // Verify updated fields
        assertEquals(response.jsonPath().getString("name"), "Updated Book Title");
    }


    @Test(priority = 5, dependsOnMethods = "testCreateBook")
    public void testDeleteBook() {
        assertNotNull(accessToken);
        assertNotNull(bookId);

        RestAssured
            .given()
                .header("Authorization", "Bearer " + accessToken)
            .when()
                .delete("/books/" + bookId)
            .then()
                .statusCode(200);  // or 204 depending on your API

        System.out.println("Book with ID " + bookId + " deleted successfully.");
    }
}
