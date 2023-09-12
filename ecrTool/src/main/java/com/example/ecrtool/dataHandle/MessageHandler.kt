package com.example.ecrtool.dataHandle

import android.util.Log
import com.example.ecrtool.appData.AppData
import com.example.ecrtool.listeners.MessageListener
import com.example.ecrtool.listeners.PaymentResultListener
import com.example.ecrtool.listeners.ProcessFlowsListener
import com.example.ecrtool.models.trafficToPos.PaymentToPosResult
import com.example.ecrtool.server.MyEcrServer
import com.example.ecrtool.server.MyEcrServerSingleton
import com.example.ecrtool.utils.Constants
import com.example.ecrtool.utils.Constants.Companion.ECR_ACK_RESULT_REQUEST_PREFIX
import com.example.ecrtool.utils.Constants.Companion.ECR_AMOUNT_COMPLETION_REQUEST_PREFIX
import com.example.ecrtool.utils.Constants.Companion.ECR_AMOUNT_INSTALM_REQUEST_PREFIX
import com.example.ecrtool.utils.Constants.Companion.ECR_AMOUNT_MAIL_REQUEST_PREFIX
import com.example.ecrtool.utils.Constants.Companion.ECR_AMOUNT_REFUND_REQUEST_PREFIX
import com.example.ecrtool.utils.Constants.Companion.ECR_AMOUNT_REQUEST_PREFIX
import com.example.ecrtool.utils.Constants.Companion.ECR_AMOUNT_VOID_REQUEST_PREFIX
import com.example.ecrtool.utils.Constants.Companion.ECR_CONTROL_REQUEST_PREFIX
import com.example.ecrtool.utils.Constants.Companion.ECR_ECHO_REQUEST_PREFIX
import com.example.ecrtool.utils.Constants.Companion.ECR_REGRECEIPT_REQUEST_PREFIX
import com.example.ecrtool.utils.Constants.Companion.ECR_RESEND_ALL_REQUEST_PREFIX
import com.example.ecrtool.utils.Constants.Companion.ECR_RESEND_ONE_REQUEST_PREFIX
import com.example.ecrtool.utils.Constants.Companion.mappedTypes
import com.example.ecrtool.utils.Logger
import com.example.ecrtool.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.UnsupportedEncodingException


class MessageHandler private constructor() : MessageListener {
    // IMPORTANT: DO NOT change it to variable initialization. It will throw IllegalStateException
    private val server: MyEcrServer by lazy { MyEcrServerSingleton.getInstance() }
    private val dt = DataTransformer.getInstance()
    private var listener: ProcessFlowsListener? = null

    companion object {
        private var instance: MessageHandler? = null

        fun getInstance(): MessageHandler {
            if (instance == null) {
                instance = MessageHandler()
            }
            return instance!!
        }
    }

    fun setListener(listener: ProcessFlowsListener) {
        this.listener = listener
    }

