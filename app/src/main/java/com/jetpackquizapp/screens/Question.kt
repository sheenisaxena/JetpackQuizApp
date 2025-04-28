package com.jetpackquizapp.screens

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jetpackquizapp.model.QuestionItem
import com.jetpackquizapp.utils.AppColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Question(
    viewModel: QuestionViewModel,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    screenState: String
) {
    // 🔥 Trigger the right fetch based on screenState
    LaunchedEffect(screenState) {
        when (screenState) {
            "api" -> {
                viewModel.getAllQuestions()
            }
            "db" -> {
                viewModel.getQuestionsFromDb()
            }
        }
    }

    // ✅ Always collect the Flow, regardless of condition
    val dataFromDbState = viewModel.dataFromDb.collectAsState()
    val dataFromApiState = viewModel.data

    //  Observe the appropriate data source
    val dataState = if (screenState == "api") {
        dataFromApiState.value
    } else {
        // This line observes the StateFlow reactively
        dataFromDbState.value
    }

    val loading = dataState.loading == true
    val questions = dataState.data?.toMutableList()
    Log.d("data size = ", "ques : ${questions?.size}")

    val questionIndex = remember {
        mutableIntStateOf(1)
    }
    if (loading || questions == null) {
        Log.e("loading com ==", "true")
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Log.e("loading com ==", "false")

        val questionAsPerindex = questions.getOrNull(questionIndex.intValue - 1)

        if (questionAsPerindex != null) {
            QuestionDisplay(
                snackbarHostState = snackbarHostState,
                coroutineScope = coroutineScope,
                question = questionAsPerindex,
                questionIndex,
                viewModel,
                screenState
            ) {
                questionIndex.intValue = questionIndex.intValue + 1
            }
        } else {
            // last question completed now display final score
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                color = AppColors.mDarkPurple
            ) {
                val totalPercent = ((viewModel.correctAnswerCount.value.toFloat() / questions.size.toFloat()) * 100)
                val formattedPercent = String.format("%.1f", totalPercent)

                Column(modifier = Modifier.padding(top = 10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Your total score percentage = $formattedPercent%",
                        modifier = Modifier
                            .padding(all = 10.dp)
                            .fillMaxHeight(0.3f),
                        color = AppColors.mOffWhite,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 22.sp,
                    )
                }
            }
        }
    }
}

