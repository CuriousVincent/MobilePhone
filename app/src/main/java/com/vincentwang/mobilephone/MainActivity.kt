package com.vincentwang.mobilephone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vincentwang.mobilephone.ui.currency.CurrencyFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, CurrencyFragment()).commit()
    }
}