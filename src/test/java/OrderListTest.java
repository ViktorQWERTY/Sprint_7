import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import jdk.jfr.Description;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class OrderListTest {

    static private String endPointOrder = "/api/v1/orders";

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";
    }

    @Test
    @DisplayName("Проверяем получение списка заказов: не пустой, код 200")
    @Description("get(/api/v1/orders)")
    public void checkStatusCodeAndOrderListNotNull() {
        given()
                .when()
                .get(endPointOrder)
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("orders", notNullValue());
    }
}