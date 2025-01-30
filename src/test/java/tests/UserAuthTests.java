package tests;

import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lib.BaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import lib.Assertions;
import lib.ApiCoreRequests;


import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;


@Epic("Authorisation cases")
@Feature("Authorization")
public class UserAuthTests extends BaseTestCase {

    String cookie;
    String header;
    int userIdOnAuth;
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @BeforeEach
    public void loginUser(){
        Map<String,String> authData = new HashMap<>();
        authData.put("email","vinkotov@example.com");
        authData.put("password","1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/" + this.getApiURL() + "/user/login",authData);

        this.cookie = this.getCookie(responseGetAuth,"auth_sid");
        this.header = this.getHeader(responseGetAuth,"x-csrf-token");
        this.userIdOnAuth = this.getIntFromJson(responseGetAuth,"user_id");
    }

    @Test
    @Description("This test successfully authorize user by email and password")
    @DisplayName("Test positive auth user")
    public  void  testAuthUser() {

        Response responseCheckAuth = apiCoreRequests
                .makeGetRequest(
                        "https://playground.learnqa.ru/" + this.getApiURL() + "/user/auth",
                        this.header,
                        this.cookie);

//        int userIdOnCheck = responseCheckAuth.getInt("user_id");
//        assertTrue(userIdOnCheck > 0, "Unexpected user id " + userIdOnCheck);
//
//        assertEquals(
//                userIdOnAuth,
//                userIdOnCheck,
//                "user id from auth request is not equal to user_id from check request"
//        );
        System.out.println(responseCheckAuth.asString());
        Assertions.asserJsonByName(responseCheckAuth,"user_id",this.userIdOnAuth);
    }
    @Description("This test checks authorization status w/o sending auth cookie or token")
    @DisplayName("Test negative auth user")
    @ParameterizedTest
    @ValueSource(strings = {"cookie","headers"})
    public void testNegativeAuthUser(String condition){
        Allure.parameter("condition", condition);
//        Map<String,String> authData = new HashMap<>();
//        authData.put("email","vinkotov@example.com");
//        authData.put("password","1234");
//
//        Response responseGetAuth = RestAssured
//                .given()
//                .body(authData)
//                .post("https://playground.learnqa.ru/api/user/login")
//                .andReturn();
//
//        Map<String,String> cookies = responseGetAuth.getCookies();
//        Headers headers = responseGetAuth.getHeaders();



        RequestSpecification spec = RestAssured.given();
        spec.baseUri("https://playground.learnqa.ru/" + this.getApiURL() + "/user/auth");

        if (condition.equals("cookie")) {
            Response responseForCheck = apiCoreRequests.makeGetRequestWithCookie(
                    "https://playground.learnqa.ru/" + this.getApiURL() + "/user/auth",
                    this.cookie
            );
            Assertions.asserJsonByName(responseForCheck,"user_id", 0);
        } else if (condition.equals("headers")) {
            Response responseForCheck = apiCoreRequests.makeGetRequestWithToken(
                    "https://playground.learnqa.ru/" + this.getApiURL() + "/user/auth",
                    this.header
            );
            Assertions.asserJsonByName(responseForCheck,"user_id", 0);
        } else {
            throw new IllegalArgumentException("Condition value is not known" + condition);
        }


//        if (condition.equals("cookie")) {
//            spec.cookie("auth_sid", this.cookie);
//        } else if (condition.equals("headers")){
//            spec.header("x-csrf-token", this.header);
//        } else {
//            throw new IllegalArgumentException("Condition value is known; " + condition);
//        }
//
//        Response responseForCheck = spec.get().andReturn();
//        assertEquals(0,responseForCheck.getInt("user_id"), "user_id shouldbe 0 for unauth request");
//        Assertions.asserJsonByName(responseForCheck,"user_id",0);

    }
}
