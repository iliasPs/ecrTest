package com.example.ecrtool.listeners

import com.example.ecrtool.models.trafficEcr.*

interface ProcessFlowsListener {

    suspend fun onAmountRequestReceived(amountRequest: AmountRequest)
    suspend fun onEchoRequestReceived(echoRequest: EchoRequest?)
    suspend fun onControlRequestReceived(controlRequest: ControlRequest)
    suspend fun onRefundRequestReceived(refundRequest: RefundRequest)
    suspend fun onResendRequestReceived(resendRequest: ResendRequest)
    suspend fun onResendAllRequestReceived(resendAllRequest: ResendAllRequest)
    suspend fun onRegReceiptRequestReceived(regReceiptRequest: RegReceiptRequest?)
    suspend fun onAckResultRequestReceived(ackResultRequest: AckResultRequest)

}