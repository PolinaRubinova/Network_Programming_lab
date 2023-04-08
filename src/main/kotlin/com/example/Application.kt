package com.example

import com.example.models.BlockChain
import com.example.plugins.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import kotlinx.coroutines.*
import java.net.ConnectException
import kotlin.random.Random

val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
const val STRING_LENGTH = 256
var node1Port = ""
var node2Port = ""
var currentNodePort = ""
var mainNode = false

/*
 args:
 [0] - current node port
 [1] - first node port
 [2] - second node port
 [3] - is current node main or not ("1" - main, "0" - secondary)
 */

fun main(args: Array<String>) {
    currentNodePort = args[0]
    node1Port = args[1]
    node2Port = args[2]
    mainNode = args[3] == "1"
    embeddedServer(CIO, port = currentNodePort.toInt(), host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()

    if (mainNode) {
        BlockChain.generateGenesis()
    }

    configureAskThirdNodeReceiverRouting()
    configureRouting()
    configureValidationReceiverRouting()
    var job = if (mainNode) {
        startMining(this)
    } else { // if current node isn't main it just asks for actual blockChain every 0.5 second until gets it
        waitForMainNode(this)
    }
    configureNotificationReceiverRouting(object : NotificationReceivedCallback {
        override fun onNotificationReceived() {
            println("Notification received => restarting coroutine and mining")
            job.cancel()
            job = startMining(this@module)
        }
    })
}

private fun startMining(coroutineScope: CoroutineScope): Job {
    return coroutineScope.launch(Dispatchers.Default) {
        while (true) Controller.generateNewBlock()
    }
}

private fun waitForMainNode(coroutineScope: CoroutineScope): Job {
    return coroutineScope.launch(Dispatchers.IO) {
        while (BlockChain.blockChain.isEmpty()) {
            try {
                Controller.validateBlockChain()
            } catch (_: ConnectException) {
            }
            delay(500)
        }
        cancel()
        startMining(coroutineScope)
    }
}

fun generateRandomData(): String {
    return (1..STRING_LENGTH)
        .map { Random.nextInt(0, charPool.size).let { charPool[it] } }
        .joinToString("")
}