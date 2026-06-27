package omni.toolbox.ui.screens.lifestyle

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
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
                "Game of Life" -> GameOfLife()
                "Clash Deck" -> ClashDeckBuilder()
                "Roulette" -> RouletteGame()
                "Memory Game" -> MemoryGameV2()
                "Number Guessing" -> NumberGuessingGame()
                "Random Gen" -> RandomGeneratorGame()
                else -> {
                    Icon(Icons.Default.Casino, contentDescription = null, modifier = Modifier.size(64.dp))
                    Text("Simulation for $title active. Scores being tracked.")
                }
            }
        }
    }
}

@Composable
fun ChessGame() {
    var isPvP by remember { mutableStateOf(true) }
    var gameStarted by remember { mutableStateOf(false) }

    if (!gameStarted) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Chess", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = { isPvP = true; gameStarted = true }, modifier = Modifier.fillMaxWidth()) { Text("Player vs Player") }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { isPvP = false; gameStarted = true }, modifier = Modifier.fillMaxWidth()) { Text("Player vs AI") }
        }
    } else {
        ChessBoard(isPvP) { gameStarted = false }
    }
}

@Composable
fun ChessBoard(isPvP: Boolean, onReset: () -> Unit) {
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
    var isWhiteTurn by remember { mutableStateOf(true) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(if (isPvP) "Chess (PvP)" else "Chess (vs AI)", style = MaterialTheme.typography.titleMedium)
        Text("Turn: ${if (isWhiteTurn) "White" else "Black"}")
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
                        .clickable(enabled = isPvP || isWhiteTurn) {
                            if (selectedIndex == -1) {
                                if (board[i].isNotEmpty()) {
                                    val isBlack = "♜♞♝♛♚♟".contains(board[i])
                                    if (isWhiteTurn && !isBlack) selectedIndex = i
                                    else if (!isWhiteTurn && isBlack && isPvP) selectedIndex = i
                                }
                            } else {
                                if (i != selectedIndex) {
                                    board[i] = board[selectedIndex]
                                    board[selectedIndex] = ""
                                    isWhiteTurn = !isWhiteTurn
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
        Row {
            Button(onClick = { onReset() }) { Text("Quit") }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = {
                board.clear()
                board.addAll(listOf(
                    "♜", "♞", "♝", "♛", "♚", "♝", "♞", "♜", "♟", "♟", "♟", "♟", "♟", "♟", "♟", "♟",
                    "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
                    "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
                    "♙", "♙", "♙", "♙", "♙", "♙", "♙", "♙", "♖", "♘", "♗", "♕", "♔", "♗", "♘", "♖"
                ))
                isWhiteTurn = true
            }) { Text("Restart") }
        }

        if (!isWhiteTurn && !isPvP) {
            LaunchedEffect(isWhiteTurn) {
                delay(1000)
                val blackPieces = board.indices.filter { board[it].isNotEmpty() && "♜♞♝♛♚♟".contains(board[it]) }
                if (blackPieces.isNotEmpty()) {
                    val from = blackPieces.random()
                    val possibleTo = board.indices.filter { it != from }.random()
                    board[possibleTo] = board[from]
                    board[from] = ""
                    isWhiteTurn = true
                }
            }
        }
    }
}

@Composable
fun LudoGame() {
    var isPvP by remember { mutableStateOf(true) }
    var gameStarted by remember { mutableStateOf(false) }

    if (!gameStarted) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Ludo", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = { isPvP = true; gameStarted = true }, modifier = Modifier.fillMaxWidth()) { Text("Player vs Player") }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { isPvP = false; gameStarted = true }, modifier = Modifier.fillMaxWidth()) { Text("Player vs AI") }
        }
    } else {
        LudoBoard(isPvP) { gameStarted = false }
    }
}

