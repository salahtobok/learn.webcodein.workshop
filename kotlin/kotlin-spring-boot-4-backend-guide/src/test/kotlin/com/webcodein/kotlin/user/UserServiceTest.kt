package com.webcodein.kotlin.user

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class UserServiceTest {

    private val userService = UserService()

    @Test
    @DisplayName("Should find initial users by id and email with null safety guarantees")
    fun testFindUser() {
        val user1 = userService.findById(1L)
        assertThat(user1).isNotNull
        assertThat(user1?.username).isEqualTo("ada")
        assertThat(user1?.email).isEqualTo("ada@lovelace.dev")

        val missingUser = userService.findById(999L)
        assertThat(missingUser).isNull()

        val foundByEmail = userService.findByEmail("grace@hopper.dev")
        assertThat(foundByEmail).isNotNull
        assertThat(foundByEmail?.username).isEqualTo("grace")
    }

    @Test
    @DisplayName("Should create new user with optional nullable bio")
    fun testCreateUser() {
        val request = CreateUserRequest(
            username = "linus",
            email = "linus@kernel.org",
            bio = "Linux creator"
        )
        val created = userService.save(request)

        assertThat(created.id).isNotNull
        assertThat(created.username).isEqualTo("linus")
        assertThat(created.email).isEqualTo("linus@kernel.org")
        assertThat(created.bio).isEqualTo("Linux creator")

        assertThat(userService.findById(created.id)).isEqualTo(created)
    }

    @Test
    @DisplayName("Should format user summary correctly using extension function")
    fun testUserExtensionFunction() {
        val withBio = User(10L, "alan", "alan@turing.org", "Enigma codebreaker")
        assertThat(withBio.toSummary()).isEqualTo("alan (alan@turing.org): Enigma codebreaker")

        val withoutBio = User(11L, "dennis", "dmr@bell.org", null)
        assertThat(withoutBio.toSummary()).isEqualTo("dennis (dmr@bell.org)")
    }
}
