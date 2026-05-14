package view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.family_hub.R
import com.example.family_hub.ui.theme.LightBlue

@Composable
fun FamigliaScreen(navController: NavController) {
    var codiceInserito by remember { mutableStateOf("") }
    var showBottone by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Transparent).height(90.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        IconButton(onClick = { navController.navigate("login")}) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Pagina login",
                tint = Color.Black
            )
        }
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(50.dp).padding(top = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Image(
            painter = painterResource(R.drawable.logo2_removebg_preview),
            contentDescription = "Logo",
            modifier = Modifier.size(140.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Sembra che tu non abbia una famiglia", color = Color.Black)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            modifier = Modifier.fillMaxWidth().height(60.dp).clip(RoundedCornerShape(16.dp))
                .border(1.dp, Color.Black, shape = RoundedCornerShape(32.dp)),
            onClick = { navController.navigate("creazioneFamiglia") },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
            Text(text = "Crea Famiglia", color = Color.Black)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            modifier = Modifier.fillMaxWidth().height(60.dp).clip(RoundedCornerShape(16.dp))
                .border(1.dp, Color.Black, shape = RoundedCornerShape(32.dp)),
            onClick = {navController.navigate("accediFamiglia")},
            colors = ButtonDefaults.buttonColors(containerColor = LightBlue)
        ) {
            Text(text = "Unisciti ad una famiglia", color = Color.Black)
        }
        Spacer(modifier = Modifier.height(40.dp))

    }
}




@Preview(showBackground = true)
@Composable
fun FamigliaScreenPreview() {
    val navController = rememberNavController()
    FamigliaScreen(navController = navController)
}