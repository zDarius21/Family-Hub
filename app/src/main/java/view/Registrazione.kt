package view

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.family_hub.R
import com.example.family_hub.ui.theme.BluCheckBox
import com.example.family_hub.ui.theme.GrigioScritta
import com.example.family_hub.ui.theme.LightBlue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun RegisterScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    var checked by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nome by remember { mutableStateOf("") }
    var cognome by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var dataNascita by remember { mutableStateOf("") }
    var numeroTelefono by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current


    Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState).clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) {
        focusManager.clearFocus()
    }) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(R.drawable.logo2_removebg_preview),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(140.dp)

            )
            Spacer(modifier = Modifier.width(16.dp))
            Text("Registrati")

            Spacer(modifier = Modifier.height(16.dp))


            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text(text = "Username", color = Color.Gray) },
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
                )
            )

            Spacer(modifier = Modifier.height(8.dp))


            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text(text = "Nome", color = Color.Gray) },
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
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = cognome,
                onValueChange = { cognome = it },
                label = { Text(text = "Cognome", color = Color.Gray) },
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
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(text = "Email", color = Color.Gray) },
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
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                value = numeroTelefono,
                onValueChange = { numeroTelefono = it },
                label = { Text(text = "Numero di telefono", color = Color.Gray) },
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
                )
            )

            Spacer(modifier = Modifier.height(8.dp))




            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(text = "Password*", color = Color.Gray) },
                visualTransformation = PasswordVisualTransformation(),
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
                )
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text("*La password deve contenere almeno una lettera maiuscola, una lettera minuscola e un numero.", color = Color.Gray, fontSize = 10.sp)
            Spacer(modifier = Modifier.height(8.dp))


            DatePickerFieldToModalRegistration(
                selectedDate = dataNascita,
                onDateSelected = { dataNascita = it }
            )

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth().clickable(onClick = { checked = !checked }),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = { checked = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = BluCheckBox,
                        uncheckedColor = Color.Gray,
                        checkmarkColor = Color.Black,
                        disabledCheckedColor = Color.LightGray,
                        disabledUncheckedColor = Color.LightGray
                    ),
                    modifier = Modifier.padding(start = 0.dp)
                )
                Text(
                    text = "Remember me",
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(start = 0.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    Registrazione(username = username,
                        nome = nome,
                        cognome = cognome,
                        email = email,
                        password = password,
                        dataNascita = dataNascita,
                        numeroTelefono = numeroTelefono,
                        checked = checked,
                        context = context,
                        navController = navController,
                        auth = auth,
                        firestore = firestore,
                        sharedPref = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE),
                        setLoading = { isLoading = it },
                        setErrorMessage = { errorMessage = it })
                },
                        modifier = Modifier.fillMaxWidth().testTag("registerButton"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LightBlue
                )
            ) {
                Text("Registrati", color = Color.Black)
            }

            if (isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = it, color = Color.Red)
            }
        }
    }
}

@Composable
fun DatePickerFieldToModalRegistration(
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showModal by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = selectedDate,
        onValueChange = {},
        label = { Text(text = "Seleziona la data di nascita", color = Color.Gray) },
        placeholder = { Text("DD/MM/YYYY") },
         trailingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.baseline_edit_calendar_24),
                contentDescription = "Select date",
                tint = if (selectedDate !="") Color.Black else Color.Gray
            )
        },
        modifier = modifier
            .fillMaxWidth()
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

fun Registrazione(
    username: String,
    nome: String,
    cognome: String,
    email: String,
    password: String,
    dataNascita: String,
    numeroTelefono: String,
    checked: Boolean,
    context: Context,
    navController: NavController,
    auth: FirebaseAuth,
    firestore: FirebaseFirestore,
    sharedPref: SharedPreferences,
    setLoading: (Boolean) -> Unit,
    setErrorMessage: (String?) -> Unit
) {
    if (username.isBlank() || nome.isBlank() || cognome.isBlank() || email.isBlank() ||
        password.isBlank() || dataNascita.isBlank() || numeroTelefono.isBlank()) {
        setErrorMessage("Tutti i campi sono obbligatori.")
        return
    }

    if (!password.matches(Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$"))) {
        setErrorMessage("La password deve contenere almeno una lettera maiuscola, una minuscola, un numero e almeno 6 caratteri.")
        return
    }

    setLoading(true)
    setErrorMessage(null)

    firestore.collection("users")
        .whereEqualTo("username", username)
        .get()
        .addOnSuccessListener { result ->
            if (!result.isEmpty) {
                setLoading(false)
                setErrorMessage("Questo username è già in uso.")
                return@addOnSuccessListener
            } else {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            val uid = user?.uid
                            val userData = hashMapOf(
                                "username" to username,
                                "nome" to nome,
                                "cognome" to cognome,
                                "email" to email,
                                "password" to password,
                                "dataNascita" to dataNascita,
                                "numeroTelefono" to numeroTelefono
                            )

                            if (uid != null) {
                                firestore.collection("users")
                                    .document(uid)
                                    .set(userData)
                                    .addOnSuccessListener {
                                        val editor = sharedPref.edit()
                                        editor.putString("nome", nome)
                                        if (checked) {
                                            editor.putString("email", email)
                                            editor.putString("password", password)
                                            editor.putString("uid", uid)
                                        }
                                        editor.commit()

                                        setLoading(false)
                                        navController.navigate("famiglia") {
                                            popUpTo("register") { inclusive = true }
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        setLoading(false)
                                        setErrorMessage("Errore nel salvataggio su Firestore: ${e.message}")
                                    }
                            } else {
                                setLoading(false)
                                setErrorMessage("Errore: UID non trovato.")
                            }
                        } else {
                            setLoading(false)
                            setErrorMessage(task.exception?.message ?: "Errore durante la registrazione.")
                        }
                    }
            }
        }
        .addOnFailureListener { e ->
            setLoading(false)
            setErrorMessage("Errore durante il controllo dello username: ${e.message}")
        }
}


@Preview(showBackground = true)
@Composable
fun RegistrazionePreview(){
    val navController = rememberNavController()
    RegisterScreen(navController)
}
