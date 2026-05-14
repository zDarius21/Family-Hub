package view


import Transazione
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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

data class tipologie(
    val percentuale: Float,
    val color: Color,
    val tipologia: String
)

data class Spesa(
    val descrizione: String = "",
    val giorno: String = "",
    val idUnicoFamiglia: String = "",
    val importo: Double = 0.0,
    val negozio: String = "",
    val tipologia: String = "",
    val utente: String = ""
)

val transazione = Transazione()


@Composable
fun TransactionScreen(navController: NavController) {
    val scrollState = rememberScrollState()
    val showOverlay = remember { mutableStateOf(false) }
    val spesaSelezionata = remember { mutableStateOf<Spesa?>(null) }
    val fabVisible = remember { mutableStateOf(true) }
    var lastScroll = remember { mutableStateOf(0) }
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
                            tint = Color.Black,
                            contentDescription = "Calendario"
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Calendario", fontSize = 9.sp, color = Color.Black)
                    }

                    Column(
                        modifier = Modifier.fillMaxHeight().weight(1f).clickable { navController.navigate("transaction") },
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
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            AnimatedVisibility(
                visible = fabVisible.value,
                enter = fadeIn(animationSpec = tween(500)),
                exit = fadeOut(animationSpec = tween(500))
            ) {
                FloatingActionButton(
                    onClick = { navController.navigate("aggiuntaTransazione") },
                    containerColor = LightBlue,
                ){Icon(Icons.Filled.Add, "Floating action button.", tint= Color.Black)}
            }},
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(innerPadding)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {Row(
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
                        text = "Transazioni",
                        fontSize = 30.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = Color.Black,
                        textAlign = TextAlign.Center

                    )
                }
                Spacer(modifier = Modifier.height(35.dp))
                GraficoSpese()
                Spacer(modifier = Modifier.height(35.dp))
                ListaSpese(showOverlay,spesaSelezionata)

            }

        }


    )
    if (showOverlay.value){
        Descrizione(showOverlay, spesaSelezionata.value)
    }
}


@Composable
fun GraficoSpese() {
    var slices by remember { mutableStateOf<List<tipologie>>(emptyList()) }
    var totalCost by remember { mutableStateOf(0.0) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        slices = transazione.calcoloGrafico()
        totalCost = transazione.calcoloTotale()
        isLoading = false
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(12.dp))
            .background(Color.White, shape = RoundedCornerShape(12.dp))
            .border(1.dp, Color.LightGray, shape = RoundedCornerShape(12.dp))
            .padding(25.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = LightBlue)
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Distribuzione mensile delle Spese",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Torta(slices = slices, totalCost = totalCost)
                    }

                    Spacer(modifier = Modifier.width(30.dp))

                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        horizontalAlignment = Alignment.Start,
                    ) {
                        slices.forEach { slice ->
                            Legenda(slice.color, slice.tipologia)
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun Torta(slices: List<tipologie>, totalCost: Double) {
    Box(
        modifier = Modifier
            .size(160.dp)
            .clip(CircleShape)
            .background(color = Color.Transparent, shape = CircleShape)
            .border(0.2.dp, Color.Black, shape = CircleShape)
    ) {
        Box(
            modifier = Modifier.size(170.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val thickness = 80.dp.toPx()
                var startAngle = -90f
                slices.forEach { slice ->
                    drawArc(
                        color = slice.color,
                        startAngle = startAngle,
                        sweepAngle = slice.percentuale * 360f,
                        useCenter = false,
                        style = Stroke(width = thickness, cap = StrokeCap.Butt),
                    )
                    startAngle += slice.percentuale * 360f
                }
            }

            Box(
                modifier = Modifier
                    .size(100.dp) 
                    .background(Color.White, shape = CircleShape)
                    .border(0.2.dp, Color.Black, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("TOTALE", color = Color.Black, fontWeight = FontWeight.Bold)
                    Text("€ %.2f".format(totalCost), color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}



@Composable
fun Legenda(colore: Color, nome: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(colore, shape = CircleShape)
                .border(0.2.dp, Color.Black, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = nome, fontSize = 12.sp, color= Color.Black)
    }
}

@Composable
fun ListaSpese(
    showOverlay: MutableState<Boolean>,
    spesaSelezionata: MutableState<Spesa?>
) {
    var loading by remember { mutableStateOf(true) }
    val spese by produceState<List<Spesa>>(initialValue = emptyList()) {
        val listaTransazioni = transazione.ListaTransazioni()
        value = listaTransazioni.map { transazioneSpesa ->
            Spesa(
                utente = transazioneSpesa.utente,
                negozio = transazioneSpesa.negozio,
                tipologia = transazioneSpesa.tipologia,
                importo = transazioneSpesa.importo,
                descrizione = transazioneSpesa.descrizione
            )
        }
        loading = false
    }

    if (loading) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = LightBlue,)
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(12.dp))
                .background(Color.White, shape = RoundedCornerShape(12.dp))
                .border(1.dp, Color.LightGray, shape = RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Spese mensili effettuate",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                spese.forEach { spesa ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .clickable {
                                spesaSelezionata.value = spesa
                                showOverlay.value = true
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = spesa.utente,
                            color = Color.Black,
                            modifier = Modifier.weight(1f),
                            fontSize = 13.sp
                        )
                        Text(
                            text = spesa.negozio,
                            color = Color.Black,
                            modifier = Modifier.weight(1.2f),
                            fontSize = 13.sp
                        )
                        Text(
                            text = spesa.tipologia,
                            color = Color.Black,
                            modifier = Modifier.weight(1f),
                            fontSize = 13.sp
                        )
                        Text(
                            text = "€ %.2f".format(spesa.importo),
                            modifier = Modifier.weight(0.9f),
                            fontSize = 13.sp,
                            color = Color.Black,
                            textAlign = TextAlign.End
                        )
                    }
                    HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                }
            }
        }
    }
}






