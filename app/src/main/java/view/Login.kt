package view

import DataBase.Famiglia
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.family_hub.R
import com.example.family_hub.ui.theme.BluCheckBox
import com.example.family_hub.ui.theme.LightBlue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var checked by remember { mutableStateOf(false) }
    val sharedPref = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
    val savedEmail = sharedPref.getString("email", null)
    val savedPassword = sharedPref.getString("password", null)
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        if (!savedEmail.isNullOrEmpty() && !savedPassword.isNullOrEmpty()) {
            isLoading = true
            delay(1000L)
            auth.signInWithEmailAndPassword(savedEmail, savedPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        isLoading = false
                    }
                }
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(R.drawable.logo2_removebg_preview),
                    contentDescription = "Logo",
                    modifier = Modifier.size(160.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text= "Login in corso", color = Color.Black, fontSize = 20.sp, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator(
                    color = LightBlue,
                    strokeWidth = 7.dp,
                )
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.logo2_removebg_preview),
            contentDescription = "Logo",
            modifier = Modifier.size(140.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(textAlign = TextAlign.Center, text = "Accedi", color = Color.Black)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Email o Username", color = Color.Gray) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
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

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Password", color = Color.Gray) },
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
                unfocusedTextColor = Color.Gray
            )
        )


        Row(modifier = Modifier.fillMaxWidth().clickable(onClick = { checked = !checked }), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start){
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
            Text(text="Remember me", color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.padding(start = 0.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                Login(
                    email = email,
                    password = password,
                    checked = checked,
                    auth = auth,
                    firestore = FirebaseFirestore.getInstance(),
                    sharedPref = sharedPref,
                    navController = navController,
                    scope = scope,
                    setLoading = { isLoading = it },
                    setErrorMessage = { errorMessage = it }
                )
            },

            modifier = Modifier.fillMaxWidth().testTag("loginButton"),
            colors = ButtonDefaults.buttonColors(containerColor = LightBlue)
        ) {
            Text(text = "Accedi", color = Color.Black)
        }


        if (isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = it, color = Color.Red)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Non hai un account? Registrati",
            color = Color.Blue,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(16.dp)
                .clickable {
                    navController.navigate("registrazione")
                }
        )
    }
}

fun Login(
    email: String,
    password: String,
    checked: Boolean,
    auth: FirebaseAuth,
    firestore: FirebaseFirestore,
    sharedPref: SharedPreferences,
    navController: NavController,
    scope: CoroutineScope,
    setLoading: (Boolean) -> Unit,
    setErrorMessage: (String?) -> Unit
) {
    if (email.isBlank() || password.isBlank()) {
        setErrorMessage("Inserisci email o username e password")
        return
    }

    setLoading(true)

    val db = firestore
    val isEmail = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun loginWithEmail(emailToUse: String) {
        auth.signInWithEmailAndPassword(emailToUse, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val uid = user?.uid
                    if (uid != null) {
                        db.collection("users").document(uid).get()
                            .addOnSuccessListener { document ->
                                val editor = sharedPref.edit()

                                if (document.exists()) {
                                    val nomeUtente = document.getString("nome") ?: ""
                                    editor.putString("nome", nomeUtente)
                                }

                                if (checked) {
                                    editor.putString("email", emailToUse)
                                    editor.putString("password", password)
                                    editor.putString("uid", uid)
                                }

                                editor.commit()

                                val famiglia = Famiglia()
                                scope.launch {
                                    val famigliaTrovata = famiglia.TrovaFamiglia(uid)
                                    setLoading(false)
                                    if (famigliaTrovata != "Sconosciuto") {
                                        navController.navigate("home") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    } else {
                                        navController.navigate("famiglia") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    }
                                }
                            }
                            .addOnFailureListener {
                                setErrorMessage("Errore nel recupero utente")
                                setLoading(false)
                            }
                    } else {
                        setErrorMessage("UID non disponibile")
                        setLoading(false)
                    }
                } else {
                    setLoading(false)
                    setErrorMessage(task.exception?.message ?: "Login fallito")
                }
            }
    }

    if (isEmail) {
        loginWithEmail(email)
    } else {
        db.collection("users")
            .whereEqualTo("username", email)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val userDoc = result.documents[0]
                    val foundEmail = userDoc.getString("email")
                    if (!foundEmail.isNullOrEmpty()) {
                        loginWithEmail(foundEmail)
                    } else {
                        setLoading(false)
                        setErrorMessage("Email non trovata per questo username")
                    }
                } else {
                    setLoading(false)
                    setErrorMessage("Username non trovato")
                }
            }
            .addOnFailureListener {
                setLoading(false)
                setErrorMessage("Errore durante la ricerca dell'utente")
            }
    }
}




@Preview(showBackground = true)
@Composable
fun LoginPreview(){
    val navController = rememberNavController()
    LoginScreen(navController)
}

