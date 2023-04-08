package com.example

import com.example.models.Block
import com.example.models.BlockChain
import com.example.models.BlockChain.sha256Hash
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object Controller {
    private val url1 = "http://127.0.0.1:$node1Port"
    private val url2 = "http://127.0.0.1:$node2Port"

    suspend fun validateBlockChain(): Boolean {
        val requestedBlockChain = Repo.get().validateBlockChain("$url1/validate_blockchain")
        return if (requestedBlockChain == BlockChain.blockChain) {
            true
        } else {
            BlockChain.blockChain = requestedBlockChain
            false
        }
    }

    suspend fun generateNewBlock(): Block? {
        var gotBlock = false
        val prevBlock = BlockChain.blockChain.lastOrNull()
        if (prevBlock == null) {
            Repo.get().validateBlockChain("$url1/validate_blockchain")
            return null
        }
        val currentBlock = Block(BlockChain.blockChain.size, prevBlock.hash, "", generateBlockData(), 0)
        while (!gotBlock) {
            val hashInput = currentBlock.index.toString() +
                    currentBlock.prevHash +
                    currentBlock.data +
                    currentBlock.nonce.toString()
            val hashOutput = sha256Hash(hashInput)
            if (hashOutput.takeLast(6) == "000000") {
                gotBlock = true
                currentBlock.hash = hashOutput
                handleReceivedBlock(currentBlock, currentNodePort)
                withContext(Dispatchers.IO) {
                    try {
                        Repo.get().notifyBlockInserted(currentBlock, "$url1/notify_block_inserted")
                        Repo.get().notifyBlockInserted(currentBlock, "$url2/notify_block_inserted")
                    } catch (_: Exception) {}
                }
            } else currentBlock.nonce++
        }
        return currentBlock
    }

    suspend fun handleReceivedBlock(
        block: Block,
        senderNodePort: String,
        notificationReceivedCallback: NotificationReceivedCallback? = null
    ) {
        val lastBlock = BlockChain.blockChain.last()
        if (block.index == lastBlock.index + 1 && block.prevHash == lastBlock.hash) { // got a valid block
            Repo.get().insertBlock(block)
            notificationReceivedCallback?.onNotificationReceived()
        } else if (senderNodePort != currentNodePort) { // if node is in minority or sender is in minority (IF SENDER IS CURRENT NODE JUST SKIP)
            val askUrl = if (senderNodePort == node1Port) "$url2/ask_third_node" else "$url1/ask_third_node"
            val thirdNodeBlock = Repo.get().askThirdNode(askUrl)
            if (thirdNodeBlock.hash == block.hash) { // => our node is in minority
                val validateUrl = if (senderNodePort == node1Port) "$url2/validate_blockchain" else "$url1/validate_blockchain"
                Repo.get().validateBlockChain(validateUrl)
            }
        }
    }

    fun sendLastBlock(): Block {
        return Repo.get().sendLastBlock()
    }

    private fun generateBlockData(): String {
        return generateRandomData()
    }
}

interface NotificationReceivedCallback {
    fun onNotificationReceived()
}