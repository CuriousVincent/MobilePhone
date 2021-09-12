package com.vincentwang.mobilephone.ui.currency

import android.annotation.SuppressLint
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jakewharton.rxbinding2.view.RxView
import com.orhanobut.logger.Logger
import com.vincentwang.mobilephone.model.CurrencyRepository
import com.vincentwang.mobilephone.model.data.CurrencyListData
import com.vincentwang.mobilephone.model.data.CurrencyLiveResponse
import com.vincentwang.mobilephone.utils.SchedulerProvider
import com.vincentwang.mobilephone.utils.SingleLiveEvent
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import java.util.concurrent.TimeUnit

class CurrencyViewModel(
    private val repository: CurrencyRepository,
    private val scheduler: SchedulerProvider
) : ViewModel() {

    private val disposables by lazy { CompositeDisposable() }

    val amount = MutableLiveData<String>()
    val selectCurrency = MutableLiveData<String>()
    val submitList = SingleLiveEvent<ArrayList<CurrencyListData>>()
    val showErrorDialog = SingleLiveEvent<String>()

    init {
        getCurrency()
    }


    private fun getCurrencyWithInterval() {
        disposables.add(
            Observable.interval(30, TimeUnit.MINUTES)
                .flatMap {
                    repository.getCurrencyLive()
                }.subscribeOn(scheduler.io())
                .subscribeWith(object : DisposableObserver<CurrencyLiveResponse>() {
                    override fun onNext(t: CurrencyLiveResponse) {
                        if (t.success) {
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
                .subscribeWith(object : DisposableObserver<List<CurrencyListData>>() {
                    override fun onNext(data: List<CurrencyListData>) {
                        selectCurrency.value = data[0].source
                        submitList.value = ArrayList(data)
                        getCurrencyWithInterval()
                    }

                    override fun onError(e: Throwable) {
                        showErrorDialog.value = e.message.orEmpty()
                    }

                    override fun onComplete() {}

                })
        )
    }

    fun showCurrencyListDialog(): MutableLiveData<List<String>> {
        val liveData = MutableLiveData<List<String>>()
        repository.getCurrencyListFromDB()
            .observeOn(scheduler.ui())
            .subscribeOn(scheduler.io())
            .subscribeWith(object:DisposableSingleObserver<List<String>>(){
            override fun onSuccess(t: List<String>) {
                liveData.value = t
            }

            override fun onError(e: Throwable) {
                showErrorDialog.value = e.message.orEmpty()
            }

        })
        return liveData
    }

    fun selectCurrency(text: String) {
        Observable.just(text != selectCurrency.value)
            .filter {
                it
            }.flatMap {
                repository.getSelectCurrencyList( text)
            }
            .observeOn(scheduler.ui())
            .subscribeOn(scheduler.io())
            .subscribeWith(object : DisposableObserver<ArrayList<CurrencyListData>>() {
                override fun onNext(t: ArrayList<CurrencyListData>) {
                    selectCurrency.value = text
                    submitList.value = t
                }

                override fun onError(e: Throwable) {
                    showErrorDialog.value = e.message.orEmpty()
                }

                override fun onComplete() {}

            })
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