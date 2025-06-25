package com.example.cobacapstone.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "token_table")
data class TokenEntity(
    @PrimaryKey val id: Int = 1,
    val token: String
)
