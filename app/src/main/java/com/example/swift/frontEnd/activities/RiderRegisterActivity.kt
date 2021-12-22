package com.example.swift.frontEnd.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Html
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.example.swift.R
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_rider_register.*
import java.util.concurrent.TimeUnit

class RiderRegisterActivity : AppCompatActivity() {
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation =  (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        setContentView(R.layout.activity_rider_register)
        supportActionBar?.hide()

        val items = listOf("Male", "Female", "Other")
        val adapter = ArrayAdapter(this, R.layout.gender_dropdown_list_item, items)
        registerRider_genderInput.setAdapter(adapter)

        var html = "<u><b>Login<b></u>"
        backToLogin_button.text = Html.fromHtml(html)

        backToLogin_button.setOnClickListener {
            this.finish()
        }


        registerRider_button.setOnClickListener{
            if(isInputsValid()) {
                val name = registerRider_nameInput.text?.toString()
                val gender = registerRider_genderInput.text?.toString()
                val email = registerRider_emailInput.text?.toString()
                val password = registerRider_passwordInput.text?.toString()
                val phoneNumber = registerRider_phoneNumberInput.text?.toString()

                FirebaseApp.initializeApp(this)
                var flag = 0
                val options = PhoneAuthOptions.newBuilder(Firebase.auth)
                    .setPhoneNumber(phoneNumber.toString())       // Phone number to verify
                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                    .setActivity(this)                 // Activity (for callback binding)
                    .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                        }

                        override fun onVerificationFailed(p0: FirebaseException) {
                            Toast.makeText(
                                this@RiderRegisterActivity,
                                "Please check Internet Connection",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        override fun onCodeSent(
                            p0: String,
                            p1: PhoneAuthProvider.ForceResendingToken
                        ) {
                            super.onCodeSent(p0, p1)
                            createActivity(name, null, gender, email, phoneNumber, password, p0)
                        }
                    })          // OnVerificationStateChangedCallbacks
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)

                if (flag == 1) {

                }
            }

        }
    }

    private fun createActivity(name:String?, age:Int?, gender:String?, email:String?, phoneNumber:String?, password:String?, id:String?) {
        var db = FirebaseFirestore.getInstance()
        //making sure that phone Number is added
        if (phoneNumber != null) {
            db.collection("Rider").document(phoneNumber).get()        //whereArrayContains("PhoneNumber", phoneNumber)
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        Toast.makeText(this, "User already Registered", Toast.LENGTH_SHORT).show()
                    } else {
                        val intent = Intent(this, RiderRegistrationOtpActivity::class.java)
                        intent.putExtra("name", name)
                        intent.putExtra("age", age)
                        intent.putExtra("gender", gender)
                        intent.putExtra("email", email)
                        intent.putExtra("password", password)
                        intent.putExtra("phoneNumber", phoneNumber)
                        intent.putExtra("otpId", id)
                        startActivity(intent)
                    }
                }
        }
    }


    //check whether the inputs are valid or not,
    // and show errors
    private fun isInputsValid(): Boolean {
        var isValid = true

        // for name
        var flag = 1
        for(element in registerRider_nameInput.text!!){
            if(element !in 'a'..'z' && element !in 'A'..'Z' && element != ' '){
                flag = 0
                break
            }
        }
        if(flag==0 || registerRider_nameInput.text!!.isEmpty()){
            registerRider_nameLayout.error = "Invalid name"
            isValid = false
        }else{
            registerRider_nameLayout.error = null
            registerRider_nameLayout.isErrorEnabled = false
        }

        // validation error for gender
        if(registerRider_genderInput.text!!.isEmpty()){
            registerRider_genderLayout.error = "Select Gender"
            isValid = false
        }else{
            registerRider_genderLayout.error = null
            registerRider_genderLayout.isErrorEnabled = false
        }

        // checking email
        var email = registerRider_emailInput.text.toString().trim()
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        if (!email.matches(emailPattern.toRegex())) {
            registerRider_emailLayout.error = "Invalid email"
            isValid = false
        } else {
            registerRider_emailLayout.error = null
            registerRider_emailLayout.isErrorEnabled = false
        }

        //checking phoneNo validation
        if(registerRider_phoneNumberInput.text == null) {
            showPhoneError()
            Toast.makeText(applicationContext, "Enter Mobile", Toast.LENGTH_SHORT).show()
            isValid = false
        }
        else if(registerRider_phoneNumberInput.text.toString().trim().isEmpty()) {
            showPhoneError()
            Toast.makeText(applicationContext, "Enter Mobile", Toast.LENGTH_SHORT).show()
            isValid = false
        }
        else {
            removePhoneError()
        }


        //checking password validation
        if(registerRider_passwordInput.text?.length!! < 8) {
            registerRider_passwordLayout.error = "Minimum Password length should be 8"
            isValid = false
        }
        else {
            registerRider_passwordLayout.error = null
            registerRider_passwordLayout.isErrorEnabled = false
        }


        //checking confirm password validation
        if(registerRider_confirmPasswordInput.text!!.length != registerRider_passwordInput.text!!.length){
            registerRider_confirmPasswordLayout.error = "Password not matches"
            isValid = false
        }else{
            var flag = 1
            for(i in registerRider_confirmPasswordInput.text!!.indices){
                if(registerRider_confirmPasswordInput.text!![i] != registerRider_passwordInput.text!![i]){
                    flag = 0
                    break
                }
            }
            if(flag == 0){
                registerRider_confirmPasswordLayout.error = "Password not matches"
                isValid = false
            }else {
                registerRider_confirmPasswordLayout.error = null
                registerRider_confirmPasswordLayout.isErrorEnabled = false
            }
        }

        return isValid
    }


    private fun showPhoneError(){
        val border = GradientDrawable()
        border.setStroke(3, -0x4f0000) //red border with full opacity
        border.cornerRadius = 50F
        registerRider_phoneNumberInput.setBackgroundDrawable(border)
    }
    private fun removePhoneError(){
        val border = GradientDrawable()
        border.setStroke(3, -0xFFA500) //reset color
        border.cornerRadius = 50F
        registerRider_phoneNumberInput.setBackgroundDrawable(border)
    }
}