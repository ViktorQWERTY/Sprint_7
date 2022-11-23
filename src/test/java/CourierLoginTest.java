import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import jdk.jfr.Description;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pojo.Courier;

import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CourierLoginTest {

    static private String loginTest = RandomStringUtils.randomAlphabetic(7);
    static private String passwordTest = "qweqwe123";
    static private String firstNameTest = "qwe";
    static private String endPointCreate = "/api/v1/courier";
    static private String endPointLogin = "/api/v1/courier/login";
    static private String endPointDelete = "/api/v1/courier/";

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";
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
    @DisplayName("Курьер может авторизоваться")
    @Description("/api/v1/courier/login post: login, password")
    public void loginCourierAndCheckResponse(){
        Courier courierLogin  = new Courier(loginTest, passwordTest);
        given()
                .header("Content-type", "application/json")
                .and()
                .body(courierLogin)
                .when()
                .post(endPointLogin)
                .then().assertThat().statusCode(200)
                .and()
                .body("id", notNullValue());;
    }

    @Test
    @DisplayName("Для авторизации нужно передать все поля. Нет пароля.")
    @Description("/api/v1/courier/login post: password null")
    public  void loginCourierWithoutPassword() {
        Courier courierCreate  = new Courier(loginTest, "");
        given()
                .body(courierCreate)
                .when()
                .post(endPointLogin)
                .then()
                .assertThat()
                .statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для входа")); //Service unavailable
    }
    @Test
    @DisplayName("Ошибка, если неправильный пароль.")
    @Description("/api/v1/courier/login post: wrong password")
    public  void loginCourierWithWrongPassword() {
        Courier courierCreate  = new Courier(loginTest, passwordTest + "ошибка");
        given()
                .body(courierCreate)
                .when()
                .post(endPointLogin)
                .then()
                .assertThat()
                .statusCode(404)
                .and()
                .body("message", equalTo("Учетная запись не найдена"));
    }
    @Test
    @DisplayName("Ошибка, если неправильный логин.")
    @Description("/api/v1/courier/login post: wrong login")
    public  void loginCourierWithWrongLogin() {
        Courier courierCreate  = new Courier(loginTest + "mistake", passwordTest);
        given()
                .body(courierCreate)
                .when()
                .post(endPointLogin)
                .then()
                .assertThat()
                .statusCode(404)
                .and()
                .body("message", equalTo("Учетная запись не найдена"));
    }
    @Test
    @DisplayName("Ошибка, если несуществующий логин и пароль.")
    @Description("/api/v1/courier/login post: wrong login and password")
    public  void loginCourierWithWrongLoginAndPassword() {
        Courier courierCreate  = new Courier(loginTest + "ошибка", passwordTest + "ошибка");
        given()
                .body(courierCreate)
                .when()
                .post(endPointLogin)
                .then()
                .assertThat()
                .statusCode(404)
                .and()
                .body("message", equalTo("Учетная запись не найдена"));
    }
}