    /**
     * This method handles incoming messages and delegates their parsing to the corresponding DataTransformer
     * methods based on the message prefix. If the prefix is not recognized, the message is treated as an unknown
     * message type.
     * @param message the incoming message to be handled
     * @throws IllegalArgumentException if the incoming message is empty or has an unrecognized prefix
     */
    private suspend fun handleRequest(message: String) {
        for (prefix in mappedTypes) {
            if (message.startsWith(prefix, 0, true)) {
                when (prefix) {
                    ECR_ECHO_REQUEST_PREFIX -> listener?.onEchoRequestReceived(dt.parseEchoRequest(message))
                    ECR_AMOUNT_REQUEST_PREFIX -> listener?.onAmountRequestReceived(dt.parseAmountRequest(message, Constants.TYPE_AMOUNT_SALE_REQUEST))
                    ECR_ACK_RESULT_REQUEST_PREFIX -> listener?.onAckResultRequestReceived(dt.parseAckResultRequest(message))
                    ECR_REGRECEIPT_REQUEST_PREFIX -> listener?.onRegReceiptRequestReceived(dt.parseRegReceiptRequest(message))
                    ECR_RESEND_ONE_REQUEST_PREFIX -> listener?.onResendRequestReceived(dt.parseResendRequest(message))
                    ECR_RESEND_ALL_REQUEST_PREFIX -> listener?.onResendAllRequestReceived(dt.parseResendAllRequest(message))
                    ECR_CONTROL_REQUEST_PREFIX -> listener?.onControlRequestReceived(dt.parseControlRequest(message))
                    ECR_AMOUNT_REFUND_REQUEST_PREFIX -> listener?.onAmountRequestReceived(dt.parseAmountRequest(message, Constants.TYPE_AMOUNT_REFUND_REQUEST))
                    ECR_AMOUNT_VOID_REQUEST_PREFIX -> listener?.onAmountRequestReceived(dt.parseAmountRequest(message, Constants.TYPE_AMOUNT_VOID_REQUEST))
                    ECR_AMOUNT_INSTALM_REQUEST_PREFIX -> listener?.onAmountRequestReceived(dt.parseAmountRequest(message, Constants.TYPE_AMOUNT_SALE_REQUEST))
                    ECR_AMOUNT_COMPLETION_REQUEST_PREFIX -> listener?.onAmountRequestReceived(dt.parseAmountRequest(message, Constants.TYPE_AMOUNT_COMPLETION_REQUEST))
                    ECR_AMOUNT_MAIL_REQUEST_PREFIX -> listener?.onAmountRequestReceived(dt.parseAmountRequest(message, Constants.TYPE_AMOUNT_MAIL_REQUEST))
                }
                return
            }
        }

        // Unknown message type
    }

    private fun handleRequests(message: String) {
        val scope = CoroutineScope(context = Dispatchers.IO)
        scope.launch {
            mappedTypes.forEach {
                /**
                 * An incoming (production) message can be like the following:
                 * ??ECR0110X/CFB77000028
                 * The first two bytes (??DC4) is the length whereas the following is the other part of the header.
                 * In order to recognizethe message of the type we must check from the 9 index and after.
                 * Maybe, we sould first parse the header in order to fill the BaseModel variables
                 */
                if (message.startsWith(it, 0, true)) {
                    handleRequest(message)
                    return@launch
                }
            }
        }
    }

    override fun onMessage(message: String) {
        Logger.logToFile("FROM ECR: $message")
        val bytes = message.encodeToByteArray()
        Log.d("MessageHandler", "------- FROM ECR -------" )

        Log.d("MessageHandler", formatHexDump(bytes, 0, bytes.size))

        val cleanMessage = Utils.extractMessage(message)

        if(Utils.validateMk(cleanMessage)) {
            handleRequests(cleanMessage)
        } else dt.createErrorResponseMessage(Constants.MAC_ERROR)
    }

    private fun formatHexDump(array: ByteArray, offset: Int, length: Int): String {
        val width = 16
        val builder = StringBuilder()
        var rowOffset = offset
        while (rowOffset < offset + length) {
            builder.append(String.format("%06d:  ", rowOffset))
            for (index in 0 until width) {
                if (rowOffset + index < array.size) {
                    builder.append(String.format("%02x ", array[rowOffset + index]))
                } else {
                    builder.append("   ")
                }
            }
            if (rowOffset < array.size) {
                val asciiWidth = Math.min(width, array.size - rowOffset)
                builder.append("  |  ")
                try {
                    builder.append(
                        String(
                            array,
                            rowOffset,
                            asciiWidth,
                            charset("US-ASCII")
                        ).replace("[^\\x20-\\x7E]".toRegex(), ".")
                    )
                } catch (ignored: UnsupportedEncodingException) {
                    //If UTF-8 isn't available as an encoding then what can we do?!
                }
            }
            builder.append(String.format("%n"))
            rowOffset += width
        }
        return builder.toString()
    }

    override fun sendMessage(message: String) {
        val finalMessage = Utils.generateMessage(message)
        Log.d("TAG", "sendMessage: $finalMessage")
        val bytes = message.encodeToByteArray()
        Log.d("MessageHandler", "------- FROM POS -------" )
        Log.d("MessageHandler", formatHexDump(bytes, 0, bytes.size))
        server.sendMessage(finalMessage)
    }
}