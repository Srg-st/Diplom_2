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
import static org.hamcrest.CoreMatchers.notNullValue;

public class LoginUserTest {
    private String email;
    private String password;
    private String name;
    private UserStep userStep;
    private LoginUser loginUser;
    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = Constants.TEST_URL;
        userStep = new UserStep();
        loginUser = new LoginUser();
        email = RandomStringUtils.randomAlphabetic(10) + "@gmail.com";
        password = RandomStringUtils.randomAlphabetic(10);
        name = RandomStringUtils.randomAlphabetic(10);

        userStep.createUser(new User(email, password, name))
                .then()
                .assertThat().statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Логин пользователя")
    @Description("Проверяем, что пользователь может войти в систему")
    public void loginUser() {
        Response loginResponse = userStep.loginUser(new LoginUser(email, password));
        loginResponse
                .then()
                .assertThat().statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", startsWith("Bearer"))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());

        accessToken = loginResponse.jsonPath().getString("accessToken");
    }

    @Test
    @DisplayName("Логин пользователя с неверным паролем")
    @Description("Проверяем, что пользователь не может войти в систему с неверным паролем")
    public void loginUserWithWrongPassword() {
        Response loginResponse = userStep.loginUser(new LoginUser(email, password + "1"));
                loginResponse.then()
                .assertThat().statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
                //Если пользователь сможет войти в систему с неверным паролем, то accessToken будет не пустым и в блоке After будет удален
        accessToken = loginResponse.jsonPath().getString("accessToken");
    }

    @Test
    @DisplayName("Логин пользователя с неверным email")
    @Description("Проверяем, что пользователь не может войти в систему с неверным email")
    public void loginUserWithWrongEmail() {
        Response loginResponse = userStep.loginUser(new LoginUser("1" + email, password));
                loginResponse.then()
                .assertThat().statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));

        //Если пользователь сможет войти в систему с неверным email, то accessToken будет не пустым и в блоке After будет удален
        accessToken = loginResponse.jsonPath().getString("accessToken");
    }

    @Test
    @DisplayName("Логин пользователя без пароля")
    @Description("Проверяем, что пользователь не может войти в систему без пароля")
    public void loginUserWithoutPassword() {
        Response loginResponse = userStep.loginUser(new LoginUser(email, ""));
                loginResponse.then()
                .assertThat().statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
        //Если пользователь сможет войти в систему без пароля, то accessToken будет не пустым и в блоке After будет удален
                accessToken = loginResponse.jsonPath().getString("accessToken");
    }

    @Test
    @DisplayName("Логин пользователя без email")
    @Description("Проверяем, что пользователь не может войти в систему без email")
    public void loginUserWithoutEmail() {
        Response loginResponse = userStep.loginUser(new LoginUser("", password));
                loginResponse.then()
                .assertThat().statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    //Если пользователь сможет войти в систему без email, то accessToken будет не пустым и в блоке After будет удален
    accessToken = loginResponse.jsonPath().getString("accessToken");
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
