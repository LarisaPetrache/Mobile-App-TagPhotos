package com.example.tagphotos.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.tagphotos.daos.ImaginiQuery
import com.example.tagphotos.daos.TagImaginiQuery
import com.example.tagphotos.daos.TagsQuery
import com.example.tagphotos.entities.Converters
import com.example.tagphotos.entities.Imagini
import com.example.tagphotos.entities.TagImagini
import com.example.tagphotos.entities.Tags

@Database(entities = arrayOf(Tags::class, Imagini::class, TagImagini::class), version = 3)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun tagsDao(): TagsQuery
    abstract fun imaginiDao(): ImaginiQuery
    abstract fun tagImaginiDao(): TagImaginiQuery
}