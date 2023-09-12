package com.example.ecrtool.utils

class Constants {

    companion object {
        const val TYPE_AMOUNT_SALE_REQUEST = "amount_sale_request"
        const val TYPE_AMOUNT_VOID_REQUEST = "amount_void_request"
        const val TYPE_AMOUNT_MAIL_REQUEST = "amount_mail_request"
        const val TYPE_AMOUNT_COMPLETION_REQUEST = "amount_completion_request"
        const val TYPE_AMOUNT_REFUND_REQUEST = "amount_refund_request"
        const val TYPE_AMOUNT_INSTALLMENTS_REQUEST = "amount_installments_request"

        // errors
        const val PROTOCOL_NOT_SUPPORTED = "protocol not supported"
        const val DUPLICATE_REQUEST = "duplicate request received"
        const val SYNTAX_ERROR = "Syntax error in request"
        const val INVALID_CURRENCY = "Invalid currency"
        const val INTERNAL_ERROR = "Internal EFTPOS error"
        const val INVALID_COMMAND = "Invalid command"
        const val WRONG_PARAMETER = "Wrong parameter"
        const val MISSING_MAC = "Missing MAC"
        const val MAC_ERROR = "MAC error"
        const val MAC_NOT_SUPPORTED = "MAC not supported"
        const val EFTPOS_NOT_CONNECTED = "EFTPOS not connected"
        const val BUSY = "BUSY"

        //commands
        const val UNBIND_POS = "UNBIND_POS"
        const val MAC_K = "MAC_K"

        //urls
        const val MK_URL = "https://www1.aade.gr/tameiakes/mysec/eftposmk.php/"


        //direction
        const val DIRECTION = "POS"

        //01: default λειτουργία (“variant 1”)
        //02: η ECR αναλαμβάνει την εκτύπωση της
        //απόδειξης του EFTPOS (“variant 2”)
        const val PROTOCOL_VARIANT = "01"

        //Καθορίζει τη σύνταξη στο σώμα του
        //μηνύματος. Για το προτεινόμενο πρωτόκολλο
        // η τιμή του είναι 10.
        const val PROTOCOL_VERSION = "10"

        // incoming messages
        //requests prefix
        const val ECR_ECHO_REQUEST_PREFIX = "X/"
        //amount ecr request
        const val ECR_AMOUNT_REQUEST_PREFIX = "A/"
        // ecr acknowledge result request
        const val ECR_ACK_RESULT_REQUEST_PREFIX  = "R/"
        // ecr regreceipt request (async payment)
        const val ECR_REGRECEIPT_REQUEST_PREFIX = "W/"
        // ecr resend one request
        const val ECR_RESEND_ONE_REQUEST_PREFIX = "O/"
        // ecr resend all request
        const val ECR_RESEND_ALL_REQUEST_PREFIX = "L/"
        // ecr control pos request
        const val ECR_CONTROL_REQUEST_PREFIX = "U/"
        // ecr amount-instalm request
        const val ECR_AMOUNT_INSTALM_REQUEST_PREFIX = "I/"
        // ecr amount refund request
        const val ECR_AMOUNT_REFUND_REQUEST_PREFIX = "Z/"
        // ecr amount void request
        const val ECR_AMOUNT_VOID_REQUEST_PREFIX = "V/"
        // ecr amount completion request
        const val ECR_AMOUNT_COMPLETION_REQUEST_PREFIX = "P/"
        // ecr amount mail request
        const val ECR_AMOUNT_MAIL_REQUEST_PREFIX = "M/"

        const val AADE_ENDPOINT_ESEND = "https//www1.aade.gr/tameiakes/mysec/eftposmk.php"
        const val POSTXN = "POSTXN"

        //outgoing messages
        // pos confirmed response
        const val POS_CONFIRM_RESPONSE_PREFIX = "A/"
        // pos result response
        const val POS_RESULT_CONFIRMATION_PREFIX = "R/"
        // pos error response
        const val POS_ERROR_RESPONSE_PREFIX = "E/"
        // pos response on success
        const val POS_SUCCESS_RESPONSE_PREFIX = "E/000"
        // pos amount-instalm confirmation
        const val POS_AMOUNT_INSTALM_CONFIRM_RESPONSE_PREFIX = "I/"
        // pos amount refund confirmation
        const val POS_AMOUNT_REFUND_RESPONSE_PREFIX = "Z/"
        // pos amount void confirmation
        const val POS_AMOUNT_VOID_CONFIRMATION_PREFIX = "V/"
        // pos amount completion response
        const val POS_AMOUNT_COMPLETION_RESPONSE_PREFIX = "P/"
        // pos amount mail response
        const val POS_AMOUNT_MAIL_CONFIRMATION_RESPONSE_PREFIX = "M/"

        val mappedErrors = mapOf(
            PROTOCOL_NOT_SUPPORTED to "001",
            DUPLICATE_REQUEST to "002",
            SYNTAX_ERROR to "003",
            INVALID_CURRENCY to "004",
            INTERNAL_ERROR to "100",
            INVALID_COMMAND to "500",
            WRONG_PARAMETER to "501",
            MISSING_MAC to "502",
            MAC_ERROR to "503",
            MAC_NOT_SUPPORTED to "504",
            EFTPOS_NOT_CONNECTED to "777",
            BUSY to "999"
        )


        val mappedTypes = listOf(
            ECR_ECHO_REQUEST_PREFIX,
            ECR_AMOUNT_REQUEST_PREFIX,
            ECR_ACK_RESULT_REQUEST_PREFIX,
            ECR_REGRECEIPT_REQUEST_PREFIX,
            ECR_RESEND_ONE_REQUEST_PREFIX,
            ECR_RESEND_ALL_REQUEST_PREFIX,
            ECR_CONTROL_REQUEST_PREFIX,
        )
    }




}

    val euCurrencySymbols = mapOf(
        978 to "€",
        826 to "£",
        975 to "лв",
        203 to "Kč",
        208 to "kr",
        191 to "kn",
        348 to "Ft",
        985 to "zł",
        946 to "lei",
        752 to "SEK",
        578 to "NOK",
        352 to "ISK",
        756 to "CHF"
    )

val responseCodes = mapOf(
    "00" to "Success",
    "03" to "User canceled or timeout",
    "04" to "Declined by terminal",
    "05" to "Declined by the host",
    "06" to "Communication problem",
    "09" to "Bank host unreachable",
    "33" to "Rejected",
    "66" to "System error in EFTPOS"
)