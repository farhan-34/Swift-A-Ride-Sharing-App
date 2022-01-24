package com.example.swift.frontEnd.activities

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.example.swift.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_reset_password.*

class ResetPasswordActivity : AppCompatActivity() {

    //flags for correct input
    private var flag_new = false
    private var flag_confirm = false
    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation =  (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        setContentView(R.layout.activity_reset_password)

        //setting actionbar attributes
        val actionBar = supportActionBar
        actionBar!!.title = "Reset Password"
        actionBar.setDisplayHomeAsUpEnabled(true)

        //show errors
        checkInputs()

        // set buttons
        resetPass_btn_reset.setOnClickListener {
            if(flag_new and flag_confirm){
                //reset password
                var phoneNumber = intent.getStringExtra("phoneNumber")
                db.collection("Rider").document(phoneNumber!!).update("password",reset_newPassword_input.text.toString())
                Toast.makeText(this, "Password Reset",Toast.LENGTH_SHORT).show()
                //sign out required to go to sign in page
                //FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            }
        }
    }


    private fun checkInputs() {

        reset_newPassword_input.doOnTextChanged { text, start, before, count ->
            flag_new = false
            if(text!!.length < 8){
                reset_newPassword_layout.setError("Minimum Password length should be 8")
            }else{
                reset_newPassword_layout.error = null
                reset_newPassword_layout.isErrorEnabled = false
                flag_new = true
            }
            checkConfirmPasswordOnly() //for checking confirm password again
        }

        reset_confirmPassword_input.doOnTextChanged { text, start, before, count ->
            flag_confirm = false
            if(text!!.length != reset_newPassword_input.text!!.length){
                reset_confirmPassword_layout.error = "Password dosn't match"
            }else{
                var flag = 1
                for(i in text.indices){
                    if(text[i] != reset_newPassword_input.text!![i]){
                        flag = 0
                        break
                    }
                }
                if(flag == 0){
                    reset_confirmPassword_layout.error = "Password dosn't match"
                }else {
                    reset_confirmPassword_layout.error = null
                    reset_confirmPassword_layout.isErrorEnabled = false
                    flag_confirm = true
                }
            }
        }

    }

    //for checking confirm password again
    private fun checkConfirmPasswordOnly() {
        flag_confirm = false
        if(reset_confirmPassword_input.text!!.length != reset_newPassword_input.text!!.length){
            reset_confirmPassword_layout.error = "Password dosn't match"
        }else{
            var flag = 1
            for(i in reset_confirmPassword_input.text!!.indices){
                if(reset_confirmPassword_input.text!![i] != reset_newPassword_input.text!![i]){
                    flag = 0
                    break
                }
            }
            if(flag == 0){
                reset_confirmPassword_layout.error = "Password dosn't match"
            }else {
                reset_confirmPassword_layout.error = null
                reset_confirmPassword_layout.isErrorEnabled = false
                flag_confirm = true
            }
        }

    }


}