package com.example.plugins

import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.request.*

import com.example.Controller
import com.example.models.BlockChain
import com.example.NotificationReceivedCallback
import com.example.models.Block


// ask third node routing
fun Application.configureAskThirdNodeReceiverRouting() {
    routing {
        get("/ask_third_node") {
            call.respond(Controller.sendLastBlock())
        }
    }
}

//block inserted
fun Application.configureNotificationReceiverRouting(notificationReceivedCallback: NotificationReceivedCallback) {
    routing {
        post("/notify_block_inserted") {
            val block = call.receive(Block::class)
            val senderNodePort = call.request.headers["PORT"]
            println("Notification from other node. It generated block $block")
            if (senderNodePort != null) {
                Controller.handleReceivedBlock(block, senderNodePort, notificationReceivedCallback)
            }
            call.respond(1)
        }
    }
}

//validate blockchain
fun Application.configureValidationReceiverRouting() {
    routing {
        get("/validate_blockchain") {
            call.respond(BlockChain.blockChain)
        }
    }
}

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respond(BlockChain.blockChain.map { listOf(it.index, it.prevHash, it.hash).joinToString(separator = " ") })
        }
    }
}