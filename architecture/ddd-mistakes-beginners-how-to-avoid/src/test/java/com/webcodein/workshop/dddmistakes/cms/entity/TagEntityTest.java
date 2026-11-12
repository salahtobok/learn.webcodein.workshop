package com.webcodein.workshop.dddmistakes.cms.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("TagEntity Simple CRUD Tests")
class TagEntityTest {

    @Test
    @DisplayName("Should create and mutate simple CRUD entity for administrative data")
    void shouldCreateAndMutateTag() {
        TagEntity tag = new TagEntity("Java");
        assertEquals("Java", tag.getName());

        tag.setName("Spring Boot");
        assertEquals("Spring Boot", tag.getName());
    }
}
