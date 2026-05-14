package view

import DataBase.ToDoList
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.family_hub.R
import com.example.family_hub.ui.theme.BluCheckBox
import com.example.family_hub.ui.theme.ColoreTastoSelezionato
import com.example.family_hub.ui.theme.GrigioScritta
import com.example.family_hub.ui.theme.LightBlue
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


data class ListItem(
    val name: String,
    var isChecked: Boolean = false
)



@Composable
fun ModificaToDoScreen(navController: NavController){
    val scrollState = rememberScrollState()
    val fabVisible = remember { mutableStateOf(true) }
    var itemList by remember { mutableStateOf<List<ListItem>>(emptyList()) }
    val showTextField = remember { mutableStateOf(false) }
    val newTask = remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val toDo = ToDoList()
    val scope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val uid = user?.uid
    val openDialog = remember { mutableStateOf(false) }
    val selectedTaskForEdit = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        scope.launch {
            val listaTesti = toDo.ListaToDo()
            itemList = listaTesti.map { ListItem(name = it) }
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
                            tint = ColoreTastoSelezionato,
                            contentDescription = "TODO"
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "To Do List", fontSize = 9.sp, color = ColoreTastoSelezionato)
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
                ){
                    Icon(Icons.Filled.Add, "Floating action button.", tint= Color.Black)}
            }},
            content = { innerPadding ->
                Box(modifier = Modifier
                    .background(color = GrigioScritta)
                    .padding(innerPadding).clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        focusManager.clearFocus()
                    }) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)

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
                            ModificaToDo(
                                items = itemList,
                                navController = navController,
                                newTask = newTask,
                                showTextField = showTextField,
                                selectedTaskForEdit = selectedTaskForEdit,
                                )
                        }

                        Spacer(modifier = Modifier.height(1.dp).background(color = Color.LightGray).fillMaxWidth())
                        Spacer(modifier = Modifier.height(20.dp))

                        Column(
                            modifier = Modifier

                                .verticalScroll(scrollState)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CheckboxList(items = itemList) { index, isChecked ->
                                itemList = itemList.toMutableList().also {
                                    it[index] = it[index].copy(isChecked = isChecked)
                                }
                            }
                        }
                        Spacer(
                            modifier = Modifier
                                .height(10.dp)
                                .padding(innerPadding)
                        )
                        AnimatedVisibility(visible = showTextField.value) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                OutlinedTextField(
                                    value = newTask.value,
                                    onValueChange = { newTask.value = it },
                                    label = { Text("Inserisci una nuova attività", color = Color.Gray) },
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
                                        title = { Text("Conferma ToDO") },
                                        text = { Text("Sei sicuro di voler inserire la ToDo?") },
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
                                                                toDo.rimuoviToDo(listOf(it))
                                                            }

                                                            if (newTask.value.isNotBlank()) {
                                                                toDo.aggiungiToDo(newTask.value, uid.toString())
                                                            }

                                                            newTask.value = ""
                                                            selectedTaskForEdit.value = null
                                                            navController.navigate("todo")
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

            }
        )
}

@Composable
fun ModificaToDo(
    items: List<ListItem>,
    navController: NavController,
    newTask: MutableState<String>,
    showTextField: MutableState<Boolean>,
    selectedTaskForEdit: MutableState<String?>
) {
    val toDo = ToDoList()
    val scope = rememberCoroutineScope()
    val checkedItems = items.filter { it.isChecked }
    val counter = checkedItems.size

    Row(modifier = Modifier.padding(end = 10.dp)) {
        if (counter >= 2) {
            IconButton(onClick = {
                scope.launch {
                    toDo.rimuoviToDo(checkedItems.map { it.name })
                    navController.navigate("todo")
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Conferma",
                    tint = Color.Black
                )
            }
        }
    }

    if (counter == 1) {
        Row(modifier = Modifier.padding(end = 10.dp)) {
            IconButton(onClick = {
                selectedTaskForEdit.value = checkedItems.first().name
                newTask.value = checkedItems.first().name
                showTextField.value = true
            }) {
                Icon(
                    painter = painterResource(R.drawable.baseline_mode_24),
                    contentDescription = "Modifica",
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            IconButton(onClick = {
                scope.launch {
                    toDo.rimuoviToDo(checkedItems.map { it.name })
                    navController.navigate("todo")
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Conferma",
                    tint = Color.Black
                )
            }
        }
    }
}




@Composable
fun CheckboxList(
    items: List<ListItem>,
    onCheckedChange: (Int, Boolean) -> Unit
) {
    Column(modifier = Modifier.padding(8.dp)) {
        items.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = item.isChecked,
                    onCheckedChange = { isChecked -> onCheckedChange(index, isChecked) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = BluCheckBox,
                        uncheckedColor = Color.Gray,
                        checkmarkColor = Color.Black,
                        disabledCheckedColor = Color.LightGray,
                        disabledUncheckedColor = Color.LightGray
                    )
                )
                Text(
                    text = item.name,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(start = 8.dp),
                    color = Color.Black
                )
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun ModificaToDoScreenPreview() {
    val navController = rememberNavController()
    ModificaToDoScreen(navController = navController)
}
