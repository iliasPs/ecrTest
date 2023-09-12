package com.example.ecrtool.listeners

import com.example.ecrtool.models.trafficToPos.PaymentToPosResult

interface PaymentResultListener {
    fun paymentResultReceived(paymentToPosResult: PaymentToPosResult)
}