import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JunitHomeWork {

    @Test
    public void testLength() {
        String hello = "Hello, world!!!!";
        assertTrue( hello.length() > 15,"Unexpected length");
    }

    @Test
    public void testCookie() {
        Response responseHomeWorkCookie = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();
        assertEquals("hw_value",responseHomeWorkCookie.getCookie("HomeWork"), "Unexpected cookie");
    }

    @Test
    public void testHeader() {
        Response responseHomeWorkCookie = RestAssured
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();
        assertEquals("Some secret value",responseHomeWorkCookie.getHeader("x-secret-homework-header"), "Unexpected x-secret-homework-header:");
    }



}
