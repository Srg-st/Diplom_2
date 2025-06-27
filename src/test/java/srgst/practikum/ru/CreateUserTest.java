package srgst.practikum.ru;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

public class CreateUserTest {
    private String email;
    private String password;
    private String name;
    private UserStep userStep;
    private String accessToken;
    private LoginUser loginUser;

    @Before
    public void setUp() {
        RestAssured.baseURI = Constants.TEST_URL;
        userStep = new UserStep();
        loginUser = new LoginUser();
        email = RandomStringUtils.randomAlphabetic(10) + "@gmail.com";
        password = RandomStringUtils.randomAlphabetic(10);
        name = RandomStringUtils.randomAlphabetic(10);
    }

    @Test
    @DisplayName("Создание пользователя")
    @Description("Создание пользователя с корректными данными")
    public void createUserTest() {
        userStep.createUser(new User(email, password, name))
        .then()
                .assertThat().statusCode(200)
        .body("success", equalTo(true))
                .body("user.email", equalTo(email.toLowerCase()))
                .body("user.name", equalTo(name))
                .body("accessToken", startsWith("Bearer "))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());

    }

    @Test
    @DisplayName("Создание пользователя без емейла")
    @Description("Создание пользователя без емейла")
    public void createUserWithoutEmailTest() {
        userStep.createUser(new User(null, password, name))
                .then()
                .assertThat().statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    @Description("Создание пользователя без пароля")
    public void createUserWithoutPasswordTest() {
        userStep.createUser(new User(email, null, name))
                .then()
                .assertThat().statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));

    }

    @Test
    @DisplayName("Создание пользователя без имени")
    @Description("Создание пользователя без имени")
    public void createUserWithoutNameTest() {
        userStep.createUser(new User(email, password, null))
                .then()
                .assertThat().statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя,  если такой пользователь уже существует")
    @Description("Создание пользователя,  если такой пользователь уже существует")
    public void createUserIfUserExistsTest() {
        userStep.createUser(new User(email, password, name))
                .then()
                .assertThat().statusCode(200)
                .body("success", equalTo(true));

        userStep.createUser(new User(email, password, name))
                .then()
                .assertThat().statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @After
    public void tearDown() {
        Response loginResponse = userStep.loginUser(new LoginUser(email, password));
        accessToken = loginResponse.jsonPath().getString("accessToken");

        try {
            if (accessToken != null) {
                userStep.deleteUser(accessToken);
            }
        } catch (Exception e) {
            System.out.println("Ошибка при удалении пользователя: " + e.getMessage());
        }
    }

}

