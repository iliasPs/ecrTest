package com.example.ecrtool.listeners

interface MessageListener {
    fun onMessage(message: String)
    fun sendMessage(message: String)
}
