package tests;
import org.testng.Assert;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;

import io.restassured.RestAssured;
import io.restassured.RestAssured.*;

import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import io.restassured.response.Response;

public class TestExamples {
	

@Test
	public void test_1() {
		Response response = RestAssured.get("https://reqres.in/api/users?page=2");
		System.out.println(response.getStatusCode());
		System.out.println(response.getBody().asString());
		System.out.println(response.getStatusLine());
		System.out.println(response.getHeader("content-type"));
		int StatusCode = response.getStatusCode();
		Assert.assertEquals(StatusCode,200);
		
		
		
	}

@Test
	public void test2_() {
	
	baseURI= "https://reqres.in/api/";
			
	given().
	get("/users?page=2").
	then().statusCode(200);

	
}

@Test
public void test3_() {

baseURI= "https://reqres.in/api/";
		
given().
get("/users?page=2").

then().
	statusCode(200).
	body("data[1].id",equalTo(8))
	.log().all();


}
}
