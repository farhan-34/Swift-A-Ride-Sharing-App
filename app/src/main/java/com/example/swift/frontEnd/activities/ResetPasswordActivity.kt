package com.example.swift.frontEnd.activities

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import com.example.swift.R
import kotlinx.android.synthetic.main.activity_receive_otp_for_password_reset.*
import kotlinx.android.synthetic.main.activity_reset_password.*
import kotlinx.android.synthetic.main.activity_rider_register.*

class ResetPasswordActivity : AppCompatActivity() {

    //flags
    var flag_new = false
    var flag_confirm = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation =  (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        setContentView(R.layout.activity_reset_password)

        checkInputs()

        resetPass_btn_reset.setOnClickListener {
            if(flag_new and flag_confirm){
                startActivity(Intent(this, SignInActivity::class.java))
            }
            else{
                Toast.makeText(applicationContext, "Enter Correct Password", Toast.LENGTH_SHORT).show()
            }

        }
    }


    private fun checkInputs() {



        val normalColorList = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_focused),  // Unfocused
                intArrayOf(android.R.attr.state_focused)    // Focused
            ),
            intArrayOf(
                Color.GRAY,     // The color for the Unfocused state
                Color.parseColor("#E87C35")        // The color for the Focused state
            )
        )
        val errorColorList = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_focused),  // Unfocused
                intArrayOf(android.R.attr.state_focused)    // Focused
            ),
            intArrayOf(
                Color.parseColor("#e02a1d"),     // The color for the Unfocused state
                Color.parseColor("#e02a1d")        // The color for the Focused state
            )
        )



        reset_newPassword_input.doOnTextChanged { text, start, before, count ->
            flag_new = false
            if(text!!.length < 8){
                reset_newPassword_layout.setBoxStrokeColorStateList(errorColorList)
            }else{
                reset_newPassword_layout.setBoxStrokeColorStateList(normalColorList)
                flag_new = true
            }
        }

        reset_confirmPassword_input.doOnTextChanged { text, start, before, count ->
            flag_confirm = false
            if(text!!.length != reset_newPassword_input.text!!.length){
                reset_newPassword_layout.setBoxStrokeColorStateList(errorColorList)
                reset_confirmPassword_layout.setBoxStrokeColorStateList(errorColorList)
            }else{
                var flag = 1
                for(i in text.indices){
                    if(text[i] != reset_newPassword_input.text!![i]){
                        flag = 0
                        break
                    }
                }
                if(flag == 0){
                    reset_newPassword_layout.setBoxStrokeColorStateList(errorColorList)
                    reset_confirmPassword_layout.setBoxStrokeColorStateList(errorColorList)
                }else {
                    reset_newPassword_layout.setBoxStrokeColorStateList(normalColorList)
                    reset_confirmPassword_layout.setBoxStrokeColorStateList(normalColorList)
                    flag_confirm = true
                }
            }
        }

    }

}