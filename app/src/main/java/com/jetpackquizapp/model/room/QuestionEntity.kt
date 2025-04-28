package com.jetpackquizapp.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val answer: String,
    val question: String,
    val choices: List<String>
)
