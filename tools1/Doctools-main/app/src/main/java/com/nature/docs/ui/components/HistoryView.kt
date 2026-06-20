package com.nature.docs.ui.components

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nature.docs.ui.theme.*
import coil.compose.AsyncImage
import com.nature.docs.data.image.PdfPageRequest

@Composable
fun HistoryView(onItemClick: (Uri, String, Int) -> Unit) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    
    val historyItems = SessionManager.history

    val filteredItems = remember(searchQuery, historyItems.size) {
        historyItems.filter { 
            it.name.contains(searchQuery, ignoreCase = true) || 
            it.tool.contains(searchQuery, ignoreCase = true) 
        }
    }

    LinenCanvas(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Standardized Header
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "History Archives.",
                        style = MaterialTheme.typography.displayLarge,
                        color = InkBrown
                    )

                    Surface(
                        color = BotanicalGreen.copy(alpha = 0.1f),
                        shape = CircleShape,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Outlined.History, null, modifier = Modifier.padding(8.dp).size(24.dp), tint = BotanicalGreen)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    color = InkBrown.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    border = BorderStroke(0.5.dp, InkBrown.copy(0.1f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Icon(Icons.Filled.Search, null, tint = InkBrown.copy(0.4f), modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Box(Modifier.weight(1f)) {
                            if (searchQuery.isEmpty()) {
                                Text("Search archived specimens...", style = MaterialTheme.typography.bodyMedium, color = InkBrown.copy(0.4f))
                            }
                            androidx.compose.foundation.text.BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                textStyle = MaterialTheme.typography.bodyMedium.copy(color = InkBrown),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Filled.Close, null, tint = InkBrown.copy(0.4f), modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            Text(
                text = "LOCAL SPECIMENS",
                style = MaterialTheme.typography.labelSmall,
                color = InkBrown.copy(alpha = 0.4f),
                modifier = Modifier.padding(top = 16.dp, bottom = 12.dp, start = 24.dp)
            )

            if (filteredItems.isEmpty()) {
                Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    EmptyHistoryState(searchQuery)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredItems, key = { it.id }) { item ->
                        HistoryItem(item,
                            onClick = { item.uri?.let { uri -> onItemClick(uri, item.name, item.pageCount) } },
                            onDownload = {
                                try {
                                    val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                        type = "application/pdf"
                                        putExtra(android.content.Intent.EXTRA_STREAM, item.uri)
                                        addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(android.content.Intent.createChooser(intent, "Export Archive"))
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Cannot export archive", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                    item(key = "footer_spacer") { Spacer(modifier = Modifier.height(100.dp)) }
                }
            }
        }
    }
}

@Composable
fun EmptyHistoryState(query: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
        Canvas(modifier = Modifier.size(120.dp).alpha(0.15f)) {
            // Botanical sketch of an empty field
            val path = Path().apply {
                moveTo(0f, size.height * 0.8f)
                quadraticBezierTo(size.width * 0.5f, size.height * 0.7f, size.width, size.height * 0.85f)
                // Some grass blades
                for (i in 0..10) {
                    val x = size.width * (i / 10f)
                    moveTo(x, size.height * (0.75f + (i % 2) * 0.05f))
                    lineTo(x + 5f, size.height * 0.6f)
                }
            }
            drawPath(path, color = BotanicalGreen, style = Stroke(width = 2.dp.toPx()))
        }
        Spacer(Modifier.height(24.dp))
        Text(
            if (query.isEmpty()) "The journal is blank" else "No matching specimens found",
            style = MaterialTheme.typography.headlineMedium,
            color = InkBrown.copy(0.6f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Text(
            if (query.isEmpty()) "Start your journey by processing files" else "Try searching for another term",
            style = MaterialTheme.typography.bodyMedium,
            color = InkBrown.copy(0.4f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun HistoryItem(entry: ActivityEntry, onClick: () -> Unit, onDownload: () -> Unit) {
    AgedPaperCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp)
            .clickable(enabled = entry.uri != null) { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rich Thumbnail with leaf badge
            Box(modifier = Modifier.size(52.dp).clip(RoundedCornerShape(8.dp)).background(InkBrown.copy(0.05f))) {
                if (entry.uri != null && entry.uri.toString().endsWith(".pdf")) {
                    AsyncImage(
                        model = PdfPageRequest(entry.uri, 0, null, 0.2f),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else {
                    Icon(entry.icon, null, tint = BotanicalGreen, modifier = Modifier.padding(12.dp).fillMaxSize())
                }

                // Leaf-badge for file type
                Surface(
                    modifier = Modifier.align(Alignment.BottomEnd).size(16.dp),
                    color = BotanicalGreen,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Eco, null, tint = Color.White, modifier = Modifier.padding(3.dp))
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(entry.name, style = MaterialTheme.typography.bodyLarge, color = InkBrown, maxLines = 1)
                Text("${entry.tool.uppercase()} • ${entry.size}", style = MaterialTheme.typography.labelSmall, color = InkBrown.copy(0.5f))
            }
            
            IconButton(onClick = onDownload) {
                Icon(Icons.Filled.Share, null, tint = BotanicalGreen.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
            }
            
            Spacer(Modifier.width(8.dp))

            Surface(
                onClick = onClick,
                modifier = Modifier.height(32.dp),
                shape = RoundedCornerShape(16.dp),
                color = BotanicalGreen,
                shadowElevation = 2.dp
            ) {
                Box(Modifier.padding(horizontal = 12.dp), contentAlignment = Alignment.Center) {
                    Text(
                        "STUDY",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}
