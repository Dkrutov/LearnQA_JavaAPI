import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
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

    @ParameterizedTest
    @CsvSource(value ={
            "Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30|Mobile|No|Android",
            "Mozilla/5.0 (iPad; CPU OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/91.0.4472.77 Mobile/15E148 Safari/604.1|Mobile|Chrome|iOS",
            "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)|Googlebot|Unknown|Unknown",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.100.0|Web|Chrome|No",
            "Mozilla/5.0 (iPad; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1|Mobile|No|iPhone"
    },delimiter = '|')
    public void testUserAgent(String userAgent,String platform,String browser,String device) {
        Map<String,String> queryParams = new HashMap<>();

        if (userAgent.length() > 0) {
            queryParams.put("user-agent",userAgent);
        }

        JsonPath responseUserAgentCheck = RestAssured
                .given()
                .headers(queryParams)
                .get("https://playground.learnqa.ru/ajax/api/user_agent_check")
                .jsonPath();
        responseUserAgentCheck.prettyPrint();
        String deviceAct  = responseUserAgentCheck.get("device");
        String browserAct  = responseUserAgentCheck.get("browser");
        String platformAct  = responseUserAgentCheck.get("platform");
        String deviceExt  = device;
        String browserExt  = browser;
        String platformExt = platform;
        assertEquals(deviceExt,deviceAct,"device: " + deviceAct);
        assertEquals(browserExt,browserAct,"browser: " + browserAct);
        assertEquals(platformExt,platformAct,"platform: " + platformAct);
    }



}
