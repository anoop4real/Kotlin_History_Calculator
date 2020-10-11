package com.example.simplehistorycalculator

import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var resultText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        resultText = editText

        resultText.setRawInputType(InputType.TYPE_CLASS_TEXT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            resultText.setShowSoftInputOnFocus(false);
        } else {
            resultText.setTextIsSelectable(true);
        }
        val ic = editText.onCreateInputConnection(EditorInfo())
        val keyboard1 = keyboard
        keyboard1.setUpInputConnection(ic)
    }
}

