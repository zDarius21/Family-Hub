package view

import DataBase.Famiglia
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.family_hub.R
import com.example.family_hub.ui.theme.ColoreTastoSelezionato
import com.example.family_hub.ui.theme.LightBlue
import com.google.firebase.auth.FirebaseAuth


@Composable
fun AccountScreen(navController: NavController) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val openDialog = remember { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val uid = user?.uid
    val famiglia = remember { Famiglia() }
    var codiceFamiglia by remember { mutableStateOf<String?>("") }

    LaunchedEffect(uid) {
        uid?.let {
            codiceFamiglia = famiglia.TrovaCodiceFamiglia(it)
        }
    }

    if (codiceFamiglia.isNullOrEmpty() && uid != null) {
        return
    }

    fun logout(){
        val sharedPref = context.getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()
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
                            tint = ColoreTastoSelezionato,
                            contentDescription = "Account"
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Account", fontSize = 9.sp, color = ColoreTastoSelezionato)
                    }

                }
            }
        },
        content = { innerPadding ->
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
                    Column() {
                        Row(verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painter = painterResource(R.drawable.logo2_removebg_preview),
                                contentDescription = "Logo",
                                modifier = Modifier
                                    .size(70.dp)
                                    .clickable { navController.navigate("home") }
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "Account",
                                fontSize = 30.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = Color.Black,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    Column(modifier=Modifier.fillMaxSize(), horizontalAlignment = Alignment.End) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_logout_24),
                            tint = Color.Black,
                            contentDescription = "Logout",
                            modifier = Modifier.size(30.dp).clickable(onClick = {
                               openDialog.value=true
                            })
                        )
                    }

                }

                Spacer(modifier = Modifier.height(35.dp))
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    Text(text="Codice Famiglia: $codiceFamiglia", fontSize = 20.sp, color = Color.Black)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.size(width = 400.dp, height = 100.dp).clickable(onClick = {navController.navigate("datiAccount")}),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_settings_24),
                            tint = Color.Black,
                            contentDescription = "Dati Account",
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(30.dp)
                        )
                        Text(
                            text = "Dati Account",
                            fontSize = 25.sp,
                            color = Color.Black
                        )
                    }

                    HorizontalDivider( thickness = 2.dp, color = Color.LightGray)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.size(width = 400.dp, height = 100.dp).clickable(onClick = {navController.navigate("gestioneFamiglia")}),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.account_group),
                            tint = Color.Black,
                            contentDescription = "Gestione Famiglia",
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(30.dp)
                        )
                        Text(
                            text = "Membri Famiglia",
                            fontSize = 25.sp,
                            color = Color.Black
                        )
                    }

                }

            }
            if (openDialog.value) {
                AlertDialog(
                    onDismissRequest = { openDialog.value = false },
                    title = { Text("Conferma logout") },
                    text = { Text("Sei sicuro di voler fare logout?") },
                    confirmButton = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextButton(onClick = { openDialog.value = false }) {
                                Text("Annulla", color = Color.Gray)
                            }
                            Button(
                                modifier = Modifier,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LightBlue,
                                    contentColor = Color.Black
                                ),
                                onClick = {
                                    openDialog.value = false
                                    logout()
                                    navController.navigate("login")
                                }
                            ) {
                                Text("OK")
                            }
                        }
                    },
                    dismissButton = {}
                )
            }

        }

    )
}











@Preview(showBackground = true)
@Composable
fun AccountScreenPreview() {
    val navController = rememberNavController()
    AccountScreen(navController = navController)
}