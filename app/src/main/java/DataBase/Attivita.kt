package DataBase

import Transazione
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import view.AttivitaCane
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class Attivita {

    data class AttivitaConNome(
        val nomeUtente: String,
        val tipo: String,
        val descrizione: String,
        val ora: String,
        val coloreUtente: String?,
        val uid: String? = null,
        val data: String? = null
    )

    suspend fun aggiungiAttivita(data: String, ora: String, tipo: String, descrizione: String) {
        val db = Firebase.firestore
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user?.uid
        val famiglia = Famiglia()
        val idUnicoFamiglia = famiglia.TrovaCodiceFamiglia(uid.toString())

        val attivita = hashMapOf(
            "idUnicoFamiglia" to idUnicoFamiglia,
            "uid" to uid,
            "data" to data,
            "ora" to ora,
            "tipo" to tipo,
            "descrizione" to descrizione,
            "timestamp" to com.google.firebase.Timestamp.now(),
            "coordinate" to emptyList<Map<String, Double>>() // ← inizializza campo coordinate vuoto
        )

        db.collection("attivita")
            .add(attivita)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "Attività aggiunta con ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Errore durante l'aggiunta dell'attività", e)
            }
    }


    fun eliminaAttivita(attivitaConNome: AttivitaConNome?) {
        val db = Firebase.firestore

        if (attivitaConNome == null) {
            return
        }


        db.collection("attivita")
            .whereEqualTo("ora", attivitaConNome.ora)
            .whereEqualTo("tipo", attivitaConNome.tipo)
            .whereEqualTo("descrizione", attivitaConNome.descrizione)
            .whereEqualTo("uid", attivitaConNome.uid)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                } else {
                    for (document in documents) {
                        db.collection("attivita").document(document.id)
                            .delete()
                            .addOnSuccessListener {
                                Log.d("Firestore", "Attività rimossa con successo: ${document.id}")
                            }
                            .addOnFailureListener { e ->
                                Log.w("Firestore", "Errore durante la rimozione dell'attività", e)
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Errore durante la ricerca dell'attività", e)
            }
    }


    suspend fun prendiAttivitaPerData(data: String): List<AttivitaConNome> {
        val db = Firebase.firestore
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser ?: return emptyList()
        val uid = user.uid
        val famiglia = Famiglia()
        val idUnicoFamiglia = famiglia.TrovaCodiceFamiglia(uid)

        return try {
            val snapshot = db.collection("attivita")
                .whereEqualTo("data", data)
                .whereEqualTo("idUnicoFamiglia", idUnicoFamiglia)
                .get()
                .await()

            val famigliaDoc = db.collection("famiglie").document(idUnicoFamiglia).get().await()
            val datiFamiglia = famigliaDoc.data

            val utenti = datiFamiglia?.get("utenti") as? List<Map<String, Any>>

            val lista = snapshot.documents.mapNotNull { doc ->
                val dati = doc.data ?: return@mapNotNull null
                val uidUtente = dati["uid"] as? String ?: return@mapNotNull null
                val tipo = dati["tipo"] as? String ?: ""
                val descrizione = dati["descrizione"] as? String ?: ""
                val ora = dati["ora"] as? String ?: ""
                val transazione = Transazione()
                val nomeUtente = transazione.ottieniNomeUtente(uidUtente)

                val coloreUtente = utenti?.firstOrNull { utente ->
                    utente["uid"] == uidUtente
                }?.get("colore") as? String ?: "DefaultColore"

                AttivitaConNome(nomeUtente, tipo, descrizione, ora, coloreUtente, uidUtente)
            }

            lista.sortedBy { att ->
                val parts = att.ora.split(":")
                val hour = parts.getOrNull(0)?.toIntOrNull() ?: 0
                val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
                hour * 60 + minute
            }

        } catch (e: Exception) {
            Log.e("Firestore", "Errore durante la ricerca dell'attività", e)
            emptyList()
        }
    }

    suspend fun prendiAttivitaOggi(): List<AttivitaConNome> {
        val db = Firebase.firestore
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser ?: return emptyList()
        val uid = user.uid
        val famiglia = Famiglia()
        val idUnicoFamiglia = famiglia.TrovaCodiceFamiglia(uid)

        val dataOggi = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        return try {
            val snapshot = db.collection("attivita")
                .whereEqualTo("data", dataOggi)
                .whereEqualTo("idUnicoFamiglia", idUnicoFamiglia)
                .get()
                .await()

            val famigliaDoc = db.collection("famiglie").document(idUnicoFamiglia).get().await()
            val datiFamiglia = famigliaDoc.data
            val utenti = datiFamiglia?.get("utenti") as? List<Map<String, Any>>

            val lista = snapshot.documents.mapNotNull { doc ->
                val dati = doc.data ?: return@mapNotNull null
                val uidUtente = dati["uid"] as? String ?: return@mapNotNull null
                val tipo = dati["tipo"] as? String ?: ""
                val descrizione = dati["descrizione"] as? String ?: ""
                val ora = dati["ora"] as? String ?: ""
                val transazione = Transazione()
                val nomeUtente = transazione.ottieniNomeUtente(uidUtente)

                val coloreUtente = utenti?.firstOrNull { utente ->
                    utente["uid"] == uidUtente
                }?.get("colore") as? String ?: "DefaultColore"

                AttivitaConNome(nomeUtente, tipo, descrizione, ora, coloreUtente)
            }

            lista.sortedBy { att ->
                val parts = att.ora.split(":")
                val hour = parts.getOrNull(0)?.toIntOrNull() ?: 0
                val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
                hour * 60 + minute
            }.take(5)

        } catch (e: Exception) {
            Log.e("Firestore", "Errore durante la ricerca dell'attività", e)
            emptyList()
        }
    }


    suspend fun caricaAttivitaCanePassate(uid: String): List<AttivitaCane> {
        val db = FirebaseFirestore.getInstance()
        val idUnicoFamiglia = Famiglia().TrovaCodiceFamiglia(uid)
        return try {
            val snapshot = db.collection("attivita")
                .whereEqualTo("tipo", "Portare a spasso il cane")
                .whereEqualTo("idUnicoFamiglia", idUnicoFamiglia)
                .get()
                .await()

            println("Documenti trovati: ${snapshot.documents.size}")

            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            snapshot.documents.mapNotNull { doc ->
                println("Documento ID: ${doc.id}")

                val data = doc.getString("data")
                val ora = doc.getString("ora")
                val coordinateRaw = doc.get("coordinate")

                if (data == null) {
                    println("Data mancante in documento ${doc.id}")
                    return@mapNotNull null
                }

                if (coordinateRaw !is List<*>) {
                    println("Coordinate mancanti o formato errato in documento ${doc.id}")
                    return@mapNotNull null
                }

                val coordinateList = coordinateRaw.mapNotNull {
                    when (it) {
                        is Map<*, *> -> {
                            val lat = (it["lat"] ?: it["latitude"]) as? Double
                            val lon = (it["lon"] ?: it["longitude"]) as? Double
                            if (lat != null && lon != null) Pair(lat, lon)
                            else {
                                println("Coordinate parziali errate: $it in doc ${doc.id}")
                                null
                            }
                        }
                        else -> {
                            println("Elemento coordinate non è mappa in doc ${doc.id}: $it")
                            null
                        }
                    }
                }

                val dataAttivita = try {
                    formatter.parse(data)
                } catch (e: Exception) {
                    println("Errore parsing data in doc ${doc.id}: ${e.message}")
                    null
                }

                if (dataAttivita != null && dataAttivita.before(today)) {
                    println("Attività valida doc ${doc.id}")
                    AttivitaCane(data, ora ?: "", coordinateList) to dataAttivita
                } else {
                    println("Attività non passata (data >= oggi) o data null doc ${doc.id}")
                    null
                }
            }
                .filterNotNull()
                .sortedByDescending { it.second }  // ordina per data (Date) discendente
                .map { it.first }  // ritorna solo la lista di AttivitaCane
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun recuperaOraInizioPasseggiata(uid: String): String? {
        return try {
            val db = FirebaseFirestore.getInstance()
            val oggi = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

            val result = db.collection("attivita")
                .whereEqualTo("uid", uid)
                .whereEqualTo("data", oggi)
                .whereEqualTo("tipo", "Portare a spasso il cane")
                .get()
                .await()

            if (!result.isEmpty) {
                val ora = result.documents[0].getString("ora")
                Log.d("recuperaOraInizio", "Ora trovata: $ora")
                ora
            } else {
                Log.d("recuperaOraInizio", "Nessuna attività trovata per oggi")
                null
            }
        } catch (e: Exception) {
            Log.e("recuperaOraInizio", "Errore durante il recupero", e)
            null
        }
    }

}
