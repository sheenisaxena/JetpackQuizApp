package com.jetpackquizapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jetpackquizapp.model.room.QuestionEntity

@Database(entities = [QuestionEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class QuizDatabase : RoomDatabase() {
    abstract fun questionDao() : QuestionDao
}