@Composable
fun Descrizione(
    showOverlay: MutableState<Boolean>,
    spesaSelezionata: Spesa?
){
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
                    .padding(bottom = 10.dp, end = 16.dp, start = 16.dp)
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
                ){
                    Column {
                        Row (
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Utente:",
                                color = Color.Black,
                                fontSize = 17.sp,
                                modifier = Modifier.padding(bottom = 5.dp)
                            )
                            Text(text = spesaSelezionata?.utente ?: "", color = Color.Black, fontSize = 15.sp, modifier = Modifier.padding(bottom = 5.dp))
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Negozio:",
                                color = Color.Black,
                                fontSize = 17.sp,
                                modifier = Modifier.padding(bottom = 5.dp)
                            )
                            Text(text = spesaSelezionata?.negozio ?: "", color = Color.Black, fontSize = 15.sp, modifier = Modifier.padding(bottom = 5.dp)) }
                        Row (
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ){
                            Text(
                                text = "Tipologia acquisto:",
                                color = Color.Black,
                                fontSize = 17.sp,
                                modifier = Modifier.padding(bottom = 5.dp)
                            )
                            Text(text = spesaSelezionata?.tipologia ?: "", color = Color.Black, fontSize = 15.sp, modifier = Modifier.padding(bottom = 5.dp))
                        }
                        Row (
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ){
                            Text(
                                text = "Costo dell'acquisto:",
                                color = Color.Black,
                                fontSize = 17.sp,
                                modifier = Modifier.padding(bottom = 5.dp)
                            )
                            Text(text = "€ %.2f".format(spesaSelezionata?.importo ?: 0.0), modifier = Modifier.padding(bottom = 5.dp), fontSize = 15.sp, color = Color.Black, textAlign = TextAlign.End)
                        }
                        Row (
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ){
                            Text(
                                text = "Descrizione della spesa:",
                                color = Color.Black,
                                fontSize = 17.sp,
                                modifier = Modifier.padding(bottom = 5.dp).weight(1f)
                            )


                        Text(text = spesaSelezionata?.descrizione ?: "", fontSize = 15.sp, color = Color.Black, modifier = Modifier.padding(bottom = 5.dp)
                        )
                            }
                    }

                }
            }

        }
    }
}








@Preview(showBackground = true)
@Composable
fun TransactionPreview() {
    val navController = rememberNavController()
    TransactionScreen(navController = navController)
}

