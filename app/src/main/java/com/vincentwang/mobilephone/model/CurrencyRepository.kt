package com.vincentwang.mobilephone.model

import com.vincentwang.mobilephone.api.CurrencyService
import com.vincentwang.mobilephone.database.dao.CurrencyDao
import com.vincentwang.mobilephone.model.data.CurrencyListData
import com.vincentwang.mobilephone.model.data.CurrencyLiveResponse

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

    fun getCurrencyList(list : ArrayList<CurrencyListData>) : ArrayList<String>{
        val res = arrayListOf<String>()
        for(data in list){
            res.add(data.currency)
        }
        return res
    }

    fun insertCurrencyToDB(list:List<CurrencyListData>) = dao.add(list)

    fun getCurrencyFromDB() = dao.findAll()

}