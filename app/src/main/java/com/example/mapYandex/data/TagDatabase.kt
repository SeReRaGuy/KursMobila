package com.example.mapYandex.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mapYandex.TagConverters

@Database(entities = [Tag::class], version = 10)
@TypeConverters(TagConverters::class)
abstract class TagDatabase : RoomDatabase(){
    abstract fun tagDao(): TagDao
    companion object{
        private var tagDatabase: TagDatabase? = null
        fun getInstance(context: Context): TagDatabase {
            synchronized(this){
                var databaseInstance = tagDatabase
                if (databaseInstance == null){
                    databaseInstance = Room.databaseBuilder(context, TagDatabase::class.java, "tagDatabase").fallbackToDestructiveMigration().build()
                    tagDatabase = databaseInstance
                }
                return databaseInstance
            }
        }
    }
}