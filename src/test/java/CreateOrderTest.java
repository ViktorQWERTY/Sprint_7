import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.After;
import org.junit.Test;
import pojo.Order;

@RunWith(Parameterized.class)
public class CreateOrderTest {
    static private String endPointOrder = "/api/v1/orders";
    static private String endPointOrderCancel = "/api/v1/orders/cancel";
    private String[] color;
    private int track;
    private String firstName = "qwe";
    private String lastName = "rty";
    private String address = "www leningrad, spb.ru";
    private String metroStation = "4";
    private String phone = "+7-800-800-80-80";
    private int rentTime = 5;
    private String deliveryDate = "2022-11-23";
    private String comment = "спасибо!";

    public CreateOrderTest(String[] color) {
        this.color = color;
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";
    }

    @After
    public void tearDown() {
        String cancelTrack = "{\"track\":" + track + "}";
        given()
                .body(cancelTrack)
                .when()
                .put(endPointOrderCancel);
    }

    @Parameterized.Parameters
    public static Object[][] getOrderData() {
        return new Object[][]{
                { new String[] {"BLACK"}},
                { new String[] {"GREY"}},
                { new String[] {"BLACK", "GREY"}},
                { new String[] {}}
        };
    }

    @Test
    @DisplayName("Можно указать один из цветов, оба цвета, без цвета, получить track")
    @Description("/api/v1/courier/login post: Parameterized color")
    public void createOrderTest() {
        Order order = new Order(firstName, lastName, address, metroStation, phone, rentTime, deliveryDate, comment, color);
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(order)
                .when()
                .post(endPointOrder);
        response.then()
                .assertThat()
                .statusCode(201)
                .and()
                .body("track", notNullValue());

        String responseAsString = response.asString();
        JsonPath jsonPath = new JsonPath(responseAsString);
        track = jsonPath.getInt("track");
    }
}