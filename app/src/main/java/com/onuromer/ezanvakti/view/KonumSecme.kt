package com.onuromer.ezanvakti.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.onuromer.ezanvakti.R
import com.onuromer.ezanvakti.viewModel.KonumSecmeViewModel
import kotlinx.android.synthetic.main.fragment_konum_secme.*

class KonumSecme : Fragment() {
    private lateinit var viewModel : KonumSecmeViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel= ViewModelProviders.of(this).get(KonumSecmeViewModel::class.java)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_konum_secme, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Konum id boş ise konumlar yüklenir dolu ise diğer fragment a geçilir

            if (viewModel.ozelSharedPreferences.konumIdAl()!=0){
                val action=KonumSecmeDirections.actionKonumSecmeToEzanSaatleri()
                Navigation.findNavController(view).navigate(action)
            }else{
                recyclerViewFragment.layoutManager= LinearLayoutManager(context)
                recyclerViewFragment.adapter=viewModel.listeAdapter
                //Search edilen kelime listener fonksiyonu
                searchEditText.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        viewModel.listeAdapter.filter.filter(s)
                    }
                    override fun afterTextChanged(s: Editable?) {}
                })
        }

            //Geri tuşuna basıldığında bişey olmayacak istersen curly bracketler arasına yaz
            requireActivity().onBackPressedDispatcher.addCallback (this){
            }





    }





}