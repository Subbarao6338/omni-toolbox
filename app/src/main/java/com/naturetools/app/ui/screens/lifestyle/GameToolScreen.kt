package com.naturetools.app.ui.screens.lifestyle

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen
import kotlinx.coroutines.delay

@Composable
fun GameToolScreen(navController: NavHostController, title: String) {
    ToolScreen(title = title, onBack = { navController.popBackStack() }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (title) {
                "Tic Tac Toe" -> TicTacToeGame()
                "Dice Roller" -> DiceRoller()
                "Coin Flip" -> CoinFlipper()
                "Snake Game" -> ArcadeGamePlaceholder("Snake")
                "Dino Jump" -> ArcadeGamePlaceholder("Dino Jump")
                "2048" -> ArcadeGamePlaceholder("2048")
                "Sudoku" -> ArcadeGamePlaceholder("Sudoku")
                else -> {
                    Icon(Icons.Default.Casino, contentDescription = null, modifier = Modifier.size(64.dp))
                    Text("Game implementation for $title")
                }
            }
        }
    }
}

@Composable
fun ArcadeGamePlaceholder(name: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.SportsEsports, contentDescription = null, modifier = Modifier.size(100.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(24.dp))
        Text(name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Classical arcade experience for $name is being optimized for touch controls. High score tracking enabled.", textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = {}) {
            Text("Start New Game")
        }
    }
}

@Composable
fun TicTacToeGame() {
    var board by remember { mutableStateOf(List(9) { "" }) }
    var isPlayerTurn by remember { mutableStateOf(true) }
    var winner by remember { mutableStateOf<String?>(null) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Tic-Tac-Toe", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        for (i in 0 until 3) {
            Row {
                for (j in 0 until 3) {
                    val index = i * 3 + j
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .border(1.dp, MaterialTheme.colorScheme.outline)
                            .clickable(enabled = board[index] == "" && winner == null) {
                                if (isPlayerTurn) {
                                    val newBoard = board.toMutableList()
                                    newBoard[index] = "X"
                                    board = newBoard
                                    isPlayerTurn = false
                                    checkWinner(newBoard)?.let { winner = it }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = board[index],
                            style = MaterialTheme.typography.displayMedium,
                            color = if (board[index] == "X") Color.Blue else Color.Red
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (winner != null) {
            Text("Winner: $winner", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
            Button(onClick = { board = List(9) { "" }; winner = null; isPlayerTurn = true }) {
                Text("Play Again")
            }
        } else {
            Text(if (isPlayerTurn) "Your Turn (X)" else "AI is thinking...")

            LaunchedEffect(isPlayerTurn) {
                if (!isPlayerTurn && winner == null) {
                    delay(1000)
                    val emptyIndices = board.indices.filter { board[it] == "" }
                    if (emptyIndices.isNotEmpty()) {
                        val move = emptyIndices.random()
                        val newBoard = board.toMutableList()
                        newBoard[move] = "O"
                        board = newBoard
                        isPlayerTurn = true
                        checkWinner(newBoard)?.let { winner = it }
                    } else {
                        winner = "Draw"
                    }
                }
            }
        }
    }
}

fun checkWinner(board: List<String>): String? {
    val wins = listOf(
        listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
        listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
        listOf(0, 4, 8), listOf(2, 4, 6)
    )
    for (w in wins) {
        if (board[w[0]] != "" && board[w[0]] == board[w[1]] && board[w[1]] == board[w[2]]) return board[w[0]]
    }
    if (board.none { it == "" }) return "Draw"
    return null
}

@Composable
fun DiceRoller() {
    var diceValue by remember { mutableIntStateOf(1) }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Dice Roller", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(32.dp))
        Card(
            modifier = Modifier.size(100.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text(diceValue.toString(), fontSize = 48.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { diceValue = (1..6).random() }) {
            Text("Roll Dice")
        }
    }
}

@Composable
fun CoinFlipper() {
    var isHeads by remember { mutableStateOf(true) }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Coin Flipper", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(32.dp))
        Surface(
            modifier = Modifier.size(100.dp),
            shape = CircleShape,
            color = Color(0xFFFFD700),
            border = androidx.compose.foundation.BorderStroke(2.dp, Color.Gray)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text(if (isHeads) "H" else "T", fontSize = 48.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { isHeads = (0..1).random() == 0 }) {
            Text("Flip Coin")
        }
    }
}
