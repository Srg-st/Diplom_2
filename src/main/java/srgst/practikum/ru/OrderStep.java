package srgst.practikum.ru;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class OrderStep {
    private List<String> ingredients;

    @Step("Получение списка ингредиентов")
    public Response getListIngredients(Order order) {
        Response response = given().log().all().
                headers("Content-Type", "application/json").
                when().
                get(Constants.GET_INGREDIENTS);

        return response;
    }

    @Step("Создание заказа")
    public Response createOrder(String accessToken, List<String> ingredients) {
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("ingredients", ingredients);

        Response response = given().log().all().
                headers("Content-Type", "application/json").
                headers("Authorization", accessToken).
                body(bodyMap).
                when().
                post(Constants.TEST_ORDER);
        return response;
    }

}


