package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class UserControllerTest {
    @Autowired
    private MockMvc mvc;

    @DisplayName("Scenario: make bad POST request expect code 400")
    @MethodSource("postBadRequestTestSource")
    @ParameterizedTest(name = "{index} Test with {0}")
    public void postBadRequestTest(String name, String urlTemplate, String body) throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(urlTemplate)
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> postBadRequestTestSource() {
        return Stream.of(
                Arguments.of("empty email", "/users", "{\"login\": \"bek\", \"name\": \"Bek\"" +
                        ", \"birthday\": \"1955-20-20\"}"),
                Arguments.of("invalid email format", "/users", "{\"login\": \"no dog\", \"name\": \"No dog\"," +
                        "\"email\":\"dogmail.ru\", \"birthday\": \"2010-08-15\"}"),
                Arguments.of("invalid email format", "/users", "{\"login\": \"incorrect\", \"name\": \"Mail\"," +
                        "\"email\":\"123ya.ru@\", \"birthday\": \"1999-09-20\"}"),
                Arguments.of("empty request body", "/users", ""),
                Arguments.of("login include spaces", "/users", "{\"login\": \"1 2 3\", \"email\": \"123@ya.com\"," +
                        "\"birthday\": \"1997-09-29\"}"),
                Arguments.of("future birthday", "/users", "{\"login\": \"test\", \"email\": \"test@test.com\"," +
                        "\"birthday\": \"2100-08-20\"}"),
                Arguments.of("all incorrect values", "/users", "{\"login\": \"no no\", \"email\":\"nonono\"," +
                        "\"birthday\": \"9999-09-09\"}")
        );
    }

    @DisplayName("Scenario: make valid POST request expect code 200")
    @MethodSource("postValidRequestTestSource")
    @ParameterizedTest(name = "{index} Test with {0}")
    public void postValidRequestTest(String name, String urlTemplate, String body) throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(urlTemplate)
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private static Stream<Arguments> postValidRequestTestSource() {
        return Stream.of(
                Arguments.of("valid user", "/users", "{\"login\": \"testlogin\", \"name\": \"testName\"," +
                        "\"email\":\"ya@mail.ru\", \"birthday\": \"1999-08-09\"}"),
                Arguments.of(  "valid noname user","/users", "{\"login\": \"logi\", \"email\":\"m@ya.ru\"," +
                        "\"birthday\": \"1986-08-20\"}")
        );
    }

    @DisplayName("Scenario: make bad PUT request expect code 400")
    @MethodSource("putInvalidRequestTestSource")
    @ParameterizedTest(name = "{index} Test with {0}")
    public void putInvalidRequestTest(String name, String urlTemplate, String body) throws Exception {
        mvc.perform(MockMvcRequestBuilders.put(urlTemplate)
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> putInvalidRequestTestSource() {
        return Stream.of(
                Arguments.of("empty request", "/users", ""),
                Arguments.of("no email","/users", "{ \"id\": 1,\"login\": \"login\", \"name\": \"name\"," +
                        "\"birthday\": \"1999-09-09\"}"),
                Arguments.of("wrong email", "/users", "{ \"id\": 1,\"login\": \"login\", \"name\": \"name\"," +
                        "\"email\":\"123ya.ru\", \"birthday\": \"1946-08-20\"}"),
                Arguments.of("wrong email","/users", "{ \"id\": 1,\"login\": \"log\", \"name\": \"test\"," +
                        "\"email\":\"mailmail.ru@\", \"birthday\": \"1999-09-09\"}"),
                Arguments.of("empty request","/users", ""),
                Arguments.of("login includes spaces","/users", "{ \"id\": 1,\"login\": \"ni gol\"" +
                        ", \"email\": \"ya@gmail.com\",\"birthday\": \"1999-09-09\"}"),
                Arguments.of("wrong birthday","/users", "{ \"id\": 1,\"login\": \"nigol\", \"email\": \"mail@ya.ya\"," +
                        "\"birthday\": \"2111-11-11\"}")
        );
    }

    @DisplayName("Scenario: make valid PUT request expect code 200")
    @MethodSource("putValidRequestTestSource")
    @ParameterizedTest(name = "{index} Test {0}")
    public void putValidRequestTest(String name, String urlTemplate, String body) throws Exception {
        mvc.perform(MockMvcRequestBuilders.put(urlTemplate)
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private static Stream<Arguments> putValidRequestTestSource() {
        return Stream.of(
                Arguments.of("init user","/users", "{\"login\": \"before\", \"name\": \"before\", \"id\": 1," +
                        "\"email\": \"before@yandex.ru\",\"birthday\": \"1999-09-09\"}"),
                Arguments.of("update user","/users", "{\"login\": \"after\", \"name\": \"after\",  \"id\": 1," +
                        "\"email\":\"after@mail.ru\", \"birthday\": \"1999-08-08\"}")
        );
    }

    @DisplayName("\"Scenario: make wrong ID PUT request expect code 404\"")
    @MethodSource("putNotExistingIdTestSource")
    @ParameterizedTest(name = "{index} Test with {0}")
    public void putInvalidIdTest(String name, String urlTemplate, String body) throws Exception {
        mvc.perform(MockMvcRequestBuilders.put(urlTemplate)
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    private static Stream<Arguments> putNotExistingIdTestSource() {
        return Stream.of(
                Arguments.of("positive wrong id", "/users", "{\"id\": 999,\"login\": \"loglog\", \"name\": \"testtest\"," +
                        "\"email\":\"ya@ya.ru\", \"birthday\": \"1989-09-29\"}"),
                Arguments.of("negative id","/users", "{ \"id\": -1,\"login\": \"ololo\", \"name\": \"tatatat\"," +
                        "\"email\":\"ma@ya.ru\", \"birthday\": \"1999-06-26\"}")
        );
    }
}
