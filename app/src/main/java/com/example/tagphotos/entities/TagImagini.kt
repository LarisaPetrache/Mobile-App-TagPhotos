package com.example.tagphotos.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = Imagini::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("id_imagine"),
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Tags::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("id_tag"),
        onDelete = ForeignKey.CASCADE
    )]
)

data class TagImagini(
    @PrimaryKey val id: Int = 1,
    @ColumnInfo(name = "id_imagine") var id_imagine: Int,
    @ColumnInfo(name = "id_tag") var id_tag: Int

)
