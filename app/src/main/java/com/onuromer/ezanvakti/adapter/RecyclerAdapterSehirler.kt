package com.onuromer.ezanvakti.adapter

import android.app.Application
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Filter
import android.widget.Filterable
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.onuromer.ezanvakti.R
import com.onuromer.ezanvakti.util.OzelSharedPreferences
import com.onuromer.ezanvakti.view.KonumSecmeDirections
import kotlinx.android.synthetic.main.recycler_row.view.*
import java.util.*


class RecyclerAdapterSehirler(var adresler : Map<String,Int>, val application: Application) : RecyclerView.Adapter<RecyclerAdapterSehirler.AdreslerViewHolder>(),Filterable {
    private val ozelSharedPreferences = OzelSharedPreferences(application)
    var  filteredAdresler =adresler
    class AdreslerViewHolder(itemView : View) :  RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdreslerViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recycler_row,parent,false)
        return AdreslerViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdreslerViewHolder, position: Int) {
        holder.itemView.animation=AnimationUtils.loadAnimation(holder.itemView.context,R.anim.fade_scale_animation)
        holder.itemView.sehirlerTextView.text=filteredAdresler.keys.elementAt(position)
        holder.itemView.setOnClickListener {
            //lokasyon seçildiğinde konumId sharedPrefences ile kaydoluyor
            ozelSharedPreferences.konumIdKaydet(filteredAdresler.getValue(filteredAdresler.keys.elementAt(position)))
            ozelSharedPreferences.konumKaydet(filteredAdresler.keys.elementAt(position))
            val action = KonumSecmeDirections.actionKonumSecmeToEzanSaatleri()
            Navigation.findNavController(it).navigate(action)

        }

    }

    override fun getItemCount(): Int {
        return filteredAdresler.size
    }

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                //constraint aranan kelime
                if (constraint != null) {
                    constraint.toString()
                    if (constraint.isEmpty()){
                        filteredAdresler=adresler
                    }else{
                        val tempAdresler= mutableMapOf<String,Int>()
                        for (item in adresler){
                            if (item.key.toLowerCase(Locale.ROOT).contains(constraint.toString().toLowerCase(Locale.ROOT))) {
                                tempAdresler.put(item.key,item.value)
                            }
                        }
                        filteredAdresler=tempAdresler

                    }

                }
                val filterResult=FilterResults()
                filterResult.values=filteredAdresler
                return  filterResult
            }
            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
               filteredAdresler= results?.values as Map<String, Int>
                notifyDataSetChanged()
            }

        }
    }
}