@Composable
fun LudoBoard(isPvP: Boolean, onReset: () -> Unit) {
    var diceValue by remember { mutableIntStateOf(1) }
    var turn by remember { mutableIntStateOf(1) } // 1: Red, 2: Green
    var redPos by remember { mutableIntStateOf(0) }
    var greenPos by remember { mutableIntStateOf(0) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Ludo - ${if (turn == 1) "Red's Turn" else "Green's Turn"}", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.size(300.dp).border(2.dp, Color.Black)) {
            // Simplified Ludo Board Visualization
            Column(modifier = Modifier.fillMaxSize()) {
                Row(modifier = Modifier.weight(6f).fillMaxWidth()) {
                    Box(modifier = Modifier.weight(6f).fillMaxHeight().background(Color.Red).border(1.dp, Color.Black)) {
                        if (redPos == 0) Text("🔴", modifier = Modifier.align(Alignment.Center), fontSize = 30.sp)
                    }
                    Box(modifier = Modifier.weight(3f).fillMaxHeight().background(Color.White).border(1.dp, Color.Black))
                    Box(modifier = Modifier.weight(6f).fillMaxHeight().background(Color.Green).border(1.dp, Color.Black)) {
                        if (greenPos == 0) Text("🟢", modifier = Modifier.align(Alignment.Center), fontSize = 30.sp)
                    }
                }
                Row(modifier = Modifier.weight(3f).fillMaxWidth()) {
                    Box(modifier = Modifier.weight(6f).fillMaxHeight().background(Color.White).border(1.dp, Color.Black)) {
                         if (redPos > 0) Text("🔴", modifier = Modifier.align(Alignment.Center).offset(x = (redPos * 5).dp), fontSize = 20.sp)
                    }
                    Box(modifier = Modifier.weight(3f).fillMaxHeight().background(Color.Gray).border(1.dp, Color.Black))
                    Box(modifier = Modifier.weight(6f).fillMaxHeight().background(Color.White).border(1.dp, Color.Black)) {
                         if (greenPos > 0) Text("🟢", modifier = Modifier.align(Alignment.Center).offset(x = (greenPos * -5).dp), fontSize = 20.sp)
                    }
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
        Row {
            Button(
                onClick = {
                    diceValue = (1..6).random()
                    if (turn == 1) {
                        redPos += diceValue
                        if (redPos > 15) redPos = 15
                        turn = 2
                    } else {
                        greenPos += diceValue
                        if (greenPos > 15) greenPos = 15
                        turn = 1
                    }
                },
                enabled = isPvP || turn == 1
            ) { Text("Roll Dice ($diceValue)") }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = onReset) { Text("Quit") }
        }

        if (!isPvP && turn == 2) {
            LaunchedEffect(turn) {
                delay(1500)
                diceValue = (1..6).random()
                greenPos += diceValue
                if (greenPos > 15) greenPos = 15
                turn = 1
            }
        }
    }
}

data class CarromCoin(
    var x: Float,
    var y: Float,
    var vx: Float = 0f,
    var vy: Float = 0f,
    val color: Color,
    val isStriker: Boolean = false,
    var inPocket: Boolean = false
)

@Composable
fun CarromsGame() {
    var coins by remember {
        mutableStateOf(
            mutableListOf<CarromCoin>().apply {
                // Striker
                add(CarromCoin(150f, 250f, color = Color.Yellow, isStriker = true))
                // Queen
                add(CarromCoin(150f, 150f, color = Color.Red))
                // Black and White coins around center
                repeat(6) { i ->
                    val angle = (i * 60) * (Math.PI / 180)
                    add(CarromCoin(150f + (30 * Math.cos(angle)).toFloat(), 150f + (30 * Math.sin(angle)).toFloat(), color = if (i % 2 == 0) Color.Black else Color.White))
                }
            }
        )
    }
    var score by remember { mutableIntStateOf(0) }
    var strikerX by remember { mutableFloatStateOf(150f) }
    var isAiming by remember { mutableStateOf(true) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Carroms", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Score: $score", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .size(300.dp)
                .background(Color(0xFFDEB887))
                .border(8.dp, Color(0xFF5D4037))
        ) {
            // Pockets
            repeat(4) { i ->
                Box(modifier = Modifier.size(35.dp).background(Color.Black, CircleShape).align(when(i) {
                    0 -> Alignment.TopStart
                    1 -> Alignment.TopEnd
                    2 -> Alignment.BottomStart
                    else -> Alignment.BottomEnd
                }).offset(
                    x = if (i % 2 == 0) (-5).dp else 5.dp,
                    y = if (i < 2) (-5).dp else 5.dp
                ))
            }

            // Board Content
            coins.filter { !it.inPocket }.forEach { coin ->
                Box(
                    modifier = Modifier
                        .size(if (coin.isStriker) 24.dp else 18.dp)
                        .offset(x = (coin.x - (if (coin.isStriker) 12 else 9)).dp, y = (coin.y - (if (coin.isStriker) 12 else 9)).dp)
                        .background(coin.color, CircleShape)
                        .border(1.dp, Color.DarkGray, CircleShape)
                )
            }

            if (isAiming) {
                // Striker position slider (visualized on board)
                Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(Color.Black.copy(0.2f)).align(Alignment.TopCenter).offset(y = 250.dp))
            }
        }

        LaunchedEffect(isAiming) {
            if (!isAiming) {
                while (coins.any { it.vx != 0f || it.vy != 0f }) {
                    delay(20)
                    val nextCoins = coins.map { it.copy() }
                    for (i in nextCoins.indices) {
                        val c = nextCoins[i]
                        if (c.inPocket) continue

                        c.x += c.vx
                        c.y += c.vy
                        c.vx *= 0.98f
                        c.vy *= 0.98f
                        if (Math.abs(c.vx) < 0.1f) c.vx = 0f
                        if (Math.abs(c.vy) < 0.1f) c.vy = 0f

                        // Wall collisions
                        if (c.x < 10 || c.x > 290) { c.vx *= -1; c.x = c.x.coerceIn(10f, 290f) }
                        if (c.y < 10 || c.y > 290) { c.vy *= -1; c.y = c.y.coerceIn(10f, 290f) }

                        // Pocketing
                        if ((c.x < 25 || c.x > 275) && (c.y < 25 || c.y > 275)) {
                            c.inPocket = true
                            c.vx = 0f; c.vy = 0f
                            if (!c.isStriker) {
                                score += if (c.color == Color.Red) 50 else 10
                            } else {
                                score -= 10
                            }
                        }
                    }

                    // Coin-to-coin collisions (Simplified)
                    for (i in nextCoins.indices) {
                        for (j in i + 1 until nextCoins.indices.last + 1) {
                            val c1 = nextCoins[i]
                            val c2 = nextCoins[j]
                            if (c1.inPocket || c2.inPocket) continue
                            val dx = c2.x - c1.x
                            val dy = c2.y - c1.y
                            val dist = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
                            val minDist = if (c1.isStriker || c2.isStriker) 21f else 18f
                            if (dist < minDist) {
                                // Swap velocities as a simple elastic collision
                                val tvx = c1.vx; c1.vx = c2.vx; c2.vx = tvx
                                val tvy = c1.vy; c1.vy = c2.vy; c2.vy = tvy
                                // Move apart to avoid sticking
                                val overlap = minDist - dist
                                c1.x -= overlap * (dx / dist) / 2
                                c1.y -= overlap * (dy / dist) / 2
                                c2.x += overlap * (dx / dist) / 2
                                c2.y += overlap * (dy / dist) / 2
                            }
                        }
                    }
                    coins = nextCoins.toMutableList()
                }
                isAiming = true
                // Reset striker
                val resetCoins = coins.toMutableList()
                val sIdx = resetCoins.indexOfFirst { it.isStriker }
                resetCoins[sIdx] = CarromCoin(strikerX, 250f, color = Color.Yellow, isStriker = true)
                coins = resetCoins
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        if (isAiming) {
            Slider(value = strikerX, onValueChange = { strikerX = it; val nc = coins.toMutableList(); nc[0] = nc[0].copy(x = it); coins = nc }, valueRange = 50f..250f)
            Button(onClick = {
                val nc = coins.toMutableList()
                nc[0] = nc[0].copy(vy = -15f, vx = (Random.nextFloat() - 0.5f) * 5f)
                coins = nc
                isAiming = false
            }) { Text("Strike!") }
        } else {
            Text("Moving...")
        }

        Button(onClick = {
            score = 0
            coins = mutableListOf<CarromCoin>().apply {
                add(CarromCoin(150f, 250f, color = Color.Yellow, isStriker = true))
                add(CarromCoin(150f, 150f, color = Color.Red))
                repeat(6) { i ->
                    val angle = (i * 60) * (Math.PI / 180)
                    add(CarromCoin(150f + (30 * Math.cos(angle)).toFloat(), 150f + (30 * Math.sin(angle)).toFloat(), color = if (i % 2 == 0) Color.Black else Color.White))
                }
            }
            isAiming = true
        }, modifier = Modifier.padding(top = 8.dp)) { Text("Reset Board") }
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
    var isPvP by remember { mutableStateOf(true) }
    var gameStarted by remember { mutableStateOf(false) }

    if (!gameStarted) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Tic-Tac-Toe", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = { isPvP = true; gameStarted = true }, modifier = Modifier.fillMaxWidth()) { Text("Player vs Player") }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { isPvP = false; gameStarted = true }, modifier = Modifier.fillMaxWidth()) { Text("Player vs AI") }
        }
    } else {
        TicTacToeBoard(isPvP) { gameStarted = false }
    }
}

