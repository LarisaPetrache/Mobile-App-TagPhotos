package com.example.tagphotos.daos

import android.nfc.Tag
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.tagphotos.entities.Imagini
import com.example.tagphotos.entities.Tags

@Dao
interface TagsQuery {
    @Query("SELECT * FROM Tags")
    fun getALL(): List<Tags>

    @Query("SELECT id FROM Tags WHERE nume = :tagName")
    fun findByName(tagName: String): Int

    @Query("SELECT * FROM Imagini JOIN TagImagini on Imagini.id = TagImagini.id_imagine WHERE TagImagini.id_tag = :tagID")
    fun getImagesByTag(tagID: Int): List<Imagini>

    @Query("SELECT * FROM Imagini JOIN TagImagini on Imagini.id = TagImagini.id_imagine WHERE TagImagini.id_tag IN(:tagIds) GROUP BY id_imagine")
    fun findImagesByTagIds(tagIds: ArrayList<Int>): List<Imagini>

    @Query("SELECT * FROM Tags JOIN TagImagini on Tags.id = TagImagini.id_tag WHERE TagImagini.id_imagine = :id")
    fun findNameTagByImgId(id: Int): List<Tags>

    @Query("SELECT * FROM Tags JOIN TagImagini on Tags.id = TagImagini.id_tag WHERE TagImagini.id_imagine != :id GROUP BY Tags.nume")
    fun findNameTagExceptId(id: Int): List<Tags>

    @Query("UPDATE Tags SET nume = :newName WHERE nume LIKE :oldName")
    fun changeTagName(oldName: String, newName: String)

    @Query("SELECT * FROM Tags WHERE nume = :nume")
    fun getTagByName(nume: String): List<Tags>

    @Query("DELETE FROM Tags WHERE nume = :nume")
    fun deleteTagByName(nume: String)

    @Query("DELETE FROM TagImagini WHERE id_tag IN (SELECT id FROM Tags WHERE nume = :tagName)")
    fun deleteByTagName(tagName: String)

    @Insert
    fun insertAll(vararg tags: Tags)

    @Delete
    fun delete(tags: Tags)

    @Query("DELETE FROM Tags")
    fun clearTable()
}