package omni.toolbox.ui.screens.social

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import omni.toolbox.ui.components.ToolScreen
import java.io.InputStream

@Composable
fun ProfilePhotoMakerScreen(navController: NavHostController) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var borderColor by remember { mutableStateOf(Color.Blue) }
    var borderWidth by remember { mutableFloatStateOf(8f) }
    var backgroundColor by remember { mutableStateOf(Color.LightGray) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    val colors = listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Cyan, Color.Magenta, Color.Black, Color.White)

    ToolScreen(
        title = "Profile Photo Maker",
        onBack = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .clip(CircleShape)
                    .background(backgroundColor)
                    .then(
                        if (borderWidth > 0) Modifier.background(borderColor).padding(borderWidth.dp).clip(CircleShape) else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    val inputStream: InputStream? = context.contentResolver.openInputStream(selectedImageUri!!)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(100.dp), tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = { launcher.launch("image/*") }) {
                Text("Select Image")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Border Width: ${borderWidth.toInt()}dp")
            Slider(value = borderWidth, onValueChange = { borderWidth = it }, valueRange = 0f..20f)

            Spacer(modifier = Modifier.height(16.dp))

            Text("Border Color")
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                colors.take(4).forEach { color ->
                    IconButton(onClick = { borderColor = color }) {
                        Box(modifier = Modifier.size(24.dp).background(color, CircleShape).then(if(borderColor == color) Modifier.background(Color.Gray.copy(alpha=0.3f), CircleShape) else Modifier))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Background Color")
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                colors.drop(4).forEach { color ->
                    IconButton(onClick = { backgroundColor = color }) {
                        Box(modifier = Modifier.size(24.dp).background(color, CircleShape).then(if(backgroundColor == color) Modifier.background(Color.Gray.copy(alpha=0.3f), CircleShape) else Modifier))
                    }
                }
            }
        }
    }
}
