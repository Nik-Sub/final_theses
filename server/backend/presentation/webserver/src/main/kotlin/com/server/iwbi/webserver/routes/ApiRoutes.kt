package com.server.iwbi.webserver.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

fun Route.apiRoutes() {
    route("/api") {
        get("/hello-world") {
            call.respond(HttpStatusCode.OK, "Hello, World!")
        }


        get("/{...}") {
            call.respond(HttpStatusCode.NotFound)
        }
    }
}
