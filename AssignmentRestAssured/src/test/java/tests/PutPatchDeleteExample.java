package tests;
import org.json.simple.JSONObject;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;

import io.restassured.RestAssured;
import io.restassured.RestAssured.*;
import io.restassured.http.ContentType;

import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

public class PutPatchDeleteExample {
	
//	@Test
	public void testPut() {
		JSONObject request = new JSONObject();	
		request.put("name", "Raghav");
		request.put("job","teacher");
		System.out.println(request.toJSONString());
		baseURI ="https://reqres.in/api";		
		given().
		header("Content-Type","application/json").
		contentType(ContentType.JSON).accept(ContentType.JSON).
		body(request.toJSONString()).
		when().
		put("/users/2").
		then().
		statusCode(200).
		log().all();
		}

//	@Test
	public void testPatch() {
		JSONObject request = new JSONObject();
		request.put("name", "Raghav");
		request.put("job","teacher");
		System.out.println(request.toJSONString());
		baseURI ="https://reqres.in/api";	
		given().
		header("Content-Type","application/json").
		contentType(ContentType.JSON).accept(ContentType.JSON).
		body(request.toJSONString()).
		when().
		patch("/api/users/2").
		then().
		statusCode(200).
		log().all();
		}
	
	
//	@Test
	public void testDelete() {
		JSONObject request = new JSONObject();
		baseURI ="https://reqres.in/api";		
		when().
		patch("/api/users/2").
		then().
		statusCode(401).
		log().all();
		}
}
