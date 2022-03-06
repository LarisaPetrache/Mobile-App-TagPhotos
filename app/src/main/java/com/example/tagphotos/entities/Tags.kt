package com.example.tagphotos.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Tags(
    @PrimaryKey val id: Int = 1,
    @ColumnInfo(name = "nume") var nume: String?
)
