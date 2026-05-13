package com.naturetools.app.ui.screens.lifestyle

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.SportsEsports
import kotlin.random.Random
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
                "Snake", "Snake Game", "snake" -> SnakeGame()
                "Dino Jump" -> DinoJumpGame()
                "2048" -> Game2048()
                "Sudoku" -> SudokuGame()
                "Ludo", "ludo" -> LudoGame()
                "Carroms", "carroms" -> CarromsGame()
                "Chess", "chess" -> ChessGame()
                else -> {
                    Icon(Icons.Default.Casino, contentDescription = null, modifier = Modifier.size(64.dp))
                    Text("Game implementation for $title")
                }
            }
        }
    }
}

@Composable
fun ChessGame() {
    val board = remember {
        mutableStateListOf(
            "♜", "♞", "♝", "♛", "♚", "♝", "♞", "♜",
            "♟", "♟", "♟", "♟", "♟", "♟", "♟", "♟",
            "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "",
            "♙", "♙", "♙", "♙", "♙", "♙", "♙", "♙",
            "♖", "♘", "♗", "♕", "♔", "♗", "♘", "♖"
        )
    }
    var selectedIndex by remember { mutableIntStateOf(-1) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Chess", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(8),
            modifier = Modifier.size(320.dp).border(2.dp, Color.Black)
        ) {
            items(64) { i ->
                val row = i / 8
                val col = i % 8
                val isDark = (row + col) % 2 != 0
                val isSelected = selectedIndex == i

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .background(if (isSelected) Color.Yellow else if (isDark) Color(0xFF769656) else Color(0xFFEEEED2))
                        .clickable {
                            if (selectedIndex == -1) {
                                if (board[i].isNotEmpty()) selectedIndex = i
                            } else {
                                if (i != selectedIndex) {
                                    board[i] = board[selectedIndex]
                                    board[selectedIndex] = ""
                                }
                                selectedIndex = -1
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    val piece = board[i]
                    val isBlackPiece = piece.isNotEmpty() && "♜♞♝♛♚♟".contains(piece)
                    Text(piece, fontSize = 24.sp, color = if (isBlackPiece) Color.Black else Color.White)
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(if (selectedIndex == -1) "Select a piece" else "Select destination")
    }
}

@Composable
fun LudoGame() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Ludo", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.size(300.dp).border(2.dp, Color.Black)) {
            // Simplified Ludo Board Visualization
            Column(modifier = Modifier.fillMaxSize()) {
                Row(modifier = Modifier.weight(6f).fillMaxWidth()) {
                    Box(modifier = Modifier.weight(6f).fillMaxHeight().background(Color.Red).border(1.dp, Color.Black))
                    Box(modifier = Modifier.weight(3f).fillMaxHeight().background(Color.White).border(1.dp, Color.Black))
                    Box(modifier = Modifier.weight(6f).fillMaxHeight().background(Color.Green).border(1.dp, Color.Black))
                }
                Row(modifier = Modifier.weight(3f).fillMaxWidth()) {
                    Box(modifier = Modifier.weight(6f).fillMaxHeight().background(Color.White).border(1.dp, Color.Black))
                    Box(modifier = Modifier.weight(3f).fillMaxHeight().background(Color.Yellow).border(1.dp, Color.Black))
                    Box(modifier = Modifier.weight(6f).fillMaxHeight().background(Color.White).border(1.dp, Color.Black))
                }
                Row(modifier = Modifier.weight(6f).fillMaxWidth()) {
                    Box(modifier = Modifier.weight(6f).fillMaxHeight().background(Color.Blue).border(1.dp, Color.Black))
                    Box(modifier = Modifier.weight(3f).fillMaxHeight().background(Color.White).border(1.dp, Color.Black))
                    Box(modifier = Modifier.weight(6f).fillMaxHeight().background(Color.Yellow).border(1.dp, Color.Black))
                }
            }
            Text("🎲", modifier = Modifier.align(Alignment.Center), fontSize = 40.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {}) { Text("Roll Dice") }
    }
}

@Composable
fun CarromsGame() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Carroms", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .size(300.dp)
                .background(Color(0xFFDEB887))
                .border(8.dp, Color(0xFF5D4037)),
            contentAlignment = Alignment.Center
        ) {
            // Pockets
            Box(modifier = Modifier.size(30.dp).background(Color.Black, CircleShape).align(Alignment.TopStart).offset(x = (-5).dp, y = (-5).dp))
            Box(modifier = Modifier.size(30.dp).background(Color.Black, CircleShape).align(Alignment.TopEnd).offset(x = (5).dp, y = (-5).dp))
            Box(modifier = Modifier.size(30.dp).background(Color.Black, CircleShape).align(Alignment.BottomStart).offset(x = (-5).dp, y = (5).dp))
            Box(modifier = Modifier.size(30.dp).background(Color.Black, CircleShape).align(Alignment.BottomEnd).offset(x = (5).dp, y = (5).dp))

            // Center
            Box(modifier = Modifier.size(80.dp).border(1.dp, Color.Black, CircleShape))
            repeat(9) { i ->
                Box(modifier = Modifier.size(15.dp).background(if (i == 0) Color.Red else if (i % 2 == 0) Color.Black else Color.White, CircleShape).offset(x = (Math.cos(i * 0.7) * 20).dp, y = (Math.sin(i * 0.7) * 20).dp))
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("Flick the striker to play")
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
fun SnakeGame() {
    var score by remember { mutableIntStateOf(0) }
    var gameStarted by remember { mutableStateOf(false) }
    var direction by remember { mutableStateOf("RIGHT") }
    var headPos by remember { mutableStateOf(0 to 0) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Snake", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Box(modifier = Modifier.size(200.dp).background(Color.Black)) {
            if (!gameStarted) {
                Button(onClick = { gameStarted = true; score = 0; headPos = 5 to 5 }, modifier = Modifier.align(Alignment.Center)) { Text("Start") }
            } else {
                Text("Score: $score", color = Color.White, modifier = Modifier.align(Alignment.TopStart).padding(4.dp))
                LaunchedEffect(gameStarted) {
                    while(gameStarted) {
                        delay(300)
                        headPos = when(direction) {
                            "UP" -> headPos.first to (headPos.second - 1 + 10) % 10
                            "DOWN" -> headPos.first to (headPos.second + 1) % 10
                            "LEFT" -> (headPos.first - 1 + 10) % 10 to headPos.second
                            else -> (headPos.first + 1) % 10 to headPos.second
                        }
                        score++
                    }
                }
                Text("🐍", fontSize = 20.sp, modifier = Modifier.offset(x = (headPos.first * 20).dp, y = (headPos.second * 20).dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Column {
                Spacer(modifier = Modifier.width(40.dp))
                Button(onClick = { direction = "UP" }, modifier = Modifier.size(40.dp), contentPadding = PaddingValues(0.dp)) { Text("↑") }
            }
        }
        Row {
            Button(onClick = { direction = "LEFT" }, modifier = Modifier.size(40.dp), contentPadding = PaddingValues(0.dp)) { Text("←") }
            Button(onClick = { direction = "DOWN" }, modifier = Modifier.size(40.dp), contentPadding = PaddingValues(0.dp)) { Text("↓") }
            Button(onClick = { direction = "RIGHT" }, modifier = Modifier.size(40.dp), contentPadding = PaddingValues(0.dp)) { Text("→") }
        }
    }
}

@Composable
fun DinoJumpGame() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Dino Jump", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Box(modifier = Modifier.fillMaxWidth().height(150.dp).background(Color.LightGray)) {
            Text("🌵", modifier = Modifier.align(Alignment.BottomEnd).padding(end = 50.dp))
            Text("🦖", modifier = Modifier.align(Alignment.BottomStart).padding(start = 50.dp, bottom = 40.dp))
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {}) { Text("Jump") }
    }
}

@Composable
fun Game2048() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("2048", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        val grid = listOf(2, 4, 0, 0, 0, 2, 0, 0, 0, 0, 8, 0, 16, 0, 0, 0)
        LazyVerticalGrid(columns = GridCells.Fixed(4), modifier = Modifier.size(240.dp)) {
            items(grid) { value ->
                Card(modifier = Modifier.padding(4.dp).aspectRatio(1f), colors = CardDefaults.cardColors(containerColor = if (value == 0) Color.Gray else Color.Yellow)) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        if (value != 0) Text(value.toString(), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("Swipe to move tiles")
    }
}

@Composable
fun SudokuGame() {
    val puzzle = remember {
        mutableStateListOf<String>().apply {
            addAll(List(81) { if (Random.nextInt(10) > 7) (1..9).random().toString() else "" })
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Sudoku", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(9), modifier = Modifier.size(300.dp).border(1.dp, Color.Black)) {
            items(81) { i ->
                Box(
                    modifier = Modifier
                        .border(0.5.dp, Color.LightGray)
                        .aspectRatio(1f)
                        .clickable {
                            val current = puzzle[i].toIntOrNull() ?: 0
                            puzzle[i] = if (current == 9) "" else (current + 1).toString()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(puzzle[i], fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {
            puzzle.clear()
            puzzle.addAll(List(81) { if (Random.nextInt(10) > 7) (1..9).random().toString() else "" })
        }) { Text("New Puzzle") }
    }
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
