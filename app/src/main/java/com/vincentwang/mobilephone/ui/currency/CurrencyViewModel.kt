package com.vincentwang.mobilephone.ui.currency

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.orhanobut.logger.Logger
import com.vincentwang.mobilephone.model.CurrencyRepository
import com.vincentwang.mobilephone.model.data.CurrencyListData
import com.vincentwang.mobilephone.model.data.CurrencyLiveResponse
import com.vincentwang.mobilephone.utils.SchedulerProvider
import com.vincentwang.mobilephone.utils.SingleLiveEvent
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableObserver
import java.util.concurrent.TimeUnit

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
    val showErrorDialog = SingleLiveEvent<String>()
    init {
        getCurrency()
    }

    fun onClick(v: View) {
        clickLiveEvent.postValue(v.id)
    }

    private fun getCurrencyWithInterval(){
        disposables.add(
        Observable.interval( 30, TimeUnit.MINUTES)
            .flatMap {
                repository.getCurrencyLive()
            }.subscribeOn(scheduler.io())
            .subscribeWith(object:DisposableObserver<CurrencyLiveResponse>(){
                override fun onNext(t: CurrencyLiveResponse) {
                    if(t.success){
                        repository.insertCurrencyDataToDB(t)
                    }
                }

                override fun onError(e: Throwable) {
                    Logger.e(e.message.orEmpty())
                }

                override fun onComplete() {
                }

            })
        )
    }


    private fun getCurrency() {
        disposables.add(
            repository.getCurrencyFromDB().toObservable()
                .flatMap { list ->
                    return@flatMap if (list.isEmpty()) {
                        repository.getCurrencyLive()
                            .observeOn(scheduler.ui())
                            .filter {
                                if (!it.success) {
                                    showErrorDialog.value = "UnSuccess"
                                }
                                it.success
                            }
                            .observeOn(scheduler.io())
                            .map {
                                repository.getCurrencyRateListByResponse(it)
                            }
                    } else {

                        Observable.just(list)
                    }
                }
                .observeOn(scheduler.ui())
                .subscribeOn(scheduler.io())
                .subscribeWith(object:DisposableObserver<List<CurrencyListData>>(){
                    override fun onNext(data: List<CurrencyListData>) {
                        selectCurrency.value = data[0].source
                        currencyData = ArrayList(data)
                        submitList.value = currencyData
                        getCurrencyWithInterval()
                    }

                    override fun onError(e: Throwable) {
                        showErrorDialog.value = e.message.orEmpty()
                    }

                    override fun onComplete() {}

                })
        )
    }

    fun showCurrencyListDialog():MutableLiveData<ArrayList<String>>{
        val liveData = MutableLiveData<ArrayList<String>>()
        liveData.value = repository.getCurrencyList(currencyData)
        return liveData
    }

    fun selectCurrency(text:String){
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