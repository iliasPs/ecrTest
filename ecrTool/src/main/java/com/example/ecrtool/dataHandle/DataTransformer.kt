package com.example.ecrtool.dataHandle

import com.example.ecrtool.appData.AppData
import com.example.ecrtool.models.*
import com.example.ecrtool.models.trafficEcr.*
import com.example.ecrtool.utils.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.regex.PatternSyntaxException
import kotlin.math.pow

class DataTransformer {

    companion object {
        private var instance: DataTransformer? = null

        fun getInstance(): DataTransformer {
            if (instance == null) {
                instance = DataTransformer()
            }
            return instance!!
        }
    }

    /**
     * Parses an input message to extract the contents of an EchoRequest.
     *
     * The input message should be in the format "X/&lt;text&gt;" or "X/&lt;text&gt;:&lt;ecrNumber&gt;".
     * The method extracts the "text" and "ecrNumber" (if present) from the input message and creates
     * an EchoRequest object with the extracted data.
     *
     * @param message The input message to parse.
     * @return An EchoRequest object containing the extracted "text" and "ecrNumber" (if present).
     *         Returns null if the input message does not match any of the expected formats.
     * @throws IllegalArgumentException If the input message is null or empty.
     * @throws PatternSyntaxException If there is an error in the regular expression patterns used for parsing.
     */
    fun parseEchoRequest(message: String): EchoRequest? {
        if (message.isEmpty()) {
            throw IllegalArgumentException("message must not be null or empty")
        }

        val regexWithEcr = try {
            Regex("""/([^/:]+)(?::(\w+))?""")
        } catch (e: PatternSyntaxException) {
            Logger.logToFile(e.stackTrace.firstOrNull()?.methodName + e.message)
            throw PatternSyntaxException("invalid regular expression", e.pattern, e.index)
        }

        val regexWithoutEcr = try {
            Regex("/([^/]+)$")
        } catch (e: PatternSyntaxException) {
            Logger.logToFile(e.stackTrace.firstOrNull()?.methodName + e.message)
            throw PatternSyntaxException("invalid  regular expression", e.pattern, e.index)
        }

        val matchWithEcr = regexWithEcr.find(message)
        val matchWithoutEcr = regexWithoutEcr.find(message)

        if (matchWithEcr != null) {
            val text = matchWithEcr.groupValues[1]
            val ecrNumber = matchWithEcr.groupValues[2] // The second group contains the ecrNumber if present
            return EchoRequest(text, ecrNumber, ecrNumber.isNotEmpty())
        } else if (matchWithoutEcr != null) {
            val text = matchWithoutEcr.groupValues[1]
            return EchoRequest(text)
        }

        return null
    }

