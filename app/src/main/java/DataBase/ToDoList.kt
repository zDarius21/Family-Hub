package DataBase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ToDoList {

    suspend fun aggiungiToDo(
        descrizione: String,
        uid: String,
    ) {
        try {
            val db = FirebaseFirestore.getInstance()
            val famiglia = Famiglia()

            val idUnicoFamiglia = famiglia.TrovaCodiceFamiglia(uid)

            val toDo = hashMapOf(
                "descrizione" to descrizione,
                "uid" to uid,
                "idUnicoFamiglia" to idUnicoFamiglia,
            )

            db.collection("toDo").add(toDo).await()
            Log.d("Firebase", "ToDo aggiunta con successo")

        } catch (e: Exception) {
            Log.e("Firebase", "Errore aggiunta ToDo: ${e.message}")
        }
    }

    suspend fun ListaToDo(): MutableList<String> {
        val lista = mutableListOf<String>()
        try {
            val db = FirebaseFirestore.getInstance()
            val famiglia = Famiglia()
            val auth = FirebaseAuth.getInstance()
            val user = auth.currentUser
            val uid = user!!.uid
            val idUnicoFamiglia = famiglia.TrovaCodiceFamiglia(uid)

            val snapshot = db.collection("toDo")
                .whereEqualTo("idUnicoFamiglia", idUnicoFamiglia)
                .get()
                .await()

            for (document in snapshot.documents) {
                val dati = document.data
                val descrizione = dati?.get("descrizione") as? String ?: continue
                lista.add(descrizione)
            }

            Log.d("Firebase", "toDo caricate con successo")

        } catch (e: Exception) {
            Log.e("Firebase", "Errore nel caricamento ToDoList: ${e.message}")
        }
        return lista
    }

    suspend fun rimuoviToDo(descrizioni: List<String>) {
        try {
            val db = FirebaseFirestore.getInstance()
            val famiglia = Famiglia()
            val auth = FirebaseAuth.getInstance()
            val user = auth.currentUser
            val uid = user?.uid ?: return

            val idUnicoFamiglia = famiglia.TrovaCodiceFamiglia(uid)

            for (descrizione in descrizioni) {
                val snapshot = db.collection("toDo")
                    .whereEqualTo("idUnicoFamiglia", idUnicoFamiglia)
                    .whereEqualTo("descrizione", descrizione)
                    .get()
                    .await()

                for (document in snapshot.documents) {
                    db.collection("toDo").document(document.id).delete().await()
                }

                Log.d("Firebase", "ToDo con descrizione \"$descrizione\" rimossi con successo.")
            }

        } catch (e: Exception) {
            Log.e("Firebase", "Errore nella rimozione della ToDo: ${e.message}")
        }
    }



}