package com.example.ecrtest

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.example.ecrtool.server.MyEcrServerSingleton
import com.example.ecrtool.utils.Constants
import com.example.ecrtool.utils.Utils
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.conn.util.InetAddressUtils
import kotlinx.coroutines.launch
import java.io.File
import java.net.NetworkInterface

class MainActivity : AppCompatActivity(), AppMessenger {

    private lateinit var binding: ActivityMainBinding
    private val dt = DataTransformer.getInstance()
    private lateinit var ecrServer: EcrToPosMain
    private val items = mutableListOf<Message>()
    val adapter = MyRecyclerViewAdapter()
    private var myEcrEftposInit: MyEcrEftposInit? = null


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val ftpos = MyEcrEftposInit(
//            port = 5566,
//            appListener = this,
//            isCoreVersion = true,
//            TID = "80011693",
//            vatNumber = "979703476",
//            apiKey = "pubAGQR@XNzSk%b&+X!A?h?HJUVVhPHlyv/acPq0uKHQ#dEc3B85en%AXHiX2i8&",
//            MAN = "fintechiq_dok",
//            appVersion = "1.0.0",
//            validateMk = true
//        )

        binding.includedLayout.ipTv.text = "My IP: ${getWifiIPAddress()}"

        AppData.initEncryptedSharedPrefs(this)

        val recyclerView: RecyclerView = binding.itemsRv
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        recyclerView.adapter = adapter

        val itemDecorator = ItemDecorator(resources.getDimensionPixelSize(R.dimen.item_spacing))
        recyclerView.addItemDecoration(itemDecorator)

//        EcrToPosMain.initialize(ftpos, this)
//        ecrServer = EcrToPosMain.getInstance()

        binding.includedLayout.buttonFirst.setOnClickListener {

            myEcrEftposInit = MyEcrEftposInit(
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
            EcrToPosMain.initialize(myEcrEftposInit!!, this)
            ecrServer = EcrToPosMain.getInstance()
            val echoRequest =
                dt.parseEchoRequest(Utils.extractMessage("??�ECR0110X/CFB77000028"))
            Log.d("TAG", "onCreate: EchoRequest :$echoRequest")

            val response = Utils.generateMessage(
                dt.createEchoResponseMessage(
                    EchoResponse(
                        text = echoRequest?.text,
                        terminalId = AppData.getTerminalId(),
                        appVersion = AppData.getAppVersion()
                    )
                )
            )
            Log.d("TAG", "onCreate  EchoResponse : $response")


            val amountRequest = dt.parseAmountRequest(
                Utils.extractMessage(".QECR0210A/S001008/F2500:978:2/D20220524102517/RABC00111222/H121/T1020/M0/Q59D19E7D"),
                Constants.TYPE_AMOUNT_SALE_REQUEST
            )

            Log.d("TAG", "onCreate  amountRequest : $amountRequest")


            val confirmResponse = Utils.generateMessage(
                dt.createConfirmationResponse(
                    ConfirmationResponse(
                        receiptNumber = amountRequest.receiptNumber,
                        sessionNumber = amountRequest.sessionNumber,
                        amount = amountRequest.amount,
                        ecrId = amountRequest.ecrId,
                        decimals = amountRequest.decimals
                    )
                )
            )

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
//
//            val ackResultRequest =
//                dt.parseAckResultRequest(Utils.extractMessage(".QECR0110R/S001008/RABC00111222/F2500/T1020"))
//            Log.d("TAG", "onCreate: ackResultRequest :$ackResultRequest")
//
//            val regReceiptRequest =
//                dt.parseRegReceiptRequest(Utils.extractMessage(".QECR0110W/S001573/F5000:978:2/D20220711105009/RABC00111222/H121/T1228/M0/Q30ADD8A3"))
//            Log.d("TAG", "onCreate: regReceiptRequest :$regReceiptRequest")
//
//            val resendRequest =
//                dt.parseResendRequest(Utils.extractMessage(".8ECR0110O/S001058/F150:978:2/RABC00111222/T1051/QF7167A9F"))
//            Log.d("TAG", "onCreate: resendRequest :$resendRequest")
//
//            val resendAllRequest =
//                dt.parseResendAllRequest(Utils.extractMessage("./ECR0110L/RABC00111222/D20220711110645/Q6C483FCE"))
//            Log.d("TAG", "onCreate: resendAllRequest :$resendAllRequest")
//
//            val controlRequest =
//                dt.parseControlRequest(Utils.extractMessage(".#ECR0210U/RABC00111222/CUNBIND_POS:1"))
//            Log.d("TAG", "onCreate: controlRequest :$controlRequest")
//
//            val controlRequestMK =
//                dt.parseControlRequest(Utils.extractMessage(".DECR0210U/RABC00111222/CMAC_K:1ED9F7AE0B2509281BBC2DE38EF2A12B:CC5FFF"))
//            Log.d("TAG", "onCreate: controlRequestMK :$controlRequestMK")
//
//
//            MyEcrServerSingleton.getInstance()
//                .onMessageReceived(".QECR0210A/S001008/F2500:978:2/D20220524102517/RABC00111222/H121/T1020/M0/Q59D19E7D")

            //MyEcrServerSingleton.getInstance().onMessageReceived(".QECR0110X/INIT:CFB77000028")

            MyEcrServerSingleton.getInstance().onMessageReceived("??�ECR0110X/CFB77000028:1234")
//            MyEcrServerSingleton.getInstance().onMessageReceived(".DECR0210U/RABC00111222/CMAC_K:1ED9F7AE0B2509281BBC2DE38EF2A12B:CC5FFF")
        }

        binding.includedLayout.button2.setOnClickListener { lifecycleScope.launch { ecrServer.startServer() } }
        binding.includedLayout.button3.setOnClickListener {
            lifecycleScope.launch {
                lifecycleScope.launch {
                    this.launch {
                        ProcessFlow.getInstance().callAade()
                    }
                }
            }
        }
        binding.includedLayout.button4.setOnClickListener {
            openFragment()

        }
    }

    override fun sendToApp(data: Any) {
        when (data) {
            is String -> {
                runOnUiThread {
                    if (data.contains("FROM ECR")) {
                        val modifiedData = data.replace("FROM ECR:", "").trim()
                        adapter.addItem(
                            Message(
                                content = modifiedData,
                                messageType = MessageType.INCOMING
                            )
                        )
                    } else {
                        adapter.addItem(Message(content = data, messageType = MessageType.OUTGOING))

                    }
                }
            }
            is AmountRequest -> {
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

    private fun openFragment() {
        binding.itemsRv.visibility = View.GONE
        val fragment = InputFragment.newInstance() // Instantiate your custom fragment
        val fragmentManager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()

        // Set the animation for entering the fragment (right to left)
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)

        binding.includedLayoutContainer.visibility = View.GONE
        transaction.replace(
            R.id.fragmentContainer,
            fragment,
            "input"
        )
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun saveAndStartServer(myEcrEftposInit: MyEcrEftposInit) {
        binding.itemsRv.visibility = View.VISIBLE
        this.myEcrEftposInit = myEcrEftposInit
        EcrToPosMain.initialize(myEcrEftposInit.copy(isCoreVersion = true), this)
        ecrServer = EcrToPosMain.getInstance()
        binding.includedLayoutContainer.visibility = View.VISIBLE
        binding.includedLayout.portTv.text = "Port: ${myEcrEftposInit.port.toString()}"
        EcrToPosMain.initialize(myEcrEftposInit, this)
    }

    private fun getWifiIPAddress(): String? {
        try {
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            while (networkInterfaces.hasMoreElements()) {
                val networkInterface = networkInterfaces.nextElement()
                val inetAddresses = networkInterface.inetAddresses
                while (inetAddresses.hasMoreElements()) {
                    val inetAddress = inetAddresses.nextElement()
                    if (!inetAddress.isLoopbackAddress && InetAddressUtils.isIPv4Address(inetAddress.hostAddress)) {
                        return inetAddress.hostAddress
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu);
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (myEcrEftposInit != null) {
            if (item.itemId == R.id.action_info) {
                showInfoDialog()
                return true
            }
        }

        if (!adapter.getItems().isNullOrEmpty()) {
            if (item.itemId == R.id.action_share)
                shareItems(this, adapter.getItems())
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showInfoDialog() {
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.custom_dialog_layout, null)

        val portView = dialogView.findViewById<TextView>(R.id.port_value)
        val appListenerView = dialogView.findViewById<TextView>(R.id.app_listener_view)
        val tidView = dialogView.findViewById<TextView>(R.id.tid)
        val vatNoView = dialogView.findViewById<TextView>(R.id.vatNo)
        val apiKeyView = dialogView.findViewById<TextView>(R.id.apiKey)
        val manView = dialogView.findViewById<TextView>(R.id.man)
        val appVersionView = dialogView.findViewById<TextView>(R.id.appVersion)

        portView.text = myEcrEftposInit?.port.toString()
        appListenerView.text = myEcrEftposInit?.appListener!!::class.java.simpleName
        tidView.text = myEcrEftposInit?.TID
        vatNoView.text = myEcrEftposInit?.vatNumber
        apiKeyView.text = myEcrEftposInit?.apiKey
        manView.text = myEcrEftposInit?.MAN
        appVersionView.text = myEcrEftposInit?.appVersion

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)

        // Add an "OK" button to dismiss the dialog
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun shareItems(context: Context, items: MutableList<Message>) {
        // 1. Convert your list of items to a formatted string
        val formattedData = items.joinToString("\n") { item ->
            // Customize how each item is formatted
            "${item.messageType}, ${item.content}"
        }

        // 2. Create a temporary file to write the data
        val file = File(context.cacheDir, "shared_items.txt")
        file.writeText(formattedData)

        // 3. Create a content URI for the file using FileProvider
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.MainActivity",
            file
        )

        // 4. Create an Intent to share the file
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        val chooserIntent = Intent.createChooser(shareIntent, "Share Items")
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(chooserIntent)
    }

    override fun onBackPressed() {

        val fragment = supportFragmentManager.findFragmentByTag("input")

        if (fragment != null && fragment.isVisible) {
            binding.includedLayoutContainer.visibility = View.VISIBLE
            binding.itemsRv.visibility = View.VISIBLE
            (fragment as InputFragment).dismissFragment()
        } else {
            // The fragment is not visible
        }
    }

}