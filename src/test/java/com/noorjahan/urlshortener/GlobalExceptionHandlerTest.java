package com.noorjahan.urlshortener;

import com.noorjahan.urlshortener.exception.UrlNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExceptionHandlerTestController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testUrlNotFoundException_Returns404() throws Exception {
        mockMvc.perform(get("/test/not-found")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testGenericException_Returns500() throws Exception {
        mockMvc.perform(get("/test/error")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.statusCode").value(500))
                .andExpect(jsonPath("$.error").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void testErrorResponse_JsonStructure() throws Exception {
        mockMvc.perform(get("/test/not-found")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.statusCode").isNumber())
                .andExpect(jsonPath("$.message").isString())
                .andExpect(jsonPath("$.error").isString())
                .andExpect(jsonPath("$.timestamp").isString());
    }
}

@RestController
class ExceptionHandlerTestController {

    @GetMapping("/test/not-found")
    public void throwNotFoundException() {
        throw new UrlNotFoundException("Test URL not found");
    }

    @GetMapping("/test/error")
    public void throwGenericException() {
        throw new RuntimeException("Test error occurred");
    }
}
