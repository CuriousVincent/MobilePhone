package com.vincentwang.mobilephone.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vincentwang.mobilephone.model.data.CurrencyListData

@Dao
interface CurrencyDao {

    @Query("SELECT * FROM CurrencyListData")
    fun findAll(): List<CurrencyListData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(users: List<CurrencyListData>)

}