package view

import DataBase.Attivita
import DataBase.Attivita.AttivitaConNome
import DataBase.Famiglia
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.iconColor
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.family_hub.R
import com.example.family_hub.ui.theme.LightBlue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(navController: NavController){
    val scrollState = rememberScrollState()
    val fabVisible = remember { mutableStateOf(true) }
    var lastScroll = remember { mutableStateOf(0) }
    val LightBlue=LightBlue
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val uid = user?.uid
    val famiglia = Famiglia()
    val showOverlay = remember { mutableStateOf(false) }
    val nomeSalvatoAuth= auth.currentUser?.displayName
    val sharedPref = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
    val nomeSalvato = sharedPref.getString("nome", null)
    var showPermissionDialog by remember { mutableStateOf(false) }

    // Stati permessi notifica
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            } else true
        )
    }
    var showNotificationPermissionDialog by remember { mutableStateOf(false) }

    // Stati permessi posizione foreground e background
    var hasForegroundLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    var hasBackgroundLocationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
            } else true
        )
    }
    var showForegroundLocationPermissionDialog by remember { mutableStateOf(false) }
    var showBackgroundLocationPermissionDialog by remember { mutableStateOf(false) }

    // Controllo iniziale permessi
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
            showNotificationPermissionDialog = true
        }
        if (!hasForegroundLocationPermission) {
            showForegroundLocationPermissionDialog = true
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !hasBackgroundLocationPermission) {
            showBackgroundLocationPermissionDialog = true
        }
    }

    // Aggiorna token FCM quando permesso notifiche è concesso
    LaunchedEffect(hasNotificationPermission) {
        if (hasNotificationPermission) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    if (uid != null) {
                        FirebaseFirestore.getInstance().collection("users").document(uid)
                            .set(mapOf("fcmToken" to token), SetOptions.merge())
                    }
                }
            }
        }
    }

    // Popup richiesta permesso notifiche
    if (showNotificationPermissionDialog) {
        RequestNotificationPermissionAsPopup(
            showDialog = showNotificationPermissionDialog,
            onDismissRequest = { showNotificationPermissionDialog = false },
            onPermissionGranted = {
                hasNotificationPermission = true
                showNotificationPermissionDialog = false
            },
            onPermissionDenied = { shouldShowRationale ->
                hasNotificationPermission = false
                showNotificationPermissionDialog = false
            }
        )
    }

    // Popup richiesta permesso posizione foreground
    if (showForegroundLocationPermissionDialog) {
        RequestLocationPermissionPopup(
            showDialog = showForegroundLocationPermissionDialog,
            onDismissRequest = { showForegroundLocationPermissionDialog = false },
            onPermissionGranted = {
                hasForegroundLocationPermission = true
                showForegroundLocationPermissionDialog = false

                // Se permesso foreground concesso, chiedi background se serve
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !hasBackgroundLocationPermission) {
                    showBackgroundLocationPermissionDialog = true
                }
            },
            onPermissionDenied = { shouldShowRationale ->
                hasForegroundLocationPermission = false
                showForegroundLocationPermissionDialog = false
            },
            permission = Manifest.permission.ACCESS_FINE_LOCATION,
            title = "Permesso posizione",
            message = "Per usare alcune funzionalità è necessario abilitare il permesso di posizione."
        )
    }

    // Popup richiesta permesso posizione background (Android Q+)
    if (showBackgroundLocationPermissionDialog && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val isAndroidROrLater = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
        if (isAndroidROrLater) {
            AlertDialog(
                onDismissRequest = { showBackgroundLocationPermissionDialog = false },
                title = { Text("Permesso posizione in background") },
                text = { Text("Per tracciare la posizione anche quando l'app è in background, devi abilitare il permesso dalla schermata delle impostazioni dell'app.") },
                confirmButton = {
                    Button(onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                        showBackgroundLocationPermissionDialog = false
                    }) {
                        Text("Apri impostazioni")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showBackgroundLocationPermissionDialog = false }) {
                        Text("Più tardi")
                    }
                }
            )
        } else {
            RequestLocationPermissionPopup(
                showDialog = showBackgroundLocationPermissionDialog,
                onDismissRequest = { showBackgroundLocationPermissionDialog = false },
                onPermissionGranted = {
                    hasBackgroundLocationPermission = true
                    showBackgroundLocationPermissionDialog = false
                },
                onPermissionDenied = { shouldShowRationale ->
                    hasBackgroundLocationPermission = false
                    showBackgroundLocationPermissionDialog = false
                },
                permission = Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                title = "Permesso posizione in background",
                message = "Per tracciare la posizione anche quando l'app è in background, concedi il permesso posizione in background."
            )
        }
    }




    LaunchedEffect(scrollState.value) {
        if (scrollState.value > lastScroll.value) {
            fabVisible.value = false
        } else if (scrollState.value < lastScroll.value) {
            fabVisible.value = true
        }
        lastScroll.value = scrollState.value
    }
    Scaffold(
        bottomBar = {
            BottomAppBar(modifier = Modifier, containerColor = com.example.family_hub.ui.theme.LightBlue) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(com.example.family_hub.ui.theme.LightBlue),
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
                            tint = Color.Black,
                            contentDescription = "Spese"
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Transazioni", fontSize = 9.sp, color = Color.Black)
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
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            AnimatedVisibility(
                visible = fabVisible.value,
                enter = fadeIn(animationSpec = tween(500)),
                exit = fadeOut(animationSpec = tween(500))
            ) {
                val listState = rememberLazyListState()
                val fabVisible by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }
                val elementi = listOf(
                    Triple(R.drawable.cash_multiple, "Aggiungi Transazione","aggiuntaTransazione"),
                    Triple(R.drawable.clipboard_list_outline, "Aggiungi To Do","modificaToDo"),
                    Triple(R.drawable.outline_shopping_bag_24, "Aggiungi Spesa", "shoppingList"),
                    Triple(R.drawable.calendar_text, "Aggiungi Attività", "aggiuntaAttivita")


                )

                var fabMenuExpanded by rememberSaveable { mutableStateOf(false) }

                    BackHandler(fabMenuExpanded) { fabMenuExpanded = false }

                    FloatingActionButtonMenu(
                        //modifier = Modifier.align(Alignment.BottomEnd),
                        expanded = fabMenuExpanded,
                        button = {
                            ToggleFloatingActionButton(
                                modifier =
                                    Modifier.semantics {
                                        traversalIndex = -1f
                                        stateDescription = if (fabMenuExpanded) "Expanded" else "Collapsed"
                                        contentDescription = "Toggle menu"
                                    }
                                        .animateFloatingActionButton(
                                            visible = fabVisible || fabMenuExpanded,
                                            alignment = Alignment.BottomEnd,
                                        ),
                                containerColor= ToggleFloatingActionButtonDefaults.containerColor(
                                    initialColor = LightBlue,
                                    finalColor = LightBlue
                                ),
                                checked = fabMenuExpanded,
                                onCheckedChange = { fabMenuExpanded = !fabMenuExpanded },
                            ) {
                                val iconResId by remember {
                                    derivedStateOf {
                                        if (checkedProgress > 0.5f) R.drawable.outline_close_24 else R.drawable.baseline_add_24

                                    }
                                }
                                Icon(
                                    tint = Color.Black,
                                    painter = painterResource(id = iconResId),
                                    contentDescription = null,
                                    modifier = Modifier.animateIcon( { checkedProgress } ,
                                        color = iconColor(
                                            initialColor = Color.Black,
                                            finalColor = Color.Black
                                        )
                                    )

                                )

                            }
                        },
                    ) {
                        elementi.forEachIndexed { i, elemento ->
                            FloatingActionButtonMenuItem(
                                modifier = Modifier.semantics {
                                    isTraversalGroup = true
                                    if (i == elementi.size - 1) {
                                        customActions = listOf(
                                            CustomAccessibilityAction(
                                                label = "Close menu",
                                                action = {
                                                    fabMenuExpanded = false
                                                    true
                                                },
                                            )
                                        )
                                    }
                                },
                                containerColor= (LightBlue),

                                onClick = {
                                    fabMenuExpanded = false
                                    navController.navigate(elemento.third)
                                },
                                icon = {
                                    Icon(
                                        painter = painterResource(id = elemento.first),
                                        contentDescription = null,
                                        tint = Color.Black
                                    )
                                },
                                text = { Text(text = elemento.second, color= Color.Black) },
                            )

                        }
                    }

            }},
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(innerPadding)
                    .verticalScroll(scrollState), horizontalAlignment = Alignment.CenterHorizontally)
            {


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 25.dp, start = 16.dp, end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Image(
                        painter = painterResource(R.drawable.logo2),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(70.dp)

                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Welcome Home",
                        fontSize = 30.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier
                    .height(25.dp)
                    .fillMaxWidth())
                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)){
                    Row(modifier = Modifier
                        .fillMaxWidth()) {
                        Column(modifier = Modifier) {
                            Text(text = "Attività Giornaliere", modifier = Modifier.offset(x = (10).dp), fontSize = 20.sp, color = Color.Black)
                            Spacer(modifier = Modifier.height(10.dp))
                            Calendario(navController)
                        }
                    }
                    Spacer(modifier = Modifier
                        .height(25.dp)
                        .fillMaxWidth())
                    Spacer(modifier = Modifier
                        .height(2.dp)
                        .fillMaxWidth()
                        .background(Color.LightGray))
                    Spacer(modifier = Modifier
                        .height(25.dp)
                        .fillMaxWidth())
                    Row(modifier = Modifier
                        .fillMaxWidth()){
                        Column(modifier = Modifier) {
                            Text(text = "To Do List", modifier = Modifier.offset(x = (10).dp), fontSize = 20.sp, color = Color.Black)
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(modifier = Modifier.clickable(onClick = { navController.navigate("todo") })){
                            ToDoListAdvanced()
                        }
                        }

                    }
                    Spacer(modifier = Modifier
                        .height(25.dp)
                        .fillMaxWidth())
                    Spacer(modifier = Modifier
                        .height(2.dp)
                        .fillMaxWidth()
                        .background(Color.LightGray))
                    Spacer(modifier = Modifier
                        .height(9.dp)
                        .fillMaxWidth())

                }
                Row(modifier = Modifier
                    .fillMaxWidth()){
                    Box(modifier = Modifier.clickable(onClick = { navController.navigate("transaction") })) {
                        GraficoSpese()
                    }
                }

            }

        }
    )
}




