package view


import DataBase.ShopList
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.family_hub.R
import com.example.family_hub.ui.theme.ColoreTastoSelezionato
import com.example.family_hub.ui.theme.LightBlue
import com.example.family_hub.ui.theme.ivory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun ShoppingListScreen(navController: NavController) {
    val scrollState = rememberScrollState()
    val fabVisible = remember { mutableStateOf(true) }
    val showTextField = remember { mutableStateOf(false) }
    var itemList by remember { mutableStateOf<List<ListItem>>(emptyList()) }
    val newItem = remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val alimenti = ShopList()
    val scope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val uid = user?.uid
    val openDialog = remember { mutableStateOf(false) }
    val selectedTaskForEdit = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        scope.launch {
            val listaAlimenti = alimenti.ListaSpesa()
            itemList = listaAlimenti.map { ListItem(name = it) }
        }
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
                            tint = ColoreTastoSelezionato,
                            contentDescription = "Spese"
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Lista Spesa", fontSize = 9.sp, color = ColoreTastoSelezionato)
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
                    onClick = { showTextField.value = true },
                    containerColor = LightBlue,
                ){Icon(Icons.Filled.Add, "Floating action button.", tint= Color.Black)}
            }},
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(innerPadding)
                    .verticalScroll(scrollState).clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        focusManager.clearFocus()
                    },
                horizontalAlignment = Alignment.CenterHorizontally)
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
                            .clickable { navController.navigate("home") }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Lista della Spesa",
                        fontSize = 30.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                }
                Spacer(modifier = Modifier.height(35.dp))
                Column(modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp))
                {
                    Row (modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween)
                    {
                        Text(
                            text = "Cosa c'è da comprare?",
                            fontSize = 20.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = Color.Black
                        )
                        Icon(painter = painterResource(R.drawable.baseline_history_24), tint = Color.Black, contentDescription = "Storico", modifier = Modifier.size(40.dp).clickable(onClick = { navController.navigate("storico") }))
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    shoppingListAdvanced(navController)
                }
                Spacer(
                    modifier = Modifier
                        .height(60.dp)
                )
                AnimatedVisibility(visible = showTextField.value) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedTextField(
                            value = newItem.value,
                            onValueChange = { newItem.value = it },
                            label = { Text("Inserisci un nuovo alimento", color = Color.Gray) },
                            modifier = Modifier
                                .width(300.dp)
                                .padding(bottom = 15.dp)
                                .focusRequester(focusRequester),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Blue,
                                unfocusedBorderColor = Color.Gray,
                                focusedLabelColor = Color.Blue,
                                unfocusedLabelColor = Color.Gray,
                                cursorColor = Color.Blue,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black
                            ),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    keyboardController?.hide()
                                }
                            )
                        )

                        Button(
                            onClick = {
                                openDialog.value = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = LightBlue,
                                contentColor = Color.Black
                            )
                        ) {
                            Text("Conferma")
                        }
                        if (openDialog.value) {
                            AlertDialog(
                                onDismissRequest = { openDialog.value = false },
                                title = { Text("Conferma Alimento") },
                                text = { Text("Sei sicuro di voler inserire questo alimento?") },
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
                                                scope.launch {
                                                    selectedTaskForEdit.value?.let {
                                                        alimenti.rimuoviItem(listOf(it))
                                                    }

                                                    if (newItem.value.isNotBlank()) {
                                                        alimenti.aggiungiItem(newItem.value, uid.toString())
                                                    }

                                                    newItem.value = ""
                                                    selectedTaskForEdit.value = null
                                                    navController.navigate("shoppingList")
                                                }
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
                }

                LaunchedEffect(showTextField.value) {
                    if (showTextField.value) {
                        focusRequester.requestFocus()
                        keyboardController?.show()
                    }
                }
            }


        }
    )
}







@Composable
fun shoppingListAdvanced(navController: NavController) {
    val ivory = ivory
    val shopList = remember{ ShopList() }
    var items by remember { mutableStateOf<List<String>>(emptyList())}
    var isLoading by remember { mutableStateOf(true)}
    var scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        items = shopList.ListaSpesa()
        isLoading = false
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, Color.LightGray, shape = RoundedCornerShape(20.dp))
            .background(ivory)
    )
    {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            when {
                isLoading -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = Color.Gray)
                    }
                }
                items.isEmpty() -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Nessuna cosa da comprare",
                            color = Color.Black,
                            fontSize = 16.sp,

                            )
                    }
                }
                else -> {
                    items.forEach { task ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 25.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("•", color = Color.Black, modifier = Modifier.padding(end = 8.dp))
                            Text(task, color = Color.Black)
                        }
                    }
                }
            }
        }
    }
    Column (modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 25.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            modifier = Modifier.padding(top = 10.dp),
            onClick = { scope.launch{shopList.spesaEffettuata()
                            navController.navigate("shoppingList")}
                         },
            colors = ButtonDefaults.buttonColors(containerColor = LightBlue),
            )
        {
            Text(
                text = "Spesa Effettuata",
                color = Color.Black)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShoppingListScreenPreview() {
   val navController = rememberNavController()
    ShoppingListScreen(navController = navController)

}