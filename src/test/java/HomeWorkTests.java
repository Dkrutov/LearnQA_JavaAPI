import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

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


}
