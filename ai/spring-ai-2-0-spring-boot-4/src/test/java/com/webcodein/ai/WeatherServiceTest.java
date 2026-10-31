package com.webcodein.ai;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.annotation.Tool;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class WeatherServiceTest {

    private final WeatherService weatherService = new WeatherService();

    @Test
    @DisplayName("Should return valid weather response for given city")
    void shouldReturnWeatherResponse() {
        WeatherService.WeatherRequest request = new WeatherService.WeatherRequest("Paris");
        WeatherService.WeatherResponse response = weatherService.getWeather(request);

        assertThat(response).isNotNull();
        assertThat(response.city()).isEqualTo("Paris");
        assertThat(response.condition()).isEqualTo("Sunny");
        assertThat(response.temperatureC()).isEqualTo(24);
    }

    @Test
    @DisplayName("Should have @Tool annotation on getWeather method with clear description")
    void shouldHaveToolAnnotation() throws NoSuchMethodException {
        Method method = WeatherService.class.getMethod("getWeather", WeatherService.WeatherRequest.class);
        Tool toolAnnotation = method.getAnnotation(Tool.class);

        assertThat(toolAnnotation).isNotNull();
        assertThat(toolAnnotation.description()).contains("Get the current weather");
    }

    @Test
    @DisplayName("Should maintain Java 25 record contract for request and response types")
    void shouldMaintainRecordContract() {
        WeatherService.WeatherRequest req1 = new WeatherService.WeatherRequest("Tokyo");
        WeatherService.WeatherRequest req2 = new WeatherService.WeatherRequest("Tokyo");
        assertThat(req1).isEqualTo(req2);
        assertThat(req1.hashCode()).isEqualTo(req2.hashCode());
        assertThat(req1.toString()).contains("Tokyo");

        WeatherService.WeatherResponse res1 = new WeatherService.WeatherResponse("Tokyo", "Sunny", 24);
        WeatherService.WeatherResponse res2 = new WeatherService.WeatherResponse("Tokyo", "Sunny", 24);
        assertThat(res1).isEqualTo(res2);
        assertThat(res1.hashCode()).isEqualTo(res2.hashCode());
    }
}
