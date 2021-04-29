package com.onuromer.ezanvakti.servis

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.onuromer.ezanvakti.model.Vakitler

@Dao
interface VakitDAO {

    // Data Access Object --> DAO

    //Vakitler listesini room ile database e ekliyoruz
    @Insert
    suspend fun insertAll(vararg vakitler: Vakitler)


    //Bütün listeyi çekmek için
    /*@Query("SELECT * FROM vakitler")
    suspend fun getAllVakitler() : List<Vakitler>*/

    //Modelimizde primaryKey olarak işaretlediğimiz date e göre veriyi bulup çekiyoruz
    @Query("SELECT * FROM vakitler WHERE date = :vakitDate")
    suspend fun getVakitler(vakitDate : String) : Vakitler

    @Query("DELETE FROM vakitler")
    suspend fun deleteAllVakitler()




}