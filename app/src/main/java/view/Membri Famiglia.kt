package view

import DataBase.Famiglia
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.family_hub.ui.theme.GrigioScritta
import com.example.family_hub.ui.theme.LightBlue
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun MembriFamiglia(navController: NavController) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    var membri by remember { mutableStateOf<List<UserAccount>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val showOverlay = remember { mutableStateOf(false) }
    val selectedUser = remember { mutableStateOf<UserAccount?>(null) }

    LaunchedEffect(uid) {
        if (uid == null) {
            error = "Utente non autenticato"
            isLoading = false
            return@LaunchedEffect
        }
        val famiglia = Famiglia()
        val risultato = famiglia.caricaMembriFamiglia(uid)
        if (risultato != null) {
            membri = risultato
        } else {
            error = "Impossibile caricare i membri della famiglia"
        }
        isLoading = false
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (error != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = error ?: "Errore sconosciuto")
        }
    } else {
        Scaffold(
            content = {innerPAdding ->
                Box(modifier = Modifier
                    .background(GrigioScritta)
                    .padding(innerPAdding)
                    .fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
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

                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Membri della famiglia",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(16.dp),
                                color = Color.Black
                            )

                            membri.forEach { membro ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                        .clickable {
                                            selectedUser.value = membro
                                            showOverlay.value = true
                                        },
                                    elevation = CardDefaults.cardElevation(4.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = LightBlue
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(text = "Nome: ${membro.nome}", color = Color.Black)
                                        Text(text = "Cognome: ${membro.cognome}", color = Color.Black)
                                        Text(text = "Email: ${membro.email}", color = Color.Black)
                                        Text(text = "Username: ${membro.username}", color = Color.Black)
                                        Text(text = "Data di nascita: ${membro.dataNascita}", color = Color.Black)
                                        Text(text = "Telefono: ${membro.numeroTelefono}", color = Color.Black)
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Text(text = "Colore: ${membro.colore ?: "N/A"}", color = Color.Black)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Box(
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .background(color = coloreDaNome(membro.colore), shape = RoundedCornerShape(4.dp))
                                                    .border(1.dp, Color.Black, shape = RoundedCornerShape(4.dp))
                                            )
                                        }
                                    }

                                }
                            }
                        }
                    }

                    if (showOverlay.value && selectedUser.value != null) {
                        DettaglioMembro(
                            showOverlay = showOverlay,
                            user = selectedUser.value!!,
                            navController = navController
                        )
                    }
                }
            }
        )
    }
}

val tuttiColori = listOf(
    "Rosa pastello" to Color(0xFFFFC1CC),
    "Giallo crema" to Color(0xFFFFF3B0),
    "Rosso chiaro" to Color(0xFFff686b),
    "Lilla" to Color(0xFFE0C3FC),
    "Menta" to Color(0xFFBFFCC6),
    "Marroncino" to Color(0xFFd4a373),
    "Corallo" to Color(0xFFFFA07A),
    "Azzurro chiaro" to Color(0xFFA9DEF9)
)

fun coloreDaNome(nomeColore: String?): Color {
    return tuttiColori.firstOrNull { it.first == nomeColore }?.second ?: Color.Gray
}


@Composable
fun DettaglioMembro(
    showOverlay: MutableState<Boolean>,
    user: UserAccount,
    navController: NavController
) {
    val auth = FirebaseAuth.getInstance()
    val currentUid = auth.currentUser?.uid ?: ""
    val famiglia = Famiglia()
    val scope = rememberCoroutineScope()

    var isAdminState by remember { mutableStateOf<Boolean?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var successo by remember { mutableStateOf(true) }
    var messaggioErrore by remember { mutableStateOf(false) }

    LaunchedEffect(currentUid) {
        isAdminState = famiglia.isUtenteAdmin(currentUid)
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Conferma eliminazione") },
            text = { Text("Vuoi eliminare questo membro dalla famiglia?") },
            confirmButton = {
                TextButton(
                    onClick = {

                        scope.launch {
                            successo = famiglia.rimuoviMembroDaFamiglia(user.uid)
                            if (successo) {
                                navController.popBackStack()

                            } else {
                                showDialog = false
                                messaggioErrore = true
                            }
                        }
                    }
                ) {
                    Text("Elimina", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Annulla")
                }
            }
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .clickable { showOverlay.value = false },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text("Dettagli Utente", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Nome: ${user.nome}")
            Text("Cognome: ${user.cognome}")
            Text("Email: ${user.email}")
            Text("Username: ${user.username}")
            Text("Data nascita: ${user.dataNascita}")
            Text("Telefono: ${user.numeroTelefono}")
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = "Colore: ${user.colore ?: "N/A"}", color = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(color = coloreDaNome(user.colore), shape = RoundedCornerShape(4.dp))
                        .border(1.dp, Color.Black, shape = RoundedCornerShape(4.dp))
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isAdminState == true) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { showDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Elimina")
                    }

                    Button(
                        onClick = {
                            scope.launch {
                                famiglia.assegnaPermessi(user.uid)
                            }
                            navController.popBackStack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                    ) {
                        Text("Assegna Permessi")
                    }
                }
            }

            if (messaggioErrore) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Non puoi eliminare il tuo account", color = Color.Red)
            }
        }
    }
}

