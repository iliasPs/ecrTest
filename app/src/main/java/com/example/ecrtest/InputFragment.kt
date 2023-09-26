package com.example.ecrtest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.example.ecrtool.models.trafficToPos.MyEcrEftposInit

/**
 * A simple [Fragment] subclass.
 * Use the [InputFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InputFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_input, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment InputFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = InputFragment()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview
    @Composable
    fun MyFragmentContent() {
        var port by rememberSaveable { mutableStateOf("port") }
        var TID by rememberSaveable { mutableStateOf("TID") }
        var vatNumber by rememberSaveable { mutableStateOf("vatNumber") }
        var apikey by rememberSaveable { mutableStateOf("apikey") }
        var MAN by rememberSaveable { mutableStateOf("MAN") }
        var appVersion by rememberSaveable { mutableStateOf("appVersion") }


        val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Blue,     // Change text color to blue when focused
            unfocusedBorderColor = Color.Gray    // Change text color to gray when unfocused
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp) // Adjust padding as needed
        ) {
            OutlinedTextField(
                value = port,
                onValueChange = {
                    port = it
                },
                label = { Text("Enter PORT") },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White) // Background color of the field
                    .padding(8.dp), // Padding for the outline
                colors = textFieldColors
            )

            Spacer(modifier = Modifier.height(16.dp)) // Add some spacing between the TextField and the Button
            OutlinedTextField(
                value = TID,
                onValueChange = {
                    TID = it
                },
                label = { Text("Enter TID") },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White) // Background color of the field
                    .padding(8.dp), // Padding for the outline
                colors = textFieldColors
            )

            Spacer(modifier = Modifier.height(16.dp)) // Add some spacing between the TextField and the Button
            OutlinedTextField(
                value = vatNumber,
                onValueChange = {
                    vatNumber = it
                },
                label = { Text("Enter VAT NUMBER") },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White) // Background color of the field
                    .padding(8.dp), // Padding for the outline
                colors = textFieldColors
            )

            Spacer(modifier = Modifier.height(16.dp)) // Add some spacing between the TextField and the Button
            OutlinedTextField(
                value = apikey,
                onValueChange = {
                    apikey = it
                },
                label = { Text("Enter API KEY") },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White) // Background color of the field
                    .padding(8.dp), // Padding for the outline
                colors = textFieldColors
            )

            Spacer(modifier = Modifier.height(16.dp)) // Add some spacing between the TextField and the Button
            OutlinedTextField(
                value = MAN,
                onValueChange = {
                    MAN = it
                },
                label = { Text("Enter MAN") },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White) // Background color of the field
                    .padding(8.dp), // Padding for the outline
                colors = textFieldColors
            )

            Spacer(modifier = Modifier.height(16.dp)) // Add some spacing between the TextField and the Button
            OutlinedTextField(
                value = appVersion,
                onValueChange = {
                    appVersion = it
                },
                label = { Text("Enter APP VERSION") },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White) // Background color of the field
                    .padding(8.dp), // Padding for the outline
                colors = textFieldColors
            )

            Spacer(modifier = Modifier.height(16.dp)) // Add some spacing between the TextField and the Button

            Button(
                onClick = {
                    (requireActivity() as MainActivity).saveInput(MyEcrEftposInit(
                        port = port.toInt(),
                        TID = TID,
                        vatNumber = vatNumber,
                        apiKey = apikey,
                        MAN = MAN,
                        appListener = requireActivity() as MainActivity,
                        appVersion = appVersion,
                        validateMk = false
                    ))
                    dismissFragment()
                },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text("Submit")
            }
        }
    }

    private fun dismissFragment() {
        val fragmentManager = requireActivity().supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.exit_to_left, 0)
        transaction.remove(this)
        transaction.commit()
    }

}
