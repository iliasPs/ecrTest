package com.example.ecrtool

import com.example.ecrtool.dataHandle.MessageHandler
import com.example.ecrtool.models.trafficEcr.*
import com.example.ecrtool.utils.Constants
import com.example.ecrtool.utils.Logger
import com.example.ecrtool.utils.euCurrencySymbols
import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.math.RoundingMode
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.regex.PatternSyntaxException
import kotlin.math.pow

class ParseTest {

    @Test
    fun parseAmountRequest_ValidInput_ReturnsAmountRequest() {
        // Given
        val input = "A/S001008/F2500:978:2/D20220524102517/RABC00111222/H121/T1020/M0/Q59D19E7D"
        val expectedSessionNumber = "001008"
        val expectedAmount = 25.00
        val expectedCurrencyCode = 978
        val expectedCurrencySymbol = "€"
        val expectedDecimals = 2
        val expectedDateTime =
            LocalDateTime.parse("20220524102517", DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        val expectedEcrId = "ABC00111222"
        val expectedOperatorNumber = "121"
        val expectedReceiptNumber = "1020"
        val expectedCustomData = "0"
        val expectedMac = "59D19E7D"

        // When
        val result = parseAmountRequest(input)

        // Then
        assertEquals(expectedSessionNumber, result.sessionNumber)
        assertEquals(expectedAmount, result.amount)
        assertEquals(expectedCurrencyCode, result.currencyCode)
        assertEquals(expectedCurrencySymbol, result.currencySymbol)
        assertEquals(expectedDecimals, result.decimals)
        assertEquals(expectedDateTime, result.dateTime)
        assertEquals(expectedEcrId, result.ecrId)
        assertEquals(expectedOperatorNumber, result.operatorNumber)
        assertEquals(expectedReceiptNumber, result.receiptNumber)
        assertEquals(expectedCustomData, result.customData)
        assertEquals(expectedMac, result.mac)
    }

    @Test
    fun parseEchoRequest_HEX_ValidInput_ReturnsEchoRequest() {
        // Given
        val hexMessage =
            "00 17 45 43 52 30 32 31 30 58 2F 48 65 6C 6C 6F 20 66 72 6F 6D 20 45 43 52"
        val expectedText = "Hello from ECR"

        // When
        val result = parseEchoRequest(extractMessage(hexMessage))

        // Then
        assertEquals(expectedText, result?.text)
    }


    @Test
    fun parseAmountRequest_HEX_ValidInput_ReturnsAmountRequest() {
        // Given
        val hexMessage =
            "00 51 45 43 52 30 32 31 30 41 2F 53 30 30 31 30 30 38 2F 46 32 35 30 30 3A 39 37 38 3A 32 2F 44 32 30 32 32 30 35 32 34 31 30 32 35 31 37 2F 52 41 42 43 30 30 31 31 31 32 32 32 2F 48 31 32 31 2F 54 31 30 32 30 2F 4D 30 2F 51 35 39 44 31 39 45 37 44"
        val expectedSessionNumber = "001008"
        val expectedAmount = 25.00
        val expectedCurrencyCode = 978
        val expectedCurrencySymbol = "€"
        val expectedDecimals = 2
        val expectedDateTime =
            LocalDateTime.parse("20220524102517", DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        val expectedEcrId = "ABC00111222"
        val expectedOperatorNumber = "121"
        val expectedReceiptNumber = "1020"
        val expectedCustomData = "0"
        val expectedMac = "59D19E7D"

        // When
        val result = parseAmountRequest(extractMessage(hexMessage))

        // Then
        assertEquals(expectedSessionNumber, result.sessionNumber)
        assertEquals(expectedAmount, result.amount)
        assertEquals(expectedCurrencyCode, result.currencyCode)
        assertEquals(expectedCurrencySymbol, result.currencySymbol)
        assertEquals(expectedDecimals, result.decimals)
        assertEquals(expectedDateTime, result.dateTime)
        assertEquals(expectedEcrId, result.ecrId)
        assertEquals(expectedOperatorNumber, result.operatorNumber)
        assertEquals(expectedReceiptNumber, result.receiptNumber)
        assertEquals(expectedCustomData, result.customData)
        assertEquals(expectedMac, result.mac)
    }

    @Test
    fun parseResendRequest_HEX_ValidInput_ReturnsResendRequest() {
        // Given
        val hexMessage =
            "00 38 45 43 52 30 31 31 30 4F 2F 53 30 30 31 30 35 38 2F 46 31 35 30 3A 39 37 38 3A 32 2F 52 41 42 43 30 30 31 31 31 32 32 32 2F 54 31 30 35 31 2F 51 46 37 31 36 37 41 39 46"
        val expectedSessionNumber = "001058"
        val expectedAmount = 1.50 // Add the expected amount here
        val expectedCurrencyCode = "978" // Add the expected currency code here
        val expectedCurrencyExponent = "2" // Add the expected currency exponent here
        val expectedEcrId = "ABC00111222"
        val expectedReceiptNumber = "1051"
        val expectedMac = "F7167A9F"

        // When
        val result = parseResendRequest(extractMessage(hexMessage))

        // Then
        assertEquals(expectedSessionNumber, result.sessionNumber)
        assertEquals(expectedAmount, result.amount)
        assertEquals(expectedCurrencyCode, result.currencyCode)
        assertEquals(expectedCurrencyExponent, result.currencyExponent)
        assertEquals(expectedEcrId, result.ecrId)
        assertEquals(expectedReceiptNumber, result.receiptNumber)
        assertEquals(expectedMac, result.mac)
    }

    @Test
    fun parseResendAllRequest_HEX_ValidInput_ReturnsResendAllRequest() {
        // Given
        val hexMessage =
            "00 2F 45 43 52 30 31 31 30 4C 2F 52 41 42 43 30 30 31 31 31 32 32 32 2F 44 32 30 32 32 30 37 31 31 31 31 30 36 34 35 2F 51 36 43 34 38 33 46 43 45"
        val expectedEcrId = "ABC00111222"
        val expectedDateTime =
            LocalDateTime.parse("20220711110645", DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        val expectedMac = "6C483FCE"

        // When
        val result = parseResendAllRequest(extractMessage(hexMessage))

        // Then
        assertEquals(expectedEcrId, result.ecrId)
        assertEquals(expectedDateTime, result.dateTime)
        assertEquals(expectedMac, result.mac)
    }

    @Test
    fun parseAckResultRequest_HEX_ValidInput_ReturnsAckResultRequest() {
        // Given
        val hexMessage =
            "52 2F 53 30 30 31 2F 52 41 42 43 30 30 31 2F 46 31 32 33 2E 34 35 2F 54 39 38 37 3A 36 35 34"
        val expectedSessionNumber = "001"
        val expectedEcrId = "ABC001"
        val expectedAmount = 123.45
        val expectedReceiptNumber = "987"
        val expectedSecondReceiptNumber = "654"

        // When
        val result = parseAckResultRequest(extractMessage(hexMessage))

        // Then
        assertEquals(expectedSessionNumber, result.sessionNumber)
        assertEquals(expectedEcrId, result.ecrId)
        assertEquals(expectedAmount, result.amount)
        assertEquals(expectedReceiptNumber, result.receiptNumber)
        assertEquals(expectedSecondReceiptNumber, result.secondReceiptNumber)
    }

    @Test
    fun parseControlRequest_HEX_ValidInput_ReturnsControlRequest() {
        // Given
        val hexInput =
            "00 23 45 43 52 30 32 31 30 55 2F 52 41 42 43 30 30 31 31 31 32 32 32 2F 43 55 4E 42 49 4E 44 5F 50 4F 53 3A 31"
        val expectedEcrId = "ABC00111222"
        val expectedCommandName = "UNBIND_POS"
        val expectedParameterValue = "1"

        // When
        val result = parseControlRequest(extractMessage(hexInput))

        // Then
        assertEquals(expectedEcrId, result.ecrId)
        assertEquals(expectedCommandName, result.commandName)
        assertEquals(expectedParameterValue, result.parameterValue)
    }

    @Test
    fun parseRegReceiptRequest_HEX_ValidInput_ReturnsRegReceiptRequest() {
        // Given
        val hexInput =
            "00 51 45 43 52 30 31 31 30 57 2F 53 30 30 31 35 37 33 2F 46 35 30 30 30 3A 39 37 38 3A 32 2F 44 32 30 32 32 30 37 31 31 31 30 35 30 30 39 2F 52 41 42 43 30 30 31 31 31 32 32 32 2F 48 31 32 31 2F 54 31 32 32 38 2F 4D 30 2F 51 33 30 41 44 44 38 41 33"

        val expectedSessionNumber = "001573"
        val expectedAmount = 50.00
        val expectedCurCode = "978"
        val expectedCurExp = "2"
        val expectedDateTime =
            LocalDateTime.parse("20220711105009", DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        val expectedEcrId = "ABC00111222"
        val expectedOperatorNumber = "121"
        val expectedReceiptNumber = "1228"
        val expectedCustomData = "0"
        val expectedMac = "30ADD8A3"

        val result = parseRegReceiptRequest(extractMessage(hexInput))

        assertEquals(expectedSessionNumber, result?.sessionNumber)
        assertEquals(expectedAmount, result?.amount)
        assertEquals(expectedCurCode, result?.curCode)
        assertEquals(expectedCurExp, result?.curExp)
        assertEquals(expectedDateTime, result?.dateTime)
        assertEquals(expectedEcrId, result?.ecrId)
        assertEquals(expectedOperatorNumber, result?.operatorNumber)
        assertEquals(expectedReceiptNumber, result?.receiptNumber)
        assertEquals(expectedCustomData, result?.customData)
        assertEquals(expectedMac, result?.mac)
    }

    @Test
    fun testParseAmountRequest() {

        // Test TYPE_AMOUNT_SALE_REQUEST
        val saleInput = "412f5330303130382f46323530303a3937383a322f4432303232303532343130323531372f5241424330303131313232322f483132312f54313032302f4d302f513539443139453744"
        val saleResult = parseAmountRequest(extractMessage(saleInput), Constants.TYPE_AMOUNT_SALE_REQUEST)
        val expectedSaleRequest = AmountRequest(
            sessionNumber = "00108",
            amount = 25.00,
            currencyCode = 978,
            currencySymbol = "€",
            decimals = 2,
            dateTime = LocalDateTime.parse("20220524102517", DateTimeFormatter.ofPattern("yyyyMMddHHmmss")),
            ecrId = "ABC00111222",
            operatorNumber = "121",
            receiptNumber = "1020",
            customData = "0",
            mac = "59D19E7D",
            type = Constants.TYPE_AMOUNT_SALE_REQUEST
        )
        assertEquals(expectedSaleRequest, saleResult)

        // Test TYPE_AMOUNT_VOID_REQUEST
        val voidInput = "56 2F 53 30 30 31 30 38 2F 46 32 35 30 30 3A 39 37 38 3A 32 2F 44 32 30 32 32 30 35 32 34 31 30 32 35 31 37 2F 52 41 42 43 30 30 31 31 31 32 32 32 2F 48 31 32 31 2F 54 31 30 32 30 2F 4D 30 2F 51 35 39 44 31 39 45 37 44"
        val voidResult = parseAmountRequest(extractMessage(voidInput), Constants.TYPE_AMOUNT_VOID_REQUEST)
        val expectedVoidRequest = AmountRequest(
            sessionNumber = "00108",
            amount = 25.00,
            currencyCode = 978,
            currencySymbol = "€",
            decimals = 2,
            dateTime = LocalDateTime.parse("20220524102517", DateTimeFormatter.ofPattern("yyyyMMddHHmmss")),
            ecrId = "ABC00111222",
            operatorNumber = "121",
            receiptNumber = "1020",
            customData = "0",
            mac = "59D19E7D",
            type = Constants.TYPE_AMOUNT_VOID_REQUEST
        )
        assertEquals(expectedVoidRequest, voidResult)

        // Test TYPE_AMOUNT_COMPLETION_REQUEST
        val completionInput = "50 2F 53 30 30 31 30 38 2F 46 32 35 30 30 3A 39 37 38 3A 32 2F 44 32 30 32 32 30 35 32 34 31 30 32 35 31 37 2F 52 41 42 43 30 30 31 31 31 32 32 32 2F 48 31 32 31 2F 54 31 30 32 30 2F 4D 30 2F 51 35 39 44 31 39 45 37 44"
        val completionResult = parseAmountRequest(extractMessage(completionInput), Constants.TYPE_AMOUNT_COMPLETION_REQUEST)
        val expectedCompletionRequest = AmountRequest(
            sessionNumber = "00108",
            amount = 25.00,
            currencyCode = 978,
            currencySymbol = "€",
            decimals = 2,
            dateTime = LocalDateTime.parse("20220524102517", DateTimeFormatter.ofPattern("yyyyMMddHHmmss")),
            ecrId = "ABC00111222",
            operatorNumber = "121",
            receiptNumber = "1020",
            customData = "0",
            mac = "59D19E7D",
            type = Constants.TYPE_AMOUNT_COMPLETION_REQUEST
        )
        assertEquals(expectedCompletionRequest, completionResult)

        // Test TYPE_AMOUNT_MAIL_REQUEST
        val mailInput = "4D 2F 53 30 30 31 30 38 2F 46 32 35 30 30 3A 39 37 38 3A 32 2F 44 32 30 32 32 30 35 32 34 31 30 32 35 31 37 2F 52 41 42 43 30 30 31 31 31 32 32 32 2F 48 31 32 31 2F 54 31 30 32 30 2F 4D 30 2F 51 35 39 44 31 39 45 37 44"
        val mailResult = parseAmountRequest(extractMessage(mailInput), Constants.TYPE_AMOUNT_MAIL_REQUEST)
        val expectedMailRequest = AmountRequest(
            sessionNumber = "00108",
            amount = 25.00,
            currencyCode = 978,
            currencySymbol = "€",
            decimals = 2,
            dateTime = LocalDateTime.parse("20220524102517", DateTimeFormatter.ofPattern("yyyyMMddHHmmss")),
            ecrId = "ABC00111222",
            operatorNumber = "121",
            receiptNumber = "1020",
            customData = "0",
            mac = "59D19E7D",
            type = Constants.TYPE_AMOUNT_MAIL_REQUEST
        )
        assertEquals(expectedMailRequest, mailResult)

        // Test TYPE_AMOUNT_REFUND_REQUEST
        val refundInput = "5A 2F 53 30 30 31 30 38 2F 46 32 35 30 30 3A 39 37 38 3A 32 2F 44 32 30 32 32 30 35 32 34 31 30 32 35 31 37 2F 52 41 42 43 30 30 31 31 31 32 32 32 2F 48 31 32 31 2F 54 31 30 32 30 2F 4D 30 2F 51 35 39 44 31 39 45 37 44"
        val refundResult = parseAmountRequest(extractMessage(refundInput), Constants.TYPE_AMOUNT_REFUND_REQUEST)
        val expectedRefundRequest = AmountRequest(
            sessionNumber = "00108",
            amount = 25.00,
            currencyCode = 978,
            currencySymbol = "€",
            decimals = 2,
            dateTime = LocalDateTime.parse("20220524102517", DateTimeFormatter.ofPattern("yyyyMMddHHmmss")),
            ecrId = "ABC00111222",
            operatorNumber = "121",
            receiptNumber = "1020",
            customData = "0",
            mac = "59D19E7D",
            type = Constants.TYPE_AMOUNT_REFUND_REQUEST
        )
        assertEquals(expectedRefundRequest, refundResult)

        // Test TYPE_AMOUNT_INSTALLMENTS_REQUEST
        val installmentsInput = "49 2F 53 30 30 31 30 38 2F 46 32 35 30 30 3A 39 37 38 3A 32 2F 44 32 30 32 32 30 35 32 34 31 30 32 35 31 37 2F 52 41 42 43 30 30 31 31 31 32 32 32 2F 48 31 32 31 2F 54 31 30 32 30 2F 4D 30 2F 51 35 39 44 31 39 45 37 44"
        val installmentsResult = parseAmountRequest(extractMessage(installmentsInput), Constants.TYPE_AMOUNT_INSTALLMENTS_REQUEST)
        val expectedInstallmentsRequest = AmountRequest(
            sessionNumber = "00108",
            amount = 25.00,
            currencyCode = 978,
            currencySymbol = "€",
            decimals = 2,
            dateTime = LocalDateTime.parse("20220524102517", DateTimeFormatter.ofPattern("yyyyMMddHHmmss")),
            ecrId = "ABC00111222",
            operatorNumber = "121",
            receiptNumber = "1020",
            customData = "0",
            mac = "59D19E7D",
            type = Constants.TYPE_AMOUNT_INSTALLMENTS_REQUEST,
            isInstallments = true
        )
        assertEquals(expectedInstallmentsRequest, installmentsResult)
    }




    fun parseRegReceiptRequest(input: String): RegReceiptRequest? {
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
    }

    fun parseAmountRequest(str: String): AmountRequest {
        try {
            val fields = str.split("/")
            val sessionNumber = fields[1].substring(1)
            val amountFields = fields[2].substring(1).split(":")
            val amount = amountFields[0].toDouble() / 10.0.pow(amountFields[2].toInt())
            val currencyCode = amountFields[1].toInt()
            val currencySymbol = euCurrencySymbols[currencyCode] ?: ""
            val decimals = amountFields[2].toInt()

            val dateTime = LocalDateTime.parse(
                fields[3].substring(1),
                DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
            )
            val ecrId = fields[3].substring(1)
            val operatorNumber = fields[5].substring(1)
            val receiptNumber = fields[6].substring(1)

            val customDataField = if (fields[7].startsWith("M")) {
                fields[7].substring(1)
            } else {
                ""
            }

            val mac = if (fields.size > 8 && fields[8].startsWith("Q")) {
                fields[8].substring(1)
            } else {
                ""
            }

            return AmountRequest(
                sessionNumber = sessionNumber,
                amount = amount,
                currencyCode = currencyCode,
                currencySymbol = currencySymbol,
                decimals = decimals,
                dateTime = dateTime,
                ecrId = ecrId,
                operatorNumber = operatorNumber,
                receiptNumber = receiptNumber,
                customData = customDataField,
                mac = mac
            )
        } catch (e: Exception) {
            Logger.logToFile(e.stackTrace.firstOrNull()?.methodName + e.message)
            //            MessageHandler.getInstance()
            //                .sendMessage(createErrorResponseMessage(Constants.mappedErrors[Constants.PROTOCOL_NOT_SUPPORTED]!!))
            throw IllegalArgumentException("Invalid input string format: ${e.message}")
        }
    }

    fun parseEchoRequest(message: String): EchoRequest? {
        if (message.isNullOrEmpty()) {
            throw IllegalArgumentException("message must not be null or empty")
        }


        val regex = try {
            Regex("""/([^/]+)$""")
        } catch (e: PatternSyntaxException) {
            Logger.logToFile(e.stackTrace.firstOrNull()?.methodName + e.message)
            throw PatternSyntaxException("invalid regular expression", e.pattern, e.index)
        }

        val match = regex.find(message)
        if (match != null) {
            return EchoRequest(match.groupValues[1])
        }
        return null
    }

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

    fun parseControlRequest(str: String): ControlRequest {
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
    }

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

    fun parseAckResultRequest(str: String): AckResultRequest {
        try {
            val fields = str.split("/")
            val sessionNumber = if (fields.size > 1) fields[1].substring(1) else null
            val ecrId = fields[2].substring(1)
            val amountFields = fields[3].substring(1).split(":")
            val amount = amountFields[0].toDouble()
            val receiptNumberWithSecond = fields[4].substring(1)
            val receiptNumberDelimiterIndex = receiptNumberWithSecond.indexOf(":")
            val receiptNumber = receiptNumberWithSecond.substring(0, receiptNumberDelimiterIndex)
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

    fun extractMessage(hexMessage: String): String {
        val bytes = hexStringToByteArray(hexMessage)
        val prefixes = listOf(
            "A/", "R/", "W/", "O/", "L/", "U/",
            "I/", "Z/", "V/", "P/", "M/", "X/"
        )
        val startIndex = findPrefixIndex(bytes, prefixes)

        if (startIndex == -1) {
            throw IllegalArgumentException("Hex message does not contain any valid prefix.")
        }

        val relevantBytes = bytes.copyOfRange(startIndex, bytes.size)
        return String(relevantBytes)
    }

    private fun findPrefixIndex(bytes: ByteArray, prefixes: List<String>): Int {
        for (prefix in prefixes) {
            val prefixBytes = prefix.toByteArray()
            val index = bytes.indexOf(prefixBytes[0])

            if (index != -1 && bytes.sliceArray(index until index + prefixBytes.size)
                    .contentEquals(prefixBytes)
            ) {
                return index
            }
        }
        return -1
    }

    private fun hexStringToByteArray(hexString: String): ByteArray {
        val cleanHexString = hexString.replace(" ", "")
        val byteArray = ByteArray(cleanHexString.length / 2)

        for (i in byteArray.indices) {
            val index = i * 2
            val hex = cleanHexString.substring(index, index + 2)
            byteArray[i] = hex.toInt(16).toByte()
        }

        return byteArray
    }

    fun parseAmountRequest(str: String, type: String): AmountRequest {
        try {
            val pattern = when (type) {
                Constants.TYPE_AMOUNT_SALE_REQUEST -> """A/S(\d{1,6})/F([\d.]+):(\d{3}):(\d)/D(\d{14})/R(\w{1,11})/H(\w{1,8})/T(\w{1,8})/M([\w\s]{1,100})/Q(\w{8})?""".toRegex(RegexOption.IGNORE_CASE)
                Constants.TYPE_AMOUNT_VOID_REQUEST -> """V/S(\d{1,6})/F([\d.]+):(\d{3}):(\d)/D(\d{14})/R(\w{1,11})/H(\w{1,8})/T(\w{1,8})/M([\w\s]{1,100})/Q(\w{8})?""".toRegex(RegexOption.IGNORE_CASE)
                Constants.TYPE_AMOUNT_MAIL_REQUEST -> """M/S(\d{1,6})/F([\d.]+):(\d{3}):(\d)/D(\d{14})/R(\w{1,11})/H(\w{1,8})/T(\w{1,8})/M([\w\s]{1,100})/Q(\w{8})?""".toRegex(RegexOption.IGNORE_CASE)
                Constants.TYPE_AMOUNT_COMPLETION_REQUEST -> """P/S(\d{1,6})/F([\d.]+):(\d{3}):(\d)/D(\d{14})/R(\w{1,11})/H(\w{1,8})/T(\w{1,8})/M([\w\s]{1,100})/Q(\w{8})?""".toRegex(RegexOption.IGNORE_CASE)
                Constants.TYPE_AMOUNT_REFUND_REQUEST -> """Z/S(\d{1,6})/F([\d.]+):(\d{3}):(\d)/D(\d{14})/R(\w{1,11})/H(\w{1,8})/T(\w{1,8})/M([\w\s]{1,100})/Q(\w{8})?""".toRegex(RegexOption.IGNORE_CASE)
                Constants.TYPE_AMOUNT_INSTALLMENTS_REQUEST -> """I/S(\d{1,6})/F([\d.]+):(\d{3}):(\d)/D(\d{14})/R(\w{1,11})/H(\w{1,8})/T(\w{1,8})/M([\w\s]{1,100})/Q(\w{8})?""".toRegex(RegexOption.IGNORE_CASE)
                else -> {
       //             Logger.logToFile("Invalid type: $type")
//                    MessageHandler.getInstance()
//                        .sendMessage(createErrorResponseMessage(Constants.mappedErrors[Constants.PROTOCOL_NOT_SUPPORTED]!!))
                    throw IllegalArgumentException("Invalid type: $type")
                }
            }

            val matchResult = pattern.find(str)

            if (matchResult != null) {
                val (sessionNumber, amount, currencyCode, decimals, dateTime, ecrId, operatorNumber,
                    receiptNumber, customData, mac) = matchResult.destructured

                val currencySymbol = euCurrencySymbols[currencyCode.toInt()] ?: ""
                val parsedDateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))

                val actualAmount = amount.toDouble() / 10.0.pow(decimals.toInt())

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
//            Logger.logToFile(e.stackTrace.firstOrNull()?.methodName + e.message)
//            MessageHandler.getInstance()
//                .sendMessage(createErrorResponseMessage(Constants.mappedErrors[Constants.PROTOCOL_NOT_SUPPORTED]!!))
            throw IllegalArgumentException("Invalid input string format: ${e.message}")
        }
    }
}



