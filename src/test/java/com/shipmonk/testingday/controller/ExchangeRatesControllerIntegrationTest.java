package com.shipmonk.testingday.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Testcontainers
@Transactional
class ExchangeRatesControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.5")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    private static WireMockServer wireMockServer;
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Override database properties with TestContainer values
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig()
            .port(8089)
            .usingFilesUnderDirectory("src/test/resources"));
        wireMockServer.start();

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @AfterEach
    void tearDown() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @Test
    void shouldGetLatestRatesForToday() throws Exception {
        // Given: Today's date
        final String today = LocalDate.of(2025, 8, 13).toString();

        // When & Then: Call the endpoint for today's date
        mockMvc.perform(get("/api/v1/rates/{day}", today)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.base").value("USD"))
            .andExpect(jsonPath("$.date").value("2025-08-13"))
            .andExpect(jsonPath("$.rates").exists())
            .andExpect(jsonPath("$.rates").isMap())
            // Verify currency conversion from EUR to USD base
            .andExpect(jsonPath("$.rates.EUR").value(0.855865))
            .andExpect(jsonPath("$.rates.GBP").value(0.740425))
            .andExpect(jsonPath("$.rates.JPY").value(147.952446))
            .andExpect(jsonPath("$.rates.CZK").value(20.943996))
            .andExpect(jsonPath("$.rates.PLN").value(3.642229));
    }

    @Test
    void shouldGetHistoricalRatesForSpecificDate() throws Exception {
        // Given: Historical date
        final String historicalDate = "2024-12-24";

        // When & Then: Call the endpoint for historical date
        mockMvc.perform(get("/api/v1/rates/{day}", historicalDate)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.base").value("USD"))
            .andExpect(jsonPath("$.date").value("2024-12-24"))
            .andExpect(jsonPath("$.rates").exists())
            .andExpect(jsonPath("$.rates").isMap())
            // Verify currency conversion from EUR to USD base for historical data
            .andExpect(jsonPath("$.rates.EUR").value(0.961402))
            .andExpect(jsonPath("$.rates.GBP").value(0.797749))
            .andExpect(jsonPath("$.rates.JPY").value(157.111991))
            .andExpect(jsonPath("$.rates.CZK").value(24.23865))
            .andExpect(jsonPath("$.rates.PLN").value(4.098149));
    }

    @Test
    void shouldGetCachedRatesFromDatabase() throws Exception {
        // Given: Populate database with cached data
        final String sqlScript = new String(Files.readAllBytes(
            Paths.get("src/test/resources/db/data/populate-cache-data.sql")));
        jdbcTemplate.execute(sqlScript);

        // Given: Date that has cached data
        final String cachedDate = "2025-08-12";

        // When & Then: Call the endpoint for cached date
        mockMvc.perform(get("/api/v1/rates/{day}", cachedDate)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.base").value("USD"))
            .andExpect(jsonPath("$.date").value("2025-08-12"))
            .andExpect(jsonPath("$.rates").exists())
            .andExpect(jsonPath("$.rates").isMap())
            // Verify some cached rates from database
            .andExpect(jsonPath("$.rates.EUR").value(0.856399))
            .andExpect(jsonPath("$.rates.GBP").value(0.740551))
            .andExpect(jsonPath("$.rates.JPY").value(147.764447))
            .andExpect(jsonPath("$.rates.CZK").value(20.955955))
            .andExpect(jsonPath("$.rates.PLN").value(3.644449));
    }
}
