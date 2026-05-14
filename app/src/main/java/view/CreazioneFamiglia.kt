package view

import DataBase.Famiglia
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.family_hub.R
import com.example.family_hub.ui.theme.GrigioScritta
import com.example.family_hub.ui.theme.LightBlue
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun CreazioneFamigliaScreen(navController: NavController) {
    var nomeFamiglia by remember { mutableStateOf("") }
    val colore = remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val uid = user?.uid
    val famiglia = Famiglia()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = GrigioScritta).height(90.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Pagina precedente",
                    tint = Color.Black
                )
            }
        }

        Spacer(
            modifier = Modifier.height(1.dp).background(color = Color.LightGray)
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(35.dp))
        Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.logo2_removebg_preview),
                contentDescription = "Logo",
                modifier = Modifier.size(140.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Crea una famiglia", color = Color.Black, fontSize = 30.sp)
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = nomeFamiglia,
                onValueChange = { nomeFamiglia = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Inserisci il nome della famiglia", color = Color.Gray) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Blue,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color.Blue,
                    unfocusedLabelColor = Color.Gray,
                    cursorColor = Color.Blue,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(35.dp))

            SceltaColore(coloreSelezionato = colore)

            Spacer(modifier = Modifier.height(35.dp))

            Button(
                onClick = {
                        scope.launch {
                            famiglia.CreazioneFamiglia(nomeFamiglia, uid.toString(), colore.value)
                            navController.navigate("home")
                        }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = LightBlue)
            ) {
                Text(text="Conferma", color = Color.Black)
            }
        }
    }
}


@Composable
fun SceltaColore(coloreSelezionato: MutableState<String>) {
    var espanso by remember { mutableStateOf(false) }
    var enable by remember { mutableStateOf(false) }
    var coloreBox by remember { mutableStateOf(Color.Transparent) }
    val coloriDisponibili = listOf(
        "Rosa pastello" to Color(0xFFFFC1CC),
        "Giallo crema" to Color(0xFFFFF3B0),
        "Rosso chiaro" to Color(0xFFff686b),
        "Lilla" to Color(0xFFE0C3FC),
        "Menta" to Color(0xFFBFFCC6),
        "Marroncino" to Color(0xFFd4a373),
        "Corallo" to Color(0xFFFFA07A),
        "Azzurro chiaro" to Color(0xFFA9DEF9)
        )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .width(250.dp)
                .clickable {
                    espanso = true
                    // Non serve enable qui, sempre false per TextField
                }
        ) {
            OutlinedTextField(
                value = coloreSelezionato.value,
                onValueChange = {},
                readOnly = true,
                enabled = false, // disabilitato, così non assorbe click
                singleLine = true,
                label = { Text(text = "Seleziona un colore", color = Color.Black) },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.Black, // imposta il testo nero anche se disabilitato
                    disabledBorderColor = Color.Black,
                    disabledLabelColor = Color.Black
                ),
                leadingIcon = {
                    if (coloreSelezionato.value.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(coloreBox)
                                .border(1.dp, Color.Black)
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_color_lens_24),
                            contentDescription = "Colore",
                            tint = Color.Black,
                            modifier = Modifier.padding(horizontal = 15.dp)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }



        DropdownMenu(
            expanded = espanso,
            onDismissRequest = { espanso = false
                                 enable = true   },
            modifier = Modifier
                .background(color = Color.White)
                .width(250.dp)
        ) {
            coloriDisponibili.forEach { (nome, colore) ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(colore)
                                    .border(1.dp, Color.Black)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(nome, color = Color.Black)
                        }
                    },
                    onClick = {
                        coloreSelezionato.value = nome
                        coloreBox = colore
                        espanso = false
                        enable = true
                    },
                    modifier = Modifier.background(Color.White)
                )
            }
        }
    }

}





@Preview(showBackground = true)
@Composable
fun CreazioneFamigliaScreenPreview() {
    val navController = rememberNavController()
    CreazioneFamigliaScreen(navController = navController)
}