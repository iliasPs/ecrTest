package com.example.ecrtool.utils

import com.example.ecrtool.appData.AppData
import com.example.ecrtool.models.trafficEcr.AmountRequest
import com.example.ecrtool.models.trafficEcr.TransData
import com.example.ecrtool.models.trafficToPos.PaymentToPosResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bouncycastle.crypto.CipherParameters
import org.bouncycastle.crypto.engines.DESEngine
import org.bouncycastle.crypto.macs.CBCBlockCipherMac
import org.bouncycastle.crypto.params.KeyParameter
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.nio.ByteBuffer
import java.security.Security
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESedeKeySpec
import javax.crypto.spec.SecretKeySpec
import kotlin.math.pow
import kotlin.random.Random

object Utils {

    // Method to create a masked PAN
    fun createMaskedPan(pan: String?): String {
        if (pan == null || pan.isEmpty()) {
            return "null"
        }

        // Get the first 6 and last 4 digits of the PAN
        val firstSix = pan.take(4)
        val lastFour = pan.takeLast(4)

        // Replace the remaining digits with asterisks
        val numAsterisks = pan.length - 10
        val asterisks = "*".repeat(numAsterisks)

        // Combine the masked digits and return the result
        return "$firstSix$asterisks$lastFour"
    }

    /**
     * Checks if all fields of the given object are non-null and non-empty.
     *
     * @param obj The object to check.
     * @return True if all fields are filled, false otherwise.
     */
    fun validateRequest(obj: Any): Boolean {
        val fields = obj.javaClass.declaredFields
        for (field in fields) {
            field.isAccessible = true
            val value = field.get(obj)
            if (value == null || value.toString().isEmpty()) {
                return false
            }
        }
        return true
    }

    fun getTransData(
        amountRequest: AmountRequest,
        paymentToPosResult: PaymentToPosResult
    ): TransData {
        return TransData(
            cardType = paymentToPosResult.cardType,
            txnType = paymentToPosResult.txnType,
            cardPanMasked = paymentToPosResult.cardPanMasked,
            amountFinal = paymentToPosResult.amountFinal,
            amountTip = paymentToPosResult.amountTip,
            amountLoyalty = paymentToPosResult.amountLoyalty,
            amount = amountRequest.amount,
            amountCashBack = paymentToPosResult.amountCashBack,
            bankId = paymentToPosResult.bankId,
            terminalId = AppData.getTerminalId(),
            batchNumber = paymentToPosResult.batchNum,
            rrn = paymentToPosResult.rrn,
            stan = paymentToPosResult.stan,
            authCode = paymentToPosResult.authCode,
            transactionDateTime = paymentToPosResult.transDateTime,
        )
    }

    fun generateTransactionDateTime(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        return currentDateTime.format(formatter)
    }

    fun String.toHex(): String {
        val hexChars = "0123456789ABCDEF"
        val builder = StringBuilder()
        for (ch in this) {
            val hex = Integer.toHexString(ch.code)
            if (hex.length == 1) {
                builder.append('0')
            }
            builder.append(hex.uppercase(Locale.ROOT))
        }
        return builder.toString()
    }


    fun generateMessage(message: String): String {

        return buildString {
            append(Constants.DIRECTION)
            append(AppData.getProtocolVariant())
            append(AppData.getProtocolVersion())
            append(message)

            val buffer = ByteBuffer.allocate(2)
            buffer.putShort(message.length.toShort())
            insert(0, buffer.array().decodeToString())
        }
    }

    fun String?.nonNull(defaultValue: String = ""): String {
        if (this == null) {
            return defaultValue
        }
        return this
    }

