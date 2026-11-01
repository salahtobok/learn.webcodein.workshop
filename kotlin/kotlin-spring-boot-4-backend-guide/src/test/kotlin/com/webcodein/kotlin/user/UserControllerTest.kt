package com.webcodein.kotlin.user

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(UserController::class)
class UserControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var userService: UserService

    @Test
    @DisplayName("GET /api/users should return all users")
    fun testGetAllUsers() {
        val users = listOf(
            User(1L, "ada", "ada@lovelace.dev", "Pioneer"),
            User(2L, "grace", "grace@hopper.dev", null)
        )
        given(userService.findAll()).willReturn(users)

        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].username").value("ada"))
            .andExpect(jsonPath("$[1].username").value("grace"))
    }

    @Test
    @DisplayName("GET /api/users/{id} should return user when found")
    fun testGetUserByIdFound() {
        val user = User(1L, "ada", "ada@lovelace.dev", "Pioneer")
        given(userService.findById(1L)).willReturn(user)

        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.username").value("ada"))
            .andExpect(jsonPath("$.bio").value("Pioneer"))
    }

    @Test
    @DisplayName("GET /api/users/{id} should return 404 when user not found")
    fun testGetUserByIdNotFound() {
        given(userService.findById(999L)).willReturn(null)

        mockMvc.perform(get("/api/users/999"))
            .andExpect(status().isNotFound)
    }

    @Test
    @DisplayName("POST /api/users should create user and return HTTP 201")
    fun testCreateUser() {
        val request = CreateUserRequest("linus", "linus@kernel.org", "Linux")
        val created = User(3L, "linus", "linus@kernel.org", "Linux")
        given(userService.save(request)).willReturn(created)

        mockMvc.perform(
            post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"username":"linus","email":"linus@kernel.org","bio":"Linux"}""")
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(3))
            .andExpect(jsonPath("$.username").value("linus"))
    }
}
