package com.example.ecrtool.models.trafficEcr

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.ecrtool.utils.Constants
import com.fasterxml.jackson.annotation.JsonFormat
import org.valiktor.functions.hasSize
import org.valiktor.functions.isNotBlank
import org.valiktor.functions.matches
import org.valiktor.validate
import java.time.LocalDateTime

@Entity(tableName = "amount_request")
data class AmountRequest(
    val receiptNumber: String,
    @PrimaryKey
    val sessionNumber: String,
    val amount: Double,
    val currencyCode: Int,
    val currencySymbol: String,
    val decimals: Int,
    @JsonFormat(pattern = "yyyyMMddHHmmss")
    val dateTime: LocalDateTime, // YYYYMMDDhhmmss
    val ecrId: String,
    val operatorNumber: String,
    val customData: String,
    val mac: String,
    var completed: Boolean = false,
    val type: String = Constants.TYPE_AMOUNT_SALE_REQUEST,
    val isInstallments: Boolean = false
): BaseModel()
//{
//    init {
//        val alphanumericRegex: Regex = Regex("[a-zA-Z0-9]+")
//
//        validate(this) {
//            validate(AmountRequest::receiptNumber).hasSize(min = 1, max =8)
//            validate(AmountRequest::sessionNumber).matches(alphanumericRegex).isNotBlank()
//        }
//    }
//}


