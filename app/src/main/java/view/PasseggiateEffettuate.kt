package view


import DataBase.Attivita
import android.content.Context
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.family_hub.ui.theme.BluAttivita
import com.example.family_hub.ui.theme.GrigioScritta
import com.google.firebase.auth.FirebaseAuth
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline

@Composable
fun ListaCane(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val uid = user?.uid

    var attivitaList by remember { mutableStateOf<List<AttivitaCane>>(emptyList()) }
    var selectedCoordinates by remember { mutableStateOf<List<Pair<Double, Double>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }



    LaunchedEffect(uid) {
        if (uid != null) {
            attivitaList = Attivita().caricaAttivitaCanePassate(uid)
        }
        isLoading = false
    }
    Scaffold(
        content = { innerPadding ->
            Box(modifier = Modifier
                .background(GrigioScritta)
                .padding(innerPadding)
                .fillMaxSize()
            ) {
                Column(modifier = Modifier.fillMaxSize()) {

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
                    if (isLoading) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else if (attivitaList.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Nessuna attività passata trovata.")
                        }
                    } else {
                        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            Text("Passeggiate passate:", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(8.dp))

                            attivitaList.forEachIndexed { index, attivita ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {
                                            selectedCoordinates = attivita.coordinate
                                        },
                                    colors = CardDefaults.cardColors
                                        (
                                        containerColor = BluAttivita
                                    )
                                ) {
                                    Column(Modifier.padding(12.dp)) {
                                        Text("Passeggiata #${index + 1}")
                                        Text("Data: ${attivita.data} - Ora: ${attivita.ora}")
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                if (selectedCoordinates.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f))
                            .clickable {
                                selectedCoordinates = emptyList()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .height(300.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .clickable(enabled = false) {}
                        ) {
                            OsmMapView(coordinateList = selectedCoordinates)
                        }
                    }
                }
            }
        }
    )
}


data class AttivitaCane(
    val data: String,
    val ora: String,
    val coordinate: List<Pair<Double, Double>>
)



@Composable
fun OsmMapView(coordinateList: List<Pair<Double, Double>>) {
    val context = LocalContext.current

    AndroidView(
        factory = {
            org.osmdroid.config.Configuration.getInstance()
                .load(context, context.getSharedPreferences("osm_prefs", Context.MODE_PRIVATE))

            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)

                if (coordinateList.isNotEmpty()) {
                    val geoPoints = coordinateList.map { org.osmdroid.util.GeoPoint(it.first, it.second) }

                    controller.setZoom(17.5)
                    controller.setCenter(geoPoints.first())

                    val polyline = Polyline().apply {
                        setPoints(geoPoints)
                        outlinePaint.color = android.graphics.Color.BLUE
                        outlinePaint.strokeWidth = 6f
                    }
                    overlays.add(polyline)
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}



