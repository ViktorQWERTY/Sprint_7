import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
        import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import pojo.Courier;
        import org.junit.After;
        import org.junit.Before;
        import org.junit.Test;

import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class CourierCreationTest {

    static private String loginTest = RandomStringUtils.randomAlphabetic(7);
    static private String passwordTest = "qweqwe123";
    static private String firstNameTest = "qwe";
    static private String endPointCreate = "/api/v1/courier";
    static private String endPointLogin = "/api/v1/courier/login";
    static private String endPointDelete = "/api/v1/courier/";

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";
    }

    @After
    public void tearDown() {
        Courier courierDelete  = new Courier(loginTest, passwordTest);
        String response = given()
                .header("Content-type", "application/json")
                .and()
                .body(courierDelete)
                .when()
                .post(endPointLogin)
                .asString();

        JsonPath jsonPath = new JsonPath(response);
        String userId = jsonPath.getString("id");
        delete(endPointDelete + userId);
    }

    @Test
    @DisplayName("Создание нового курьера с правильными данными")
    @Description("/api/v1/courier post: login, password, firstName")
    public void createNewCourierAndCheckResponse(){
        Courier courierCreate  = new Courier(loginTest, passwordTest, firstNameTest);
        given()
                .header("Content-type", "application/json")
                .and()
                .body(courierCreate)
                .when()
                .post(endPointCreate)
                .then().assertThat().statusCode(201)
                .and()
                .body("ok", equalTo(true));
    }
    @Test
    @DisplayName("Создание нового курьера без обязательного поля firstName")
    @Description("/api/v1/courier post: login, password")
    public  void createCourierWithoutFirstName() {
        Courier courierCreate  = new Courier(loginTest, passwordTest);
        given()
                .body(courierCreate)
                .when()
                .post(endPointCreate)
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
    @Test
    @DisplayName("Создание двух одинаковых курьеров")
    @Description("/api/v1/courier post: login, password, firstName")
    public void createCourierWithSameLoginAndCheckResponse(){
        Courier courierCreate  = new Courier(loginTest, passwordTest, firstNameTest);
        given()
                .header("Content-type", "application/json")
                .and()
                .body(courierCreate)
                .when()
                .post(endPointCreate)
                .then().assertThat().statusCode(201)
                .and()
                .body("ok", equalTo(true));
        given()
                .header("Content-type", "application/json")
                .and()
                .body(courierCreate)
                .when()
                .post(endPointCreate)
                .then().assertThat().statusCode(409)
                .and()
                .body("message", equalTo("Этот логин уже используется"));
    }
}