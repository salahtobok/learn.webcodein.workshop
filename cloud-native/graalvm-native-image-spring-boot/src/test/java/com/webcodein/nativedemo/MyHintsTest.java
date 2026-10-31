package com.webcodein.nativedemo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.predicate.RuntimeHintsPredicates;

class MyHintsTest {

    @Test
    @DisplayName("Should register reflection hints for MyDynamicClass")
    void shouldRegisterReflectionHints() {
        RuntimeHints hints = new RuntimeHints();
        new MyHints().registerHints(hints, getClass().getClassLoader());

        assertThat(RuntimeHintsPredicates.reflection()
                .onType(MyDynamicClass.class))
                .accepts(hints);
    }

    @Test
    @DisplayName("Should register resource pattern hints for config files")
    void shouldRegisterResourceHints() {
        RuntimeHints hints = new RuntimeHints();
        new MyHints().registerHints(hints, getClass().getClassLoader());

        assertThat(RuntimeHintsPredicates.resource()
                .forResource("config/app.json"))
                .accepts(hints);
    }
}
