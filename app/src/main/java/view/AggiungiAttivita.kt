@file:OptIn(ExperimentalMaterial3Api::class)
package view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
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
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.family_hub.R
import com.example.family_hub.ui.theme.BluAttivita
import com.example.family_hub.ui.theme.ColoreTastoSelezionato
import com.example.family_hub.ui.theme.GrigioScritta
import com.example.family_hub.ui.theme.LightBlue
import kotlinx.coroutines.launch
import DataBase.Attivita
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@Composable
fun AggiuntaAttivitaScreen(navController: NavController){
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val selectedDate = remember { mutableStateOf("") }
    val selectedTime = remember { mutableStateOf("") }
    val selectedActivity = remember { mutableStateOf("") }
    val activityDescription = remember { mutableStateOf("") }
    val conferma = remember { mutableStateOf(false) }
    Scaffold(
        bottomBar = {
            BottomAppBar(modifier = Modifier, containerColor = LightBlue) {
                Row(
                    modifier = Modifier.fillMaxWidth().background(LightBlue),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Column(
                        modifier = Modifier.fillMaxHeight().weight(1f).clickable { navController.navigate("todo") },
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
                        modifier = Modifier.fillMaxHeight().weight(1f).clickable { navController.navigate("shoppingList") },
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
                        modifier = Modifier.fillMaxHeight().weight(1f).clickable{ navController.navigate("calendar") },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.calendar_text),
                            tint = ColoreTastoSelezionato,
                            contentDescription = "Calendario"
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Calendario", fontSize = 9.sp, color = ColoreTastoSelezionato)
                    }

                    Column(
                        modifier = Modifier.fillMaxHeight().weight(1f).clickable { navController.navigate("transaction") },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.cash_multiple),
                            tint = Color.Black,
                            contentDescription = "Spese"
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Transazioni", fontSize = 9.sp, color = Color.Black)
                    }

                    Column(
                        modifier = Modifier.fillMaxHeight().weight(1f).clickable { navController.navigate("account") },
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
            Box(
                modifier = Modifier
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
                        modifier = Modifier.height(1.dp).background(color = Color.LightGray)
                            .fillMaxWidth()
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize().background(Color.White)
                            .verticalScroll(scrollState),
                        horizontalAlignment = Alignment.CenterHorizontally
                    )
                    {
                        Spacer(modifier = Modifier.height(40.dp))
                        Text(
                            text = "Aggiungi un'attività",
                            fontSize = 30.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(80.dp))
                        DatePickerFieldToModal(
                            selectedDate = selectedDate.value,
                            onDateSelected = { selectedDate.value = it }
                        )
                        Spacer(modifier = Modifier.height(80.dp))
                        SceltaOra(selectedTime)
                        Spacer(modifier = Modifier.height(80.dp))
                        SceltaAttivita(selectedActivity, activityDescription)
                        Spacer(modifier = Modifier.height(50.dp))
                        ConfermaAttivita(
                            data = selectedDate.value,
                            ora = selectedTime.value,
                            tipo = selectedActivity.value,
                            descrizione = activityDescription.value,
                            navController
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun DatePickerFieldToModal(
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showModal by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = selectedDate,
        onValueChange = {},
        label = { Text(text = "Seleziona una data", color = Color.Black) },
        placeholder = { Text("DD/MM/YYYY") },
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.baseline_edit_calendar_24),
                contentDescription = "Select date",
                tint = Color.Black
            )
        },
        modifier = modifier
            .width(300.dp)
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) {
                        showModal = true
                    }
                }
            },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Blue,
            unfocusedBorderColor = Color.Gray,
            focusedLabelColor = Color.Blue,
            unfocusedLabelColor = Color.Gray,
            cursorColor = Color.Blue,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        )
    )

    if (showModal) {
        DatePickerModal(
            onDateSelected = {
                val dateString = it?.let { millis -> convertMillisToDate(millis) } ?: ""
                onDateSelected(dateString)
                showModal = false
            },
            onDismiss = { showModal = false }
        )
    }
}

@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

