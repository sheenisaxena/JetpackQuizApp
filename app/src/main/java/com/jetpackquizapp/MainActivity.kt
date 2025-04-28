package com.jetpackquizapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jetpackquizapp.screens.Question
import com.jetpackquizapp.screens.QuizHome
import com.jetpackquizapp.ui.theme.JetpackQuizAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        enableEdgeToEdge()
        setContent {
            JetpackQuizAppTheme {
                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }
                val coroutineScope = rememberCoroutineScope()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState)
                    }) { innerPadding ->

                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") {
                            QuizHome(
                                modifier = Modifier.padding(innerPadding),
                                snackbarHostState = snackbarHostState,
                                coroutineScope = coroutineScope,
                                navController = navController
                            )
                        }

                        composable("quiz/{source}") { backStackEntry ->
                            val source = backStackEntry.arguments?.getString("source") ?: "api"
                            Question(
                                viewModel = hiltViewModel(),
                                snackbarHostState = snackbarHostState,
                                coroutineScope = coroutineScope,
                                screenState = source
                            )
                        }

                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JetpackQuizAppTheme {
        val snackbarHostState = remember { SnackbarHostState() }
        val coroutineScope = rememberCoroutineScope()
        QuizHome(snackbarHostState = snackbarHostState, coroutineScope = coroutineScope, navController = rememberNavController())
    }
}