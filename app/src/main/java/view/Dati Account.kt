package view

import DataBase.Famiglia
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.family_hub.R
import com.example.family_hub.ui.theme.GrigioScritta
import com.example.family_hub.ui.theme.tertiary
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

data class UserAccount(
    val cognome: String = "",
    val dataNascita: String = "",
    val email: String = "",
    val nome: String = "",
    val numeroTelefono: String = "",
    val password: String = "",
    val username: String = "",
    val uid: String = "",
    val colore: String? = null
)

@Composable
fun DatiAccount(navController: NavController) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    var userAccount by remember { mutableStateOf<UserAccount?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    var nome by remember { mutableStateOf("") }
    var cognome by remember { mutableStateOf("") }
    var dataNascita by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var numeroTelefono by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val famiglia = Famiglia()
    val focusManager = LocalFocusManager.current
    var showErrorEmptyFields by remember { mutableStateOf(false) }

    LaunchedEffect(uid) {
        if (uid == null) {
            errorMessage = "Utente non autenticato"
            isLoading = false
            return@LaunchedEffect
        }
        val dati = famiglia.caricaDatiUtente(uid)
        if (dati == null) {
            errorMessage = "Dati utente non trovati o errore"
        } else {
            userAccount = dati
            nome = dati.nome
            cognome = dati.cognome
            dataNascita = dati.dataNascita
            email = dati.email
            numeroTelefono = dati.numeroTelefono
            username = dati.username
            password = dati.password
        }
        isLoading = false
    }

    // Controllo che nessun campo sia vuoto
    val canSave = nome.isNotBlank() &&
            cognome.isNotBlank() &&
            dataNascita.isNotBlank() &&
            email.isNotBlank() &&
            numeroTelefono.isNotBlank() &&
            username.isNotBlank() &&
            password.isNotBlank()

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (errorMessage != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = errorMessage ?: "Errore sconosciuto")
        }
    } else {
        Scaffold (
            content = { innerPadding ->

                Column(
                    modifier = Modifier
                        .background(GrigioScritta)
                        .padding(innerPadding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            focusManager.clearFocus()
                        },
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
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceAround,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedTextField(
                            value = nome,
                            onValueChange = { nome = it },
                            label = { Text(text = "Nome", color= Color.Gray) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
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
                        OutlinedTextField(
                            value = cognome,
                            onValueChange = { cognome = it },
                            label = { Text(text = "Cognome", color= Color.Gray) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
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
                        var showModal by remember { mutableStateOf(false) }

                        OutlinedTextField(
                            value = dataNascita,
                            onValueChange = {},
                            label = { Text(text = "Data di Nascita", color = Color.Black) },
                            placeholder = { Text("DD/MM/YYYY") },
                            trailingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_edit_calendar_24),
                                    contentDescription = "Select date",
                                    tint = if (dataNascita !="") Color.Black else Color.Gray
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
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
                                    dataNascita = dateString
                                    showModal = false
                                },
                                onDismiss = { showModal = false }
                            )
                        }
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text(text = "Email", color= Color.Gray) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
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
                        OutlinedTextField(
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            value = numeroTelefono,
                            onValueChange = { numeroTelefono = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            label = { Text(text= "Telefono", color= Color.Gray)},
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
                        )
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text(text = "Username", color= Color.Gray) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
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
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text(text = "Password", color= Color.Gray) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Blue,
                                unfocusedBorderColor = Color.Gray,
                                focusedLabelColor = Color.Blue,
                                unfocusedLabelColor = Color.Gray,
                                cursorColor = Color.Blue,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            ),
                            visualTransformation = PasswordVisualTransformation()
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = {
                                if (canSave) {
                                    scope.launch {
                                        famiglia.modificaDatiUtente(
                                            uid.toString(),
                                            nome,
                                            cognome,
                                            dataNascita,
                                            email,
                                            numeroTelefono,
                                            username,
                                            password
                                        )
                                        navController.popBackStack()
                                    }
                                } else {
                                    showErrorEmptyFields = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = tertiary),
                            enabled = canSave
                        ) {
                            Text("Salva", color = Color.Black)
                        }
                        if (showErrorEmptyFields) {
                            Text(
                                text = "Per favore, compila tutti i campi.",
                                color = Color.Red,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }
        )
    }
}
