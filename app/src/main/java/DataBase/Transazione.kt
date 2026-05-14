
import DataBase.Famiglia
import android.util.Log
import androidx.compose.ui.graphics.Color
import com.example.family_hub.ui.theme.ColoreAlimentari
import com.example.family_hub.ui.theme.ColoreAltro
import com.example.family_hub.ui.theme.ColoreBollette
import com.example.family_hub.ui.theme.ColoreCasa
import com.example.family_hub.ui.theme.ColoreShopping
import com.example.family_hub.ui.theme.ColoreSvago
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import view.tipologie
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class Transazione {

    data class Spesa(
        val descrizione: String = "",
        val giorno: String = "",
        val idUnicoFamiglia: String = "",
        val importo: Double = 0.0,
        val negozio: String = "",
        val tipologia: String = "",
        val utente: String = ""
    )



    suspend fun aggiungiTransazione(
        negozio: String,
        tipologia: String,
        importo: Double?,
        descrizione: String,
        uid: String,
    ) {
        try {
            val db = FirebaseFirestore.getInstance()
            val famiglia = Famiglia()
            val calendar = Calendar.getInstance()
            val giorno = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

            val idUnicoFamiglia = famiglia.TrovaCodiceFamiglia(uid)

            val transazione = hashMapOf(
                "negozio" to negozio,
                "tipologia" to tipologia,
                "importo" to importo,
                "descrizione" to descrizione,
                "uid" to uid,
                "idUnicoFamiglia" to idUnicoFamiglia,
                "giorno" to giorno
            )

            db.collection("transazioni").add(transazione).await()
            Log.d("Firebase", "Transazione aggiunta con successo")

        } catch (e: Exception) {
            Log.e("Firebase", "Errore aggiunta transazione: ${e.message}")
        }
    }

    suspend fun ListaTransazioni(): MutableList<Spesa> {
        val spese = mutableListOf<Spesa>()
        try {
            val db = FirebaseFirestore.getInstance()
            val famiglia = Famiglia()
            val auth = FirebaseAuth.getInstance()
            val user = auth.currentUser
            val uid = user!!.uid
            val idUnicoFamiglia = famiglia.TrovaCodiceFamiglia(uid)

            val snapshot = db.collection("transazioni")
                .whereEqualTo("idUnicoFamiglia", idUnicoFamiglia)
                .get()
                .await()

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val oggi = Calendar.getInstance().time
            val trentaGiorniFa = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -30)
            }.time

            for (document in snapshot.documents) {
                val dati = document.data
                val uidUtente = dati?.get("uid") as? String ?: continue
                val giornoStr = dati["giorno"] as? String ?: continue

                val giornoDate = try {
                    dateFormat.parse(giornoStr)
                } catch (e: Exception) {
                    null
                }

                if (giornoDate == null || giornoDate.before(trentaGiorniFa)) continue

                val nomeUtente = ottieniNomeUtente(uidUtente)

                val spesa = Spesa(
                    utente = nomeUtente,
                    negozio = dati["negozio"] as? String ?: "",
                    tipologia = dati["tipologia"] as? String ?: "",
                    importo = (dati["importo"] as? Number)?.toDouble() ?: 0.0,
                    descrizione = dati["descrizione"] as? String ?: "",
                    idUnicoFamiglia = dati["idUnicoFamiglia"] as? String ?: "",
                    giorno = giornoStr
                )
                spese.add(spesa)
            }

            spese.sortByDescending {
                try {
                    dateFormat.parse(it.giorno)
                } catch (e: Exception) {
                    Date(0)
                }
            }

            Log.d("Firebase", "Transazioni caricate con successo")

        } catch (e: Exception) {
            Log.e("Firebase", "Errore nel caricamento transazioni: ${e.message}")
        }
        return spese
    }




    suspend fun calcoloTotale(): Double {
        val spese = ListaTransazioni()
        val totale = spese.sumOf { it.importo }
        val arrotondato = BigDecimal(totale).setScale(2, RoundingMode.HALF_UP).toDouble()
        return arrotondato
    }


    suspend fun calcoloGrafico(): List<tipologie> {
        val spese = ListaTransazioni()
        val totale = calcoloTotale()
        val listaTipologia = mutableListOf<tipologie>()

        if (totale <= 0) return emptyList()

        val spesePerTipologia = spese
            .groupBy { it.tipologia }
            .toList()
            .sortedWith(compareBy(
                { if (it.first == "Altro") "ZZZ" else it.first }
            ))

        for ((tipologia, gruppo) in spesePerTipologia) {
            val somma = gruppo.sumOf { it.importo }
            val percentuale = (somma / totale).toFloat()
            val colore = when (tipologia) {
                "Shopping" -> ColoreShopping
                "Casa" -> ColoreCasa
                "Altro" -> ColoreAltro
                "Svago" -> ColoreSvago
                "Bollette" -> ColoreBollette
                "Alimentari" -> ColoreAlimentari
                else -> Color.Gray
            }


            if (percentuale > 0) {
                listaTipologia.add(
                    tipologie(
                        percentuale = percentuale,
                        color = colore,
                        tipologia = tipologia
                    )
                )
            }
        }
        return listaTipologia
    }

    suspend fun ottieniNomeUtente(uid: String): String {
        return try {
            val db = FirebaseFirestore.getInstance()
            val snapshot = db.collection("users").document(uid).get().await()
            snapshot.getString("nome") ?: "Utente Sconosciuto"
        } catch (e: Exception) {
            "Errore"
        }
    }



}