    /**
     * Parses the given input string and returns an AmountRequest object based on the specified type.
     *
     * @param str The input string to parse.
     * @param type The type of amount request.
     * @return An AmountRequest object representing the parsed amount request.
     * @throws IllegalArgumentException if the input string format is invalid.
     */
    fun parseAmountRequest(str: String, type: String): AmountRequest {
        try {
            val pattern = when (type) {
                Constants.TYPE_AMOUNT_SALE_REQUEST -> """A/S(\d{1,6})/F([\d.]+):(\d{3}):(\d)/D(\d{14})/R(\w{1,11})/H(\w{1,8})/T(\w{1,8})/M([\w\s]{1,100})/Q(\w{8})?""".toRegex(
                    RegexOption.IGNORE_CASE
                )
                Constants.TYPE_AMOUNT_VOID_REQUEST -> """V/S(\d{1,6})/F([\d.]+):(\d{3}):(\d)/D(\d{14})/R(\w{1,11})/H(\w{1,8})/T(\w{1,8})/M([\w\s]{1,100})/Q(\w{8})?""".toRegex(
                    RegexOption.IGNORE_CASE
                )
                Constants.TYPE_AMOUNT_MAIL_REQUEST -> """M/S(\d{1,6})/F([\d.]+):(\d{3}):(\d)/D(\d{14})/R(\w{1,11})/H(\w{1,8})/T(\w{1,8})/M([\w\s]{1,100})/Q(\w{8})?""".toRegex(
                    RegexOption.IGNORE_CASE
                )
                Constants.TYPE_AMOUNT_COMPLETION_REQUEST -> """P/S(\d{1,6})/F([\d.]+):(\d{3}):(\d)/D(\d{14})/R(\w{1,11})/H(\w{1,8})/T(\w{1,8})/M([\w\s]{1,100})/Q(\w{8})?""".toRegex(
                    RegexOption.IGNORE_CASE
                )
                Constants.TYPE_AMOUNT_REFUND_REQUEST -> """Z/S(\d{1,6})/F([\d.]+):(\d{3}):(\d)/D(\d{14})/R(\w{1,11})/H(\w{1,8})/T(\w{1,8})/M([\w\s]{1,100})/Q(\w{8})?""".toRegex(
                    RegexOption.IGNORE_CASE
                )
                Constants.TYPE_AMOUNT_INSTALLMENTS_REQUEST -> """I/S(\d{1,6})/F([\d.]+):(\d{3}):(\d)/D(\d{14})/R(\w{1,11})/H(\w{1,8})/T(\w{1,8})/M([\w\s]{1,100})/Q(\w{8})?""".toRegex(
                    RegexOption.IGNORE_CASE
                )
                else -> {
                    Logger.logToFile("Invalid type: $type")
                    MessageHandler.getInstance()
                        .sendMessage(createErrorResponseMessage(Constants.mappedErrors[Constants.SYNTAX_ERROR]!!))
                    throw IllegalArgumentException("Invalid type: $type")
                }
            }

            val matchResult = pattern.find(str)

            if (matchResult != null) {
                val (sessionNumber, amount, currencyCode, decimals, dateTime, ecrId, operatorNumber,
                    receiptNumber, customData, mac) = matchResult.destructured

                val currencySymbol = euCurrencySymbols[currencyCode.toInt()] ?: ""
                val parsedDateTime =
                    LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))

                val actualAmount = Utils.calculateActualAmount(amount, decimals.toInt())

                val isInstallments = type == Constants.TYPE_AMOUNT_INSTALLMENTS_REQUEST

                return AmountRequest(
                    sessionNumber = sessionNumber,
                    amount = actualAmount,
                    currencyCode = currencyCode.toInt(),
                    currencySymbol = currencySymbol,
                    decimals = decimals.toInt(),
                    dateTime = parsedDateTime,
                    ecrId = ecrId,
                    operatorNumber = operatorNumber,
                    receiptNumber = receiptNumber,
                    customData = customData,
                    mac = mac,
                    type = type,
                    isInstallments = isInstallments
                )
            } else {
                throw IllegalArgumentException("Invalid input string format")
            }
        } catch (e: Exception) {
            Logger.logToFile(e.stackTrace.firstOrNull()?.methodName + e.message)
            MessageHandler.getInstance()
                .sendMessage(createErrorResponseMessage(Constants.mappedErrors[Constants.SYNTAX_ERROR]!!))
            throw IllegalArgumentException("Invalid input string format: ${e.message}")
        }
    }

    /**
     * Parses the input string and returns a ResendRequest object.
     * @param str the input string to be parsed.
     * @return a ResendRequest object parsed from the input string.
     * @throws IllegalArgumentException if the input string does not match the expected format.
     */
    fun parseResendRequest(str: String): ResendRequest {
        try {
            val fields = str.split("/")
            val sessionNumber = fields[1].substring(1)
            val amountFields = fields[2].substring(1).split(":")
            val amountValue = amountFields[0].toDouble() / 10.0.pow(amountFields[2].toInt())
            val currencyCode = amountFields[1]
            val currencyExponent = amountFields[2].toInt()
            val ecrId = fields[3].substring(1)
            val receiptNumber = fields[4].substring(1)
            val mac = if (fields.size > 5) fields[5].substring(1) else null

            val decimalFormat = DecimalFormat("#0." + "0".repeat(currencyExponent))
            decimalFormat.roundingMode = RoundingMode.HALF_UP
            val amount = decimalFormat.format(amountValue).toDouble()

            return ResendRequest(
                sessionNumber = sessionNumber,
                amount = amount,
                currencyCode = currencyCode,
                currencyExponent = currencyExponent.toString(),
                ecrId = ecrId,
                receiptNumber = receiptNumber,
                mac = mac.toString()
            )
        } catch (e: Exception) {
            Logger.logToFile(e.stackTrace.firstOrNull()?.methodName + e.message)
            throw IllegalArgumentException("Invalid input string format: ${e.message}")
        }
    }


    /**
     * Parses a string message in the format L/R<ecr-id>/D<datetime>{/Q<mac>} and returns a ResendAll object.
     * @param str the input string to be parsed
     * @return a ResendAll object containing the parsed fields
     * @throws IllegalArgumentException if the input string is not in the expected format or if any of the
     * mandatory fields are missing or invalid
     */
    fun parseResendAllRequest(str: String): ResendAllRequest {
        try {
            val fields = str.split("/")
            val ecrId = fields[1].substring(1)
            val dateTime = LocalDateTime.parse(
                fields[2].substring(1),
                DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
            )
            val macIndex = fields.indexOfFirst { it.startsWith("Q") }
            val mac = if (macIndex != -1) {
                fields[macIndex].substring(1)
            } else {
                null
            }
            return ResendAllRequest(
                ecrId = ecrId,
                dateTime = dateTime,
                mac = mac
            )
        } catch (e: Exception) {
            Logger.logToFile(e.stackTrace.firstOrNull()?.methodName + e.message)
            throw IllegalArgumentException("Invalid input string format: ${e.message}")
        }
    }

    /**
     * Parses a string message in the format R/S<session number>/R<ecr-id>/F<amount>/T<receipt-number>{:<receipt-number>}
     * and returns an AckResult object.
     *
     * @param str the input string to be parsed
     * @return an AckResult object containing the parsed fields
     * @throws IllegalArgumentException if the input string is not in the expected format or if any of the
     * mandatory fields are missing or invalid
     */
    fun parseAckResultRequest(str: String): AckResultRequest {
        try {
            val fields = str.split("/")
            val sessionNumber = if (fields.size > 1) fields[1].substring(1) else null
            val ecrId = fields[2].substring(1)
            val amountFields = fields[3].substring(1).split(":")
            val amount = amountFields[0].toDouble()
            val receiptNumberWithSecond = fields[4].substring(1)
            val receiptNumberDelimiterIndex = receiptNumberWithSecond.indexOf(":")
            val receiptNumber = if (receiptNumberDelimiterIndex != -1) {
                receiptNumberWithSecond.substring(0, receiptNumberDelimiterIndex)
            } else {
                receiptNumberWithSecond
            }

            val secondReceiptNumber = if (receiptNumberDelimiterIndex != -1) {
                receiptNumberWithSecond.substring(receiptNumberDelimiterIndex + 1)
            } else {
                null
            }

            return AckResultRequest(
                sessionNumber = sessionNumber,
                ecrId = ecrId,
                amount = amount,
                receiptNumber = receiptNumber,
                secondReceiptNumber = secondReceiptNumber
            )
        } catch (e: Exception) {
            Logger.logToFile(e.stackTrace.firstOrNull()?.methodName + e.message)
            throw IllegalArgumentException("Invalid input string format: ${e.message}")
        }
    }

    /**
     * Creates an echo response message in the format required by the POS system.
     * @param echoResponse the echo response object containing the text, terminal ID, and app version
     * @return the echo response message in the format "*POS0210X/{text}/T{terminalId}:{appVersion}" converted to hexadecimal format
     * @throws IllegalArgumentException if the echo response object is invalid or missing required fields
     */
    fun createEchoResponseMessage(echoResponse: EchoResponse): String {
        val text = echoResponse.text ?: ""
        val terminalId = echoResponse.terminalId ?: ""
        val appVersion = echoResponse.appVersion ?: ""

        return if (!echoResponse.isInit) "X/$text/T$terminalId:$appVersion" else "X/INIT:${AppData.getEcrNumber()}/T$terminalId:$appVersion"
    }
    //this might need to have and eftpos id


    /**
     * Creates a confirmation response message from the given ConfirmationResponse object.
     * The message is returned as a string of hexadecimal characters.
     * @param confirmationResponse the ConfirmationResponse object to create the response from.
     * @return a string of hexadecimal characters representing the confirmation response message.
     * @throws IllegalArgumentException if the confirmationResponse object is invalid.
     */
    fun createConfirmationResponse(confirmationResponse: ConfirmationResponse): String {
        try {
            val sessionNumber = confirmationResponse.sessionNumber
            val amount = confirmationResponse.amount
            val ecrId = confirmationResponse.ecrId
            val receiptNumber = confirmationResponse.receiptNumber

            val amountStr = Utils.reverseAmount(amount, confirmationResponse.decimals)

            return "A/S$sessionNumber/F$amountStr/R$ecrId/T$receiptNumber"
        } catch (e: Exception) {
            Logger.logToFile(e.stackTrace.firstOrNull()?.methodName + e.message)
            throw IllegalArgumentException("Invalid confirmation object: ${e.message}")
        }
    }


    /**
     * Creates a success result message with code "E/000".
     * @return a success result message with code "E/000" as a hex-encoded string.
     */
    fun createSuccessResultMessage(): String {
        return "E/000"
    }

    /**
     * Generates an error response message in the format required by the POS device protocol,
     * based on the given error code.
     * @param code The error code for the error response message.
     * @return A string representing the error response message.
     */
    fun createErrorResponseMessage(code: String): String {
        return "E/$code"
    }

    /**
     * Parses a control request string and returns a ControlRequest object.
     *
     * @param input the control request string to parse, in the format "ECR0210U/R<ecr-id>/C<command-name>:<parameter-value>{:parameter-value}"
     * @return a ControlRequest object containing the parsed data
     * @throws IllegalArgumentException if the input string is not in the expected format
     */
    fun parseControlRequest(str: String): ControlRequest {
        if (FeatureStore.isCoreVersion()) {
            try {
                val fields = str.split("/")
                require(fields.size >= 3 && fields[0] == "U") { "Invalid input format" }

                val ecrId = fields[1].substring(1)

                val commandField = fields[2].substring(1)
                val commandParts = commandField.split(":")
                require(commandParts.size >= 2) { "Invalid command format" }

                val commandName = commandParts[0]
                val parameterValue = commandParts[1]


                return ControlRequest(
                    ecrId = ecrId,
                    commandName = commandName,
                    parameterValue = parameterValue
                )
            } catch (e: Exception) {
                Logger.logToFile(e.stackTrace.firstOrNull()?.methodName + e.message)
                throw IllegalArgumentException("Invalid input string format: ${e.message}")
            }
        } else {
            return ControlRequest()
        }
    }

    /**
     * Parses a string input in the format "ECR0110W/S<session number>/F<amount>:<cur-code>:<cur-exp>/D<datetime>/R<ecr-id>/H<operator-number>/T<receipt number>/M<custom-data>{/Q<mac>}"
     * and returns a [RegReceiptRequest] object containing the parsed data.
     *
     * @param input the input string to be parsed
     * @return a [RegReceiptRequest] object containing the parsed data
     * @throws IllegalArgumentException if the input string is not in the expected format
     */
    fun parseRegReceiptRequest(input: String): RegReceiptRequest? {
        if (FeatureStore.isCoreVersion()) {
            val parts = input.split("/")

            val amountParts = parts[2].substring(1).split(":")

            return RegReceiptRequest(
                sessionNumber = parts[1].substring(1),
                amount = amountParts[0].toDouble() / 10.0.pow(amountParts[2].toInt()),
                curCode = amountParts[1],
                curExp = amountParts[2],
                dateTime = LocalDateTime.parse(
                    parts[3].substring(1),
                    DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
                ),
                ecrId = parts[4].substring(1),
                operatorNumber = parts[5].substring(1),
                receiptNumber = parts[6].substring(1),
                customData = parts[7].substring(1),
                mac = if (parts[8].startsWith("Q")) parts[8].substring(1) else ""
            )
        } else {
            return null
        }
    }

    /**
     * Creates a result response message from the given [ResultResponse] object.
     * @param resultResponse The [ResultResponse] object to create the result response message from.
     * @return The result response message as a hexadecimal string.
     * @throws IllegalArgumentException If the given [ResultResponse] object is invalid or incomplete.
     */
    fun createResultResponse(resultResponse: ResultResponse, includePrn: Boolean): String {
        val transData = resultResponse.transData
        val rspCode = resultResponse.rspCode ?: ""
        val customData = resultResponse.customData

        val sessionNumber =
            resultResponse.sessionNumber.ifEmpty { Constants.POSTXN }
        val ecrId = resultResponse.ecrId
        val receiptNumber = resultResponse.receiptNumber
        val receiptNumberOptional = ""

        var message =
            "R/S$sessionNumber/R$ecrId/T$receiptNumber$receiptNumberOptional/M$customData/C$rspCode"

        if (rspCode == "00" && transData != null) {
            val cardType = transData.cardType ?: ""
            val txnType = transData.txnType ?: ""
            val cardPanMasked = transData.cardPanMasked ?: ""
            var amount = transData.amount ?: 0.0
            var amountFinal = transData.amountFinal ?: amount
            val amountTip = transData.amountTip ?: 0.0
            val amountLoyalty = transData.amountLoyalty ?: 0.0
            val amountCashBack = transData.amountCashBack ?: 0.0
            val bankId = transData.bankId ?: ""
            val terminalId = transData.terminalId ?: ""
            val batchNumber = transData.batchNumber ?: ""
            val rrn = transData.rrn ?: ""
            val stan = transData.stan ?: ""
            val authCode = transData.authCode ?: ""
            val transactionDateTime = transData.transactionDateTime ?: ""
            val transactionEcrStatus = transData.transactionEcrStatus ?: ""
            val prnData = if (includePrn) resultResponse.prnData?.data else null


            if (txnType == "02") {
                amount = -amount
                amountFinal = -amountFinal
            }

            val amountConverted = Utils.reverseAmount(amount, resultResponse.decimals)
            val amountFinalConverted = Utils.reverseAmount(amountFinal, resultResponse.decimals)
            val amountTipConverted = Utils.reverseAmount(amountTip, resultResponse.decimals)
            val amountLoyaltyConverted = Utils.reverseAmount(amountLoyalty, resultResponse.decimals)
            val amountCashBackConverted = Utils.reverseAmount(amountCashBack, resultResponse.decimals)

            message += "/D$cardType:$txnType:$cardPanMasked:${amountConverted}:$amountFinalConverted:$amountTipConverted:$amountLoyaltyConverted:$amountCashBackConverted:$bankId:$terminalId:$batchNumber:$rrn:$stan:$authCode:$transactionDateTime:${transactionEcrStatus}"
        }
        return message
    }
}