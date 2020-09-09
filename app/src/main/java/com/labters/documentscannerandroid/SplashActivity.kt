package com.labters.documentscannerandroid

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    private val SPLASH_TIME_OUT = 3000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()

        Handler().postDelayed(
            {
                val i = Intent(this@SplashActivity, AllPdfActivity::class.java)
                startActivity(i)
                finish()
            }, SPLASH_TIME_OUT)
    }        }

