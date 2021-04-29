package com.onuromer.ezanvakti.viewModel

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.onuromer.ezanvakti.model.Vakitler
import com.onuromer.ezanvakti.servis.VakitAPIServis
import com.onuromer.ezanvakti.servis.VakitDAOServis
import com.onuromer.ezanvakti.util.OzelSharedPreferences
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class EzanSaatleriViewModel(application: Application): BaseViewModel(application){

    val ozelSharedPreferences = OzelSharedPreferences(getApplication())
    val vakitler = MutableLiveData<Vakitler>()
    val yukleniyor = MutableLiveData<Boolean>()
    private val konumId = ozelSharedPreferences.konumIdAl()
    private val vakilerApiServis = konumId?.let { VakitAPIServis(it) }   //-->endUrl
    private var enSonRefreshVakti = ozelSharedPreferences.zamanAl()

    private val disposable = CompositeDisposable()
    @RequiresApi(Build.VERSION_CODES.O)
    private val today=LocalDateTime.now()
    //api'dakine eşitlemek için string modifikasyonu yapıldı
    @RequiresApi(Build.VERSION_CODES.O)
    private val databaseDate=today.toString().subSequence(0,11) as String +"00:00:00"


    //İnternetten veri çekme işini sürekli açık tutamayız.Disposible kapatmamız için kullanılacak
    //Rx java nın fonksiyonudur bu
    //Rx Java yı kullanmamızın asıl nedeni bu
    //20 günde bir refreshlenecek hangi gün refreshlendi ise counter o dayofYear a eşitlenecek.Yılın başında 2 kere olacak ama okadar önemli deil
    @RequiresApi(Build.VERSION_CODES.O)
    fun refreshData(){
        if((today.dayOfYear- enSonRefreshVakti!!)>25){
            yukleniyor.value=true
            if (vakilerApiServis != null) {
                disposable.add(
                        vakilerApiServis.getData()
                                .subscribeOn(Schedulers.newThread()) // Yeni bir thread açıp işleni ana thredi bloklamadan yapıyoruz
                                .observeOn(AndroidSchedulers.mainThread()) // Gözlemleme işini işe kullıcı için main thread de yapıyoruz
                                .subscribeWith(object : DisposableSingleObserver<List<Vakitler>>() {
                                    //Başarılı ise liste vakitlere atanacak
                                    override fun onSuccess(t: List<Vakitler>) {
                                        databaseSakla(t) //İnternetten veri geldiğinde SQLite  a veri gönderiliyor
                                        vakitler.value= t[0]
                                        yukleniyor.value=false
                                    }

                                    //Başarısız ise logcat
                                    override fun onError(e: Throwable) {
                                    }

                                }))
            }

        }

    }


    //dao ile bütün fonksiyonlara erişebiliriz
    //İlk önce tamamen database i temizlemek
    //Database atandığında atandığı tarihte sharedPrefences e aktarılacak
    @RequiresApi(Build.VERSION_CODES.O)
    private fun databaseSakla(vakitlerListesi : List<Vakitler>) {
        launch {

            val dao =VakitDAOServis(getApplication()).vakitlerDao()
            dao.deleteAllVakitler()
            //vakitler listesini tek tek bölerek insert ediyoruz
            dao.insertAll(*vakitlerListesi.toTypedArray())
            ozelSharedPreferences.zamanKaydet(today.dayOfYear)
        }
    }

    //Database den bugunun tarihli vakitleri alınacak tek bir vakitler dönecek(1 günlük)
    @RequiresApi(Build.VERSION_CODES.O)
    fun sqliteVeriAl(){
            yukleniyor.value=true
        launch {
            val dao=VakitDAOServis(getApplication()).vakitlerDao()
            val vakit=dao.getVakitler(databaseDate)
            vakitler.value=vakit
            yukleniyor.value=false
        }
    }


}