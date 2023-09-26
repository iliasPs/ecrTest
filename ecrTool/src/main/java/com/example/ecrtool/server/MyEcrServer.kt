package com.example.ecrtool.server

import android.util.Log
import com.example.ecrtool.dataHandle.MessageHandler
import com.example.ecrtool.dataHandle.ProcessFlow
import com.example.ecrtool.listeners.PaymentResultListener
import com.example.ecrtool.listeners.ProcessFlowsListener
import com.example.ecrtool.models.trafficToPos.MyEcrEftposInit
import com.example.ecrtool.models.trafficToPos.PaymentToPosResult
import com.example.ecrtool.utils.Logger
import kotlinx.coroutines.*
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket

class MyEcrServer(myEcrEftposInit: MyEcrEftposInit) {

    private var listener: ProcessFlowsListener = ProcessFlow.getInstance()
    private var paymentResultListener: PaymentResultListener = ProcessFlow.getInstance()
    private var messageHandler = MessageHandler.getInstance()
    private val serverSocket = ServerSocket(myEcrEftposInit.port)
    private val coroutineScope = myEcrEftposInit.coroutineScope ?: CoroutineScope(Dispatchers.IO)
    private var socket: Socket? = null
    private var isRunning = false

    init {
        messageHandler.setListener(listener)
    }


    fun start() {
        if (isRunning) {
            return
        }
        isRunning = true
        coroutineScope.launch {
            try {
                while (isRunning) {
                    socket = withContext(Dispatchers.IO) {
                        serverSocket.accept()
                    }
                    handleConnection(socket)
                }
            }catch (e: java.lang.Exception){
                Log.d("TAG", "start: cactching")
            }
        }
    }

    private fun handleConnection(socket: Socket?) {
        coroutineScope.launch {
            val inputStream = withContext(Dispatchers.IO) {
                socket?.getInputStream()
            }
            val byteArray = ByteArray(1024) // Adjust the buffer size as needed

            try {
                while (true) {
                    val bytesRead = withContext(Dispatchers.IO) {
                        inputStream?.read(byteArray)
                    }
                    if (bytesRead != -1) {
                        val message = bytesRead?.let { byteArray.copyOf(it).decodeToString() }
                        if (message != null) {
                            onMessageReceived(message)
                        }
                    } else {
                        break
                    }
                }
            } catch (e: IOException) {
                Logger.logToFile(e.stackTrace.firstOrNull()?.methodName + e.message)
                e.printStackTrace()
            } finally {
                withContext(Dispatchers.IO) {
                    socket?.close()
                }
            }
        }
    }

    fun sendMessageToEcr(message: Any) {
        if (message is PaymentToPosResult) {
            paymentResultListener.paymentResultReceived(message)
        }
    }

    fun onMessageReceived(message: String) {
        messageHandler.onMessage(message)
    }

    fun disconnect() {
        socket?.close()
        socket = null
    }

    fun stop() {
        isRunning =false
        coroutineScope.coroutineContext.cancelChildren()
        serverSocket.close()
    }

    fun sendMessage(message: String) {
        coroutineScope.launch(Dispatchers.IO) {
            val writer = socket?.getOutputStream()?.writer()
            Log.d("TAG", "getOutputStream: $message")

            writer?.write(message)
            writer?.flush()
        }
    }

}