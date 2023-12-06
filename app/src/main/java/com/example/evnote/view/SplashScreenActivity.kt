package com.example.evnote.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.evnote.R
import com.example.evnote.view.auth.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var preferences: SharedPreferences

    companion object {
        private const val PREF_NAME = "MyPreferences"
        private const val KEY_FIRST_TIME = "isFirstTime"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val isFirstTime = preferences.getBoolean(KEY_FIRST_TIME, true)

        if (isFirstTime) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(5000L)
                // Lakukan sesuatu setelah splash screen, misalnya, update preferences
                preferences.edit().putBoolean(KEY_FIRST_TIME, false).apply()
                startLoginActivity()
            }
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                delay(5000L)
                startLoginActivity()
            }
        }
    }

    private fun startLoginActivity() {
        startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
        finish()
    }
}
