package com.onuromer.ezanvakti.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext


//Bu base model coroutine içerir ve heryerde kullanabilir olduğu için base dedik
//job yani yapılacak iş yapılacak sonra main threat e dönecek
//Clear ilede job durdurulacak
open class BaseViewModel(application: Application) : AndroidViewModel(application),CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}