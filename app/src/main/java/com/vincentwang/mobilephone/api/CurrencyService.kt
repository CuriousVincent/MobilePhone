package com.vincentwang.mobilephone.api

import com.vincentwang.mobilephone.model.data.CurrencyLiveResponse
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET

interface CurrencyService {

    @GET("live?access_key=d3c4e83879c302e562636f2f90b3f640")
    fun getCurrencyLive(): Observable<CurrencyLiveResponse>

}