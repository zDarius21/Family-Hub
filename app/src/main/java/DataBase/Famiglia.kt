package DataBase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import view.UserAccount
import kotlin.random.Random


class Famiglia {
    var db = FirebaseFirestore.getInstance()
    var famiglieCollection = db.collection("famiglie")


    suspend fun CreazioneFamiglia(nomeFamiglia: String, uid: String, colore: String) {
        try {
            var idUnico: String
            var docSnapshot: com.google.firebase.firestore.DocumentSnapshot

            do {
                idUnico = Random.nextInt(10000000, 99999999).toString()
                docSnapshot = famiglieCollection.document(idUnico).get().await()
            } while (docSnapshot.exists())

            val famigliaData = hashMapOf(
                "nomeFamiglia" to nomeFamiglia,
                "utenti" to listOf(
                    mapOf(
                        "uid" to uid,
                        "colore" to colore,
                        "isAdmin" to true
                    )
                )
            )

            famiglieCollection.document(idUnico).set(famigliaData).await()

            Log.d("Firebase", "Famiglia creata con successo: $idUnico")

        } catch (e: Exception) {
            Log.e("Firebase", "Errore creazione famiglia: ${e.message}")
        }
    }


    suspend fun ControlloFamiglia(codice: String): Boolean {
        return try {
            val document = db.collection("famiglie").document(codice).get().await()
            document.exists()
        } catch (e: Exception) {
            false
        }
    }


    suspend fun getColoriUtilizzati(codiceFamiglia: String): List<String> {
        return try {
            val doc = FirebaseFirestore.getInstance()
                .collection("famiglie")
                .document(codiceFamiglia)
                .get()
                .await()

            val utenti = doc.get("utenti") as? List<Map<String, Any>>
            utenti?.mapNotNull { utente ->
                (utente["colore"] as? String)?.lowercase()
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }




    fun aggiungiUtenteAFamiglia(
        codiceFamiglia: String,
        uid: String,
        colore: String,
        onResult: (Boolean) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        val utenteData = mapOf(
            "uid" to uid,
            "colore" to colore,
            "isAdmin" to false
        )

        db.collection("famiglie")
            .document(codiceFamiglia)
            .update("utenti", FieldValue.arrayUnion(utenteData))
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    suspend fun TrovaFamiglia(uidUtente: String): String {
        try {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("famiglie")
                .get()
                .await()

            for (document in snapshot.documents) {
                val utenti = document.get("utenti") as? List<Map<String, Any>>
                if (utenti != null) {
                    for (utente in utenti) {
                        val uid = utente["uid"] as? String
                        if (uid == uidUtente) {
                            return document.getString("nomeFamiglia") ?: "Sconosciuto"
                        }
                    }
                }
            }

            return "Sconosciuto"

        } catch (e: Exception) {
            return "Errore"
        }
    }


    suspend fun TrovaCodiceFamiglia(uidUtente: String): String {
        try {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("famiglie")
                .get()
                .await()
            for (document in snapshot.documents) {
                val utenti = document.get("utenti") as? List<Map<String, Any>>
                utenti?.forEach { utente ->
                    val uid = utente["uid"] as? String
                    if (uid == uidUtente) {
                        return document.id
                    }
                }
            }

            return "Sconosciuto"

        } catch (e: Exception) {
            return "Errore"
        }
    }



    suspend fun caricaDatiUtente(uid: String): UserAccount? {
        val db = FirebaseFirestore.getInstance()
        return try {
            val snapshot = db.collection("users").document(uid).get().await()
            if (snapshot.exists()) {
                snapshot.toObject(UserAccount::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun modificaDatiUtente(
        uid: String,
        nome: String,
        cognome: String,
        dataNascita: String,
        email: String,
        numeroTelefono: String,
        username: String,
        password: String
    ): Boolean {
        return try {
            val datiAggiornati = mapOf(
                "nome" to nome,
                "cognome" to cognome,
                "dataNascita" to dataNascita,
                "email" to email,
                "numeroTelefono" to numeroTelefono,
                "username" to username,
                "password" to password
            )
            db.collection("users").document(uid).update(datiAggiornati).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun caricaMembriFamiglia(uidUtente: String): List<UserAccount>? {
        return try {
            val codiceFamiglia = TrovaCodiceFamiglia(uidUtente)
            if (codiceFamiglia == "Errore" || codiceFamiglia == "Sconosciuto") return null

            val famigliaDoc = FirebaseFirestore.getInstance()
                .collection("famiglie")
                .document(codiceFamiglia)
                .get()
                .await()

            val utentiList = famigliaDoc.get("utenti") as? List<Map<String, Any>> ?: return null

            val db = FirebaseFirestore.getInstance()
            val membri = mutableListOf<UserAccount>()

            for (utente in utentiList) {
                val uid = utente["uid"] as? String ?: continue
                val colore = utente["colore"] as? String

                val userDoc = db.collection("users").document(uid).get().await()
                val user = userDoc.toObject(UserAccount::class.java)
                if (user != null) {
                    membri.add(user.copy(uid = userDoc.id, colore = colore))
                }
            }

            membri

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }



    suspend fun rimuoviMembroDaFamiglia(uidUtente: String): Boolean {
        return try {
            val codiceFamiglia = TrovaCodiceFamiglia(uidUtente)
            if (codiceFamiglia == "Errore" || codiceFamiglia == "Sconosciuto") return false
            val auth = FirebaseAuth.getInstance()
            val user = auth.currentUser
            val uid = user?.uid

            if(uid == uidUtente) return false

            FirebaseFirestore.getInstance()
                .collection("famiglie")
                .document(codiceFamiglia)
                .collection("membri")
                .document(uidUtente)
                .delete()
                .await()

            val famigliaRef = FirebaseFirestore.getInstance()
                .collection("famiglie")
                .document(codiceFamiglia)

            val famigliaDoc = famigliaRef.get().await()
            val utenti = famigliaDoc.get("utenti") as? MutableList<Map<String, Any>> ?: mutableListOf()

            val nuoviUtenti = utenti.filterNot {
                it["uid"] == uidUtente
            }

            famigliaRef.update("utenti", nuoviUtenti).await()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun assegnaPermessi(uidUtente: String, isAdmin: Boolean = true): Boolean {
        return try {
            val codiceFamiglia = TrovaCodiceFamiglia(uidUtente)
            if (codiceFamiglia == "Errore" || codiceFamiglia == "Sconosciuto") return false

            val famigliaRef = FirebaseFirestore.getInstance()
                .collection("famiglie")
                .document(codiceFamiglia)

            val famigliaDoc = famigliaRef.get().await()

            val utenti = famigliaDoc.get("utenti") as? List<Map<String, Any>> ?: return false

            val nuoviUtenti = utenti.map { utente ->
                if (utente["uid"] == uidUtente) {
                    utente.toMutableMap().apply { this["isAdmin"] = isAdmin }
                } else {
                    utente
                }
            }

            famigliaRef.update("utenti", nuoviUtenti).await()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun isUtenteAdmin(uid: String): Boolean {
        return try {
            val codiceFamiglia = TrovaCodiceFamiglia(uid)
            if (codiceFamiglia == "Errore" || codiceFamiglia == "Sconosciuto") return false

            val famigliaDoc = FirebaseFirestore.getInstance()
                .collection("famiglie")
                .document(codiceFamiglia)
                .get()
                .await()

            val utenti = famigliaDoc.get("utenti") as? List<Map<String, Any>> ?: emptyList()
            val utente = utenti.find { it["uid"] == uid }

            (utente?.get("isAdmin") as? Boolean) == true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }




}


