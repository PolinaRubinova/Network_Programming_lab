package com.example

import com.example.models.Block
import com.example.models.BlockChain
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.gson.*

class Repo(private val client: HttpClient) {

    suspend fun notifyBlockInserted(block: Block, url: String): Boolean {
        val response = client.post(url) {
            contentType(ContentType.Application.Json)
            header("PORT", currentNodePort)
            setBody(block)
        }
        return response.bodyAsText() == "1"
    }

    suspend fun askThirdNode(url: String): Block {
        val response = client.get(url) {
            contentType(ContentType.Application.Json)
        }
        return response.body()
    }

    fun sendLastBlock(): Block {
        return BlockChain.blockChain.last()
    }

    suspend fun validateBlockChain(url: String): MutableList<Block> {
        val response = client.get(url) {
            contentType(ContentType.Application.Json)
        }
        return response.body()
    }

    fun insertBlock(block: Block) {
        BlockChain.blockChain.add(block)
    }

    companion object {
        private var instance: Repo? = null

        fun get(): Repo {
            if (instance == null) {
                instance = Repo(HttpClient(CIO) {
                    install(ContentNegotiation) {
                        gson()
                    }
                })
            }
            return instance!!
        }
    }
}