@Composable
fun SceltaOra(selectedTime: MutableState<String>) {
    val showDialog = remember { mutableStateOf(false) }
    val currentTime = Calendar.getInstance()
    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )

    OutlinedTextField(
        value = selectedTime.value,
        onValueChange = {},
        readOnly = true,
        label = { Text(text = "Seleziona un'ora", color = Color.Black) },
        singleLine = true,
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.clock_edit_outline),
                contentDescription = "Orologio",
                tint= Color.Black
            )
        },
        modifier = Modifier
            .width(300.dp)
            .pointerInput(showDialog) {
                awaitEachGesture {
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) {
                        showDialog.value = true
                    }
                }
            },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.Blue,
            unfocusedBorderColor = Color.Gray,
            focusedLabelColor = Color.Blue,
            unfocusedLabelColor = Color.Gray,
            cursorColor = Color.Blue,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        )
    )

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Aggiorna selectedTime al momento della conferma
                        selectedTime.value = String.format(
                            "%02d:%02d",
                            timePickerState.hour,
                            timePickerState.minute
                        )
                        showDialog.value = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog.value = false }) {
                    Text("Annulla")
                }
            },
            text = {
                TimePicker(
                    state = timePickerState
                )
            }
        )
    }
}



@Composable
fun SceltaAttivita(scelta: MutableState<String>, descrizione: MutableState<String>) {
    var espanso by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(
            value= scelta.value,
            onValueChange = {},
            label = { Text(text = "Seleziona un'attività", color = Color.Black) },
            readOnly = true,
            enabled = false,
            modifier = Modifier
                .clickable { espanso = true}
                .width(300.dp),
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = Color.Black,
                disabledContainerColor = Color.Transparent,
                disabledBorderColor = if (espanso) Color.Blue else Color.Gray,
                disabledLeadingIconColor = Color.Black,
                disabledLabelColor = if (espanso) Color.Blue else Color.Gray,
            ),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_edit_note_24),
                    contentDescription = "Attività",
                    tint = Color.Black,
                    modifier = Modifier.padding(horizontal = 15.dp)
                )
            }
        )

        DropdownMenu(
            expanded = espanso,
            onDismissRequest = { espanso = false },
            modifier = Modifier
                .background(color = GrigioScritta)
                .width(250.dp)
        ) {
            val opzioni = listOf("Visita Medica", "Scadenza", "Evento", "Portare a spasso il cane", "Altro")
            for (opzione in opzioni) {
                DropdownMenuItem(
                    text = { Text(opzione, color = Color.Black) },
                    onClick = {
                        scelta.value = opzione
                        espanso = false
                    },
                    modifier = Modifier.background(color = GrigioScritta)
                )
            }
        }

        if (scelta.value == "Altro" || scelta.value == "Visita Medica" || scelta.value == "Scadenza" || scelta.value == "Evento") {
            Spacer(modifier = Modifier.height(40.dp))
            TextField(
                value = descrizione.value,
                onValueChange = { descrizione.value = it },
                placeholder = { Text("Inserisci la descrizione dell'attività") },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedContainerColor = GrigioScritta,
                    unfocusedContainerColor = Color.White,
                    cursorColor = Color.Black,
                    focusedIndicatorColor = Color.Black,
                    unfocusedIndicatorColor = Color.Gray
                )
            )
        }
    }
}

@Composable
fun ConfermaAttivita(
    data: String,
    ora: String,
    tipo: String,
    descrizione: String,
    navController: NavController
) {
    val openDialog = remember { mutableStateOf(false) }
    val openErrore = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { openDialog.value = false },
            title = { Text("Conferma attività") },
            text = { Text("Vuoi salvare questa attività?") },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = { openDialog.value = false }) {
                        Text("Annulla", color = Color.Gray)
                    }
                    Button(
                        onClick = {
                            scope.launch {
                                val attivita = Attivita()
                                attivita.aggiungiAttivita(data, ora, tipo, descrizione)
                                openDialog.value = false
                                navController.navigate("calendar")
                            }
                        }
                    ) {
                        Text("OK")
                    }
                }
            },
            dismissButton = {}
        )
    }

    if (openErrore.value) {
        AlertDialog(
            onDismissRequest = { openErrore.value = false },
            title = { Text("Attenzione") },
            text = { Text("Inserire tutti i campi per confermare l'attività") },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { openErrore.value = false }
                    ) {
                        Text("OK")
                    }
                }
            },
            dismissButton = {}
        )
    }

    Button(
        onClick = {
            if (data.isEmpty() || ora.isEmpty() || tipo.isEmpty()) {
                openErrore.value = true
            } else {
                openDialog.value = true
            }
        },
        modifier = Modifier.padding(horizontal = 15.dp),
        colors = ButtonDefaults.buttonColors(containerColor = LightBlue)
    ) {
        Text(text = "Conferma", color = Color.Black)
    }
}







@Preview(showBackground = true)
@Composable
fun AggiuntaPreview() {
    val navController = rememberNavController()
    AggiuntaAttivitaScreen(navController = navController)
}