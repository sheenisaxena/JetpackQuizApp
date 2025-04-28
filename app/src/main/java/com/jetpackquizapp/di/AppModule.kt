package com.jetpackquizapp.di

import android.content.Context
import androidx.room.Room
import com.jetpackquizapp.data.QuestionDao
import com.jetpackquizapp.data.QuizDatabase
import com.jetpackquizapp.network.QuestionApi
import com.jetpackquizapp.repository.QuestionRepository
import com.jetpackquizapp.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideQuestionRepository(api : QuestionApi, dao : QuestionDao) = QuestionRepository(api, dao)

    @Singleton
    @Provides
    fun provideQuestionApi() : QuestionApi{
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QuestionApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context : Context) : QuizDatabase {
        return Room.databaseBuilder(context,
            QuizDatabase::class.java,
            "quiz_db"
        ).build()
    }

    @Provides
    fun provideQuestionDao(db : QuizDatabase) : QuestionDao = db.questionDao()
}