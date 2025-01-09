package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("Delete user cases")
@Feature("Delete user")
public class UserDeleteTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Description("This negative delete user with ID = 2")
    @DisplayName("Test negative delete user")
    public void testNotDeleteUser2() {
        //LOGIN
        Map<String,String> authData = new HashMap<>();
        authData.put("email","vinkotov@example.com");
        authData.put("password","1234");

        Response responseGetAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api_dev/user/login",
                authData);
//        System.out.println("LOGIN: " + responseGetAuth.asString());

        //DELETE
        Response responseDeleteUser = apiCoreRequests.makeDeleteRequest(
                "https://playground.learnqa.ru/api_dev/user/2",
                this.getHeader(responseGetAuth,"x-csrf-token"),
                this.getCookie(responseGetAuth,"auth_sid"));
//        System.out.println("DELETE: " + responseDeleteUser.asString());
        Assertions.asserJsonByName(responseDeleteUser,"error","Please, do not delete test users with ID 1, 2, 3, 4 or 5.");

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api_dev/user/2",
                this.getHeader(responseGetAuth,"x-csrf-token"),
                this.getCookie(responseGetAuth,"auth_sid"));
//        System.out.println("GET: " + responseUserData.asString());

       Assertions.asserJsonByName(responseUserData,"id","2");
    }

    @Test
    @Description("This positive delete user")
    @DisplayName("Test positive delete user")
    public void testDeleteUser() {
        //GENERATE USER
        Map<String,String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateUser = apiCoreRequests.makePostRequestJSON(
                "https://playground.learnqa.ru/api_dev/user/",
                userData);

        String userId = responseCreateUser.getString("id");
//        System.out.println("GENERATE: " + responseCreateUser.prettyPrint());

        //LOGIN
        Map<String,String> authData = new HashMap<>();
        authData.put("email",userData.get("email"));
        authData.put("password",userData.get("password"));

        Response responseGetAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api_dev/user/login",
                authData);
//        System.out.println("LOGIN: " + responseGetAuth.asString());

        //DELETE
        Response responseEditUser = apiCoreRequests.makeDeleteRequest(
                "https://playground.learnqa.ru/api_dev/user/" + userId,
                this.getHeader(responseGetAuth,"x-csrf-token"),
                this.getCookie(responseGetAuth,"auth_sid"));
//        System.out.println("DELETE: " + responseEditUser.asString());

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api_dev/user/" + userId,
                this.getHeader(responseGetAuth,"x-csrf-token"),
                this.getCookie(responseGetAuth,"auth_sid"));
//       System.out.println("GET: " + responseUserData.asString());

        Assertions.assertResponseTextEquals(responseUserData,"User not found");
    }

    @Test
    @Description("This negative delete user")
    @DisplayName("Test negative delete user")
    public void testDeleteUserAuthAnotherUser() throws InterruptedException {
        //GENERATE USER AUTH
        Map<String,String> userDataAuth = DataGenerator.getRegistrationData();

        JsonPath responseCreateUserAuth = apiCoreRequests.makePostRequestJSON(
                "https://playground.learnqa.ru/api_dev/user/",
                userDataAuth);

//        System.out.println("GENERATE: " + responseCreateUserAuth.prettyPrint());
        Thread.sleep(1000);
        //GENERATE USER DELETE
        Map<String,String> userDataDelete = DataGenerator.getRegistrationData();

        JsonPath responseCreateUserEdit = apiCoreRequests.makePostRequestJSON(
                "https://playground.learnqa.ru/api_dev/user/",
                userDataDelete);

        String userDeleteId = responseCreateUserEdit.getString("id");
//        System.out.println("GENERATE DELETE: " + responseCreateUserEdit.prettyPrint());

        //LOGIN
        Map<String,String> authData = new HashMap<>();
        authData.put("email",userDataAuth.get("email"));
        authData.put("password",userDataAuth.get("password"));

        Response responseGetAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api_dev/user/login",
                authData);
//        System.out.println("LOGIN: " + responseGetAuth.asString());

        //DELETE
        Response responseEditUser = apiCoreRequests.makeDeleteRequest(
                "https://playground.learnqa.ru/api_dev/user/" + userDeleteId,
                this.getHeader(responseGetAuth,"x-csrf-token"),
                this.getCookie(responseGetAuth,"auth_sid"));
//        System.out.println("DELETE: " + responseEditUser.asString());
        Assertions.asserJsonByName(responseEditUser,"error","This user can only delete their own account.");

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api_dev/user/" + userDeleteId,
                this.getHeader(responseGetAuth,"x-csrf-token"),
                this.getCookie(responseGetAuth,"auth_sid"));
//        System.out.println("GET: " + responseUserData.asString());

        Assertions.asserJsonByName(responseUserData,"username",userDataDelete.get("username"));
    }

}
