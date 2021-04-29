package com.onuromer.ezanvakti.servis

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.onuromer.ezanvakti.model.Vakitler

//Database imizi oluşturazağız
//Burayı asenkron deilde senkron yapmalıyız çünki database e hem ekleme hemde çıkarma ve okuma gibi
//işlemlerin aynı anda yapılması çakışmaya neden olur --> Singleton
//Database de değişlik yaparsan versiyonu arttır

@Database(entities= [Vakitler::class],version=1,exportSchema = false)
abstract class  VakitDAOServis : RoomDatabase() {

    abstract fun vakitlerDao() : VakitDAO

    companion object {

        //database in daha önce oluşturulup oluşturulmadığı kontrol ediliyor
        @Volatile private var instance : VakitDAOServis? =null
        private  val lock = Any()
        operator fun invoke(context: Context) = instance ?: synchronized(lock){
            instance ?: databaseOlustur(context).also {
                instance=it
            }
        }



        private fun databaseOlustur(context: Context) = Room.databaseBuilder(
                context.applicationContext,
                VakitDAOServis::class.java,"vakitlerdatabase").build()

    }


}