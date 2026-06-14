package omni.toolbox.ui.screens.lifestyle

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen

@Composable
fun PackingListScreen(navController: NavHostController) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("packing_list", Context.MODE_PRIVATE) }
    var items by remember {
        mutableStateOf(prefs.getStringSet("items", emptySet())?.toList() ?: emptyList())
    }
    var newItemName by remember { mutableStateOf("") }

    fun saveItems(newItems: List<String>) {
        items = newItems
        prefs.edit().putStringSet("items", newItems.toSet()).apply()
    }

    ToolScreen(title = "Packing List", onBack = { navController.popBackStack() }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = newItemName,
                    onValueChange = { newItemName = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Add Item") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = {
                    if (newItemName.isNotBlank()) {
                        saveItems(items + newItemName)
                        newItemName = ""
                    }
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(items) { item ->
                    ListItem(
                        headlineContent = { Text(item) },
                        trailingContent = {
                            IconButton(onClick = {
                                saveItems(items.filter { it != item })
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                        }
                    )
                    HorizontalDivider()
                }
            }

            if (items.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Your packing list is empty.", color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}
