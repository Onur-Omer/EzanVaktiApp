package com.onuromer.ezanvakti.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit


//Bu class ile applicationda tek bir sharedpreferences oluşturucağız
//Konum ve zaman(ayda bir refresh data için) kaydelecek
class OzelSharedPreferences {

    companion object {

        private var sharedPreferences : SharedPreferences? =null
        @Volatile private var instance : OzelSharedPreferences?=null
        private val lock = Any()

        operator fun invoke(context: Context) : OzelSharedPreferences = instance ?: synchronized(lock){
            instance ?: ozelSharedPrefencesOlustur(context).also {
                instance=it
            }

        }

        private fun ozelSharedPrefencesOlustur(context: Context) : OzelSharedPreferences{
            sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
            return OzelSharedPreferences()
        }

    }

    //Konum Kaydediliyoru
    fun konumKaydet(konum : String){

        sharedPreferences?.edit(commit=true){
            putString("konum",konum)
        }

    }
    //Konum Alınıyor
    fun konumlAl() = sharedPreferences?.getString("konum","")

    fun konumIdKaydet(konum : Int){
        sharedPreferences?.edit(commit=true){
            putInt("konumId",konum)
        }
    }

    fun konumIdAl() = sharedPreferences?.getInt("konumId",0)

    //refreshlenen vakit dayofYear olarak kaydediliyor
    fun zamanKaydet(zaman : Int){
        sharedPreferences?.edit(commit = true){
            putInt("zaman",zaman)
        }
    }
    //Refresh datanın dayofYear olarak zamanı alınıryor(0-365)
    fun zamanAl() = sharedPreferences?.getInt("zaman",0)

    fun temaKaydet(night:Boolean){
        sharedPreferences?.edit(commit = true){
            putBoolean("night",night)
        }
    }

    fun temaAl()= sharedPreferences?.getBoolean("night",false)
}