package com.server.iwbi.webserver.routes

import com.iwbi.domain.shopping.ShoppingItem
import com.iwbi.domain.shopping.ShoppingNote
import com.server.iwbi.application.shoppingnotes.input.ShoppingNotesServicePort
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable
import ktorutils.firebase.FirebasePrincipal
import org.koin.ktor.ext.inject

fun Route.shoppingNotesRoutes() {
    val shoppingNotesService by inject<ShoppingNotesServicePort>()

    route("/shopping-notes") {
        get {
            val principal = requireNotNull(call.principal<FirebasePrincipal>())
            val userId = principal.token.uid

            val notes = shoppingNotesService.getShoppingNotes(userId)
            call.respond(HttpStatusCode.OK, notes)
        }

        get("/{noteId}") {
            val principal = requireNotNull(call.principal<FirebasePrincipal>())
            val userId = principal.token.uid
            val noteId = call.parameters["noteId"] ?: return@get call.respond(HttpStatusCode.BadRequest)

            val note = shoppingNotesService.getShoppingNote(noteId, userId)
            if (note != null) {
                call.respond(HttpStatusCode.OK, note)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        post {
            val principal = requireNotNull(call.principal<FirebasePrincipal>())
            val userId = principal.token.uid
            val request = call.receive<CreateShoppingNoteRequest>()

            val note = shoppingNotesService.createShoppingNote(
                title = request.title,
                createdBy = userId, // Use authenticated user ID for security
                userIds = request.sharedWith
            )
            call.respond(HttpStatusCode.Created, note)
        }

        put("/{noteId}") {
            val noteId = call.parameters["noteId"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            val principal = requireNotNull(call.principal<FirebasePrincipal>())
            val userId = principal.token.uid

            val note = call.receive<ShoppingNote>()
            // Ensure the noteId from URL matches the note object for security
            val noteToUpdate = note.copy(id = noteId)
            shoppingNotesService.updateShoppingNote(noteToUpdate, userId)
            call.respond(HttpStatusCode.OK)
        }

        post("/{noteId}/items") {
            val noteId = call.parameters["noteId"] ?: return@post call.respond(HttpStatusCode.BadRequest)
            val principal = requireNotNull(call.principal<FirebasePrincipal>())
            val userId = principal.token.uid

            val item = call.receive<ShoppingItem>()
            shoppingNotesService.addItem(noteId, item, userId)
            call.respond(HttpStatusCode.OK)
        }

        put("/{noteId}/items/{itemIndex}/toggle") {
            val noteId = call.parameters["noteId"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            val itemIndex = call.parameters["itemIndex"]?.toIntOrNull() ?: return@put call.respond(HttpStatusCode.BadRequest)
            val principal = requireNotNull(call.principal<FirebasePrincipal>())
            val userId = principal.token.uid

            shoppingNotesService.toggleItem(noteId, itemIndex, userId)
            call.respond(HttpStatusCode.OK)
        }

        delete("/{noteId}/items/{itemIndex}") {
            val noteId = call.parameters["noteId"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            val itemIndex = call.parameters["itemIndex"]?.toIntOrNull() ?: return@delete call.respond(HttpStatusCode.BadRequest)
            val principal = requireNotNull(call.principal<FirebasePrincipal>())
            val userId = principal.token.uid

            shoppingNotesService.removeItem(noteId, itemIndex, userId)
            call.respond(HttpStatusCode.OK)
        }

        post("/{noteId}/share") {
            val noteId = call.parameters["noteId"] ?: return@post call.respond(HttpStatusCode.BadRequest)
            val principal = requireNotNull(call.principal<FirebasePrincipal>())
            val userId = principal.token.uid

            val request = call.receive<ShareNoteRequest>()
            shoppingNotesService.shareNoteWithUser(noteId, request.newUserId, userId)
            call.respond(HttpStatusCode.OK)
        }

        delete("/{noteId}") {
            val noteId = call.parameters["noteId"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            val principal = requireNotNull(call.principal<FirebasePrincipal>())
            val userId = principal.token.uid

            shoppingNotesService.deleteShoppingNote(noteId, userId)
            call.respond(HttpStatusCode.OK)
        }
    }
}

@Serializable
data class CreateShoppingNoteRequest(
    val title: String,
    val createdBy: String, // This will be ignored and replaced with authenticated user ID
    val sharedWith: List<String> = emptyList()
)

@Serializable
data class ShareNoteRequest(
    val newUserId: String
)
