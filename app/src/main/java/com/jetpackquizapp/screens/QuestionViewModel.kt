package com.jetpackquizapp.screens

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetpackquizapp.data.DataOrException
import com.jetpackquizapp.model.QuestionItem
import com.jetpackquizapp.repository.QuestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionViewModel @Inject constructor(val repository: QuestionRepository)
    : ViewModel(){

    var data : MutableState<DataOrException<ArrayList<QuestionItem>, Boolean, Exception>> =
        mutableStateOf(DataOrException(null, true, Exception("")))

    private val _dataFromDb = MutableStateFlow(DataOrException<ArrayList<QuestionItem>, Boolean, Exception>())
    val dataFromDb: StateFlow<DataOrException<ArrayList<QuestionItem>, Boolean, Exception>> = _dataFromDb

    var correctAnswerCount = mutableStateOf<Int>(0)
        private set

    init {
        getAllQuestions()
    }

    internal fun getAllQuestions(){
        data = mutableStateOf(DataOrException(null, true, Exception("")))
        viewModelScope.launch {
            data.value.loading = true
            data.value = repository.getAllQuestions()
            if(data.value.data.toString().isNotEmpty()){
                data.value.loading = false
            }
        }
    }

    internal fun getQuestionsFromDb() {
        viewModelScope.launch {
            dataFromDb.value.loading = true
            Log.e("loading f view ==", "true")
            val localQuestions = repository.getAllQuestionFromDB()
            _dataFromDb.value = localQuestions.copy(loading = false)
            // Use or display localQuestions as needed
        }
    }

    fun getTotalQuestionCount() :Int{
        return data.value.data?.toMutableList()?.size?:0
    }
    fun getTotalQuestionCountFromDb() :Int{
        return dataFromDb.value.data?.toMutableList()?.size?:0
    }

    fun incrementCorrectCount() {
        correctAnswerCount.value++
    }
}