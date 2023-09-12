package com.example.ecrtool.models.trafficToPos

import com.example.ecrtool.models.trafficEcr.PrnData
import com.example.ecrtool.models.trafficEcr.ResultResponse
import com.example.ecrtool.models.trafficEcr.TransData

data class PaymentToPosResult(
    val success: Boolean = true,
    val code: String = "",
    val cardType: String = "", //O τύπος της κάρτας (Visa, Mastercard κλπ)
    val txnType: String = "", // Τύπος συναλλαγής: 00: αγορά 01: ακύρωση 02: επιστροφή 03: καταχώρηση προέγκρισης 04: mail order 05: αγορά με δόσεις
    val cardPanMasked: String = "", // Ο αριθμός της κάρτας με καλυμμένα τα ενδιάμεσα ψηφία.
    val amountFinal: Double = 0.0, // Μπορεί να διαφέρει από το amount σε περίπτωση loyalty με εξαργύρωση ή προσθήκης φιλοδωρήματος. Το πεδίο έχει πρόσημο συν αν πρόκειται για πληρωμή με κάρτα, ενώ έχει το πρόσημο μείον αν πρόκειται για επιστροφή σε κάρτα.
    val amountTip: Double = 0.0, // Ποσό φιλοδωρήματος
    val amountLoyalty: Double = 0.0, // Ποσό εξαργύρωσης loyalty
    val amountCashBack: Double = 0.0, // Ποσό cashback
    val bankId: String = "", // Kωδικός του παρόχου υπηρεσιών πληρωμών ή της τελικής τράπεζας εκκαθάρισης. Χρήσιμο σε διαμορφώσεις με περισσότερες τράπεζες εγκρίσεων (multiacquiring)
    val batchNum: String = "", // Ο αριθμός πακέτου στο EFTPOS
    val rrn: String = "", // Το rrn της συναλλαγής Σε κάποιες υλοποιήσεις μπορεί να είναι κενό στην περίπτωση offline σ/γών.
    val stan: String = "", // Ο αριθμός συναλλαγής στο EFTPOS,
    val authCode: String = "", // Ο κωδικός έγκρισης
    val transDateTime: String = "", // Hμ/νία και ώρα έγκρισης σε μορφή YYYYMMDDhhmmss
    val prnData: String = "",
    val receiptNumber: String = "",
    val sessionNumber: String = "",
    val amount: Double = 0.0,
    val ecrId: String = "",
    val rspCode: String = "",
    val transactionEcrStatus: String = "",
    val terminalId: String = "",
    val decimalPoints: Int,
    val customData: String

//Please check below to see how transactionEcrStatus is filled
//txn-ecr-status num 1 Χαρακτηρίζει τη συναλλαγή ως προς την
//επικοινωνία με την ECR
// 0 – έναρξη από ECR – επιτυχία
// 1 - έναρξη από ECR – αποτυχία στην
//ολοκλήρωση (αρχικό RESULT)
// 2 – έναρξη στο EFTPOS με χρήση
//στοιχείων προφορτωμένης ΑΛΠ/ΑΠΥ/
//τιμολογίου
// 3 – έναρξη στο EFTPOS με στοιχεία
//ΑΛΠ/ΑΠΥ/τιμολογίου τα οποία έχουν
//εντοπιστεί σε προγενέστερη εγγραφή
//20
//του EFTPOS.
// 4 – έναρξη από EFTPOS χωρίς
//εισαγωγή στοιχείων
//ΑΛΠ/ΑΠΥ/τιμολογίου (αντιστοιχεί σε
//βλάβη της ECR).
// 5 – Αδυναμία επικοινωνίας ΕCREFTPOS λόγω βλάβης των επιμέρους
//συστημάτων διασύνδεσης (βλάβη
//υποδομής)
)

fun PaymentToPosResult.toPrnData(): PrnData{
    return PrnData(data = this.prnData)
}

fun PaymentToPosResult.toTransData(): TransData{
    return TransData(
        cardType = this.cardType,
        txnType = this.txnType,
        cardPanMasked = this.cardPanMasked,
        amountFinal = this.amountFinal,
        amount = this.amount,
        amountTip = this.amountTip,
        amountLoyalty = this.amountLoyalty,
        amountCashBack = this.amountCashBack,
        bankId = this.bankId,
        terminalId = this.terminalId,
        batchNumber = this.batchNum,
        rrn = this.rrn,
        stan = this.stan,
        authCode = this.authCode,
        transactionDateTime = this.transDateTime,
        transactionEcrStatus = this.transactionEcrStatus.toString()
    )
}

fun PaymentToPosResult.toResultResponse(): ResultResponse {
    return ResultResponse(
        receiptNumber = this.receiptNumber,
        sessionNumber = this.sessionNumber,
        amount = this.amount,
        ecrId = this.ecrId,
        _rspCode = this.rspCode,
        transData = this.toTransData(),
        prnData = this.toPrnData(),
        decimals = this.decimalPoints,
        customData = this.customData
    )
}