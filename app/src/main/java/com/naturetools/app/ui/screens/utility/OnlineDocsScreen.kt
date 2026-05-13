package com.naturetools.app.ui.screens.utility

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naturetools.app.ui.components.ToolScreen

data class OnlineDocTool(val name: String, val url: String, val description: String)

@Composable
fun OnlineDocsScreen(navController: NavHostController) {
    val tools = listOf(
        OnlineDocTool("TinyWow", "https://tinywow.com", "Comprehensive PDF, Image and Video tools."),
        OnlineDocTool("iLovePDF", "https://www.ilovepdf.com", "Every tool you need to work with PDFs in one place."),
        OnlineDocTool("SmallPDF", "https://smallpdf.com", "The first and only PDF software you’ll actually love."),
        OnlineDocTool("PDF2Go", "https://www.pdf2go.com", "Online PDF editor for editing and converting PDF files."),
        OnlineDocTool("SodaPDF", "https://www.sodapdf.com", "The complete PDF solution for any device."),
        OnlineDocTool("PDF Candy", "https://pdfcandy.com", "A web-based PDF tool for high-quality processing."),
        OnlineDocTool("Sejda", "https://www.sejda.com", "Help with your PDF tasks. Easy, pleasant and productive."),
        OnlineDocTool("PDF24 Tools", "https://tools.pdf24.org", "Free and easy to use online PDF tools.")
    )

    ToolScreen(
        title = "Online Document Tools",
        onBack = { navController.popBackStack() }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            items(tools) { tool ->
                ListItem(
                    headlineContent = { Text(tool.name) },
                    supportingContent = { Text(tool.description) },
                    leadingContent = { Icon(Icons.Default.Language, contentDescription = null) },
                    trailingContent = { Icon(Icons.Default.Description, contentDescription = null) },
                    modifier = Modifier.clickable {
                        navController.navigate("web?url=${tool.url}&showBar=true&title=${tool.name}")
                    }
                )
            }
        }
    }
}
