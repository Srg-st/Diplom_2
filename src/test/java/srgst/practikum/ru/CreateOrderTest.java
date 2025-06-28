package srgst.practikum.ru;


import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.notNullValue;


public class CreateOrderTest {
    private String email;
    private String password;
    private String name;
    private UserStep userStep;
    private Order order;
    private OrderStep orderStep;
    private List<String> ingredients;
    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = Constants.TEST_URL;
        userStep = new UserStep();
        orderStep = new OrderStep();
        email = RandomStringUtils.randomAlphabetic(10) + "@gmail.com";
        password = RandomStringUtils.randomAlphabetic(10);
        name = RandomStringUtils.randomAlphabetic(10);

        userStep.createUser(new User(email, password, name))
                .then()
                .assertThat().statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Создание заказа")
    @Description("Создание заказа с авторизацией и ингредиентами")
    public void createOrderAuthorAndIngredTest() {

        Response loginResponse = userStep.loginUser(new LoginUser(email, password));
        loginResponse.then().assertThat().
                statusCode(200).
                body("success", equalTo(true));
        accessToken = loginResponse.jsonPath().getString("accessToken");

        Response ingredientsResponse = orderStep.getListIngredients(order);
        ingredientsResponse.then().assertThat().
                statusCode(200).
                body("success", equalTo(true));
        ingredients = ingredientsResponse.jsonPath().getList("data._id");

        order = new Order((ArrayList<String>) ingredients, accessToken);

        Response createOrderResponse = orderStep.createOrder(accessToken, ingredients);
        createOrderResponse.then()
                .assertThat().statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа")
    @Description("Создание заказа с авторизацией и без ингредиентов")
    public void createOrderAuthorAndWithoutIngredTest() {

        Response loginResponse = userStep.loginUser(new LoginUser(email, password));
        loginResponse.then().assertThat().
                statusCode(200).
                body("success", equalTo(true));
        accessToken = loginResponse.jsonPath().getString("accessToken");

        List<String> nonIngredients = new ArrayList<>();

        order = new Order((ArrayList<String>) nonIngredients, accessToken);

        Response createOrderResponse = orderStep.createOrder(accessToken, nonIngredients);
        createOrderResponse.then()
                .assertThat().statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));


    }

    @Test
    @DisplayName("Создание заказа")
    @Description("Создание заказа без авторизации, с ингредиентами")
    public void createOrderWithoutAuthorButIngredTest() {
        String zeroAccessToken = " ";

        Response ingredientsResponse = orderStep.getListIngredients(order);
        ingredientsResponse.then().assertThat().
                statusCode(200).
                body("success", equalTo(true));
        ingredients = ingredientsResponse.jsonPath().getList("data._id");

        order = new Order((ArrayList<String>) ingredients, accessToken);

        Response createOrderResponse = orderStep.createOrder(zeroAccessToken, ingredients);
        createOrderResponse.then()
                .assertThat().statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа")
    @Description("Создание заказа без авторизации, без ингредиентов")
    public void createOrderWithoutAuthorAndWithoutIngredTest() {
        String zeroAccessToken = " ";

        List<String> nonIngredients = new ArrayList<>();

        order = new Order((ArrayList<String>) nonIngredients, zeroAccessToken);

        Response createOrderResponse = orderStep.createOrder(zeroAccessToken, nonIngredients);
        createOrderResponse.then()
                .assertThat().statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));

    }

    @Test
    @DisplayName("Создание заказа")
    @Description("Создание заказа c авторизацией и неправильным хешем ингредиентов")
    public void createOrderAuthorAndWrongIngredTest() {

        Response loginResponse = userStep.loginUser(new LoginUser(email, password));
        loginResponse.then().assertThat().
                statusCode(200).
                body("success", equalTo(true));
        accessToken = loginResponse.jsonPath().getString("accessToken");

        List<String> wrongIngredients = new ArrayList<>();
        wrongIngredients.add("wronghashigredient1");
        wrongIngredients.add("wronghashigredient2");

        order = new Order((ArrayList<String>) wrongIngredients, accessToken);
        Response createOrderResponse = orderStep.createOrder(accessToken, wrongIngredients);
        createOrderResponse.then()
                .assertThat().statusCode(500);
    }

    @After
    public void tearDown() {
        try {
            if (accessToken != null) {
                userStep.deleteUser(accessToken);
            }
        } catch (Exception e) {
            System.out.println("Ошибка при удалении пользователя: " + e.getMessage());
        }

    }
}
