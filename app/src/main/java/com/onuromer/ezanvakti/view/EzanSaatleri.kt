package com.onuromer.ezanvakti .view

import android.annotation.SuppressLint
import android.content.Context.POWER_SERVICE
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.PowerManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.onuromer.ezanvakti.R
import com.onuromer.ezanvakti.model.Vakitler
import com.onuromer.ezanvakti.util.OzelCountDownTimer
import com.onuromer.ezanvakti.viewModel.EzanSaatleriViewModel
import kotlinx.android.synthetic.main.fragment_ezan_saatleri.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class EzanSaatleri : Fragment() {

    private var my = OzelCountDownTimer.countDownTimer
    private lateinit var powerManager: PowerManager
    private lateinit var viewModel: EzanSaatleriViewModel
    private lateinit var guncelVakit :LocalDateTime
    private lateinit var sonrakiVakit : LocalDateTime
    private lateinit var guncelVakitString: String
    private var clicked=false
    private  var modeNight =false
    private val rotateOpen : Animation by lazy {AnimationUtils.loadAnimation(context,R.anim.rotate_open_animation)}
    private val rotateClose : Animation by lazy {AnimationUtils.loadAnimation(context,R.anim.rotate_close_animation)}
    private val fromBottom : Animation by lazy {AnimationUtils.loadAnimation(context,R.anim.from_bottom_animation)}
    private val toBottom : Animation by lazy {AnimationUtils.loadAnimation(context,R.anim.to_bottom_animation)}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ezan_saatleri, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        powerManager = context?.getSystemService(POWER_SERVICE) as PowerManager

        println("Ezan Fragment Starting...")

        //Ezan saatleri ViewModelimizi bağlıyoruz artık EzanSaatleriViewModel in Bütün data ve fonksiyonlarına ulaşabiliriz
        viewModel = ViewModelProviders.of(this).get(EzanSaatleriViewModel::class.java)
        viewModel.refreshData()
        viewModel.sqliteVeriAl()


        //Sol üstteki Konum textini tanımlıyoruz
        konumTextView.text = viewModel.ozelSharedPreferences.konumlAl()

        //vakitler tanımlandığında textler initialize olacaklar
        viewModel.vakitler.observe(viewLifecycleOwner, {
            it?.let {

                imsakZamanTextView.text = it.fajr
                sabahZamanTextView.text = it.sun
                ogleZamanTextView.text = it.dhuhr
                ikindiZamanTextView.text = it.asr
                aksamZamanTextView.text = it.maghrib
                yatsiZamanTextView.text = it.yatsi
                currentTime(it)
                progressBar.visibility = View.INVISIBLE
                constraintLayoutUp.visibility = View.VISIBLE
                constraintLayoutDown.visibility = View.VISIBLE
            }
        })
        //Vakitler tanımlanana kadar progress bar gözükecek
        viewModel.yukleniyor.observe(viewLifecycleOwner, {
            if (it) {
                progressBar.visibility = View.VISIBLE
                constraintLayoutUp.visibility = View.INVISIBLE
                constraintLayoutDown.visibility = View.INVISIBLE
            }
        })

        //Geri tuşuna basıldığında bişey olmayacak istersen curly bracketler arasına yaz
        requireActivity().onBackPressedDispatcher.addCallback(this) {
        }


        //Float button setOnclickListener
        floatingActionButton.setOnClickListener {
            onAddButtonClicked()
        }
        floatingActionButtonLocation.setOnClickListener {
            viewModel.ozelSharedPreferences.konumIdKaydet(0)
            viewModel.ozelSharedPreferences.zamanKaydet(0)
            val action = EzanSaatleriDirections.actionEzanSaatleriToKonumSecme()
            Navigation.findNavController(it).navigate(action)
            clicked=true
        }
        floatingActionButtonMode.setOnClickListener {
            modeNight= viewModel.ozelSharedPreferences.temaAl()!!
            modeNight=!modeNight
            viewModel.ozelSharedPreferences.temaKaydet(modeNight)
            clicked=true

            if(modeNight){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

    }


    private fun onAddButtonClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        clicked=!clicked
    }

    private fun setAnimation(clicked : Boolean) {

        if(!clicked){
            floatingActionButtonLocation.startAnimation(fromBottom)
            floatingActionButtonMode.startAnimation(fromBottom)
            floatingActionButton.startAnimation(rotateOpen)
        }else{
            floatingActionButtonLocation.startAnimation(toBottom)
            floatingActionButtonMode.startAnimation(toBottom)
            floatingActionButton.startAnimation(rotateClose)
        }
    }
    private fun setVisibility(clicked : Boolean){

        if(!clicked){
            floatingActionButtonLocation.visibility=View.VISIBLE
            floatingActionButtonMode.visibility=View.VISIBLE
        }
        else{
            floatingActionButtonLocation.visibility=View.GONE
            floatingActionButtonMode.visibility=View.GONE
        }
    }


    //Geri Sayım sayacı fonksiyonu
        // Vakitleri saniyeler dahil milisaniyeyee çevirip karşılaştırır ve kalan saat ve dakikayı bulur
        //LocalTime saniyesi 0 olduğunda dakika bir azalır
        //Milisaniye biterse finish fonksiyonu  çalışır oda tekrar RemainigTimer ı çalıştırır
        //Bu fonksiyon arkaplanda değil uygulama açıldığı anda hesap yapıp çalışır
        @SuppressLint("SetTextI18n")
        @RequiresApi(Build.VERSION_CODES.O)
        fun remainingTimer() {
            try {
                var millisinFuture=0
                KalanYaziTextView.text = guncelVakitString
                val nextTime = sonrakiVakit  // Next time ı alıyoruz
                val millisinFutureCurrentTime = ((LocalTime.now().hour) * (60 * 60 * 1000)) + ((LocalTime.now().minute) * (60 * 1000)) + (LocalTime.now().second * (1000))
                val millisinFutureNextTime = ((nextTime.hour) * (60 * 60 * 1000)) + ((nextTime.minute) * (60 * 1000))

                if (guncelVakit.dayOfWeek==sonrakiVakit.dayOfWeek){
                    millisinFuture = (millisinFutureNextTime - millisinFutureCurrentTime)
                }else{
                    val newMillisinCurrentTime= (24*60*60*1000)-millisinFutureCurrentTime
                    millisinFuture = (millisinFutureNextTime + newMillisinCurrentTime)
                }
                var millisinFutureToTime = LocalTime.of(((millisinFuture / (1000 * 60 * 60)) % 24), ((millisinFuture / (1000 * 60)) % 60), ((millisinFuture / 1000) % 60))
                millisinFutureToTime = millisinFutureToTime.plusMinutes(1)
                KalanZamanTextView.text = "${millisinFutureToTime.hour} : ${millisinFutureToTime.minute}"
                my = object : CountDownTimer(millisinFuture.toLong(), (1000)) {
                    @SuppressLint("SetTextI18n")
                    override fun onTick(millisUntilFinished: Long) {
                        if(clicked){
                            cancel()
                        }
                        if(powerManager.isInteractive){
                            if (LocalTime.now().second == 0) {

                                    millisinFutureToTime = millisinFutureToTime.minusMinutes(1)
                                   KalanZamanTextView.text = "${millisinFutureToTime.hour} : ${millisinFutureToTime.minute}"
                                } }
                        else{
                            cancel()
                            remainingTimer()
                        }

                    }
                    override fun onFinish() {
                        remainingTimer()    //--->Recursive Function
                    }

                }.start()

            } catch (e: Exception) {
            }


        }

    //Bize şimdiki ve sonraki ezaz ve namaz vaktini ve sonraki ezan vakti text ini gönderir
    @RequiresApi(Build.VERSION_CODES.O)
    fun currentTime(vakitler: Vakitler):Boolean{        //2021-02-1605:07
            val time = LocalDateTime.now()
            val date=vakitler.date?.subSequence(0,10)
            val today= LocalDate.parse(date)
            val midnightTime=LocalTime.of(0,0)
            val tomorrow=today.plusDays(1)

            val TomorrowFajr=LocalDateTime.parse(tomorrow.toString()+"T"+vakitler.fajr)
            val fajr=LocalDateTime.parse(today.toString()+"T"+vakitler.fajr)
            val sun=LocalDateTime.parse(today.toString()+"T"+vakitler.sun)
            val dhuhr=LocalDateTime.parse(today.toString()+"T"+vakitler.dhuhr)
            val asr=LocalDateTime.parse(today.toString()+"T"+vakitler.asr)
            val maghrib=LocalDateTime.parse(today.toString()+"T"+vakitler.maghrib)
            val isha=LocalDateTime.parse(today.toString()+"T"+vakitler.yatsi)
            val midnight=LocalDateTime.of(today,midnightTime)

            if (time.isAfter(isha) && time.isBefore(TomorrowFajr)) {
                guncelVakitString="İmsak Vaktine Kalan"
                guncelVakit=isha
                sonrakiVakit=TomorrowFajr
                remainingTimer()
                return true
            }
            if(time.isAfter(midnight) && time.isBefore(fajr)){
                guncelVakitString="İmsak Vaktine Kalan"
                guncelVakit=isha
                sonrakiVakit=fajr
                remainingTimer()
                return true
            }
            if (time.isAfter(fajr) && time.isBefore(sun)) {
                guncelVakit=fajr
                sonrakiVakit=sun
                guncelVakitString="Güneşin Doğmasına Kalan"
                remainingTimer()
                return true
            }
            if (time.isAfter(sun) && time.isBefore(dhuhr)) {
                guncelVakitString="Öğle Namazına Kalan"
                guncelVakit=sun
                sonrakiVakit=dhuhr
                remainingTimer()
                return true
            }
            if (time.isAfter(dhuhr) && time.isBefore(asr)) {
                guncelVakitString="İkindi Namazına Kalan"
                guncelVakit=dhuhr
                sonrakiVakit=asr
                remainingTimer()
                return true
            }
            if (time.isAfter(asr) && time.isBefore(maghrib)) {
                guncelVakitString="Akşam Namazına Kalan"
                guncelVakit=asr
                sonrakiVakit=maghrib
                remainingTimer()
                return true
            }
            if (time.isAfter(maghrib) && time.isBefore(isha)) {
                guncelVakitString="Yatsı Namazına Kalan"
                guncelVakit=maghrib
                sonrakiVakit=isha
                remainingTimer()
                return true
            }
        remainingTimer()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        clicked=true

    }


    }