@Composable
fun QuestionDisplay(
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    question: QuestionItem,
    questionIndex: MutableState<Int>,
    viewModel: QuestionViewModel,
    screenState: String,
    onNextClicked: (Int) -> Unit = {},
) {
    val choicesState = remember(question) {
        question.choices.toMutableList()
    }
    val answerStateUserIndex = remember(question) {
        mutableStateOf<Int?>(null)
    }
    val correctAnswerState = remember(question) {
        mutableStateOf<Boolean?>(null)
    }
    val answeredState = remember(question) {
        mutableStateOf(false)
    }
    val errorState = remember(question) {
        mutableStateOf(false)
    }

    val updateAnswer: (Int) -> Unit = remember(question) {
        {
            if (!answeredState.value) {
                answerStateUserIndex.value = it
                correctAnswerState.value = choicesState[it] == question.answer
                if (choicesState[it] == question.answer) {
                    viewModel.incrementCorrectCount()
                }
                answeredState.value = true
            }
        }
    }
    val pathEffectValue = PathEffect.dashPathEffect(
        intervals = floatArrayOf(10f, 10f), phase = 0f
    )
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        color = AppColors.mDarkPurple
    ) {

        Column(
            modifier = Modifier.padding(25.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.Start
        ) {

            if(questionIndex.value > 3) ShowProgress(questionIndex.value, correctAnswerCount = viewModel.correctAnswerCount.value)

            QuestionTracker(counter = questionIndex.value, outOf = if (screenState == "api") viewModel.getTotalQuestionCount() else viewModel.getTotalQuestionCountFromDb())

            DrawDottedLine(pathEffect = pathEffectValue)

            Column(modifier = Modifier.padding(top = 10.dp)) {
                Text(
                    text = question.question,
                    modifier = Modifier
                        .padding(all = 10.dp)
                        .align(alignment = Alignment.Start)
                        .fillMaxHeight(0.3f),
                    color = AppColors.mOffWhite,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp,
                )

                //choices
                choicesState.forEachIndexed { index, choicesText ->

                    val isSelected = answerStateUserIndex.value == index
                    val isCorrect = correctAnswerState.value == true && isSelected
                    val isIncorrect = correctAnswerState.value == false && isSelected

                    Row(
                        modifier = Modifier
                            .padding(3.dp)
                            .fillMaxWidth()
                            .height(45.dp)
                            .then(
                                if (!answeredState.value) Modifier.clickable { updateAnswer(index) }
                                else Modifier // No clickable after answered
                            )
                            .border(
                                width = 4.dp, brush = Brush.linearGradient(
                                    colors = listOf(
                                        AppColors.mOffDarkPurple,
                                        AppColors.mOffDarkPurple
                                    )
                                ),
                                shape = RoundedCornerShape(15.dp)
                            )
                            .clip(
                                RoundedCornerShape(
                                    topStartPercent = 50,
                                    topEndPercent = 50, bottomEndPercent = 50,
                                    bottomStartPercent = 50
                                )
                            )
                            .background(color = Color.Transparent)
                            .clickable{
                                updateAnswer(index)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = null,
                            modifier = Modifier.padding(start = 10.dp),
                            colors = RadioButtonDefaults.colors(
                                selectedColor = if (isCorrect) {
                                    Color.Green
                                } else {
                                    Color.Red
                                }
                            )
                        )

                        Text(
                            text = choicesText,
                            color = if (isCorrect) {
                                Color.Green
                            } else if (isIncorrect) {
                                Color.Red
                            } else {
                                Color.White
                            }
                        )

                    }
                }

                Button(
                    onClick = {
                        if(answeredState.value) {
                            errorState.value = false
                            onNextClicked(questionIndex.value)
                        } else {
                            errorState.value = true
                             coroutineScope.launch {
                                snackbarHostState.showSnackbar("Please select an answer before proceeding.")
                            }
                        } },
                    modifier = Modifier
                        .padding(3.dp)
                        .align(alignment = Alignment.CenterHorizontally),
                    shape = RoundedCornerShape(34.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.mLightBlue
                    ),
                    content = {
                        Text(
                            text = "Next",
                            modifier = Modifier.padding(4.dp),
                            color = AppColors.mOffWhite,
                            fontSize = 17.sp
                        )

                    })


            }

            if (errorState.value) {
                Text(
                    text = "Please select an answer before proceeding.",
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .align(Alignment.CenterHorizontally)
                )
                LaunchedEffect(Unit) {
                    delay(3000)
                    errorState.value = false
                }
            }
        }
    }
}

@Composable
fun QuestionTracker(counter: Int = 10, outOf: Int = 100) {
    Text(text = buildAnnotatedString {
        withStyle(style = ParagraphStyle(textIndent = TextIndent.None)) {
            withStyle(
                style = SpanStyle(
                    color = AppColors.mOffWhite,
                    fontWeight = FontWeight.Bold, fontSize = 26.sp
                )
            ) {
                append(text = "Question $counter/")
            }
            withStyle(
                style = SpanStyle(
                    color = AppColors.mLightGray,
                    fontWeight = FontWeight.Bold, fontSize = 16.sp
                )
            ) {
                append(text = "$outOf")
            }
        }
    }, modifier = Modifier.padding(top = 10.dp))
}

@Composable
fun DrawDottedLine(pathEffect: PathEffect) {
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(1.dp)
        .padding(top = 10.dp)) {
        drawLine(
            color = AppColors.mLightGray,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = pathEffect,
        )
    }
}

@Preview
@Composable
fun ShowProgress(score: Int = 12, correctAnswerCount: Int = 0){

    val gradient = Brush.linearGradient(listOf(Color(0xFFF95075),
        Color(0xFFBE6BE5)))

    val progressFactor = remember(score){
        mutableStateOf(score * 0.005f)
    }

    Row(modifier = Modifier.padding(all = 5.dp).fillMaxWidth()
        .height(45.dp)
        .border(width = 3.dp,
            brush = Brush.linearGradient(colors = listOf(AppColors.mLightPurple, AppColors.mLightPurple))
            , shape = RoundedCornerShape(34.dp)
        )
        .clip(shape = RoundedCornerShape(50))
        .background(Color.Transparent)
    ) {
        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth(progressFactor.value)
                .background(brush = gradient),
            enabled = false,
            elevation = null,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            )
        ){
            Text(text = (correctAnswerCount * 10).toString(),
                modifier = Modifier
                    .fillMaxWidth().align(alignment = Alignment.CenterVertically),
                color = AppColors.mOffWhite,
                fontSize = 16.sp,
                maxLines = 1,
                softWrap = false,
                textAlign = TextAlign.Start
            )
        }
    }
}
