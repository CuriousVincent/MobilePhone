package com.vincentwang.mobilephone.ui.currency

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.orhanobut.logger.Logger
import com.vincentwang.mobilephone.model.CurrencyRepository
import com.vincentwang.mobilephone.model.data.CurrencyListData
import com.vincentwang.mobilephone.utils.SchedulerProvider
import com.vincentwang.mobilephone.utils.SingleLiveEvent
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableObserver

class CurrencyViewModel(
    private val repository: CurrencyRepository,
    private val scheduler: SchedulerProvider
) : ViewModel() {

    private val disposables by lazy { CompositeDisposable() }

    val amount = MutableLiveData<String>()
    val selectCurrency = MutableLiveData<String>()
    var currencyData = ArrayList<CurrencyListData>()
    val submitList = SingleLiveEvent<ArrayList<CurrencyListData>>()
    val clickLiveEvent by lazy { SingleLiveEvent<Int>() }
    init {
        getCurrency()
    }

    fun onClick(v: View) {
        clickLiveEvent.postValue(v.id)
    }

    private fun getCurrency() {
        disposables.add(
            repository.getCurrencyLive()
                .observeOn(scheduler.ui())
                .filter {
                    if (!it.success) {
                        Logger.e("UnSuccess")
                    }
                    selectCurrency.value = it.source
                    it.success
                }
                .observeOn(scheduler.io())
                .map {
                    repository.getCurrencyRateListByResponse(it)
                }
                .observeOn(scheduler.ui())
                .subscribeOn(scheduler.io())
                .subscribeWith(object : DisposableObserver<ArrayList<CurrencyListData>>() {
                    override fun onNext(data: ArrayList<CurrencyListData>) {
                        currencyData = data
                        submitList.value = currencyData
                    }

                    override fun onError(e: Throwable) {
                        Logger.e(e.message.orEmpty())
                    }

                    override fun onComplete() {

                    }
                })
        )
    }

    fun showCurrencyListDialog():MutableLiveData<ArrayList<String>>{
        val liveData = MutableLiveData<ArrayList<String>>()
        liveData.value = repository.getCurrencyList(currencyData)
        return liveData
    }

    fun selectCurrency(text:String){
        //TODO repository
        if(text != selectCurrency.value){
            selectCurrency.value = text
            currencyData.find { it.currency == text }?.apply {
                val selectRate = rate
                val list = arrayListOf<CurrencyListData>()
                for(data in currencyData){
                    list.add(CurrencyListData(source = text, data.currency, data.rate / selectRate))
                }
                currencyData = list
                submitList.value = currencyData
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}

data class CurrencyItemViewModel(val data: CurrencyListData) {
    val currency = MutableLiveData("${data.source} / ${data.currency}")
    val rate = MutableLiveData(String.format("%.3f", data.rate))
}