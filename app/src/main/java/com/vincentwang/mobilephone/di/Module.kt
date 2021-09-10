package com.vincentwang.mobilephone.di

import com.vincentwang.mobilephone.model.CurrencyRepository
import com.vincentwang.mobilephone.api.CurrencyService
import com.vincentwang.mobilephone.api.LoggerInterceptor
import com.vincentwang.mobilephone.ui.currency.CurrencyViewModel
import com.vincentwang.mobilephone.utils.AppSchedulerProvider
import com.vincentwang.mobilephone.utils.SchedulerProvider
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val currencyModule = module {
    single { CurrencyRepository(get()) }
    single { createOkHttpClient() }
    single { createWebService<CurrencyService>(get()) }
    single { AppSchedulerProvider() as SchedulerProvider }
    viewModel { CurrencyViewModel(get(),get()) }


}



inline fun <reified T> createWebService(okHttpClient: OkHttpClient): T {
    val url = "http://api.currencylayer.com/"
    val retrofit = Retrofit.Builder()
        .baseUrl(url)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.createSynchronous())
        .build()
    return retrofit.create(T::class.java)
}
fun createOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
        .retryOnConnectionFailure(true)
        .addNetworkInterceptor(LoggerInterceptor())
        .connectTimeout(60L, TimeUnit.SECONDS)
        .readTimeout(60L, TimeUnit.SECONDS)
        .connectionPool(ConnectionPool(0, 1, TimeUnit.NANOSECONDS))
        .build()
}