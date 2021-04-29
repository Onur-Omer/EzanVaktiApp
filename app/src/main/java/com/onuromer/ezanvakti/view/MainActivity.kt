package com.onuromer.ezanvakti.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.onuromer.ezanvakti.R
import com.onuromer.ezanvakti.util.OzelSharedPreferences


class MainActivity : AppCompatActivity() {
    private  var modeNight =false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val ozelSharedPreferences = OzelSharedPreferences(application)
                modeNight= ozelSharedPreferences.temaAl()!!
                if(modeNight){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }

    }







}