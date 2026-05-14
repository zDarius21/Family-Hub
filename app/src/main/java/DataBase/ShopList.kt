package DataBase

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import view.SpesaStorica
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ShopList {

    suspend fun aggiungiItem(
        nome: String,
        uid: String,
    ) {
        try {
            val db = FirebaseFirestore.getInstance()
            val famiglia = Famiglia()

            val idUnicoFamiglia = famiglia.TrovaCodiceFamiglia(uid)

            val alimenti = hashMapOf(
                "nome" to nome,
                "idUnicoFamiglia" to idUnicoFamiglia,
            )

            db.collection("alimenti").add(alimenti).await()
            Log.d("Firebase", "Alimento aggiunto con successo")

        } catch (e: Exception) {
            Log.e("Firebase", "Errore durante l'aggiunta dell'alimento: ${e.message}")
        }
    }

    suspend fun ListaSpesa(): MutableList<String> {
        val lista = mutableListOf<String>()
        try {
            val db = FirebaseFirestore.getInstance()
            val famiglia = Famiglia()
            val auth = FirebaseAuth.getInstance()
            val user = auth.currentUser
            val uid = user!!.uid
            val idUnicoFamiglia = famiglia.TrovaCodiceFamiglia(uid)

            val snapshot = db.collection("alimenti")
                .whereEqualTo("idUnicoFamiglia", idUnicoFamiglia)
                .get()
                .await()

            for (document in snapshot.documents) {
                val dati = document.data
                val nome = dati?.get("nome") as? String ?: continue
                lista.add(nome)
            }

            Log.d("Firebase", "Lista degli alimenti caricata con successo")

        } catch (e: Exception) {
            Log.e("Firebase", "Errore nel caricamento della lista spesa: ${e.message}")
        }
        return lista
    }

    suspend fun rimuoviItem(nomi: List<String>) {
        try {
            val db = FirebaseFirestore.getInstance()
            val famiglia = Famiglia()
            val auth = FirebaseAuth.getInstance()
            val user = auth.currentUser
            val uid = user?.uid ?: return

            val idUnicoFamiglia = famiglia.TrovaCodiceFamiglia(uid)

            for (nome in nomi) {
                val snapshot = db.collection("toDo")
                    .whereEqualTo("idUnicoFamiglia", idUnicoFamiglia)
                    .whereEqualTo("nome", nome)
                    .get()
                    .await()

                for (document in snapshot.documents) {
                    db.collection("alimenti").document(document.id).delete().await()
                }

                Log.d("Firebase", "Lista alimenti con descrizione \"$nome\" rimossi con successo.")
            }

        } catch (e: Exception) {
            Log.e("Firebase", "Errore nella rimozione dell'alimento: ${e.message}")
        }
    }

    suspend fun spesaEffettuata() {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser ?: return
        val uid = currentUser.uid

        try {
            // Ottieni i dati utente per recuperare nome e famiglia
            val userSnapshot = db.collection("users").document(uid).get().await()
            val nomeUtente = userSnapshot.getString("nome") ?: "Sconosciuto"
            val famigliaId = Famiglia().TrovaCodiceFamiglia(uid)

            // Ottieni tutti gli alimenti della famiglia
            val alimentiSnapshot = db.collection("alimenti")
                .whereEqualTo("idUnicoFamiglia", famigliaId)
                .get().await()

            Log.d("spesaEffettuata", "Famiglia ID: $famigliaId")
            Log.d("spesaEffettuata", "User: $nomeUtente")

            // Se non ci sono alimenti, interrompi la funzione
            if (alimentiSnapshot.isEmpty) {
                Log.d("spesaEffettuata", "Nessun alimento da acquistare. Operazione annullata.")
                return
            }

            // Ottieni la lista di nomi alimenti e prepara batch delete
            val alimentiAcquistati = mutableListOf<String>()
            val batch = db.batch()

            for (document in alimentiSnapshot.documents) {
                val nome = document.getString("nome")
                if (nome != null) {
                    alimentiAcquistati.add(nome)
                    batch.delete(document.reference)
                }
            }

            // Esegui batch delete
            batch.commit().await()

            // Crea documento per storico
            val storicoData = hashMapOf(
                "alimenti" to alimentiAcquistati,
                "data" to Timestamp(Date()),
                "nomeUtente" to nomeUtente,
                "idUnicoFamiglia" to famigliaId
            )

            db.collection("storicoSpesa").add(storicoData).await()
            Log.d("spesaEffettuata", "Spesa registrata nello storico.")
        } catch (e: Exception) {
            Log.e("ShopList", "Errore in spesaEffettuata: ${e.message}", e)
        }
    }

    suspend fun caricaStorico(uid: String): Map<String, List<SpesaStorica>> {
        val db = FirebaseFirestore.getInstance()

        val famigliaId = withContext(Dispatchers.IO) {
            Famiglia().TrovaCodiceFamiglia(uid)
        }

        val snapshot = db.collection("storicoSpesa")
            .whereEqualTo("idUnicoFamiglia", famigliaId)
            .get()
            .await()

        val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.ITALY)

        val spese = snapshot.documents.mapNotNull { doc ->
            val data = doc.getTimestamp("data")?.toDate()
            val alimenti = doc.get("alimenti") as? List<String>
            val nomeUtente = doc.getString("nomeUtente") ?: "Sconosciuto"
            if (data != null && alimenti != null) {
                SpesaStorica(data, alimenti, nomeUtente)
            } else null
        }.sortedByDescending { it.data }

        return spese.groupBy { formatter.format(it.data) }
    }






}