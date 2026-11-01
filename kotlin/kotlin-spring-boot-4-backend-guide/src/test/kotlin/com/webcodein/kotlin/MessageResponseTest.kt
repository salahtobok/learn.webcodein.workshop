package com.webcodein.kotlin

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class MessageResponseTest {

    private val mapper = jacksonObjectMapper()

    @Test
    @DisplayName("Should verify data class default arguments, copy method, and immutability")
    fun verifyDataClassContracts() {
        val message = MessageResponse(text = "Welcome to Kotlin")

        assertThat(message.text).isEqualTo("Welcome to Kotlin")
        assertThat(message.author).isEqualTo("WebCodein")

        val modified = message.copy(text = "Updated greeting", author = "Engineer")
        assertThat(modified.text).isEqualTo("Updated greeting")
        assertThat(modified.author).isEqualTo("Engineer")

        // Destructuring declaration (componentN functions)
        val (txt, auth) = message
        assertThat(txt).isEqualTo("Welcome to Kotlin")
        assertThat(auth).isEqualTo("WebCodein")
    }

    @Test
    @DisplayName("Should serialize and deserialize JSON using Jackson Kotlin Module")
    fun testJsonRoundTrip() {
        val original = MessageResponse(text = "Serialization Test", author = "KotlinTeam")
        val json = mapper.writeValueAsString(original)

        assertThat(json).contains("\"text\":\"Serialization Test\"")
        assertThat(json).contains("\"author\":\"KotlinTeam\"")

        val deserialized: MessageResponse = mapper.readValue(json)
        assertThat(deserialized).isEqualTo(original)
    }
}
