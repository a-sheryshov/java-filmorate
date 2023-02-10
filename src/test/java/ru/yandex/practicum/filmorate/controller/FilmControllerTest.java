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

import java.util.Arrays;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class FilmControllerTest {
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
                //noname
                Arguments.of("/films", "{ \"description\": \"film\"," +
                        "\"releaseDate\": \"2010-10-15\",  \"duration\": 50}"),
                //empty
                Arguments.of("/films", ""),
                //negative duration
                Arguments.of("/films", "{ \"name\": \"negative\",  \"description\": \"description\"," +
                        "\"releaseDate\": \"1999-03-28\",  \"duration\": -42}"),
                //too early
                Arguments.of("/films", "{ \"name\": \"film\",  \"description\": \"film descr\"," +
                        "\"releaseDate\": \"1800-01-01\",  \"duration\": 2}"),
                //long description
                Arguments.of("/films", "{" + getLongDescriptionFilm() + "}")
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
                //correct
                Arguments.of("/films", "{ \"name\": \"avatar\",  \"description\": \"so-so\"," +
                        "\"releaseDate\": \"2023-01-01\",  \"duration\": 200}"),
                //border condition
                Arguments.of("/films", "{ \"name\": \"first film\",  \"description\": \"first film\"," +
                        "\"releaseDate\": \"1895-12-28\",  \"duration\": 2}")

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
                //empty
                Arguments.of("/films", ""),
                //noname
                Arguments.of("/films", "{ \"id\": 1, \"description\": \"film\"," +
                        "\"releaseDate\": \"2000-10-15\",  \"duration\": 10}"),
                //negative duration
                Arguments.of("/films", "{ \"id\": 1, \"name\": \"negative\"," +
                        "\"description\": \"negative\", \"releaseDate\": \"1999-09-25\", \"duration\": -1}"),
                //invalid date
                Arguments.of("/films", "{ \"id\": 1, \"name\": \"film\"," +
                        "\"description\": \"too old test\", \"releaseDate\": \"1895-12-27\", \"duration\": 100}"),
                //long description
                Arguments.of("/films", "{\"id\": 1," + getLongDescriptionFilm() + "}")
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
                Arguments.of("/films", "{ \"id\": 1, \"name\": \"film\",  \"description\": \"test\"," +
                        "\"releaseDate\": \"2010-02-27\",  \"duration\": 10}"),
                Arguments.of("/films", "{ \"id\": 1, \"name\": \"edit film\",  \"description\": \"edit test\"," +
                        "\"releaseDate\": \"2011-12-20\",  \"duration\": 11}")
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
                Arguments.of("/films", "{ \"id\": 777, \"name\": \"port-wine\",  \"description\": \"777\"," +
                        "\"releaseDate\": \"1977-07-07\",  \"duration\": 77}")
        );
    }

    private static String getLongDescriptionFilm() {
        char[] arr = new char[201];
        Arrays.fill(arr, '8');
        return String.format("\"name\": \"long description film\",  \"description\": \"%s\",", new String(arr)) +
                "\"releaseDate\": \"2023-01-01\",  \"duration\": 199";
    }
}
