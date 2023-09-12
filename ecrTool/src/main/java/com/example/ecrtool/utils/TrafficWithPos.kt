package com.example.ecrtool.utils

import com.example.ecrtool.models.trafficEcr.AmountRequest
import com.example.ecrtool.models.trafficToPos.PaymentToPosRequest

class TrafficWithPos {

    companion object {
        private var instance: TrafficWithPos? = null

        fun getInstance(): TrafficWithPos {
            if (instance == null) {
                instance = TrafficWithPos()
            }
            return instance!!
        }
    }

    suspend fun sendPayment(paymentToPosRequest: AmountRequest) {
        //TODO("send async? to app)
    }

    suspend fun getPaymentResult() {}

    fun setPaymentResult() {}
}