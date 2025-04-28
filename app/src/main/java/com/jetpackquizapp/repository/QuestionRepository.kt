package com.jetpackquizapp.repository

import android.util.Log
import com.jetpackquizapp.data.DataOrException
import com.jetpackquizapp.data.QuestionDao
import com.jetpackquizapp.data.toEntity
import com.jetpackquizapp.data.toQuestionItem
import com.jetpackquizapp.model.QuestionItem
import com.jetpackquizapp.network.QuestionApi
import javax.inject.Inject

class QuestionRepository @Inject constructor(private val api : QuestionApi, private val dao : QuestionDao) {

    private val dataOrException = DataOrException<ArrayList<QuestionItem>, Boolean, Exception>()

    suspend fun getAllQuestions() : DataOrException<ArrayList<QuestionItem>, Boolean, Exception> {
        try {
            dataOrException.loading = true
            dataOrException.data = api.getAllQuestions()
            if(dataOrException.data.toString().isNotEmpty())
                dataOrException.loading = false
            val existingDBQuestions = dao.getAllQuestionsFromDB()
            if(existingDBQuestions.isEmpty()) {
                dataOrException.data?.let {
                    // Save first 20 questions into Room
                    val first20Entities = it.take(20).map { it.toEntity() }
                    dao.insertQuestions(first20Entities)
                }
            }
        } catch (e: Exception){
            dataOrException.exception = e
            Log.d("Quiz Exception = ", "getAllQuestions: ${dataOrException.exception!!.localizedMessage}")
        }

        return dataOrException
    }

    suspend fun getAllQuestionFromDB() : DataOrException<ArrayList<QuestionItem>, Boolean, Exception> {
        try{
           dataOrException.loading = true
           dataOrException.data = dao.getAllQuestionsFromDB().map { it.toQuestionItem() } as ArrayList<QuestionItem>?
            if(dataOrException.data.toString().isNotEmpty())
                dataOrException.loading = false
        } catch (e : Exception){
            dataOrException.exception = e
        }
        return dataOrException
    }
}


