package com.vincentwang.mobilephone.model

import com.vincentwang.mobilephone.api.CurrencyService
import com.vincentwang.mobilephone.database.dao.CurrencyDao
import com.vincentwang.mobilephone.model.data.CurrencyListData
import com.vincentwang.mobilephone.model.data.CurrencyLiveResponse
import io.reactivex.rxjava3.core.Observable

class CurrencyRepository(private val service: CurrencyService,private val dao: CurrencyDao) {

    fun getCurrencyLive() = service.getCurrencyLive()


    fun getCurrencyRateListByResponse(response: CurrencyLiveResponse):ArrayList<CurrencyListData>{
        val list = arrayListOf<CurrencyListData>()
        val entries = response.quotes.entrySet()

        for(entry in entries){
            val source = response.source
            val currency = entry.key.substring(3)
            val rate = entry.value.asDouble
            list.add(CurrencyListData(source, currency, rate))
        }
        insertCurrencyToDB(list)
        return list
    }

    fun insertCurrencyDataToDB(response: CurrencyLiveResponse){
        val list = arrayListOf<CurrencyListData>()
        val entries = response.quotes.entrySet()

        for(entry in entries){
            val source = response.source
            val currency = entry.key.substring(3)
            val rate = entry.value.asDouble
            list.add(CurrencyListData(source, currency, rate))
        }
        insertCurrencyToDB(list)
    }

    fun getCurrencyList(list : ArrayList<CurrencyListData>) : ArrayList<String>{
        val res = arrayListOf<String>()
        for(data in list){
            res.add(data.currency)
        }
        return res
    }

    private fun insertCurrencyToDB(list:List<CurrencyListData>) = dao.add(list)

    fun getCurrencyFromDB() = dao.findAll()


    fun getSelectCurrencyList(currencyData:ArrayList<CurrencyListData>,text:String): Observable<ArrayList<CurrencyListData>> {
        return Observable.create{emit->
            val data = currencyData.find { it.currency == text }
            if(data != null){
                val selectRate = data.rate
                val list = arrayListOf<CurrencyListData>()
                for(cData in currencyData){
                    list.add(CurrencyListData(source = text, cData.currency, cData.rate / selectRate))
                }
                emit.onNext(list)
                emit.onComplete()
            }else{
               emit.onComplete()
            }
        }
    }
}