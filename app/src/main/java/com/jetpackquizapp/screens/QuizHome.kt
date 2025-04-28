package com.jetpackquizapp.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.jetpackquizapp.utils.AppColors
import kotlinx.coroutines.CoroutineScope

@Composable
fun QuizHome(
    viewModel: QuestionViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    navController: NavHostController
) {

            Column(modifier = modifier.padding(all = 20.dp)) {
                Button(
                    onClick = {
                        navController.navigate("quiz/api")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(34.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.mLightBlue
                    ),
                    content = {
                        Text(
                            text = "Questions from API",
                            modifier = Modifier.padding(4.dp),
                            color = AppColors.mOffWhite,
                            fontSize = 17.sp
                        )
                    }
                )

                Button(
                    onClick = {
                        navController.navigate("quiz/db")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(34.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.mLightBlue
                    ),
                    content = {
                        Text(
                            text = "Questions from DB",
                            modifier = Modifier.padding(4.dp),
                            color = AppColors.mOffWhite,
                            fontSize = 17.sp
                        )
                    }
                )
            }


}
