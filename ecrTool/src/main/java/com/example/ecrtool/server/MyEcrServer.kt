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
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

class MyEcrServer(myEcrEftposInit: MyEcrEftposInit) {

    private var listener: ProcessFlowsListener = ProcessFlow.getInstance()
    private var paymentResultListener: PaymentResultListener = ProcessFlow.getInstance()
    private var messageHandler = MessageHandler.getInstance()
    private var init = myEcrEftposInit
    private var serverSocket: ServerSocket? = null
    private val coroutineScope = myEcrEftposInit.coroutineScope ?: CoroutineScope(Dispatchers.IO)
    private var socket: Socket? = null
    private var isRunning = false
    private var job: Job? = null

    init {
        messageHandler.setListener(listener)
    }


    fun start() {

        if(serverSocket == null) {
            serverSocket = ServerSocket(init.port)
            serverSocket!!.reuseAddress = true
            serverSocket!!.bind(InetSocketAddress(init.port))
        }

        if (isRunning) {
            return
        }
        isRunning = true
        if (job != null && job!!.isActive) {
            Log.d("TAG", "start: job.isActive")

            return
        } else {
            job = coroutineScope.launch {
                try {
                    Log.d("TAG", "start: NEW job.")

                    while (isRunning) {
                        socket = withContext(Dispatchers.IO) {
                            if (serverSocket!!.isClosed) {
                                serverSocket = ServerSocket(init.port)
                            }
                            serverSocket!!.accept()
                        }
                        handleConnection(socket)
                    }
                } catch (e: java.lang.Exception) {
                    Log.d("TAG", "start: cactching ${e.message}")
                }
            }
            job?.start()
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
        isRunning = false
        job?.cancelChildren()
        serverSocket?.close()
    }

    fun sendMessage(message: ByteArray) {
        Logger.logToFile(MessageHandler.getInstance().formatHexDump(message, 0, message.size))

        coroutineScope.launch(Dispatchers.IO) {
            val outputStream = socket?.getOutputStream()
            outputStream?.write(message, 0, message.size)
            outputStream?.flush()
        }
    }

}