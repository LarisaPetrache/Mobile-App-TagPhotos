package com.example.tagphotos.entities

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Imagini(
    @PrimaryKey val id: Int = 1,
    @ColumnInfo(name = "nume") var nume: String?,
    @ColumnInfo(name = "bitmap") var bitmap: Bitmap?
)
