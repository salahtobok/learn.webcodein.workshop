package com.webcodein.workshop.localai;

import com.webcodein.workshop.localai.model.InferenceRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InferenceRequestTest {

    @Test
    @DisplayName("Should create valid inference request with defaults")
    void shouldCreateDefaultRequest() {
        InferenceRequest req = InferenceRequest.of("Summarize quarterly revenue");

        assertThat(req.prompt()).isEqualTo("Summarize quarterly revenue");
        assertThat(req.model()).isEqualTo("deepseek-r1:8b");
        assertThat(req.temperature()).isEqualTo(0.7);
    }

    @Test
    @DisplayName("Should throw exception when prompt is blank")
    void shouldThrowWhenPromptIsBlank() {
        assertThatThrownBy(() -> new InferenceRequest("  ", "deepseek-r1:8b", 0.5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Prompt cannot be empty");
    }

    @Test
    @DisplayName("Should throw exception when temperature exceeds 2.0")
    void shouldThrowWhenTemperatureTooHigh() {
        assertThatThrownBy(() -> new InferenceRequest("Hello", "deepseek-r1:8b", 2.5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Temperature must be between 0.0 and 2.0");
    }
}
