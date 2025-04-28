package com.jetpackquizapp.data

import com.jetpackquizapp.model.QuestionItem
import com.jetpackquizapp.model.room.QuestionEntity

    fun QuestionItem.toEntity() : QuestionEntity{
        return QuestionEntity(
            answer = this.answer,
            question = this.question,
            choices = this.choices
        )
    }

    fun QuestionEntity.toQuestionItem() : QuestionItem {
        return QuestionItem(
            answer = this.answer,
            category = "",
            question = this.question,
            choices = this.choices
        )
    }
