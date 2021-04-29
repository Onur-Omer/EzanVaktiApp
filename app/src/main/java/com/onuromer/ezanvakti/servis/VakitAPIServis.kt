package com.onuromer.ezanvakti.servis

import com.onuromer.ezanvakti.model.Vakitler
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

//RETROFİT İLE RXJAVA İLE VERİ ÇEKİYORUZ
//GETDATA FONKSİYONUNU KULLANDIĞIMIZDA LİSTE HALİNE VAKİTLER MODELİNİ ÇEKİYORUZ
//GETDATA YA ENDURL PARAMETRESİNİ YANİ BİZE ŞEHİR KODUNU VERMEMİZ LAZIM

class VakitAPIServis(private val endUrl : Int) {  //-->val endUrl : String

    private val baseurl= "https://prayertimes.api.abdus.dev/api/diyanet/"
    private val api= Retrofit.Builder()
        .baseUrl(baseurl)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(VakitAPI::class.java)

    fun getData() :Single<List<Vakitler>>{
        return api.getVakit(endUrl) // --->endUrl
    }
}