@Composable
fun Calendario(navController: NavController) {
    data class Evento(
        val ora: String,
        val nome: String,
        val descrizione: String
    )
    val attivita = Attivita()
    val eventi = remember { mutableStateOf<List<AttivitaConNome>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        eventi.value = attivita.prendiAttivitaOggi()
        isLoading = false
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = LightBlue)
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(20.dp))
                .clickable { navController.navigate("calendar") }
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, Color.DarkGray, shape = RoundedCornerShape(20.dp))
                .background(Color.White)
        ) {
            if (eventi.value.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nessuna attività prevista per oggi",
                        color = Color.DarkGray,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()

                ) {
                    eventi.value.forEach { evento ->
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
                        val coloreSelezionato = evento.coloreUtente
                        val coloreSfondo = tuttiColori.firstOrNull { it.first == coloreSelezionato }?.second
                            ?: Color.LightGray

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth(),
                            color = coloreSfondo,
                            tonalElevation = 2.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color.DarkGray)
                                    .padding(horizontal = 15.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(evento.ora, modifier = Modifier.weight(1f), color = Color.Black)
                                Text(evento.nomeUtente, modifier = Modifier.weight(1f), color = Color.Black)
                                Text(evento.tipo, modifier = Modifier.weight(1f), color = Color.Black)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RequestLocationPermissionPopup(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    onPermissionGranted: () -> Unit,
    onPermissionDenied: (shouldShowRationale: Boolean) -> Unit,
    permission: String, // per riusare questo composable per foreground o background
    title: String,
    message: String
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            onPermissionGranted()
        } else {
            val shouldShowRationale = activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(it, permission)
            } ?: false
            onPermissionDenied(shouldShowRationale)
        }
        onDismissRequest()
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(title) },
            text = { Text(message) },
            confirmButton = {
                Button(
                    onClick = {
                        permissionLauncher.launch(permission)
                    }
                ) {
                    Text("Abilita")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text("Più tardi")
                }
            }
        )
    }
}


@Composable
fun RequestNotificationPermissionAsPopup(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    onPermissionGranted: () -> Unit,
    onPermissionDenied: (shouldShowRationale: Boolean) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            onPermissionGranted()
        } else {
            val shouldShowRationale = activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.POST_NOTIFICATIONS)
            } ?: false
            onPermissionDenied(shouldShowRationale)
        }
        onDismissRequest()
    }

    if (showDialog && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text("Abilita Notifiche") },
            text = { Text("Per ricevere promemoria e aggiornamenti importanti dalla tua famiglia, per favore abilita le notifiche.") },
            confirmButton = {
                Button(
                    onClick = {

                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                ) {
                    Text("Abilita")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text("Più tardi")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()
    HomeScreen(navController = navController)
}


