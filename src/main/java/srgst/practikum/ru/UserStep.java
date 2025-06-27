package srgst.practikum.ru;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserStep {

    @Step("Создание пользователя")
    public Response createUser(User user) {
        return
                given().
                        log().all().
                        header("Content-Type", "application/json").
                        body(user).
                        when().
                        post(Constants.TEST_USER);

    }

    @Step("Авторизация пользователя")
    public Response loginUser(LoginUser loginUser) {
               Response response = given().
                        log().all().
                        header("Content-Type", "application/json").
                        body(loginUser).
                        when().
                        post(Constants.TEST_LOGIN);

        return response;
    }

    @Step("Удаление пользователя")
    public Response deleteUser(String accessToken) {
        return
                given().
                        log().all().
                        header("Authorization", accessToken).
                        delete(Constants.TEST_USER);

    }
}