    /**
     * Decodes the session key (SK) using Triple DES (T-DES) with CBC mode and a double-length key.
     *
     * @param masterKey The master key (MK) used for decryption. Must be a byte array with a length of 16 bytes.
     * @param encryptedSessionKey The encrypted session key (SK) to be decoded.
     * @return The decoded session key as a byte array, or null if an error occurs during decryption.
     */
    fun decodeSessionKey(masterKey: ByteArray, encryptedSessionKey: ByteArray): ByteArray? {
        try {
            val finalKey = extentKeyTo3DES(masterKey)

            val keySpec = SecretKeySpec(masterKey, "DESede")
            val cipher: Cipher = Cipher.getInstance("DESede/ECB/NoPadding")

            cipher.init(Cipher.DECRYPT_MODE, keySpec)
            return cipher.doFinal(finalKey)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun validateMk(message: String): Boolean {
        // Extract the MAC field from the message
        var macField = message.substringAfterLast("/Q", "")
        macField = ""
        // If the message doesn't contain "/Q", consider it as a message that its not encrypted and return true
        if (macField.isEmpty()) {
            return true
        }

        // Extract the message without the MAC field
        val messageWithoutMac = message.substringBeforeLast("/Q")

        // Perform MAC calculation on the message (without the MAC field)
        val calculatedMac = calculateMac(messageWithoutMac, AppData.getMk())

        // Compare the calculated MAC with the extracted MAC field
        return macField.equals(calculatedMac, ignoreCase = true)
    }

    private fun extentKeyTo3DES(key: ByteArray): ByteArray {
        // Check if the key length is less than 24 bytes
        if (key.isEmpty()) {
            throw IllegalArgumentException("Key must not be empty.")
        }

        val buf = ByteArray(24)
        for (i in 0 until 24) {
            buf[i] = key[i.rem(key.size)]
        }
        return buf
    }
    fun calculateMac(message: String, masterKeyHex: String): String {
        // Initialize the Bouncy Castle provider
        Security.addProvider(BouncyCastleProvider())

        // Convert the master key hex string to bytes
        val masterKeyBytes = masterKeyHex.hexStringToByteArray()

        // Create the T-DES key
        val desedeKeySpec = DESedeKeySpec(masterKeyBytes)
        val keyFactory = SecretKeyFactory.getInstance("DESede")
        val secretKey = keyFactory.generateSecret(desedeKeySpec)

        // Create the MAC instance
        val cipher = DESEngine()
        val mac = CBCBlockCipherMac(cipher)

        // Initialize the MAC with the secret key
        val keyParam = KeyParameter(secretKey.encoded)
        val parameters: CipherParameters = keyParam
        mac.init(parameters)

        // Calculate the MAC for the message
        val messageBytes = message.toByteArray()
        mac.update(messageBytes, 0, messageBytes.size)

        // Generate the MAC
        val macBytes = ByteArray(mac.macSize)
        mac.doFinal(macBytes, 0)

        // Convert the MAC bytes to a hexadecimal string
        return macBytes.toHexString()
    }

    fun generateRandomStan(): String {
        val stan = Random.nextInt(1, 999999)
        return stan.toString().padStart(6, '0')
    }

    fun generateRandomAuthCode(): String {
        val authCodeLength = 6
        val authCode = StringBuilder(authCodeLength)
        val random = Random.Default

        repeat(authCodeLength) {
            val digit = random.nextInt(10)
            authCode.append(digit)
        }

        return authCode.toString()
    }

    fun extractMessage(message: String): String {
        val prefixes = listOf(
            "A/", "R/", "W/", "O/", "L/", "U/",
            "I/", "Z/", "V/", "P/", "M/", "X/"
        )
        val startIndex = findPrefixIndex(message, prefixes)

        if (startIndex == -1) {
            throw IllegalArgumentException("Message does not contain any valid prefix.")
        }

        val startOfDetails = message.indexOf("ECR")

        if (startOfDetails == -1) {
            throw IllegalArgumentException("Message does not contain any valid details.")
        }

        val detailsMessage = message.substring(startOfDetails)

        val relevantMessage = message.substring(startIndex)

        // Extracting direction (characters 2-4)
        val direction = detailsMessage.substring(0, 3)

        // Extracting protocol variant (characters 5-6)
        val variant = detailsMessage.substring(3, 5)

        // Extracting protocol version (characters 7-8)
        val version = detailsMessage.substring(5, 7)

        // Extracting main message (characters 9 and onwards)

        AppData.setProtocolVariant(variant)
        AppData.setProtocolVersion(version)

        return relevantMessage
    }


    private fun findPrefixIndex(message: String, prefixes: List<String>): Int {
        for (prefix in prefixes) {
            val index = message.indexOf(prefix)

            if (index != -1 && message.substring(index, index + prefix.length) == prefix) {
                return index
            }
        }
        return -1
    }

    fun String.hexStringToByteArray(): ByteArray {
        val len = length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(this[i], 16) shl 4) + Character.digit(this[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

    fun ByteArray.toHexString(): String {
        return joinToString("") { "%02X".format(it) }
    }

    suspend fun <T> executeDbQuery(query: suspend () -> T): T {
        return withContext(Dispatchers.IO) {
            query()
        }
    }


    fun reverseAmount(amount: Double, decimalPoints: Int): String {
        val reversedAmount = amount * 10.0.pow(decimalPoints)
        return String.format("%.0f", reversedAmount)
    }

    fun calculateActualAmount(amountStr: String, decimalPoints: Int): Double {
        val amount = amountStr.toDouble()
        return amount / 10.0.pow(decimalPoints)
    }
}