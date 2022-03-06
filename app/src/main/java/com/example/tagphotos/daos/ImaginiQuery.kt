package com.example.tagphotos.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.tagphotos.entities.Imagini

@Dao
interface ImaginiQuery {
    @Query("SELECT * FROM Imagini")
    fun getALL(): List<Imagini>

    @Query("SELECT * FROM Imagini WHERE id = :id")
    fun getImageById(id: Int): List<Imagini>

    @Query("DELETE FROM Imagini WHERE id = :id")
    fun deleteById(id: Int)

    @Insert
    fun insertAll(vararg imagini: Imagini)

    @Delete
    fun delete(imagini: Imagini)

    @Query("DELETE FROM Imagini")
    fun clearTable()
}