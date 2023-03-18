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
class FilmControllerTest {
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
                Arguments.of("no name value", "/films", "{ \"description\": \"film\"," +
                        "\"releaseDate\": \"2010-10-15\",  \"duration\": 50}"),
                Arguments.of("empty value", "/films", ""),
                Arguments.of("negative duration", "/films", "{ \"name\": \"negative\",  \"description\": \"description\"," +
                        "\"releaseDate\": \"1999-03-28\",  \"duration\": -42}"),
                Arguments.of("too early date", "/films", "{ \"name\": \"film\",  \"description\": \"film descr\"," +
                        "\"releaseDate\": \"1800-01-01\",  \"duration\": 2}"),
                Arguments.of("too long description", "/films", "{" + getLongDescriptionFilm() + "}")
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
                Arguments.of("valid request", "/films", "{ \"name\": \"avatar\",  \"description\": \"so-so\"," +
                        "\"releaseDate\": \"2023-01-01\",  \"duration\": 200}"),
                Arguments.of("border condition", "/films", "{ \"name\": \"first film\",  \"description\": \"first film\"," +
                        "\"releaseDate\": \"1895-12-28\",  \"duration\": 2}")
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
                Arguments.of("empty request", "/films", ""),
                Arguments.of("no name value", "/films", "{ \"id\": 1, \"description\": \"film\"," +
                        "\"releaseDate\": \"2000-10-15\",  \"duration\": 10}"),
                Arguments.of("negative duration", "/films", "{ \"id\": 1, \"name\": \"negative\"," +
                        "\"description\": \"negative\", \"releaseDate\": \"1999-09-25\", \"duration\": -1}"),
                Arguments.of("invalid date", "/films", "{ \"id\": 1, \"name\": \"film\"," +
                        "\"description\": \"too old test\", \"releaseDate\": \"1895-12-27\", \"duration\": 100}"),
                Arguments.of("long description", "/films", "{\"id\": 1," + getLongDescriptionFilm() + "}")
        );
    }

    @DisplayName("Scenario: make valid PUT request expect code 200")
    @MethodSource("putValidRequestTestSource")
    @ParameterizedTest(name = "{index} Test with {0}")
    public void putValidRequestTest(String name, String urlTemplate, String body) throws Exception {
        mvc.perform(MockMvcRequestBuilders.put(urlTemplate)
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private static Stream<Arguments> putValidRequestTestSource() {
        return Stream.of(
                Arguments.of("init value", "/films", "{ \"id\": 1, \"name\": \"film\",  \"description\": \"test\"," +
                        "\"releaseDate\": \"2010-02-27\",  \"duration\": 10}"),
                Arguments.of("update value", "/films", "{ \"id\": 1, \"name\": \"edit film\",  \"description\": \"edit test\"," +
                        "\"releaseDate\": \"2011-12-20\",  \"duration\": 11}")
        );
    }

    @DisplayName("Scenario: make not existing ID PUT request expect code 404")
    @MethodSource("putNotExistingIdTestSource")
    @ParameterizedTest(name = "{index} Test with {0}")
    public void putNotExistingIdTest(String name, String urlTemplate, String body) throws Exception {
        mvc.perform(MockMvcRequestBuilders.put(urlTemplate)
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    private static Stream<Arguments> putNotExistingIdTestSource() {
        return Stream.of(
                Arguments.of("wrong ID", "/films", "{ \"id\": 777, \"name\": \"port-wine\",  \"description\": \"777\"," +
                        "\"releaseDate\": \"1977-07-07\",  \"duration\": 77}")
        );
    }

    @DisplayName("Scenario: make negative ID PUT request expect code 400")
    @MethodSource("putNegativeIdTestSource")
    @ParameterizedTest(name = "{index} Test with {0}")
    public void putNegativeIdTest(String name, String urlTemplate, String body) throws Exception {
        mvc.perform(MockMvcRequestBuilders.put(urlTemplate)
                        .content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> putNegativeIdTestSource() {
        return Stream.of(
                Arguments.of("negative ID", "/films", "{ \"id\": -777, \"name\": \"port-wine\",  \"description\": \"777\"," +
                        "\"releaseDate\": \"1977-07-07\",  \"duration\": 77}")
        );
    }

    private static String getLongDescriptionFilm() {
        var longDescription = "8".repeat(201);
        return String.format("\"name\": \"long description film\",  \"description\": \"%s\",", longDescription)
                + "\"releaseDate\": \"2023-01-01\",  \"duration\": 199";
    }
}