@Composable
fun TicTacToeBoard(isPvP: Boolean, onReset: () -> Unit) {
    var board by remember { mutableStateOf(List(9) { "" }) }
    var turnX by remember { mutableStateOf(true) }
    var winner by remember { mutableStateOf<String?>(null) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(if (isPvP) "Tic-Tac-Toe (PvP)" else "Tic-Tac-Toe (vs AI)", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))

        for (i in 0 until 3) {
            Row {
                for (j in 0 until 3) {
                    val index = i * 3 + j
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .border(1.dp, MaterialTheme.colorScheme.outline)
                            .clickable(enabled = board[index] == "" && winner == null && (isPvP || turnX)) {
                                if (isPvP || turnX) {
                                    val newBoard = board.toMutableList()
                                    newBoard[index] = if (turnX) "X" else "O"
                                    board = newBoard
                                    turnX = !turnX
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
            Row {
                Button(onClick = { board = List(9) { "" }; winner = null; turnX = true }) { Text("Restart") }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = onReset) { Text("Quit") }
            }
        } else {
            Text(if (turnX) "Turn: X" else "Turn: O")

            if (!isPvP && !turnX) {
                LaunchedEffect(turnX) {
                    delay(1000)
                    val emptyIndices = board.indices.filter { board[it] == "" }
                    if (emptyIndices.isNotEmpty()) {
                        val move = emptyIndices.random()
                        val newBoard = board.toMutableList()
                        newBoard[move] = "O"
                        board = newBoard
                        turnX = true
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
    var gameOver by remember { mutableStateOf(false) }
    var direction by remember { mutableStateOf("RIGHT") }
    var snakeBody by remember { mutableStateOf(listOf(5 to 5, 4 to 5, 3 to 5)) }
    var foodPos by remember { mutableStateOf(Random.nextInt(10) to Random.nextInt(10)) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Snake", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Box(modifier = Modifier.size(200.dp).background(Color.Black).border(2.dp, Color.DarkGray)) {
            if (!gameStarted || gameOver) {
                Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    if (gameOver) {
                        Text("GAME OVER", color = Color.Red, fontWeight = FontWeight.Bold)
                        Text("Score: $score", color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Button(onClick = {
                        snakeBody = listOf(5 to 5, 4 to 5, 3 to 5)
                        direction = "RIGHT"
                        score = 0
                        gameOver = false
                        gameStarted = true
                        foodPos = generateFood(snakeBody)
                    }) {
                        Text(if (gameOver) "Restart" else "Start Game")
                    }
                }
            } else {
                LaunchedEffect(Unit) {
                    while (!gameOver) {
                        delay(250)
                        val head = snakeBody.first()
                        val nextHead = when (direction) {
                            "UP" -> head.first to head.second - 1
                            "DOWN" -> head.first to head.second + 1
                            "LEFT" -> head.first - 1 to head.second
                            "RIGHT" -> head.first + 1 to head.second
                            else -> head
                        }

                        if (nextHead.first !in 0..9 || nextHead.second !in 0..9 || snakeBody.contains(nextHead)) {
                            gameOver = true
                        } else {
                            val newBody = (mutableListOf(nextHead) + snakeBody).toMutableList()
                            if (nextHead == foodPos) {
                                score += 10
                                foodPos = generateFood(newBody)
                            } else {
                                newBody.removeAt(newBody.size - 1)
                            }
                            snakeBody = newBody.toList()
                        }
                    }
                }

                // Food
                Text("🍎", fontSize = 18.sp, modifier = Modifier.offset(x = (foodPos.first * 20).dp, y = (foodPos.second * 20).dp))

                // Snake
                snakeBody.forEachIndexed { index, pos ->
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .offset(x = (pos.first * 20).dp, y = (pos.second * 20).dp)
                            .background(if (index == 0) Color.Green else Color(0xFF4CAF50), RoundedCornerShape(4.dp))
                            .border(1.dp, Color.Black)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Score: $score", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        // Controls
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { if (direction != "DOWN") direction = "UP" }, modifier = Modifier.size(50.dp)) { Text("↑") }
            Row {
                Button(onClick = { if (direction != "RIGHT") direction = "LEFT" }, modifier = Modifier.size(50.dp)) { Text("←") }
                Spacer(modifier = Modifier.width(50.dp))
                Button(onClick = { if (direction != "LEFT") direction = "RIGHT" }, modifier = Modifier.size(50.dp)) { Text("→") }
            }
            Button(onClick = { if (direction != "UP") direction = "DOWN" }, modifier = Modifier.size(50.dp)) { Text("↓") }
        }
    }
}

fun generateFood(snake: List<Pair<Int, Int>>): Pair<Int, Int> {
    var pos = Random.nextInt(10) to Random.nextInt(10)
    while (snake.contains(pos)) {
        pos = Random.nextInt(10) to Random.nextInt(10)
    }
    return pos
}

@Composable
fun DinoJumpGame() {
    var dinoY by remember { mutableFloatStateOf(0f) }
    var dinoVelocity by remember { mutableFloatStateOf(0f) }
    var cactusX by remember { mutableFloatStateOf(300f) }
    var score by remember { mutableIntStateOf(0) }
    var gameOver by remember { mutableStateOf(false) }
    var gameStarted by remember { mutableStateOf(false) }

    val gravity = 0.8f
    val jumpStrength = -12f

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Dino Jump", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Score: $score", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color(0xFFF7F7F7))
                .border(1.dp, Color.LightGray)
                .clickable {
                    if (!gameStarted) {
                        gameStarted = true
                        gameOver = false
                        score = 0
                        cactusX = 400f
                        dinoY = 0f
                    } else if (dinoY == 0f && !gameOver) {
                        dinoVelocity = jumpStrength
                    }
                }
        ) {
            if (!gameStarted || gameOver) {
                Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    if (gameOver) Text("GAME OVER", color = Color.Red, fontWeight = FontWeight.Bold)
                    Text(if (gameOver) "Tap to Restart" else "Tap to Start", color = Color.Gray)
                }
            }

            // Ground
            Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(Color.Gray).align(Alignment.BottomCenter).offset(y = (-40).dp))

            // Dino
            Text(
                text = "🦖",
                fontSize = 40.sp,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 50.dp, y = (-40).dp + dinoY.dp)
            )

            // Cactus
            Text(
                text = "🌵",
                fontSize = 30.sp,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = cactusX.dp, y = (-40).dp)
            )

            LaunchedEffect(gameStarted, gameOver) {
                if (gameStarted && !gameOver) {
                    while (!gameOver) {
                        delay(20)
                        // Physics
                        dinoVelocity += gravity
                        dinoY += dinoVelocity
                        if (dinoY > 0f) {
                            dinoY = 0f
                            dinoVelocity = 0f
                        }

                        cactusX -= 8f + (score / 100f) // Speed up over time
                        if (cactusX < -50f) {
                            cactusX = 400f
                            score += 10
                        }

                        // Collision (rough)
                        if (cactusX > 40f && cactusX < 80f && dinoY > -30f) {
                            gameOver = true
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { if (dinoY == 0f && !gameOver) dinoVelocity = jumpStrength }) {
            Text("Jump")
        }
        Text("Tip: Tap the game area to jump", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
fun Game2048() {
    var board by remember { mutableStateOf(List(16) { 0 }) }
    var score by remember { mutableIntStateOf(0) }
    var gameOver by remember { mutableStateOf(false) }

    if (board.all { it == 0 }) {
        val newBoard = board.toMutableList()
        repeat(2) {
            val empty = newBoard.indices.filter { newBoard[it] == 0 }
            if (empty.isNotEmpty()) newBoard[empty.random()] = if (Random.nextFloat() > 0.9f) 4 else 2
        }
        board = newBoard
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("2048", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Score: $score", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.size(280.dp).background(Color(0xFFBBADA0), RoundedCornerShape(8.dp)).padding(8.dp)) {
            LazyVerticalGrid(columns = GridCells.Fixed(4)) {
                items(board) { value ->
                    Card(
                        modifier = Modifier.padding(4.dp).aspectRatio(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = when(value) {
                                0 -> Color(0xFFCDC1B4)
                                2 -> Color(0xFFEEE4DA)
                                4 -> Color(0xFFEDE0C8)
                                8 -> Color(0xFFF2B179)
                                16 -> Color(0xFFF59563)
                                32 -> Color(0xFFF67C5F)
                                64 -> Color(0xFFF65E3B)
                                128 -> Color(0xFFEDCF72)
                                256 -> Color(0xFFEDCC61)
                                512 -> Color(0xFFEDC850)
                                1024 -> Color(0xFFEDC53F)
                                2048 -> Color(0xFFEDC22E)
                                else -> Color(0xFF3C3A32)
                            }
                        )
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            if (value != 0) Text(
                                text = value.toString(),
                                fontWeight = FontWeight.Bold,
                                fontSize = if (value < 100) 24.sp else if (value < 1000) 20.sp else 16.sp,
                                color = if (value <= 4) Color(0xFF776E65) else Color.White
                            )
                        }
                    }
                }
            }
            if (gameOver) {
                Box(modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.7f)), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Game Over!", style = MaterialTheme.typography.headlineMedium, color = Color.Black)
                        Button(onClick = {
                            val newBoard = List(16) { 0 }.toMutableList()
                            repeat(2) {
                                val empty = newBoard.indices.filter { newBoard[it] == 0 }
                                if (empty.isNotEmpty()) newBoard[empty.random()] = 2
                            }
                            board = newBoard
                            score = 0
                            gameOver = false
                        }) { Text("Try Again") }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { val (nb, s) = move2048(board, "LEFT"); if(nb != board) { board = addRandom(nb); score += s }; if(is2048GameOver(board)) gameOver = true }) { Text("←") }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { val (nb, s) = move2048(board, "UP"); if(nb != board) { board = addRandom(nb); score += s }; if(is2048GameOver(board)) gameOver = true }) { Text("↑") }
                Button(onClick = { val (nb, s) = move2048(board, "DOWN"); if(nb != board) { board = addRandom(nb); score += s }; if(is2048GameOver(board)) gameOver = true }) { Text("↓") }
            }
            Button(onClick = { val (nb, s) = move2048(board, "RIGHT"); if(nb != board) { board = addRandom(nb); score += s }; if(is2048GameOver(board)) gameOver = true }) { Text("→") }
        }
    }
}

fun move2048(board: List<Int>, direction: String): Pair<List<Int>, Int> {
    val newBoard = board.toIntArray()
    var addedScore = 0
    val rows = when(direction) {
        "LEFT" -> listOf(0..3, 4..7, 8..11, 12..15)
        "RIGHT" -> listOf(3 downTo 0, 7 downTo 4, 11 downTo 8, 15 downTo 12)
        "UP" -> listOf(listOf(0,4,8,12), listOf(1,5,9,13), listOf(2,6,10,14), listOf(3,7,11,15))
        "DOWN" -> listOf(listOf(12,8,4,0), listOf(13,9,5,1), listOf(14,10,6,2), listOf(15,11,7,3))
        else -> emptyList()
    }

    for (indices in rows) {
        val original = indices.map { board[it] }.filter { it != 0 }
        val merged = mutableListOf<Int>()
        var skip = false
        for (i in original.indices) {
            if (skip) { skip = false; continue }
            if (i + 1 < original.size && original[i] == original[i+1]) {
                merged.add(original[i] * 2)
                addedScore += original[i] * 2
                skip = true
            } else {
                merged.add(original[i])
            }
        }
        val resultRow = merged + List(4 - merged.size) { 0 }
        indices.forEachIndexed { index, boardIdx ->
            newBoard[boardIdx] = resultRow[index]
        }
    }
    return newBoard.toList() to addedScore
}

fun addRandom(board: List<Int>): List<Int> {
    val empty = board.indices.filter { board[it] == 0 }
    if (empty.isEmpty()) return board
    val newBoard = board.toMutableList()
    newBoard[empty.random()] = if (Random.nextFloat() > 0.9f) 4 else 2
    return newBoard
}

fun is2048GameOver(board: List<Int>): Boolean {
    if (board.any { it == 0 }) return false
    for (i in 0..3) {
        for (j in 0..2) {
            if (board[i * 4 + j] == board[i * 4 + j + 1]) return false
            if (board[j * 4 + i] == board[(j + 1) * 4 + i]) return false
        }
    }
    return true
}

@Composable
fun SudokuGame() {
    var puzzle by remember { mutableStateOf(generateSudoku()) }
    var userGrid by remember { mutableStateOf(puzzle.map { if (it == 0) "" else it.toString() }) }
    var initialGrid by remember { mutableStateOf(puzzle.map { it != 0 }) }
    var statusMessage by remember { mutableStateOf("Fill the grid") }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Sudoku", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(statusMessage, style = MaterialTheme.typography.bodyMedium, color = if (statusMessage.contains("Correct")) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.size(320.dp).border(2.dp, Color.Black)) {
            LazyVerticalGrid(columns = GridCells.Fixed(9)) {
                items(81) { i ->
                    val row = i / 9
                    val col = i % 9
                    val isInitial = initialGrid[i]

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .background(if (isInitial) Color(0xFFF0F0F0) else Color.White)
                            .border(0.5.dp, Color.LightGray)
                            .clickable(enabled = !isInitial) {
                                val current = userGrid[i].toIntOrNull() ?: 0
                                val next = if (current == 9) "" else (current + 1).toString()
                                val newList = userGrid.toMutableList()
                                newList[i] = next
                                userGrid = newList
                                statusMessage = "Playing..."
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        // Thicker borders for 3x3 blocks
                        if (col % 3 == 0 && col > 0) Box(modifier = Modifier.fillMaxHeight().width(2.dp).background(Color.Black).align(Alignment.CenterStart))
                        if (row % 3 == 0 && row > 0) Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(Color.Black).align(Alignment.TopCenter))

                        Text(
                            text = userGrid[i],
                            fontWeight = if (isInitial) FontWeight.Bold else FontWeight.Normal,
                            color = if (isInitial) Color.Black else Color.Blue
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = {
                if (validateSudoku(userGrid)) {
                    statusMessage = "Correct! Well done!"
                } else {
                    statusMessage = "Not quite right yet. Keep trying!"
                }
            }) { Text("Check") }

            Button(onClick = {
                puzzle = generateSudoku()
                userGrid = puzzle.map { if (it == 0) "" else it.toString() }
                initialGrid = puzzle.map { it != 0 }
                statusMessage = "New game started"
            }) { Text("New Game") }
        }
    }
}

fun generateSudoku(): List<Int> {
    // Very simple generator: take a solved grid and remove some numbers
    val base = listOf(
        5, 3, 4, 6, 7, 8, 9, 1, 2,
        6, 7, 2, 1, 9, 5, 3, 4, 8,
        1, 9, 8, 3, 4, 2, 5, 6, 7,
        8, 5, 9, 7, 6, 1, 4, 2, 3,
        4, 2, 6, 8, 5, 3, 7, 9, 1,
        7, 1, 3, 9, 2, 4, 8, 5, 6,
        9, 6, 1, 5, 3, 7, 2, 8, 4,
        2, 8, 7, 4, 1, 9, 6, 3, 5,
        3, 4, 5, 2, 8, 6, 1, 7, 9
    )
    // Randomly shuffle rows/cols within blocks and blocks themselves to make it "new"
    // For simplicity here, just hide random cells
    return base.map { if (Random.nextFloat() > 0.4f) it else 0 }
}

fun validateSudoku(grid: List<String>): Boolean {
    if (grid.any { it.isEmpty() }) return false
    val nums = grid.map { it.toInt() }

    // Check rows, cols, blocks
    for (i in 0 until 9) {
        val row = nums.subList(i * 9, (i + 1) * 9)
        if (row.toSet().size != 9) return false

        val col = (0 until 9).map { nums[it * 9 + i] }
        if (col.toSet().size != 9) return false
    }

    for (blockRow in 0 until 3) {
        for (blockCol in 0 until 3) {
            val block = mutableListOf<Int>()
            for (r in 0 until 3) {
                for (c in 0 until 3) {
                    block.add(nums[(blockRow * 3 + r) * 9 + (blockCol * 3 + c)])
                }
            }
            if (block.toSet().size != 9) return false
        }
    }
    return true
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
fun GameOfLife() {
    var grid by remember { mutableStateOf(List(100) { Random.nextBoolean() }) }
    var running by remember { mutableStateOf(false) }

    LaunchedEffect(running) {
        while (running) {
            delay(500)
            val newGrid = grid.toMutableList()
            for (i in 0 until 100) {
                val row = i / 10
                val col = i % 10
                var neighbors = 0
                for (dr in -1..1) {
                    for (dc in -1..1) {
                        if (dr == 0 && dc == 0) continue
                        val nr = (row + dr + 10) % 10
                        val nc = (col + dc + 10) % 10
                        if (grid[nr * 10 + nc]) neighbors++
                    }
                }
                if (grid[i]) newGrid[i] = neighbors in 2..3
                else newGrid[i] = neighbors == 3
            }
            grid = newGrid
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Conway's Game of Life", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(10), modifier = Modifier.size(250.dp).border(1.dp, Color.Gray)) {
            items(100) { i ->
                Box(modifier = Modifier.aspectRatio(1f).background(if (grid[i]) Color.Green else Color.Black).border(0.5.dp, Color.DarkGray))
            }
        }
        Button(onClick = { running = !running }, Modifier.padding(top = 16.dp)) {
            Text(if (running) "Pause" else "Start Simulation")
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ClashDeckBuilder() {
    val cards = listOf("Giant", "Archer", "Knight", "Dragon", "Goblin", "Wizard", "P.E.K.K.A", "Balloon")
    var selectedCards by remember { mutableStateOf(setOf<String>()) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Battle Deck Builder", style = MaterialTheme.typography.titleMedium)
        Text("Selected: ${selectedCards.size}/8")
        Spacer(Modifier.height(16.dp))
        FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            cards.forEach { card ->
                FilterChip(
                    selected = selectedCards.contains(card),
                    onClick = {
                        if (selectedCards.contains(card)) selectedCards -= card
                        else if (selectedCards.size < 8) selectedCards += card
                    },
                    label = { Text(card) },
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RouletteGame() {
    var result by remember { mutableIntStateOf(0) }
    var spinning by remember { mutableStateOf(false) }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("European Roulette", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(32.dp))
        Box(modifier = Modifier.size(150.dp).background(Color(0xFF006400), CircleShape).border(4.dp, Color.Yellow, CircleShape), contentAlignment = Alignment.Center) {
            Text(if (spinning) "?" else result.toString(), fontSize = 48.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }
        Button(onClick = {
            spinning = true
        }, enabled = !spinning, modifier = Modifier.padding(top = 32.dp)) {
            if (spinning) {
                LaunchedEffect(Unit) {
                    delay(2000)
                    result = (0..36).random()
                    spinning = false
                }
                Text("Spinning...")
            } else Text("Place Bet & Spin")
        }
    }
}

@Composable
fun MemoryGameV2() {
    val baseEmojis = listOf("🎮", "🪐", "🚀", "🤖", "🔑", "💻", "🍎", "🍌")
    val itemsDeck = remember { (baseEmojis + baseEmojis).shuffled() }
    var cardStates by remember { mutableStateOf(List(16) { false }) }
    var matchedCards by remember { mutableStateOf(setOf<Int>()) }
    var selectedIndices by remember { mutableStateOf(listOf<Int>()) }
    var movesCount by remember { mutableIntStateOf(0) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Memory Match Pro", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text("Moves: $movesCount", fontSize = 14.sp)
        Spacer(Modifier.height(16.dp))

        LazyVerticalGrid(columns = GridCells.Fixed(4), modifier = Modifier.size(300.dp)) {
            items(16) { idx ->
                val isFlipped = cardStates[idx] || matchedCards.contains(idx)
                val isMatched = matchedCards.contains(idx)

                Card(
                    modifier = Modifier
                        .padding(4.dp)
                        .aspectRatio(1f)
                        .clickable(enabled = !isFlipped && selectedIndices.size < 2) {
                            val currentStates = cardStates.toMutableList()
                            currentStates[idx] = true
                            cardStates = currentStates
                            selectedIndices = selectedIndices + idx
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isMatched) Color(0xFF4CAF50).copy(alpha = 0.3f)
                        else if (isFlipped) MaterialTheme.colorScheme.secondaryContainer
                        else MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        if (isFlipped) Text(itemsDeck[idx], fontSize = 24.sp)
                        else Icon(Icons.Default.SportsEsports, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f))
                    }
                }
            }
        }

        LaunchedEffect(selectedIndices) {
            if (selectedIndices.size == 2) {
                movesCount++
                delay(1000)
                if (itemsDeck[selectedIndices[0]] == itemsDeck[selectedIndices[1]]) {
                    matchedCards = matchedCards + selectedIndices[0] + selectedIndices[1]
                } else {
                    val currentStates = cardStates.toMutableList()
                    currentStates[selectedIndices[0]] = false
                    currentStates[selectedIndices[1]] = false
                    cardStates = currentStates
                }
                selectedIndices = emptyList()
            }
        }
    }
}

@Composable
fun NumberGuessingGame() {
    var target by remember { mutableIntStateOf(Random.nextInt(1, 101)) }
    var guess by remember { mutableStateOf("") }
    var hint by remember { mutableStateOf("Guess a number between 1 and 100") }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(hint, modifier = Modifier.padding(16.dp))
        OutlinedTextField(value = guess, onValueChange = { guess = it }, label = { Text("Your Guess") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        Button(onClick = {
            val g = guess.toIntOrNull()
            if (g == null) hint = "Invalid number"
            else if (g < target) hint = "Higher!"
            else if (g > target) hint = "Lower!"
            else hint = "Correct! Target was $target. New game started."
            if (g == target) target = Random.nextInt(1, 101)
            guess = ""
        }, Modifier.padding(top = 16.dp)) { Text("Guess") }
    }
}

@Composable
fun RandomGeneratorGame() {
    var result by remember { mutableStateOf("0") }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Random Number Generator", style = MaterialTheme.typography.titleMedium)
        Text(result, style = MaterialTheme.typography.displayLarge, modifier = Modifier.padding(32.dp))
        Button(onClick = { result = (1..100).random().toString() }) { Text("Generate (1-100)") }
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
