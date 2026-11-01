package com.webcodein.kotlin.user

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

data class User(
    val id: Long,
    val username: String,
    val email: String,
    val bio: String? = null
)

data class CreateUserRequest(
    val username: String,
    val email: String,
    val bio: String? = null
)

fun User.toSummary(): String = if (bio != null) "$username ($email): $bio" else "$username ($email)"

@Service
class UserService {
    private val users = ConcurrentHashMap<Long, User>()
    private val idCounter = AtomicLong(1L)

    init {
        save(CreateUserRequest(username = "ada", email = "ada@lovelace.dev", bio = "Computing pioneer"))
        save(CreateUserRequest(username = "grace", email = "grace@hopper.dev"))
    }

    fun findById(id: Long): User? = users[id]

    fun findByEmail(email: String): User? = users.values.firstOrNull { it.email.equals(email, ignoreCase = true) }

    fun findAll(): List<User> = users.values.toList()

    fun save(request: CreateUserRequest): User {
        val id = idCounter.getAndIncrement()
        val user = User(id = id, username = request.username, email = request.email, bio = request.bio)
        users[id] = user
        return user
    }
}

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @GetMapping
    fun getAllUsers(): List<User> = userService.findAll()

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<User> {
        val user = userService.findById(id)
        return if (user != null) {
            ResponseEntity.ok(user)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(@RequestBody request: CreateUserRequest): User = userService.save(request)

    @GetMapping("/{id}/summary")
    fun getUserSummary(@PathVariable id: Long): ResponseEntity<String> {
        val user = userService.findById(id)
        return if (user != null) {
            ResponseEntity.ok(user.toSummary())
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
