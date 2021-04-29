package com.onuromer.ezanvakti.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity
data class Vakitler(

        @ColumnInfo
        @SerializedName("date")
        val date : String?,
        @ColumnInfo
        @SerializedName("fajr")
        val fajr : String?,
        @ColumnInfo
        @SerializedName("sun")
        val sun : String?,
        @ColumnInfo
        @SerializedName("dhuhr")
        val dhuhr  : String?,
        @ColumnInfo
        @SerializedName("asr")
        val asr: String?,
        @ColumnInfo
        @SerializedName("maghrib")
        val maghrib : String?,
        @ColumnInfo(name="isha")
        @SerializedName("isha")
        val yatsi : String?
        ){

        @PrimaryKey(autoGenerate =true)
        var uuid : Long =0
}