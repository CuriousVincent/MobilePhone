package com.vincentwang.mobilephone.model

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.Gson
import com.vincentwang.mobilephone.api.CurrencyService
import com.vincentwang.mobilephone.database.dao.CurrencyDao
import com.vincentwang.mobilephone.model.data.CurrencyListData
import com.vincentwang.mobilephone.model.data.CurrencyLiveResponse
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.io.*


class CurrencyRepositoryTest {

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val service : CurrencyService = mockk()
    private val dao : CurrencyDao = mockk()

    val repository = CurrencyRepository(service, dao)



    @Test
    fun getCurrencyLive() {
        val data = Gson().fromJson(readJsonFile("currencyResponse.json"),CurrencyLiveResponse::class.java)
        every { service.getCurrencyLive() } returns Observable.just(data)
        repository.getCurrencyLive().subscribe {
            assertEquals(it.success,data.success)
            assertEquals(it.source,data.source)
        }
        verify { service.getCurrencyLive() }
    }

    @Test
    fun getCurrencyRateListByResponse() {
        val data = Gson().fromJson(readJsonFile("currencyResponse.json"),CurrencyLiveResponse::class.java)
        every { dao.add(any()) } returns Unit
        val list= repository.getCurrencyRateListByResponse(data)
        verify { dao.add(any()) }
        assertEquals("USD",list[0].source)
        assertEquals("AED",list[0].currency)
        assertEquals(3.67298,list[0].rate,0.001)
    }

    @Test
    fun insertCurrencyDataToDB() {
        val data = Gson().fromJson(readJsonFile("currencyResponse.json"),CurrencyLiveResponse::class.java)
        every { dao.add(any()) } returns Unit
        repository.insertCurrencyDataToDB(data)
        verify { dao.add(any()) }
    }

    @Test
    fun getCurrencyList() {

        val data= arrayListOf<CurrencyListData>(CurrencyListData("USD","AED",3.67298))
        val stringList = repository.getCurrencyList(data)
        assertEquals("AED",stringList[0])
        assertEquals(1,stringList.size)
    }

    @Test
    fun getCurrencyFromDB() {
        val data= arrayListOf<CurrencyListData>(CurrencyListData("USD","AED",3.67298))
        every { dao.findAll() } returns Single.just(data)
        repository.getCurrencyFromDB().subscribe { list ->
            assertEquals("USD",list[0].source)
            assertEquals("AED",list[0].currency)
            assertEquals(3.67298,list[0].rate,0.001)
            assertEquals(1,list.size)
        }
        verify { dao.findAll() }

    }

    @Throws(IOException::class)
    fun readJsonFile(filename: String): String? {
        val br =
            BufferedReader(InputStreamReader(FileInputStream("../app/src/main/assets/$filename")))
        val sb = StringBuilder()
        var line = br.readLine()
        while (line != null) {
            sb.append(line)
            line = br.readLine()
        }
        return sb.toString()
    }


}