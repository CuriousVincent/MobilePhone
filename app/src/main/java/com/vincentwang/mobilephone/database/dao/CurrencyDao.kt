package com.vincentwang.mobilephone.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vincentwang.mobilephone.model.data.CurrencyListData
import io.reactivex.rxjava3.core.Single

@Dao
interface CurrencyDao {

    @Query("SELECT * FROM CurrencyListData")
    fun findAll(): Single<List<CurrencyListData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(users: List<CurrencyListData>)

}