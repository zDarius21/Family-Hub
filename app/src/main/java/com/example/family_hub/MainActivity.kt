package com.example.family_hub

import DataBase.Attivita
import Notifiche.CheckActivityWorker
import Posizione.LocationForegroundService
import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.family_hub.ui.theme.FamilyHubTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import view.AccediFamigliaScreen
import view.AccountScreen
import view.AggiungiTransazioneScreen
import view.AggiuntaAttivitaScreen
import view.CalendarScreen
import view.CreazioneFamigliaScreen
import view.DatiAccount
import view.FamigliaScreen
import view.HomeScreen
import view.ListaCane
import view.LoginScreen
import view.MembriFamiglia
import view.ModificaToDoScreen
import view.RegisterScreen
import view.ShoppingListScreen
import view.Storico
import view.ToDoListScreen
import view.TransactionScreen
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        val hasFineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasBackgroundLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (hasFineLocation && hasBackgroundLocation) {
            lifecycleScope.launch {
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                if (uid != null) {
                    val oraInizio = Attivita().recuperaOraInizioPasseggiata(uid)
                    if (oraInizio != null) {
                        val intent =
                            Intent(this@MainActivity, LocationForegroundService::class.java).apply {
                                putExtra("oraInizio", oraInizio)
                            }
                        startForegroundService(intent)
                    }
                }
            }
        } else {
            Log.d("MainActivity", "Permessi posizione NON concessi, servizio non avviato")
        }

        val workRequest = PeriodicWorkRequestBuilder<CheckActivityWorker>(15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "check_activity_worker",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )


        setContent {
            FamilyHubTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "login") {
                    composable("home") { HomeScreen(navController) }
                    composable("todo") { ToDoListScreen(navController) }
                    composable("calendar") { CalendarScreen(navController) }
                    composable("shoppingList") { ShoppingListScreen(navController) }
                    composable("transaction") { TransactionScreen(navController) }
                    composable("account") { AccountScreen(navController) }
                    composable("aggiuntaAttivita") { AggiuntaAttivitaScreen(navController) }
                    composable("modificaToDo") { ModificaToDoScreen(navController) }
                    composable("aggiuntaTransazione") { AggiungiTransazioneScreen(navController) }
                    composable("registrazione") { RegisterScreen(navController) }
                    composable("login") { LoginScreen(navController) }
                    composable("famiglia") { FamigliaScreen(navController) }
                    composable("creazioneFamiglia") { CreazioneFamigliaScreen(navController) }
                    composable("accediFamiglia") { AccediFamigliaScreen(navController) }
                    composable("datiAccount") { DatiAccount(navController) }
                    composable("gestioneFamiglia") { MembriFamiglia(navController) }
                    composable("storico") { Storico(navController) }
                    composable("listaCane"){ListaCane(navController)}
                }
            }
        }
    }
}
