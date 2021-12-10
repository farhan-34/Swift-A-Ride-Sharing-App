package com.example.swift.frontEnd.activities

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.example.swift.R
import kotlinx.android.synthetic.main.activity_rider_register.*

class RiderRegisterActivity : AppCompatActivity() {
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation =  (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        setContentView(R.layout.activity_rider_register)

        val items = listOf("Male", "Female", "Other")
        val adapter = ArrayAdapter(this, R.layout.gender_dropdown_list_item, items)
        registerRider_genderInput.setAdapter(adapter)

        var html = "<u><b>Login<b></u>"
        backToLogin_button.text = Html.fromHtml(html)

        backToLogin_button.setOnClickListener {
            this.finish()
        }

        // checking all inputs
        var finalFlag = 1
        checkInputs()



        registerRider_button.setOnClickListener{
          val temp = registerRider_nameInput.text?.toString()
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
        registerRider_nameInput.doOnTextChanged { text, start, before, count ->
            var flag = 1
            for(element in text!!){
                if(element !in 'a'..'z' && element != ' '){
                    flag = 0
                    break
                }
            }
            if(flag==0){
                registerRider_nameLayout.setBoxStrokeColorStateList(errorColorList)
            }else{
                registerRider_nameLayout.setBoxStrokeColorStateList(normalColorList)
            }
        }

        registerRider_ageInput.doOnTextChanged { text, start, before, count ->
            var flag = 1
            for(element in text!!){
                if(element !in '0'..'1'){
                    flag = 0
                    break
                }
            }
            if(flag==0){
                registerRider_ageLayout.setBoxStrokeColorStateList(errorColorList)
            }else{
                registerRider_ageLayout.setBoxStrokeColorStateList(normalColorList)
            }
        }

        registerRider_emailInput.doOnTextChanged { text, start, before, count ->
            var email = text.toString().trim()
            val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
            if (email.matches(emailPattern.toRegex())) {
                registerRider_emailLayout.setBoxStrokeColorStateList(normalColorList)
            } else {
                registerRider_emailLayout.setBoxStrokeColorStateList(errorColorList)
            }
        }

        registerRider_passwordInput.doOnTextChanged { text, start, before, count ->
            if(text!!.length < 8){
                registerRider_passwordLayout.setBoxStrokeColorStateList(errorColorList)
            }else{
                registerRider_passwordLayout.setBoxStrokeColorStateList(normalColorList)
            }
        }

        registerRider_confirmPasswordInput.doOnTextChanged { text, start, before, count ->
            if(text!!.length != registerRider_passwordInput.text!!.length){
                registerRider_passwordLayout.setBoxStrokeColorStateList(errorColorList)
                registerRider_confirmPasswordLayout.setBoxStrokeColorStateList(errorColorList)
            }else{
                var flag = 1
                for(i in text!!.indices){
                    if(text!![i] != registerRider_passwordInput.text!![i]){
                        flag = 0
                        break
                    }
                }
                if(flag == 0){
                       registerRider_passwordLayout.setBoxStrokeColorStateList(errorColorList)
                       registerRider_confirmPasswordLayout.setBoxStrokeColorStateList(errorColorList)
                }else {
                    registerRider_confirmPasswordLayout.setBoxStrokeColorStateList(normalColorList)
                    registerRider_passwordLayout.setBoxStrokeColorStateList(normalColorList)
                }
            }

        }


    }
}