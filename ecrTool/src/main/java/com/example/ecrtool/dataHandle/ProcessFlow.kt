package com.example.ecrtool.dataHandle

import android.util.Log
import com.example.ecrtool.appData.AppData
import com.example.ecrtool.db.DbHandler
import com.example.ecrtool.listeners.PaymentResultListener
import com.example.ecrtool.listeners.ProcessFlowsListener
import com.example.ecrtool.models.mk.MkRequest
import com.example.ecrtool.models.trafficEcr.*
import com.example.ecrtool.models.trafficToPos.PaymentToPosResult
import com.example.ecrtool.models.trafficToPos.toResultResponse
import com.example.ecrtool.network.result.DataResult
import com.example.ecrtool.network.useCase.MkUseCase
import com.example.ecrtool.utils.Constants
import com.example.ecrtool.utils.Constants.Companion.DUPLICATE_REQUEST
import com.example.ecrtool.utils.Constants.Companion.INTERNAL_ERROR
import com.example.ecrtool.utils.Constants.Companion.INVALID_COMMAND
import com.example.ecrtool.utils.Constants.Companion.INVALID_CURRENCY
import com.example.ecrtool.utils.Constants.Companion.MAC_ERROR
import com.example.ecrtool.utils.Constants.Companion.MAC_K
import com.example.ecrtool.utils.Constants.Companion.MAC_NOT_SUPPORTED
import com.example.ecrtool.utils.Constants.Companion.UNBIND_POS
import com.example.ecrtool.utils.Constants.Companion.mappedErrors
import com.example.ecrtool.utils.Logger
import com.example.ecrtool.utils.Utils
import com.example.ecrtool.utils.Utils.hexStringToByteArray
import com.example.ecrtool.utils.Utils.toHexString
import com.example.ecrtool.utils.euCurrencySymbols
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ProcessFlow : ProcessFlowsListener, PaymentResultListener, KoinComponent {

    private val dbHandler = DbHandler.getInstance()
    private val dt = DataTransformer.getInstance()
    private val messageHandler = MessageHandler.getInstance()
    private var appListener = AppData.getMyEcrEftposInit()?.appListener

    private val mkUseCase: MkUseCase by inject()

    companion object {
        private var instance: ProcessFlow? = null

        fun getInstance(): ProcessFlow {
            if (instance == null) {
                instance = ProcessFlow()
            }
            return instance!!
        }
    }


    override suspend fun onAmountRequestReceived(amountRequest: AmountRequest) {
        appListener = AppData.getMyEcrEftposInit()?.appListener
        when (amountRequest.type) {
            Constants.TYPE_AMOUNT_MAIL_REQUEST,
            Constants.TYPE_AMOUNT_COMPLETION_REQUEST,
            Constants.TYPE_AMOUNT_VOID_REQUEST,
            Constants.TYPE_AMOUNT_REFUND_REQUEST,
            Constants.TYPE_AMOUNT_INSTALLMENTS_REQUEST -> appListener?.sendToApp(
                amountRequest
            )
            else -> {
                if (Utils.validateRequest(amountRequest)) {
                    if (dbHandler.getAmountRequestBySessionNumber(amountRequest.sessionNumber) == null) {
                        dbHandler.insertAmountRequest(amountRequest)
                        val confirmationResponse = ConfirmationResponse(
                            receiptNumber = amountRequest.receiptNumber,
                            sessionNumber = amountRequest.sessionNumber,
                            amount = amountRequest.amount,
                            ecrId = amountRequest.ecrId,
                            decimals = amountRequest.decimals
                        )

                        messageHandler.sendMessage(
                            dt.createConfirmationResponse(
                                confirmationResponse
                            )
                        )

                        dbHandler.insertConfirmationResponse(confirmationResponse)

                        if (!euCurrencySymbols.contains(amountRequest.currencyCode)) {
                            messageHandler.sendMessage(dt.createErrorResponseMessage(mappedErrors[INVALID_CURRENCY].toString()))
                            Logger.logToFile(INVALID_CURRENCY + " " + amountRequest.currencyCode)
                            return
                        }

                        // send amount request for payment
                        requestPayment(amountRequest)

                    } else {
                        // amountRequest already exists
                        messageHandler.sendMessage(dt.createErrorResponseMessage(mappedErrors[DUPLICATE_REQUEST].toString()))
                        Logger.logToFile(DUPLICATE_REQUEST + " " + amountRequest.sessionNumber)
                    }
                }
            }
        }
    }

    override suspend fun onEchoRequestReceived(echoRequest: EchoRequest?) {

        echoRequest?.ecrNumber.let {
            if (it != null) {
                AppData.setEcrNumber(it)
            }
        }

        if (echoRequest != null) {
            if (echoRequest.isInit) {
                delay(5000)
                callAade()
            }
        }

        val echoResponse = dt.createEchoResponseMessage(
            EchoResponse(
                text = echoRequest?.text,
                terminalId = AppData.getTerminalId(),
                appVersion = AppData.getAppVersion(),
                isInit = echoRequest?.isInit ?: false
            )
        )
        messageHandler.sendMessage(echoResponse)
    }

    override suspend fun onControlRequestReceived(controlRequest: ControlRequest) {
        if (Utils.validateRequest(controlRequest)) {
            if (controlRequest.commandName == UNBIND_POS) {
                when (controlRequest.commandName) {
                    "0" -> {
                        messageHandler.sendMessage(dt.createSuccessResultMessage())
                    }//todo tell app to lock keyboard
                    "1" -> {
                        messageHandler.sendMessage(dt.createSuccessResultMessage())
                    }//ecr can complete transactions on its own -> Disconnect eftpos??
                }
            }

            if (controlRequest.commandName == MAC_K) {
                if (controlRequest.parameterValue != null) {
                    Log.d("ProcessFlow", controlRequest.parameterValue)
                    try {
                        AppData.setEcrSK(controlRequest.parameterValue)
                        val localSk = handleSessionKey()
                        if (localSk != null) {
                            AppData.setLocalSk(localSk)
                            messageHandler.sendMessage(dt.createSuccessResultMessage())
                        }
                    } catch (e: Exception) {
                        Logger.logToFile(e.stackTrace.firstOrNull()?.methodName + e.message)
                        e.printStackTrace()
                        messageHandler.sendMessage(dt.createErrorResponseMessage(mappedErrors[MAC_ERROR].toString()))
                    }
                }
            }
        } else {
            val notSupportedMessage = dt.createErrorResponseMessage(INVALID_COMMAND)
            Logger.logToFile(INVALID_COMMAND + " " + controlRequest.commandName)
            messageHandler.sendMessage(notSupportedMessage)
        }
    }

    override suspend fun onRefundRequestReceived(refundRequest: RefundRequest) {
        TODO("send to app")
    }

    override suspend fun onResendRequestReceived(resendRequest: ResendRequest) {
        if (Utils.validateRequest(resendRequest)) {
            val amountRequest = resendRequest.sessionNumber?.let {
                dbHandler.getAmountRequestBySessionNumber(
                    it
                )
            }

            val resultToSend = amountRequest?.sessionNumber?.let {
                dbHandler.getResultResponseBySessionNumber(
                    it
                )
            }

            if (resultToSend != null) {
                messageHandler.sendMessage(dt.createResultResponse(resultToSend, true))
            } else {
                messageHandler.sendMessage(
                    dt.createErrorResponseMessage(
                        mappedErrors[INTERNAL_ERROR].toString()
                    )
                )
            }

        }
    }

    /**
     * Handles the reception and processing of resend requests for all result responses.
     *
     * This method processes resend requests by iterating through all result responses and incomplete
     * amount requests. It sends the result responses that match the session numbers of incomplete
     * amount requests and waits for acknowledgment before proceeding to the next item.
     *
     * @param resendAllRequest The resend request triggering the processing.
     */
    override suspend fun onResendAllRequestReceived(resendAllRequest: ResendAllRequest) {
        val ackResultRequestChannel = AppData.getAckResultRequestChannel()
        val allResultsResponses = dbHandler.getAllResultResponses()

        while (true) {
            if (Utils.validateRequest(resendAllRequest)) {
                // Move this inside the loop to update it in each iteration
                val incompleteAmountRequests =
                    dbHandler.getAllAmountRequests().filter { !it.completed }.toMutableList()

                val matchedItems = mutableListOf<ResultResponse>()

                for (resultResponse in allResultsResponses) {
                    for (amountRequest in incompleteAmountRequests) {
                        if (amountRequest.sessionNumber == resultResponse.sessionNumber) {
                            matchedItems.add(resultResponse)
                        }
                    }
                }

                if (matchedItems.isNotEmpty()) {
                    val resultResponse = matchedItems[0]
                    messageHandler.sendMessage(
                        dt.createResultResponse(
                            resultResponse,
                            false
                        )
                    )
                    val ackResultRequest = awaitAckResultRequest(ackResultRequestChannel)
                    ackResultRequest?.sessionNumber?.let {
                        markAmountRequestAsComplete(it)
                    }
                    matchedItems.remove(resultResponse) // Remove the processed item

                    // Update incompleteAmountRequests after processing an item
                    incompleteAmountRequests.removeAll { it.sessionNumber == resultResponse.sessionNumber }
                } else {
                    // No more items to process, break the loop
                    break
                }
            }
        }
    }


    /**
     * Awaits an acknowledgment result request from the provided channel.
     *
     * @param channel The channel to receive acknowledgment requests from.
     * @return The received acknowledgment result request, or null if the channel is canceled.
     */
    private suspend fun awaitAckResultRequest(channel: ReceiveChannel<AckResultRequest>): AckResultRequest? {
        return try {
            channel.receive()
        } catch (e: CancellationException) {
            null // Handle cancellation if needed
        }
    }


    private suspend fun markAmountRequestAsComplete(sessionNumber: String) {
        val amountRequest = dbHandler.getAmountRequestBySessionNumber(sessionNumber)
        if (amountRequest != null) {
            amountRequest.completed = true
            dbHandler.updateAmountRequest(amountRequest)
        }
    }

    override suspend fun onRegReceiptRequestReceived(regReceiptRequest: RegReceiptRequest?) {
        if (regReceiptRequest != null) {
            if (Utils.validateRequest(regReceiptRequest)) {
                dbHandler.insertRegReceipt(regReceiptRequest)
                messageHandler.sendMessage(dt.createSuccessResultMessage())
            } else {
                val notSupportedMessage = dt.createErrorResponseMessage(INVALID_COMMAND)
                messageHandler.sendMessage(notSupportedMessage)
            }
        }
    }

    override suspend fun onAckResultRequestReceived(ackResultRequest: AckResultRequest) {

        val ackResultRequestChannel = AppData.getAckResultRequestChannel()
        ackResultRequestChannel.send(ackResultRequest)

        val amountRequest = ackResultRequest.sessionNumber?.let {
            dbHandler.getAmountRequestBySessionNumber(
                it
            )
        }
        amountRequest?.sessionNumber?.let { markAmountRequestAsComplete(it) }
    }


    private fun requestPayment(paymentToPosRequest: AmountRequest) {
        //sending the complete request - custom data field may include payment codes etc
        appListener?.sendToApp(paymentToPosRequest)
    }

    private fun processPaymentResult(result: ResultResponse) {
        when (result.rspCode) {
            "00" -> {
                messageHandler.sendMessage(dt.createResultResponse(result, true))
                CoroutineScope(Dispatchers.IO).launch {
                    dbHandler.insertResult(result)
                }
            }
            "33", "03", "04", "05", "06", "09", "66" -> messageHandler.sendMessage(
                dt.createErrorResponseMessage(
                    mappedErrors[INTERNAL_ERROR].toString()
                )
            )
        }
    }

    private fun handleSessionKey(): String? {
        val mk = AppData.getMk()

        return if (AppData.getEcrSK().isNotEmpty()) {
            val decoded =
                Utils.decodeSessionKey(
                    mk.hexStringToByteArray(),
                    AppData.getEcrSK().hexStringToByteArray()
                )
            if (decoded == null) {
                messageHandler.sendMessage(dt.createErrorResponseMessage(mappedErrors[MAC_ERROR].toString()))
                null
            } else {
                decoded.toHexString()
            }
        } else {
            messageHandler.sendMessage(dt.createErrorResponseMessage(mappedErrors[MAC_NOT_SUPPORTED].toString()))
            Logger.logToFile("ECR SK IS EMPTY - NEED RELEVANT CONTROL REQUEST TO SET IT")
            null
        }
    }

    suspend fun callAade() {
        val scope = AppData.getMyEcrEftposInit()?.coroutineScope ?: CoroutineScope(Dispatchers.IO)
        val mkRequest = createMkRequest()
        scope.async {
            when (val mkResult = mkRequest.let { mkUseCase.invoke(it) }) {
                is DataResult.Success -> {
                    AppData.setMK(mkResult.data)
                    Logger.logToFile("callAade: AADE CALL SUCCESS")
                    Log.d(
                        ProcessFlow::class.java.name,
                        "callAade: AADE CALL SUCCESS"
                    )
                }
                is DataResult.Error -> {
                    Log.e(
                        ProcessFlow::class.java.name,
                        "callAade: ERROR WHILE REQUESTING MK",
                        mkResult.cause
                    )
                }
            }
        }.await()
    }

    private fun createMkRequest(): MkRequest {
        return MkRequest(
            tId = AppData.getTerminalId(),
            EcrId = "DLF77000016",
            afm = AppData.getVatNumber(),
            man = AppData.getMAN(),
            apiKey = AppData.getApiKey()
        )
    }

    override fun paymentResultReceived(paymentToPosResult: PaymentToPosResult) {
        processPaymentResult(paymentToPosResult.toResultResponse())
    }
}