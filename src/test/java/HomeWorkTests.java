import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class HomeWorkTests {
    @Test
    public void testHello() {
        System.out.println("Hello from Dmitrii");

    }

    @Test
    public void testGetText() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/get_text")
                .andReturn();
        response.prettyPrint();
    }

    @Test
    public void testParsJHSON() {
        JsonPath response = RestAssured
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();
        String messages = response.getString("messages[1].message");

        if (messages == null) {
            System.out.println("The key 'messages' is absent");
        } else {
            System.out.println(messages);
        }
    }

    @Test
    public void testRedirectPrint() {
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();

        String locationHeader = response.getHeader("Location");
        System.out.println(locationHeader);
    }

    @Test
    public void testLongRedirect() {
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();
        String locationHeader = response.getHeader("Location");
        int statusCode = response.getStatusCode();
        System.out.println("\nStatusCode: " + statusCode + "\nLocation: " + locationHeader);
        while (statusCode != 200) {
            response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get(locationHeader)
                    .andReturn();
            locationHeader = response.getHeader("Location");
            statusCode = response.getStatusCode();

            System.out.println("\nStatusCode: " + statusCode + "\nLocation: " + locationHeader);
        }
    }

    @Test
    public void testToken() {
        JsonPath responseForToken = RestAssured
                .given()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();
        String token = responseForToken.get("token");
        int seconds = responseForToken.get("seconds");
        System.out.println("token: " + token);
        System.out.println("seconds: " + seconds);

        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        String result = null;
        while (result == null) {
            JsonPath responseForStatus = RestAssured
                    .given()
                    .queryParams(params)
                    .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                    .jsonPath();

            String error = responseForStatus.get("error");
            String status = responseForStatus.get("status");
            if (error == null) {
                if (status.equals("Job is ready")) {
                    System.out.println(status);
                } else if (status.equals("Job is NOT ready")) {
                    System.out.println(status);
                    try {
                        Thread.sleep(seconds * 1000L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                result = responseForStatus.get("result");
//                token = "A";
//                params.put("token", token);
            } else {
                System.out.println(error);
                break;
            }
        }
        System.out.println("result: " + result);
    }
}