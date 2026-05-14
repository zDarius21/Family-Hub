package view

import DataBase.Attivita
import DataBase.Attivita.AttivitaConNome
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.material3.ToggleFloatingActionButtonDefaults
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.iconColor
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CalendarScreen(navController: NavController) {
    val scrollState = rememberScrollState()
    val fabVisible = remember { mutableStateOf(true) }
    var lastScroll = remember { mutableStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val showOverlay = remember { mutableStateOf(false) }
    val attivitaSelezionata = remember { mutableStateOf<AttivitaConNome?>(null) }
    LaunchedEffect(scrollState.value) {
        if (scrollState.value > lastScroll.value) {
            fabVisible.value = false
        } else if (scrollState.value < lastScroll.value) {
            fabVisible.value = true
        }
        lastScroll.value = scrollState.value
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
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
                            tint = ColoreTastoSelezionato,
                            contentDescription = "Calendario"
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Calendario", fontSize = 9.sp, color = ColoreTastoSelezionato)
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
                    Triple(R.drawable.baseline_add_24, "Aggiungi Attività","aggiuntaAttivita"),
                    Triple(R.drawable.paw, "Passeggiate Effettuate","listaCane")
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
                                    if (checkedProgress > 0.5f) R.drawable.outline_close_24 else R.drawable.magnify

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
                                    //modifier = Modifier.size(25.dp),
                                    painter = painterResource(id = elemento.first),
                                    contentDescription = null,
                                    tint = Color.Black
                                )
                            },
                            text = { Text(text = elemento.second, color= Color.Black) },
                        )

                    }
                }
            }
        },
        content = { innerPadding ->
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(innerPadding)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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
                                .clickable { navController.navigate("home") }
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Calendario",
                            fontSize = 30.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.width(35.dp))
                    }

                    Spacer(modifier = Modifier.height(35.dp))
                    CalendarioAttivita(showOverlay, attivitaSelezionata)

                }
            }
        }
    )
    if (showOverlay.value) {
        DescrizioneAttivita(showOverlay, attivitaSelezionata.value, navController)
    }
}



@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CalendarioAttivita(
    showOverlay: MutableState<Boolean>,
    attivitaSelezionata: MutableState<AttivitaConNome?>
) {
    val displayFormat = remember { SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault()) }
    val queryFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    var calendar by remember { mutableStateOf(Calendar.getInstance()) }
    var isNext by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = {
                isNext = false
                calendar = (calendar.clone() as Calendar).apply {
                    add(Calendar.DAY_OF_MONTH, -1)
                }
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Giorno precedente",
                    tint = Color.Black
                )
            }

            AnimatedContent(
                targetState = displayFormat.format(calendar.time),
                transitionSpec = {
                    if (isNext) {
                        slideInHorizontally { width -> width } + fadeIn() with
                                slideOutHorizontally { width -> -width } + fadeOut()
                    } else {
                        slideInHorizontally { width -> -width } + fadeIn() with
                                slideOutHorizontally { width -> width } + fadeOut()
                    }.using(SizeTransform(clip = false))
                },
                modifier = Modifier.weight(1f),
                label = "AnimatedDate"
            ) { dateString ->
                Text(
                    text = dateString,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    color = Color.Black
                )
            }

            IconButton(onClick = {
                isNext = true
                calendar = (calendar.clone() as Calendar).apply {
                    add(Calendar.DAY_OF_MONTH, 1)
                }
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Giorno successivo",
                    tint = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        val dataPerQuery = queryFormat.format(calendar.time)
        ListaAttivita(data = dataPerQuery, showOverlay, attivitaSelezionata)

    }
}


@Composable
fun ListaAttivita(
    data: String,
    showOverlay: MutableState<Boolean>,
    attivitaSelezionata: MutableState<AttivitaConNome?>
){
    val attivita = Attivita()
    val listaAttivita = remember { mutableStateOf<List<AttivitaConNome>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(data) {
        coroutineScope.launch {
            val result = attivita.prendiAttivitaPerData(data)
            listaAttivita.value = result
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 8.dp)
    ) {
        for (hour in 0 until 24) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = "$hour:00",
                    modifier = Modifier.padding(end = 8.dp),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Divider(
                    modifier = Modifier.weight(1f).height(1.dp),
                    color = Color.Gray
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .horizontalScroll(rememberScrollState())
                    .padding(start = 40.dp, end = 12.dp)
            ) {
                val attivitàInOra = listaAttivita.value.filter { att ->
                    val oraAtt = att.ora.take(2).toIntOrNull() ?: -1
                    oraAtt == hour
                }
                    attivitàInOra.forEach { att ->
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

                        val coloreSelezionato = att.coloreUtente

                        val coloreSfondo = tuttiColori.firstOrNull { it.first == coloreSelezionato }?.second ?: Color.LightGray

                        Box(
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .clickable {
                                    attivitaSelezionata.value = att
                                    showOverlay.value = true
                                }
                                .background(coloreSfondo, RoundedCornerShape(10.dp))
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .widthIn(min = 150.dp, max = 220.dp)
                        )
                         {
                            Column {
                                Text(
                                    text = att.tipo,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = 16.sp,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = att.nomeUtente,
                                    fontSize = 12.sp,
                                    color = Color.Black
                                )
                                Text(
                                    text = att.ora,
                                    fontSize = 12.sp,
                                    color = Color.Black
                                )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DescrizioneAttivita(
    showOverlay: MutableState<Boolean>,
    attivita: AttivitaConNome?,
    navController: NavController
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val isOwner = attivita?.uid == currentUser?.uid
    var showDialog by remember { mutableStateOf(false) }
    val attivitaa = Attivita()
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Conferma eliminazione") },
            text = { Text("Sei sicuro di voler eliminare questa attività?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        showOverlay.value = false
                        attivitaa.eliminaAttivita(attivita)
                        navController.navigate("calendar")
                    }
                ) {
                    Text("Elimina", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text("Annulla")
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .clickable { showOverlay.value = false }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .heightIn(min = 60.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, Color.Gray, shape = RoundedCornerShape(16.dp))
                    .background(GrigioScritta),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = {})
                        .padding(16.dp)
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(12.dp))
                        .background(Color.White, shape = RoundedCornerShape(12.dp))
                        .border(1.dp, Color.LightGray, shape = RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        DetailRow("Utente:", attivita?.nomeUtente)
                        DetailRow("Tipo:", attivita?.tipo)
                        DetailRow("Ora:", attivita?.ora)
                        DetailRow("Descrizione:", attivita?.descrizione)

                        Spacer(modifier = Modifier.height(16.dp))

                        if (isOwner) {
                            Button(
                                onClick = {
                                    showDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                            ) {
                                Text("Elimina", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun DetailRow(label: String, value: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 17.sp, color = Color.Black)
        Text(text = value ?: "", fontSize = 15.sp, color = Color.Black)
    }
}






@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    val navController = rememberNavController()
    CalendarScreen(navController = navController)
}