package com.adityagupta.mobileclouds

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.adityagupta.mobileclouds.utils.PrefsHelper

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (PrefsHelper().getName(applicationContext) != "none") {
            startActivity(object : Intent(this, MainActivity::class.java) {
                init {
                    addFlags(FLAG_ACTIVITY_NEW_TASK)
                }
            })
            finish()
        }
        setContentView(R.layout.activity_login)
        findViewById<View>(R.id.buttonLogin).setOnClickListener {
            val name = (findViewById<View>(R.id.nameField) as EditText).text.toString().trim { it <= ' ' }
            PrefsHelper().setName(applicationContext, name.toLowerCase().replace(" ", "-"))
            startActivity(object : Intent(this@LoginActivity, MainActivity::class.java) {
                init {
                    addFlags(FLAG_ACTIVITY_NEW_TASK)
                }
            })
            finish()
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}