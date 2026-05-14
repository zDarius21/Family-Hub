package view

import Transazione
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.family_hub.R
import com.example.family_hub.ui.theme.ColoreTastoSelezionato
import com.example.family_hub.ui.theme.GrigioScritta
import com.example.family_hub.ui.theme.LightBlue
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


@Composable
fun AggiungiTransazioneScreen(navController: NavController){
    val scrollState = rememberScrollState()
    val openDialog = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val uid = user?.uid
    var negozio by remember { mutableStateOf("") }
    var tipologia by remember { mutableStateOf("") }
    var importo by remember { mutableStateOf("") }
    var descrizione by remember { mutableStateOf("") }
    var transazioni = Transazione()
    val scope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            BottomAppBar(modifier = Modifier, containerColor = LightBlue) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LightBlue),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .clickable { navController.navigate("todo") },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.clipboard_list_outline),
                            tint = Color.Black,
                            contentDescription = "TODO"
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "To Do List", fontSize = 9.sp, color = Color.Black)
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .clickable { navController.navigate("shoppingList") },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_shopping_bag_24),
                            tint = Color.Black,
                            contentDescription = "Spese"
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Lista Spesa", fontSize = 9.sp, color = Color.Black)
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .clickable { navController.navigate("calendar") },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.calendar_text),
                            tint = Color.Black,
                            contentDescription = "Calendario"
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Calendario", fontSize = 9.sp, color = Color.Black)
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .clickable { navController.navigate("transaction") },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.cash_multiple),
                            tint = ColoreTastoSelezionato,
                            contentDescription = "Spese"
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Transazioni", fontSize = 9.sp, color = ColoreTastoSelezionato)
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .clickable { navController.navigate("account") },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            tint = Color.Black,
                            contentDescription = "Account"
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Account", fontSize = 9.sp, color = Color.Black)
                    }

                }
            }
        },
        content = { innerPadding ->
            Box(modifier = Modifier
                .background(color = GrigioScritta)
                .padding(innerPadding)
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) { focusManager.clearFocus() }) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = GrigioScritta),
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
                        modifier = Modifier
                            .height(1.dp)
                            .background(color = Color.LightGray)
                            .fillMaxWidth()
                    )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .verticalScroll(scrollState), horizontalAlignment = Alignment.CenterHorizontally)
            {
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    text = "Aggiungi una transazione",
                    fontSize = 30.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(50.dp))
                Negozio(value = negozio, onValueChange = { negozio = it })
                Spacer(modifier = Modifier.height(50.dp))
                Tipologia(value = tipologia, onValueChange = { tipologia = it })
                Spacer(modifier = Modifier.height(50.dp))
                Importo(value = importo , onValueChange = { importo = it })
                Spacer(modifier = Modifier.height(50.dp))
                Descrizione(value = descrizione, onValueChange = { descrizione = it })
                Spacer(modifier = Modifier.height(50.dp))
                ConfermaTransazioni {
                    openDialog.value=true
                }
                if (openDialog.value) {
                    AlertDialog(
                        onDismissRequest = { openDialog.value = false },
                        title = { Text("Conferma transazione") },
                        text = { Text("Sei sicuro di voler inserire la transazione?") },
                        confirmButton = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                TextButton(onClick = { openDialog.value = false }) {
                                    Text("Annulla", color = Color.Gray)
                                }
                                Button(
                                    modifier = Modifier,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = LightBlue,
                                        contentColor = Color.Black
                                    ),
                                    onClick = {
                                        openDialog.value = false
                                        val importoDouble = importo.toDoubleOrNull()
                                        if (importoDouble != null && uid != null) {
                                            scope.launch {
                                                transazioni.aggiungiTransazione(
                                                    negozio,
                                                    tipologia,
                                                    importoDouble,
                                                    descrizione,
                                                    uid
                                                )
                                            }
                                        }
                                        navController.navigate("transaction")
                                    }
                                ) {
                                    Text("OK")
                                }
                            }
                        },
                        dismissButton = {}
                    )
                }
            }
        }}}
    )
}

