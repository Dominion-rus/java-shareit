package ru.practicum.shareitgateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareitgateway.config.AppConfig;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = AppConfig.class)
class AppConfigTest {

    @Autowired
    private AppConfig appConfig;

    @Test
    void shouldReturnCorrectServerUrl() {
        String expectedUrl = "http://localhost:9090";
        assertThat(appConfig.getServerUrl()).isEqualTo(expectedUrl);
    }

    @Test
    void shouldReturnCorrectFullUrl() {
        String expectedFullUrl = "http://localhost:9090/bookings";
        assertThat(appConfig.getFullUrl("/bookings")).isEqualTo(expectedFullUrl);
    }

    @Test
    void shouldCreateRestTemplateWithHttpComponentsClientHttpRequestFactory() {
        RestTemplate restTemplate = appConfig.restTemplate();
        assertThat(restTemplate.getRequestFactory()).isInstanceOf(HttpComponentsClientHttpRequestFactory.class);
    }

}
