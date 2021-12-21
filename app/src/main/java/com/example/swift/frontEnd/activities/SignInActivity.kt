package com.example.swift.frontEnd.activities

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.swift.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_reset_password.*
import kotlinx.android.synthetic.main.activity_send_otp_for_password_reset.*
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation =  (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        setContentView(R.layout.activity_sign_in)
        supportActionBar?.hide()

        var html = "<u><b>Create One<b></u>"
        signIn_createBtn.text = Html.fromHtml(html)

        html = "<u>Forgot Password?</u>"
        signIn_forgotPasswordBtn.text = Html.fromHtml(html)

        signIn_createBtn.setOnClickListener{
            startActivity(Intent(this, RiderRegisterActivity::class.java))
        }

        signIn_forgotPasswordBtn.setOnClickListener{
            startActivity(Intent(this, SendOtpForPasswordResetActivity::class.java))
        }

        //checking phone number
        //link to check the functions of this input method
        //https://reposhub.com/android/textview-edittext/AlmogBaku-IntlPhoneInput.html
        if(signIn_phoneNumberInput.isValid){
            //get value of the phone number
        }
        signIn_phoneNumberInput.setOnKeyboardDone { _, isValid ->
            //TODO: after checking valid inputs redirect to home page

            if(isValid){
            }

        }

        login_button.setOnClickListener {
            if (isInputsValid()) {
                val phoneNumber = signIn_phoneNumberInput.text?.toString()
                val password = signIn_password_input.text?.toString()
                var db = FirebaseFirestore.getInstance()

                db.collection("Rider").whereEqualTo("phoneNumber", phoneNumber).get()
                    .addOnSuccessListener { doc ->
                        if (doc.size() == 0) {
                            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                        } else {
                            val passwordToVerify = doc.documents[0].data?.get("Password")
                            if (passwordToVerify == password) {
                                //make intent and go to next activity
                                val intent = Intent(this, RiderMainActivity::class.java)
                                //store data to send to home page if any
                                //go to next activity
                                startActivity(intent)
                            }
                        }
                    }

                //+startActivity(Intent(this, RiderMainActivity::class.java))
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if(FirebaseAuth.getInstance().currentUser!=null){
            startActivity(Intent(this, RiderMainActivity::class.java))
        }
    }


    //check whether the inputs are valid or not,
    // and show errors
    private fun isInputsValid(): Boolean {
        var flag = true
        when {
            signIn_phoneNumberInput.text == null -> {
                showPhoneError()
                Toast.makeText(applicationContext, "Enter Mobile", Toast.LENGTH_SHORT).show()
                flag = false
            }
            signIn_phoneNumberInput.text.toString().trim().isEmpty() -> {
                showPhoneError()
                Toast.makeText(applicationContext, "Enter Mobile", Toast.LENGTH_SHORT).show()
                flag = false
            }
            else -> {
                removePhoneError()
            }
        }
        when{
            signIn_password_input.text?.length!! < 8 ->{
                signIn_password_layout.error = "Minimum Password length should be 8"
                flag = false
            }
            signIn_password_input.text?.length!! >= 8 -> {
                signIn_password_layout.error = null
                signIn_password_layout.isErrorEnabled = false
            }
        }
            return flag
    }

    private fun showPhoneError(){
        val border = GradientDrawable()
        border.setStroke(3, -0x4f0000) //red border with full opacity
        border.cornerRadius = 50F
        signIn_phoneNumberInput.setBackgroundDrawable(border)
    }
    private fun removePhoneError(){
        val border = GradientDrawable()
        border.setStroke(3, -0xFFA500) //reset color
        border.cornerRadius = 50F
        signIn_phoneNumberInput.setBackgroundDrawable(border)
    }
}