@Composable
fun Tipologia(value: String, onValueChange: (String) -> Unit) {
    var espanso by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .width(300.dp)
                .clickable {
                    espanso = true
                }
        ){
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            enabled = false,
            label = { Text(text = "Seleziona una tipologia", color= Color.Gray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Blue,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color.Blue,
                unfocusedLabelColor = Color.Gray,
                cursorColor = Color.Blue,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Gray,

                disabledTextColor = Color.Black,
                disabledContainerColor = Color.Transparent,
                disabledBorderColor = if (espanso) Color.Blue else Color.Gray,
                disabledLeadingIconColor = Color.Black,
                disabledLabelColor = if (espanso) Color.Blue else Color.Gray,
            ),
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.baseline_edit_note_24),
                    contentDescription = "Modifica tipologia",
                    tint = Color.Black
                )
            }
        )
        }
        DropdownMenu(
            expanded = espanso,
            onDismissRequest = { espanso = false
                Log.d("Tipologia", "Dropdown dismesso")},
            modifier = Modifier
                .background(color = GrigioScritta)
                .width(300.dp)
        ) {
            val lista = listOf("Shopping", "Casa", "Svago", "Bollette", "Alimentari", "Altro")
            lista.forEachIndexed { index, voce ->
                DropdownMenuItem(
                    text = { Text(voce, color = Color.Black) },
                    onClick = {
                        espanso = false
                        onValueChange(voce)
                        Log.d("Tipologia", "Voce selezionata: $voce")
                    },
                    modifier = Modifier.background(color = GrigioScritta)
                )
                if (index != lista.lastIndex) {
                    Spacer(
                        modifier = Modifier
                            .height(2.dp)
                            .fillMaxWidth()
                            .background(color = Color.Gray)
                    )
                }
            }
        }
    }
}


@Composable
fun Negozio(value: String, onValueChange: (String) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally){
        Row(
            modifier = Modifier
                .width(300.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(text = "Inserisci il Negozio", color= Color.Gray) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Blue,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color.Blue,
                    unfocusedLabelColor = Color.Gray,
                    cursorColor = Color.Blue,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.store),
                        contentDescription = "Modifica negozio",
                        tint = Color.Black
                    )
                }
            )
        }
    }
}

@Composable
fun Importo(value: String, onValueChange: (String) -> Unit) {
    Column (horizontalAlignment = Alignment.CenterHorizontally){
        Row(
            modifier = Modifier
                .width(300.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text= "Inserisci l'importo", color= Color.Gray)},
                singleLine = true,
                colors= OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Blue,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color.Blue,
                    unfocusedLabelColor = Color.Gray,
                    cursorColor = Color.Blue,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.baseline_euro_24),
                        contentDescription = "Modifica importo",
                        tint = Color.Black
                    )
                }
            )

        }
    }

}

@Composable
fun Descrizione(value: String, onValueChange: (String) -> Unit){
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier
                .width(300.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Inserisci la descrizione", color= Color.Gray)},
                colors= OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Blue,
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color.Blue,
                    unfocusedLabelColor = Color.Gray,
                    cursorColor = Color.Blue,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.script_text),
                        contentDescription = "Modifica descrizione",
                        tint = Color.Black
                    )
                }
            )
        }
    }
}

@Composable
fun ConfermaTransazioni(onConferma: () -> Unit) {
    Button(
        onClick = onConferma,
        modifier = Modifier
            .padding(horizontal = 15.dp),
        colors = ButtonDefaults.buttonColors(containerColor = LightBlue)
    ) {
        Text(text = "Conferma", color = Color.Black)
    }
}


@Preview(showBackground = true)
@Composable
fun AggiungiTransazionePreview() {
    val navController = rememberNavController()
    AggiungiTransazioneScreen(navController = navController)
}