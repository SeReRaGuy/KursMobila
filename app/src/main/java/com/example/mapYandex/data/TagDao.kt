package com.example.mapYandex.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TagDao {
    @Insert
    suspend fun insert(vararg tags: Tag)

    @Insert
    suspend fun insert(tag: Tag): Long

    @Query("SELECT * FROM tag")
    fun findAll(): LiveData<List<Tag>>

    @Query("SELECT * FROM tag WHERE id=:id LIMIT 1")
    fun findById(id: Long): LiveData<Tag>


    @Update
    suspend fun update(tag: Tag): Int

    @Delete
    suspend fun delete(tag: Tag): Int
}