package com.onuromer.ezanvakti.viewModel

import android.app.Application
import com.onuromer.ezanvakti.adapter.RecyclerAdapterSehirler
import com.onuromer.ezanvakti.model.tumAdresler
import com.onuromer.ezanvakti.util.OzelSharedPreferences

class KonumSecmeViewModel(application: Application): BaseViewModel(application) {
     //Konum seçmede oluşturulamadığından burda oluşturuldu
     val ozelSharedPreferences = OzelSharedPreferences(application)
     var listeAdapter: RecyclerAdapterSehirler = RecyclerAdapterSehirler(tumAdresler,getApplication())
}

