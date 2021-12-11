package com.example.swift.frontEnd.activities

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import com.example.swift.R
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation =  (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        setContentView(R.layout.activity_sign_in)

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

        login_button.setOnClickListener{
            startActivity(Intent(this, ForRiderDriverOfferListAndChatActivity::class.java))
        }
    }
}