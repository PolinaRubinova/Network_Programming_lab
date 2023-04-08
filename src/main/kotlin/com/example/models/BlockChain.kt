package com.example.models

import com.example.mainNode
import java.security.MessageDigest
import javax.xml.bind.DatatypeConverter

object BlockChain {
    var blockChain: MutableList<Block> = mutableListOf()

    fun generateGenesis(): Boolean {
        if (mainNode) {
            blockChain.add(
                Block(0, "", sha256Hash("Genesis").dropLast(6).plus("000000"), "Genesis", 0)
            )
        }
        return mainNode
    }

    fun sha256Hash(input: String): String {
        val bytes = MessageDigest
            .getInstance("SHA-256")
            .digest(input.toByteArray())
        return DatatypeConverter.printHexBinary(bytes).uppercase()
    }
}

