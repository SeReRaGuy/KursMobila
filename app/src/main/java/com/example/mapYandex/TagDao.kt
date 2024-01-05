package com.example.mapYandex

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TagDao {
    @Insert
    suspend fun insert(vararg Tags: Tag)

    @Insert
    suspend fun insert(Tags: Tag)

    @Query("SELECT * FROM tag")
    fun findAll(): LiveData<List<Tag>>

    @Update
    suspend fun update(tag: Tag): Int

    @Delete
    suspend fun delete(tag: Tag): Int
}