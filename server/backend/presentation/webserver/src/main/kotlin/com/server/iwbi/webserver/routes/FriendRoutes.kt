package com.server.iwbi.webserver.routes

import com.iwbi.domain.user.User
import com.server.iwbi.application.friends.input.FriendServicePort
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.delete
import io.ktor.server.routing.route
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import ktorutils.firebase.FirebasePrincipal
import org.koin.ktor.ext.inject

@Serializable
data class UserRegistrationResponse(
    val message: String,
    val user: User
)

@Serializable
data class ErrorResponse(
    val error: String
)

@Serializable
data class MessageResponse(
    val message: String
)

fun Route.friendRoutes() {
    val friendService by inject<FriendServicePort>()

    route("/users") {
        // Register/Create user endpoint - called from mobile app after Firebase registration
        post("/register") {
            val principal = requireNotNull(call.principal<FirebasePrincipal>())
            val token = principal.token

            try {
                // Check if user already exists
                val existingUser = friendService.getUserById(token.uid)

                if (existingUser != null) {
                    // User already exists, return success with existing user data
                    KotlinLogging.logger {}.info { "User already exists: ${token.uid}" }
                    call.respond(HttpStatusCode.OK, UserRegistrationResponse(
                        message = "User already registered",
                        user = existingUser
                    ))
                } else {
                    // Create new user from Firebase token data
                    val newUser = User(
                        id = token.uid,
                        email = token.email ?: "unknown@example.com",
                        displayName = token.name ?: "User",
                        profilePictureUrl = token.picture,
                        createdAt = Clock.System.now().toEpochMilliseconds()
                    )

                    val createdUser = friendService.createUser(newUser)
                    KotlinLogging.logger {}.info { "Successfully created user: ${token.uid} (${token.email})" }

                    call.respond(HttpStatusCode.Created, UserRegistrationResponse(
                        message = "User successfully registered",
                        user = createdUser
                    ))
                }
            } catch (e: Exception) {
                KotlinLogging.logger {}.error(e) { "Failed to register user: ${token.uid}" }
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse(
                    error = "Failed to register user"
                ))
            }
        }
    }

    route("/friends") {
        // Get current user's friends
        get {
            val principal = requireNotNull(call.principal<FirebasePrincipal>())
            val userId = principal.token.uid

            val friends = friendService.getFriends(userId)
            call.respond(HttpStatusCode.OK, friends)
        }

        // Search users by query
        get("/search") {
            val query = call.request.queryParameters["q"] ?: ""
            if (query.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, ErrorResponse("Query parameter 'q' is required"))
                return@get
            }

            val principal = requireNotNull(call.principal<FirebasePrincipal>())
            val userId = principal.token.uid

            KotlinLogging.logger("NIK").info { "User $userId is searching for users with query: $query" }

            val users = friendService.searchUsers(userId, query)
            call.respond(HttpStatusCode.OK, users)
        }

        // Get pending friend requests (received)
        get("/requests/pending") {
            val principal = requireNotNull(call.principal<FirebasePrincipal>())
            val userId = principal.token.uid

            val requests = friendService.getPendingRequests(userId)
            call.respond(HttpStatusCode.OK, requests)
        }

        // Get sent friend requests
        get("/requests/sent") {
            val principal = requireNotNull(call.principal<FirebasePrincipal>())
            val userId = principal.token.uid

            val requests = friendService.getSentRequests(userId)
            call.respond(HttpStatusCode.OK, requests)
        }

        // Send friend request
        post("/requests") {
            val principal = requireNotNull(call.principal<FirebasePrincipal>())
            val userId = principal.token.uid
            val request = call.receive<SendFriendRequestRequest>()

            try {
                val friendRequest = friendService.sendFriendRequest(userId, request.toUserId)
                call.respond(HttpStatusCode.Created, friendRequest)
            } catch (e: IllegalStateException) {
                call.respond(HttpStatusCode.Conflict, ErrorResponse(e.message ?: "Conflict"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Failed to send friend request"))
            }
        }

        // Accept friend request
        post("/requests/{requestId}/accept") {
            val requestId = call.parameters["requestId"] ?: return@post call.respond(HttpStatusCode.BadRequest)
            val principal = requireNotNull(call.principal<FirebasePrincipal>())
            val userId = principal.token.uid

            try {
                val friendship = friendService.acceptFriendRequest(userId, requestId)
                call.respond(HttpStatusCode.OK, friendship)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse(e.message ?: "Not found"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Failed to accept friend request"))
            }
        }

        // Decline friend request
        post("/requests/{requestId}/decline") {
            val requestId = call.parameters["requestId"] ?: return@post call.respond(HttpStatusCode.BadRequest)
            val principal = requireNotNull(call.principal<FirebasePrincipal>())
            val userId = principal.token.uid

            try {
                friendService.declineFriendRequest(userId, requestId)
                call.respond(HttpStatusCode.OK, MessageResponse("Friend request declined"))
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse(e.message ?: "Not found"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Failed to decline friend request"))
            }
        }

        // Remove friend
        delete("/{friendId}") {
            val friendId = call.parameters["friendId"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            val principal = requireNotNull(call.principal<FirebasePrincipal>())
            val userId = principal.token.uid

            try {
                friendService.removeFriend(userId, friendId)
                call.respond(HttpStatusCode.OK, MessageResponse("Friend removed successfully"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Failed to remove friend"))
            }
        }

        // Get user by ID (for profile info)
        get("/user/{userId}") {
            val userIdParam = call.parameters["userId"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val user = friendService.getUserById(userIdParam)
            if (user != null) {
                call.respond(HttpStatusCode.OK, user)
            } else {
                call.respond(HttpStatusCode.NotFound, ErrorResponse("User not found"))
            }
        }
    }
}

@Serializable
data class SendFriendRequestRequest(
    val toUserId: String
)
