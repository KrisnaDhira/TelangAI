package com.example.cobacapstone.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TokenDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToken(token: TokenEntity)

    @Query("SELECT * FROM token_table LIMIT 1")
    suspend fun getToken(): TokenEntity?

    @Query("DELETE FROM token_table")
    suspend fun deleteToken()
}