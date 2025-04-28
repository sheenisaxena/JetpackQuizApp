package com.jetpackquizapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jetpackquizapp.model.room.QuestionEntity

@Dao
interface QuestionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Query("select * from questions")
    suspend fun getAllQuestionsFromDB() : List<QuestionEntity>
}