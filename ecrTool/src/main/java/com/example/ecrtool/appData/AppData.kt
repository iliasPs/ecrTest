package com.example.ecrtool.appData

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.ecrtool.models.mk.MkResponseModel
import com.example.ecrtool.models.trafficEcr.AckResultRequest
import com.example.ecrtool.models.trafficEcr.ResultResponse
import com.example.ecrtool.models.trafficToPos.MyEcrEftposInit
import com.example.ecrtool.utils.FeatureStore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.channels.Channel


/**
 * This class acts as a data holder for everything that needs to be resolved through the processes
 */
object AppData {

    private var masterKey: MkResponseModel = MkResponseModel()
    private var terminalId: String = ""
    private var appVersion: String = ""
    private var localSk: String = ""
    private var vatNumber: String = ""
    private var apiKey: String = ""
    private var MAN: String = ""
    private var myEcrEftposInit: MyEcrEftposInit? = null
    private var ecrNumber: String = ""
    private var ackResultRequestChannel = Channel<AckResultRequest>()
    private var protocolVariant: String = ""
    private var protocolVersion: String = ""
    private var validateMk: Boolean = false
    private var ecrSK: String = ""
    private lateinit var sharedPreferences: SharedPreferences



    fun setAppData(init: MyEcrEftposInit) {
        this.terminalId = init.TID
        this.appVersion = init.appVersion
        this.vatNumber = init.vatNumber
        this.apiKey = init.apiKey
        this.MAN = init.MAN
        this.myEcrEftposInit = init
        this.validateMk = init.validateMk
        init.isCoreVersion.let { FeatureStore.setVersion(it) }
    }

    fun getValidateMk(): Boolean {
        return validateMk
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun getAckResultRequestChannel(): Channel<AckResultRequest> {
        return if (ackResultRequestChannel.isClosedForReceive || ackResultRequestChannel.isClosedForSend) {
            ackResultRequestChannel = Channel<AckResultRequest>()
            ackResultRequestChannel
        } else{
            ackResultRequestChannel
        }
    }

    fun setEcrNumber(number: String) {
        sharedPreferences.edit().putString("ecrNumber", number).apply()
    }

    fun getEcrNumber(): String {
        return sharedPreferences.getString("ecrNumber", "") ?: ""
    }

    fun getMyEcrEftposInit(): MyEcrEftposInit? {
        return this.myEcrEftposInit
    }


    fun getMk(): String {
        return sharedPreferences.getString("posMK", "") ?: ""
    }

    fun setMasterKey(value: MkResponseModel) {
        masterKey = value
    }

    fun getTerminalId(): String {
        return terminalId
    }

    fun setTerminalId(value: String) {
        terminalId = value
    }

    fun getAppVersion(): String {
        return appVersion
    }

    fun setAppVersion(value: String) {
        appVersion = value
    }

    fun getLocalSk(): String {
        return sharedPreferences.getString("localSk", "") ?: ""
    }

    fun setLocalSk(value: String) {
        sharedPreferences.edit().putString("localSk", value).apply()
    }

    fun getVatNumber(): String {
        return vatNumber
    }

    fun setVatNumber(value: String) {
        vatNumber = value
    }

    fun getApiKey(): String {
        return apiKey
    }

    fun setApiKey(value: String) {
        apiKey = value
    }

    fun getMAN(): String {
        return MAN
    }

    fun setMAN(value: String) {
        MAN = value
    }

    fun setMK(data: MkResponseModel) {
        sharedPreferences.edit().putString("posMK", data.MACKEY).apply()
    }

    fun setProtocolVersion(version: String) {
        this.protocolVersion = version
    }

    fun setProtocolVariant(variant: String) {
        this.protocolVariant = variant
    }

    fun getProtocolVersion(): String {
        return this.protocolVersion
    }

    fun getProtocolVariant(): String {
        return this.protocolVariant
    }

    fun setEcrSK(ecrSK: String) {
        sharedPreferences.edit().putString("ecrSK", ecrSK).apply()
    }

    // Retrieve the ecrSK property
    fun getEcrSK(): String {
        return sharedPreferences.getString("ecrSK", "") ?: ""
    }

    fun initEncryptedSharedPrefs(context: Context): SharedPreferences {

        sharedPreferences = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            EncryptedSharedPreferences.create(
                context,
                "ecr_shared_prefs",
                getMasterKey(context),
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } else {
            context.getSharedPreferences(
                "ecr_shared_prefs",
                Context.MODE_PRIVATE
            )
        }
        return sharedPreferences
    }

    private fun getMasterKey(context: Context): MasterKey {
        return MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }
}