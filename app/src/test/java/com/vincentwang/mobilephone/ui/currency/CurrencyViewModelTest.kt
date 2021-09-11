package com.vincentwang.mobilephone.ui.currency

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.Gson
import com.vincentwang.mobilephone.utils.TrampolineSchedulerProvider
import com.vincentwang.mobilephone.model.CurrencyRepository
import com.vincentwang.mobilephone.model.data.CurrencyListData
import com.vincentwang.mobilephone.model.data.CurrencyLiveResponse
import com.vincentwang.mobilephone.utils.readJsonFile
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestRule

class CurrencyViewModelTest {

    @get:Rule var rule: TestRule = InstantTaskExecutorRule()

    private val schedulers = TrampolineSchedulerProvider()
    private val repository : CurrencyRepository = mockk()
    private lateinit var viewModel :CurrencyViewModel

    @Before
    fun setUp(){
        every { repository.getCurrencyFromDB() } returns Single.just(listOf(
            CurrencyListData("USD","AED",3.67298)
        ))
        val data = Gson().fromJson(readJsonFile("currencyResponse.json"), CurrencyLiveResponse::class.java)
        every { repository.getCurrencyLive() } returns Observable.just(data)

        every{ repository.insertCurrencyDataToDB(any())} returns Unit
        viewModel = CurrencyViewModel(repository,schedulers)
    }


    @Test
    fun showCurrencyListDialog() {
        every { repository.getCurrencyList(any()) } returns arrayListOf("USD")
       val data = viewModel.showCurrencyListDialog().value
        verify { repository.getCurrencyList(any()) }
        assertEquals("USD",data?.get(0))
    }

    @Test
    fun selectCurrency() {
        every { repository.getSelectCurrencyList(any(),any()) } returns Observable.just(arrayListOf(
            CurrencyListData("AFN","AED",0.043)
        ))
        viewModel.selectCurrency("AFN")
        verify { repository.getSelectCurrencyList(any(),any()) }
        assertEquals("AFN",viewModel.selectCurrency.value)
        assertEquals("AED",viewModel.submitList.value?.get(0)?.currency)
        assertEquals(0.043,viewModel.submitList.value?.get(0)?.rate)
    }
}