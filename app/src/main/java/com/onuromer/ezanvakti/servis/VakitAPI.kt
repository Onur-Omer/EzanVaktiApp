package com.onuromer.ezanvakti.servis

import com.onuromer.ezanvakti.model.Vakitler
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

//BU İNTERFACE VAKİTAPISERVİS TARAFINDAN KULLANILIYOR
//@QUERY İLE SİTE İSTEMEYİ DİNAMİK HALE GETİRİYORUZ  ? ve = i kendisi koyuyor ve Int i kendi çeviriyor
//GETVAKİT İLE VAKİTLER MODELİNİ LİSTE HALİNDE ÇEKİYORUZ

interface VakitAPI {

    @GET("prayertimes")
    fun getVakit(@Query("location_id") endUrl: Int) : Single<List<Vakitler>>

}