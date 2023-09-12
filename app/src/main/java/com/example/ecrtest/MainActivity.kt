package com.example.ecrtest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.ecrtest.databinding.ActivityMainBinding
import com.example.ecrtool.EcrToPosMain
import com.example.ecrtool.appData.AppData
import com.example.ecrtool.dataHandle.DataTransformer
import com.example.ecrtool.dataHandle.ProcessFlow
import com.example.ecrtool.listeners.AppMessenger
import com.example.ecrtool.models.trafficEcr.AmountRequest
import com.example.ecrtool.models.trafficEcr.ConfirmationResponse
import com.example.ecrtool.models.trafficEcr.EchoResponse
import com.example.ecrtool.models.trafficEcr.ResultResponse
import com.example.ecrtool.models.trafficToPos.MyEcrEftposInit
import com.example.ecrtool.models.trafficToPos.PaymentToPosResult
import com.example.ecrtool.utils.Constants
import com.example.ecrtool.utils.Utils
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), AppMessenger {

    private lateinit var binding: ActivityMainBinding
    private val dt = DataTransformer.getInstance()
    private lateinit var ecrServer : EcrToPosMain

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val ftpos = MyEcrEftposInit(
            port = 5566,
            appListener = this,
            isCoreVersion = true,
            TID = "80011693",
            vatNumber = "979703476",
            apiKey = "pubAGQR@XNzSk%b&+X!A?h?HJUVVhPHlyv/acPq0uKHQ#dEc3B85en%AXHiX2i8&",
            MAN = "fintechiq_dok",
            appVersion = "1.0.0",
            validateMk = true
        )

        AppData.initEncryptedSharedPrefs(this)



        EcrToPosMain.initialize(ftpos, this)
        ecrServer = EcrToPosMain.getInstance()

        binding.buttonFirst.setOnClickListener {
            val echoRequest = dt.parseEchoRequest(Utils.extractMessage("??�ECR0110X/CFB77000028:1234"))
            Log.d("TAG", "onCreate: EchoRequest :$echoRequest")

            val  response = Utils.generateMessage(dt.createEchoResponseMessage(
                EchoResponse(
                    text = echoRequest?.text,
                    terminalId = AppData.getTerminalId(),
                    appVersion = AppData.getAppVersion()
                )
            ))
            Log.d("TAG", "onCreate  EchoResponse : $response")


            val amountRequest = dt.parseAmountRequest(Utils.extractMessage(".QECR0210A/S001008/F2500:978:2/D20220524102517/RABC00111222/H121/T1020/M0/Q59D19E7D"), Constants.TYPE_AMOUNT_SALE_REQUEST)

            Log.d("TAG", "onCreate  amountRequest : $amountRequest")


            val confirmResponse = Utils.generateMessage(dt.createConfirmationResponse(
                ConfirmationResponse(
                    receiptNumber = amountRequest.receiptNumber,
                    sessionNumber = amountRequest.sessionNumber,
                    amount = amountRequest.amount,
                    ecrId = amountRequest.ecrId,
                    decimals = amountRequest.decimals
                )
            ))

            Log.d("TAG", "onCreate  confirmResponse : $confirmResponse")


            val resultResponse = dt.createResultResponse(
                ResultResponse(
                    receiptNumber = amountRequest.receiptNumber,
                    sessionNumber = amountRequest.sessionNumber,
                    amount = amountRequest.amount,
                    ecrId = amountRequest.ecrId,
                    _rspCode = "00"
                ), false
            )

            Log.d("TAG", "onCreate  resultResponse : $resultResponse")

            val ackResultRequest = dt.parseAckResultRequest(Utils.extractMessage(".QECR0110R/S001008/RABC00111222/F2500/T1020"))
            Log.d("TAG", "onCreate: ackResultRequest :$ackResultRequest")

            val regReceiptRequest = dt.parseRegReceiptRequest(Utils.extractMessage(".QECR0110W/S001573/F5000:978:2/D20220711105009/RABC00111222/H121/T1228/M0/Q30ADD8A3"))
            Log.d("TAG", "onCreate: regReceiptRequest :$regReceiptRequest")

            val resendRequest = dt.parseResendRequest(Utils.extractMessage(".8ECR0110O/S001058/F150:978:2/RABC00111222/T1051/QF7167A9F"))
            Log.d("TAG", "onCreate: resendRequest :$resendRequest")

            val resendAllRequest = dt.parseResendAllRequest(Utils.extractMessage("./ECR0110L/RABC00111222/D20220711110645/Q6C483FCE"))
            Log.d("TAG", "onCreate: resendAllRequest :$resendAllRequest")

            val controlRequest = dt.parseControlRequest(Utils.extractMessage(".#ECR0210U/RABC00111222/CUNBIND_POS:1"))
            Log.d("TAG", "onCreate: controlRequest :$controlRequest")

            val controlRequestMK = dt.parseControlRequest(Utils.extractMessage(".DECR0210U/RABC00111222/CMAC_K:1ED9F7AE0B2509281BBC2DE38EF2A12B:CC5FFF"))
            Log.d("TAG", "onCreate: controlRequestMK :$controlRequestMK")

            //MyEcrServerSingleton.getInstance().onMessageReceived(".QECR0210A/S001008/F2500:978:2/D20220524102517/RABC00111222/H121/T1020/M0/Q59D19E7D")

            //MyEcrServerSingleton.getInstance().onMessageReceived(".QECR0110X/INIT:CFB77000028")

//            MyEcrServerSingleton.getInstance().onMessageReceived("??�ECR0110X/CFB77000028:1234")
//            MyEcrServerSingleton.getInstance().onMessageReceived(".DECR0210U/RABC00111222/CMAC_K:1ED9F7AE0B2509281BBC2DE38EF2A12B:CC5FFF")
        }

        binding.button2.setOnClickListener{ lifecycleScope.launch {ecrServer.startServer()}}
        binding.button3.setOnClickListener { lifecycleScope.launch { ecrServer.stopServer() } }
        binding.button4.setOnClickListener { lifecycleScope.launch { this.launch {
            ProcessFlow.getInstance().callAade()
        } } }
    }

    override fun sendToApp(data: Any) {
        if(data is AmountRequest) {
            ecrServer.sendMessageToEcr(
                PaymentToPosResult(
                    success = true,
                    code = "00",
                    cardType = "Mastercard",
                    txnType = "00",
                    cardPanMasked = Utils.createMaskedPan("000012300412341234"),
                    amountFinal = data.amount,
                    amountTip = 0.0,
                    amountLoyalty = 0.0,
                    amountCashBack = 0.0,
                    bankId = "1",
                    batchNum = "2",
                    rrn = "133030119089",
                    stan = Utils.generateRandomStan(),
                    authCode = Utils.generateRandomAuthCode(),
                    transDateTime = Utils.generateTransactionDateTime(),
                    receiptNumber = data.receiptNumber,
                    sessionNumber = data.sessionNumber,
                    amount = data.amount,
                    ecrId = data.ecrId,
                    rspCode = "00",
                    transactionEcrStatus = "0",
                    prnData = "adsf",
                    terminalId = "80011693",
                    decimalPoints = data.decimals,
                    customData = data.customData
                )
            )
        }
    }
}