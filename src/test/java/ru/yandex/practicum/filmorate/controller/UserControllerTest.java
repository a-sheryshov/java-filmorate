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

    @DisplayName("POST INVALID 400")
    @MethodSource("postBadRequestTestSource")
    @ParameterizedTest(name = "{index} {0} {1}")
    public void postBadRequestTest(String urlTemplate, String body) throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(urlTemplate)
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> postBadRequestTestSource() {
        return Stream.of(
                //empty email
                Arguments.of("/users", "{\"login\": \"bek\", \"name\": \"Bek\", \"birthday\": \"1955-20-20\"}"),
                //invalid email format 1
                Arguments.of("/users", "{\"login\": \"no dog\", \"name\": \"No dog\"," +
                        "\"email\":\"dogmail.ru\", \"birthday\": \"2010-08-15\"}"),
                //invalid email format 1
                Arguments.of("/users", "{\"login\": \"incorrect\", \"name\": \"Mail\"," +
                        "\"email\":\"123ya.ru@\", \"birthday\": \"1999-09-20\"}"),
                //empty request body
                Arguments.of("/users", ""),
                //login include spaces
                Arguments.of("/users", "{\"login\": \"1 2 3\", \"email\": \"123@ya.com\"," +
                        "\"birthday\": \"1997-09-29\"}"),
                //future birthday
                Arguments.of("/users", "{\"login\": \"test\", \"email\": \"test@test.com\"," +
                        "\"birthday\": \"2100-08-20\"}"),
                //all incorrect values
                Arguments.of("/users", "{\"login\": \"no no\", \"email\":\"nonono\"," +
                        "\"birthday\": \"9999-09-09\"}")
        );
    }

    @DisplayName("POST VALID 200")
    @MethodSource("postValidRequestTestSource")
    @ParameterizedTest(name = "{index} {0} {1}")
    public void postValidRequestTest(String urlTemplate, String body) throws Exception {
        mvc.perform(MockMvcRequestBuilders.post(urlTemplate)
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private static Stream<Arguments> postValidRequestTestSource() {
        return Stream.of(
                Arguments.of("/users", "{\"login\": \"дщпшт\", \"name\": \"Nick Name\"," +
                        "\"email\":\"mail4@mail.ru\", \"birthday\": \"1986-08-20\"}"),
                //пустое имя
                Arguments.of("/users", "{\"login\": \"dolore\", \"email\":\"mai33l@mail.ru\"," +
                        "\"birthday\": \"1986-08-20\"}")
        );
    }

    @DisplayName("PUT INVALID 400")
    @MethodSource("putInvalidRequestTestSource")
    @ParameterizedTest(name = "{index} {0} {1}")
    public void putInvalidRequestTest(String urlTemplate, String body) throws Exception {
        mvc.perform(MockMvcRequestBuilders.put(urlTemplate)
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> putInvalidRequestTestSource() {
        return Stream.of(
                //empty request
                Arguments.of("/users", ""),
                //no email
                Arguments.of("/users", "{ \"id\": 1,\"login\": \"login\", \"name\": \"name\"," +
                        "\"birthday\": \"1999-09-09\"}"),
                //wrong email 1
                Arguments.of("/users", "{ \"id\": 1,\"login\": \"login\", \"name\": \"name\"," +
                        "\"email\":\"123ya.ru\", \"birthday\": \"1946-08-20\"}"),
                //wrong email 2
                Arguments.of("/users", "{ \"id\": 1,\"login\": \"log\", \"name\": \"test\"," +
                        "\"email\":\"mailmail.ru@\", \"birthday\": \"1999-09-09\"}"),
                //empty request
                Arguments.of("/users", ""),
                //login includes spaces
                Arguments.of("/users", "{ \"id\": 1,\"login\": \"ni gol\", \"email\": \"ya@gmail.com\"," +
                        "\"birthday\": \"1999-09-09\"}"),
                //wrong birthday
                Arguments.of("/users", "{ \"id\": 1,\"login\": \"nigol\", \"email\": \"mail@ya.ya\"," +
                        "\"birthday\": \"2111-11-11\"}")
        );
    }

    @DisplayName("PUT VALID 200")
    @MethodSource("putValidRequestTestSource")
    @ParameterizedTest(name = "{index} {0} {1}")
    public void putValidRequestTest(String urlTemplate, String body) throws Exception {
        mvc.perform(MockMvcRequestBuilders.put(urlTemplate)
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private static Stream<Arguments> putValidRequestTestSource() {
        return Stream.of(
                Arguments.of("/users", "{\"login\": \"before\", \"name\": \"before\", \"id\": 1," +
                        "\"email\": \"before@yandex.ru\",\"birthday\": \"1999-09-09\"}"),
                Arguments.of("/users", "{\"login\": \"after\", \"name\": \"after\",  \"id\": 1," +
                        "\"email\":\"after@mail.ru\", \"birthday\": \"1999-08-08\"}")
        );
    }

    @DisplayName("PUT NOT EXISTING ID 404")
    @MethodSource("putNotExistingIdTestSource")
    @ParameterizedTest(name = "{index} {0} {1}")
    public void putInvalidIdTest(String urlTemplate, String body) throws Exception {
        mvc.perform(MockMvcRequestBuilders.put(urlTemplate)
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    private static Stream<Arguments> putNotExistingIdTestSource() {
        return Stream.of(
                Arguments.of("/users", "{\"id\": 999,\"login\": \"loglog\", \"name\": \"testtest\"," +
                        "\"email\":\"ya@ya.ru\", \"birthday\": \"1989-09-29\"}"),
                Arguments.of("/users", "{ \"id\": -1,\"login\": \"ololo\", \"name\": \"tatatat\"," +
                        "\"email\":\"ma@ya.ru\", \"birthday\": \"1999-06-26\"}")
        );
    }
}
