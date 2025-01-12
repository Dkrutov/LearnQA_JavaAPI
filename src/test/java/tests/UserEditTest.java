package tests;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

@Epic("Edit user cases")
@Feature("Edit user")
public class UserEditTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();


    @Test
    @Description("This test successfully edit user")
    @DisplayName("Test positive edit user")
    @Tag(value = "Edit")
    @Owner(value = "Крутов Дмитрий")
    @Severity(value = SeverityLevel.BLOCKER)
    @Link("https://allurereport.org/docs/gettingstarted-readability/#description-links-and-other-metadata")
    public void testEditJustCreatedTest() {
        //GENERATE USER
        Map<String,String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = apiCoreRequests.makePostRequestJSON(
                "https://playground.learnqa.ru/api_dev/user/",
                userData);
        String userId = responseCreateAuth.getString("id");

        //LOGIN
        Map<String,String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api_dev/user/login")
                .andReturn();

        //EDIT
        String newName = "Changed Name";
        Map<String,String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth,"x-csrf-token"))
                .cookie("auth_sid",this.getCookie(responseGetAuth,"auth_sid"))
                .body(editData)
                .put("https://playground.learnqa.ru/api_dev/user/" + userId)
                .andReturn();

        //GET
        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth,"x-csrf-token"))
                .cookie("auth_sid",this.getCookie(responseGetAuth,"auth_sid"))
                .get("https://playground.learnqa.ru/api_dev/user/" + userId)
                .andReturn();

        Assertions.asserJsonByName(responseUserData,"firstName",newName);
    }

    @Test
    @Description("Test negative edit user, if non auth")
    @DisplayName("This test negative edit user")
    public void testEditNonAuth() {
        //GENERATE USER
        Map<String,String> userData = DataGenerator.getRegistrationData();
        JsonPath responseCreateAuth = apiCoreRequests.makePostRequestJSON(
                "https://playground.learnqa.ru/api_dev/user/",
                userData);

        String userId = responseCreateAuth.getString("id");
//        System.out.println("GENERATE: " + responseCreateAuth.prettyPrint());

        //EDIT
        String newName = "Changed Name";
        Map<String,String> editData = new HashMap<>();
        editData.put("username", newName);

        Response responseEditUser = apiCoreRequests.makePutRequestNonTokenCookie(
                "https://playground.learnqa.ru/api_dev/user/" + userId,
                editData);
//        System.out.println("EDIT: " + responseEditUser.asString());
        Assertions.asserJsonByName(responseEditUser,"error","Auth token not supplied");

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequestNonTokenCookie(
                "https://playground.learnqa.ru/api_dev/user/" + userId);
//        System.out.println("GET: " + responseUserData.asString());

        Assertions.asserJsonByName(responseUserData,"username",userData.get("username"));
    }

    @Test
    @Description("Test negative edit user, if auth another user")
    @DisplayName("This test negative edit user")
    public void testEditAuthAnotherUser() throws InterruptedException {
        //GENERATE USER AUTH
        Map<String,String> userDataAuth = DataGenerator.getRegistrationData();

        JsonPath responseCreateUserAuth = apiCoreRequests.makePostRequestJSON(
                "https://playground.learnqa.ru/api_dev/user/",
                userDataAuth);

        System.out.println("GENERATE: " + responseCreateUserAuth.prettyPrint());
        Thread.sleep(1000);
        //GENERATE USER EDIT
        Map<String,String> userDataEdit = DataGenerator.getRegistrationData();

        JsonPath responseCreateUserEdit = apiCoreRequests.makePostRequestJSON(
                "https://playground.learnqa.ru/api_dev/user/",
                userDataEdit);

        String userEditId = responseCreateUserEdit.getString("id");
        System.out.println("GENERATE: " + responseCreateUserEdit.prettyPrint());

        //LOGIN
        Map<String,String> authData = new HashMap<>();
        authData.put("email",userDataAuth.get("email"));
        authData.put("password",userDataAuth.get("password"));

        Response responseGetAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api_dev/user/login",
                authData);
//        System.out.println("LOGIN: " + responseGetAuth.asString());

        //EDIT
        String newName = "Changed Name";
        Map<String,String> editData = new HashMap<>();
        editData.put("username", newName);

        Response responseEditUser = apiCoreRequests.makePutRequest(
                "https://playground.learnqa.ru/api_dev/user/" + userEditId,
                this.getHeader(responseGetAuth,"x-csrf-token"),
                this.getCookie(responseGetAuth,"auth_sid"),
                editData);
//        System.out.println("EDIT: " + responseEditUser.asString());
        Assertions.asserJsonByName(responseEditUser,"error","This user can only edit their own data.");

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api_dev/user/" + userEditId,
                this.getHeader(responseGetAuth,"x-csrf-token"),
                this.getCookie(responseGetAuth,"auth_sid"));
//        System.out.println("GET: " + responseUserData.asString());

        Assertions.asserJsonByName(responseUserData,"username",userDataEdit.get("username"));
    }

    @Test
    @Description("Test negative edit email user non @")
    @DisplayName("This test negative edit user")
    public void testEditEmailNonAt() {
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

        //EDIT
        String newEmail = "lernqa2026010318081example.com";
        Map<String,String> editData = new HashMap<>();
        editData.put("email", newEmail);

        Response responseEditUser = apiCoreRequests.makePutRequest(
                "https://playground.learnqa.ru/api_dev/user/" + userId,
                this.getHeader(responseGetAuth,"x-csrf-token"),
                this.getCookie(responseGetAuth,"auth_sid"),
                editData);
//        System.out.println("EDIT: " + responseEditUser.asString());
        Assertions.asserJsonByName(responseEditUser,"error","Invalid email format");

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api_dev/user/" + userId,
                this.getHeader(responseGetAuth,"x-csrf-token"),
                this.getCookie(responseGetAuth,"auth_sid"));
//        System.out.println("GET: " + responseUserData.asString());

        Assertions.asserJsonByName(responseUserData,"email",userData.get("email"));
    }

    @Test
    @Description("Test negative edit firstName user")
    @DisplayName("This test negative edit user")
    public void testEditFirstName() {
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

        //EDIT
        String newFirstName = "A";
        Map<String,String> editData = new HashMap<>();
        editData.put("firstName", newFirstName);

        Response responseEditUser = apiCoreRequests.makePutRequest(
                "https://playground.learnqa.ru/api_dev/user/" + userId,
                this.getHeader(responseGetAuth,"x-csrf-token"),
                this.getCookie(responseGetAuth,"auth_sid"),
                editData);
//        System.out.println("EDIT: " + responseEditUser.asString());
        Assertions.asserJsonByName(responseEditUser,"error","The value for field `firstName` is too short");

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api_dev/user/" + userId,
                this.getHeader(responseGetAuth,"x-csrf-token"),
                this.getCookie(responseGetAuth,"auth_sid"));
//        System.out.println("GET: " + responseUserData.asString());

        Assertions.asserJsonByName(responseUserData,"firstName",userData.get("firstName"));
    }



}
