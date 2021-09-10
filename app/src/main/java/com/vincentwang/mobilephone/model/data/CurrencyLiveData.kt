package com.vincentwang.mobilephone.model.data

import com.google.gson.JsonObject

data class CurrencyLiveResponse(
    val privacy: String,
    val quotes: JsonObject,
    val source: String,
    val success: Boolean,
    val terms: String,
    val timestamp: Int
)

data class CurrencyListData(
    val source : String,
    val currency : String,
    val rate : Double
)