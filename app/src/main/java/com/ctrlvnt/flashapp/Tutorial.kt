package com.ctrlvnt.flashapp


import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate


class Tutorial : AppCompatActivity() {

    private lateinit var ok : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tutorial)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        ok = findViewById(R.id.i_understand)

        val t2 = findViewById<TextView>(R.id.text_tutorial) as TextView
        t2.movementMethod = LinkMovementMethod.getInstance()

        ok.setOnClickListener{
            finish()
        }

    }
}