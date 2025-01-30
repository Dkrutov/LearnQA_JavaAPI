package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

@Epic("Registration cases")
@Feature("Registration")
public class UserRegisterTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void testCreateUserWithExistingEmail() {
        String email = "vinkotov@example.com";
        Map<String,String> userData= new HashMap<>();
        userData.put("email",email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/" + this.getApiURL() + "/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth,400);
        Assertions.assertResponseTextEquals(responseCreateAuth,"Users with email '" + email + "' already exists");
    }

    @Test
    public void testCreateUserSuccessfully() {
        String email = DataGenerator.getRandomEmail();

        Map<String,String> userData= DataGenerator.getRegistrationData();

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/" + this.getApiURL() + "/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth,200);
        Assertions.assertJsonHasField(responseCreateAuth,"id");
    }

    @Test
    @Description("This test checks the impossibility of registration without a symbol @")
    @DisplayName("Test negative registration user")
    public void testCreateUserNotValidEmailAt() {
        String email = "lernqa2025010318081example.com";
        Map<String,String> userData= new HashMap<>();
        userData.put("email",email);
        userData.put("password","123");
        userData.put("username","learnqa");
        userData.put("firstName","learnqa");
        userData.put("lastName","learnqa");

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/" + this.getApiURL() + "/user/",userData);


        Assertions.assertResponseCodeEquals(responseCreateAuth,400);
        Assertions.assertResponseTextEquals(responseCreateAuth,"Invalid email format");
    }

    @Test
    @Description("This test checks if registration is impossible if the username contains 1 character")
    @DisplayName("Test negative registration user")
    public void testCreateUserNotValidFirstName1() {
        String email = DataGenerator.getRandomEmail();
        String username = "a";

        Map<String,String> userData= new HashMap<>();
        userData.put("email",email);
        userData.put("password","123");
        userData.put("username",username);
        userData.put("firstName","learnqa");
        userData.put("lastName","learnqa");

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/" + this.getApiURL() + "/user/",userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth,400);
        Assertions.assertResponseTextEquals(responseCreateAuth,"The value of 'username' field is too short");
    }

    @Test
    @Description("This test checks if registration is impossible if the username contains 251 character")
    @DisplayName("Test negative registration user")
    public void testCreateUserNotValidFirstName251() {
        String email = DataGenerator.getRandomEmail();
        String username = "qwertyuiopqwertyuiopqwertyuiopqwertyuiopqwertyuiopqwertyuiopqwertyuiopqwertyuiopqwertyuiopqwertyuiopqwertyuiopqwertyuiopqwertyuiopqwertyuiopqwertyuiopqwertyuiopqwertyuiopqwertyuiopqwertyuiopqwertyuiopqwertyuiopqwertyuiopqwertyuiopqwertyuiopqwertyuiop1";

        Map<String,String> userData= new HashMap<>();
        userData.put("email",email);
        userData.put("password","123");
        userData.put("username",username);
        userData.put("firstName","learnqa");
        userData.put("lastName","learnqa");

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/" + this.getApiURL() + "/user/",userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth,400);
        Assertions.assertResponseTextEquals(responseCreateAuth,"The value of 'username' field is too long");

    }

    @ParameterizedTest
    @ValueSource(strings ={"email","password","username","firstName","lastName"})
    @Description("This test checks if it is impossible to register without specifying one of the fields")
    @DisplayName("Test negative registration user")
    public void testCreateUserNotValid(String name) {
        String email = DataGenerator.getRandomEmail();


        Map<String,String> userData= new HashMap<>();
        userData.put("email",email);
        userData.put("password","123");
        userData.put("username","learnqa");
        userData.put("firstName","learnqa");
        userData.put("lastName","learnqa");
        userData.remove(name);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/" + this.getApiURL() + "/user/",userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth,400);
        Assertions.assertResponseTextEquals(responseCreateAuth,"The following required params are missed: " + name);

    }

}
