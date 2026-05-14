
# Family-Hub

**Descrizione**
Family-Hub è un'app Android per la gestione collaborativa della vita familiare: attività condivise, liste della spesa, transazioni fra membri, promemoria e tracking della posizione. Il progetto è scritto in Kotlin e può integrare Firebase.

**Caratteristiche principali**
- Gestione della famiglia (crea/entra in una famiglia, gestisci membri)
- To‑Do & Attività condivise
- Liste della spesa collaborative
- Tracciamento transazioni e storico
- Notifiche pianificate e worker (vedi `app/src/main/java/Notifiche/Worker.kt`)
- Servizio foreground per posizione (vedi `app/src/main/java/Posizione/LocationForegroundService.kt`)

**Download APK**
L'APK per test o installazione manuale è incluso nel repository:

- Scarica l'APK: [Family-Hub.apk](Family-Hub.apk)

**Screenshot**

Le immagini nella cartella `screenshots/` mostrano le schermate principali dell'app:

- **Home**: schermata iniziale con accesso alle funzioni principali dell'app.
	![Home](screenshots/Home.jpeg)
- **ToDo**: sezione dedicata alla gestione delle attività condivise.
	![ToDo](screenshots/ToDo.jpeg)
- **Lista della spesa**: schermata per creare e consultare la lista della spesa condivisa.
	![Lista della spesa](screenshots/ListaSpesa.jpeg)
- **Spese**: area dedicata alla visualizzazione e gestione delle spese/transazioni.
	![Spese](screenshots/Spese.jpeg)

**Build & Esecuzione**

Usa il Gradle wrapper dalla root del progetto:


