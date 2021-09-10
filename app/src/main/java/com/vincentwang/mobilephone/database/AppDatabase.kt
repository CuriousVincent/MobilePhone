package com.vincentwang.mobilephone.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vincentwang.mobilephone.database.dao.CurrencyDao
import com.vincentwang.mobilephone.model.data.CurrencyListData

@Database(entities = arrayOf(CurrencyListData::class), version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun currencyDao(): CurrencyDao
}