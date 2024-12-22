import  io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import  io.restassured.response.Response;
import org.junit.jupiter.api.Test;


import java.util.HashMap;
import java.util.Map;

public class HelloWorldTest {

    @Test
    public void testRestAssuredGet() {
        Map<String, String> params = new HashMap<>();
        params.put("name","John");

        Response response = RestAssured
                .given()
                .queryParams(params)
                .get("https://playground.learnqa.ru/api/hello")
                .andReturn();
        String name = response.prettyPrint();

        if (name == null) {
            System.out.println("The key 'answer2' is absent");
        } else {
            System.out.println(name);
        }

    }


    @Test
    public void testRestAssuredGetJsonPath() {
        Map<String, String> params = new HashMap<>();
        params.put("name","John");

        JsonPath response = RestAssured
                .given()
                .queryParams(params)
                .get("https://playground.learnqa.ru/api/hello")
                .jsonPath();
        String name = response.get("answer");

        if (name == null) {
            System.out.println("The key 'answer2' is absent");
        } else {
            System.out.println(name);
        }

    }

    @Test
    public void testRestAssuredPost() {
        Map<String,String> body = new HashMap<>();
        body.put("param1","value1");
        body.put("param2","value2");
        Response response = RestAssured
                .given()
//                .body("{\"param1\":\"value1\",\"param2\":\"value2\"}")
                .body(body)
                .post("https://playground.learnqa.ru/api/check_type")
                .andReturn();

        response.print();

    }

    @Test
    public void testRestAssuredStatus200() {

        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/check_type")
                .andReturn();

        int statusCode = response.getStatusCode();
        System.out.println(statusCode);
    }

    @Test
    public void testRestAssuredStatus500() {

        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/get_500")
                .andReturn();

        int statusCode = response.getStatusCode();
        System.out.println(statusCode);
    }

    @Test
    public void testRestAssuredStatus404() {

        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/something")
                .andReturn();

        int statusCode = response.getStatusCode();
        System.out.println(statusCode);
    }

    @Test
    public void testRestAssuredStatus300() {

        Response response = RestAssured
                .given()
                .redirects()
                .follow(true)
                .when()
                .get("https://playground.learnqa.ru/api/get_303")
                .andReturn();

        int statusCode = response.getStatusCode();
        System.out.println(statusCode);
    }

    @Test
    public void testRestAssuredHeaders() {
        Map<String,String> headers = new HashMap<>();
        headers.put("myHeader1","myValue1");
        headers.put("myHeader2","myValue2");

        Response response = RestAssured
                .given()
//                .headers(headers)
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/get_303")
                .andReturn();

//        response.prettyPrint();
//        Headers responseHeaders = response.getHeaders();
        String locationHeader = response.getHeader("Location");
        System.out.println(locationHeader);
    }

    @Test
    public void testRestAssuredCookies() {
        Map<String,String> data = new HashMap<>();
        data.put("login","secret_login");
        data.put("password","secret_pass");

        Response response = RestAssured
                .given()
                .body(data)
                .when()
                .post("https://playground.learnqa.ru/api/get_auth_cookie")
                .andReturn();

        System.out.println("\nPretty text:");
        response.prettyPrint();

        System.out.println("\nHeaders:");
        Headers responseHeaders = response.getHeaders();
        System.out.println(responseHeaders);

        System.out.println("\nCookies:");
        Map<String,String> responseCookies = response.getCookies();
//        String responseCookies = response.getCookie("auth_cookie");
        System.out.println(responseCookies);
    }

    @Test
    public void testRestAssuredCookies2() {
        Map<String,String> data = new HashMap<>();
        data.put("login","secret_login");
        data.put("password","secret_pass");

        Response response = RestAssured
                .given()
                .body(data)
                .when()
                .post("https://playground.learnqa.ru/api/get_auth_cookie")
                .andReturn();

//        System.out.println("\nPretty text:");
//        response.prettyPrint();
//
//        System.out.println("\nHeaders:");
//        Headers responseHeaders = response.getHeaders();
//        System.out.println(responseHeaders);
//
//        System.out.println("\nCookies:");
//        Map<String,String> responseCookies = response.getCookies();
        String responseCookies = response.getCookie("auth_cookie");
        System.out.println(responseCookies);
    }

    @Test
    public void testRestAssuredCookies3() {
        Map<String,String> data = new HashMap<>();
        data.put("login","secret_login2");
        data.put("password","secret_pass2");

        Response response = RestAssured
                .given()
                .body(data)
                .when()
                .post("https://playground.learnqa.ru/api/get_auth_cookie")
                .andReturn();

        System.out.println("\nPretty text:");
        response.prettyPrint();

        System.out.println("\nHeaders:");
        Headers responseHeaders = response.getHeaders();
        System.out.println(responseHeaders);

        System.out.println("\nCookies:");
        Map<String,String> responseCookies = response.getCookies();
//        String responseCookies = response.getCookie("auth_cookie");
        System.out.println(responseCookies);
    }

    @Test
    public void testRestAssuredCheckCookies4() {
        Map<String,String> data = new HashMap<>();
        data.put("login","secret_login2");
        data.put("password","secret_pass");

        Response responseForGet = RestAssured
                .given()
                .body(data)
                .when()
                .post("https://playground.learnqa.ru/api/get_auth_cookie")
                .andReturn();

        String responseCookie = responseForGet.getCookie("auth_cookie");

        Map<String,String> cookies = new HashMap<>();
        if (responseCookie != null) {
            cookies.put("auth_cookie", responseCookie);
        }

        Response responseForCheck = RestAssured
                .given()
                .body(data)
                .cookies(cookies)
                .when()
                .post("https://playground.learnqa.ru/api/check_auth_cookie")
                .andReturn();

        responseForCheck.print();
    }


}

