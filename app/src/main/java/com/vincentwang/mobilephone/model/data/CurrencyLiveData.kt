package com.vincentwang.mobilephone.model.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.JsonObject

data class CurrencyLiveResponse(
    val privacy: String,
    val quotes: JsonObject,
    val source: String,
    val success: Boolean,
    val terms: String,
    val timestamp: Int
)

@Entity
data class CurrencyListData(
    val source : String,
    @PrimaryKey
    val currency : String,
    val rate : Double
)