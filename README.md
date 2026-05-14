# Family-Hub

**Descrizione**: Family-Hub è un'app Android pensata per aiutare le famiglie a collaborare quotidianamente: gestisce attività condivise, liste della spesa, transazioni economiche tra membri, promemoria e traccia le passeggiate/posizione quando richiesto. L'app è costruita in Kotlin e usa componenti Android standard, insieme a Firebase per l'autenticazione e il backing store (configurazione via `google-services.json`).

**Caratteristiche principali**
- **Gestione Famiglia**: creare/entrare in una famiglia e gestire i membri.
- **To‑Do & Attività**: aggiungere, modificare e completare attività condivise.
- **Lista della Spesa**: creare e aggiornare liste della spesa condivise.
- **Transazioni**: tracciare entrate/uscite e visualizzare lo storico.
- **Notifiche e Worker**: task pianificati per notifiche locali (classe [app/src/main/java/Notifiche/Worker.kt](app/src/main/java/Notifiche/Worker.kt)).
- **Servizio in primo piano per la posizione**: supporto per raccogliere la posizione in foreground ([app/src/main/java/Posizione/LocationForegroundService.kt](app/src/main/java/Posizione/LocationForegroundService.kt)).

**Architettura e moduli rilevanti**
- **UI / View**: tutte le Activity principali sono in [app/src/main/java/com/example/family_hub/view/](app/src/main/java/com/example/family_hub/view/). Esempi: [Home.kt](app/src/main/java/com/example/family_hub/view/Home.kt), [Famiglia.kt](app/src/main/java/com/example/family_hub/view/Famiglia.kt), [Spesa.kt](app/src/main/java/com/example/family_hub/view/Spesa.kt).
- **Database / Modelli**: entità e modelli locali si trovano in [app/src/main/java/DataBase/](app/src/main/java/DataBase/): `Attivita.kt`, `Famiglia.kt`, `ShopList.kt`, `ToDoList.kt`, `Transazione.kt`.
- **Servizi di background**: worker e servizi in [app/src/main/java/Notifiche/](app/src/main/java/Notifiche/) e [app/src/main/java/Posizione/](app/src/main/java/Posizione/).

**Requisiti**
- Java JDK 11+ (consigliato)
- Android Studio (versione compatibile con il progetto Gradle)
- Account Firebase (se vuoi usare le feature Firebase già previste)

**Configurazione locale**
1. Assicurati di avere installato Git e Android Studio.
2. Posiziona il file `google-services.json` nella cartella `app/` se usi Firebase.
3. Controlla `local.properties` per il percorso del SDK Android (Android Studio lo genera automaticamente).

**Build & esecuzione**
Nella cartella principale del progetto puoi usare Gradle wrapper:

```powershell
./gradlew assembleDebug
./gradlew installDebug
```

Oppure apri il progetto in Android Studio e clicca Run per eseguire su emulatore o dispositivo.

**Note su permessi e privacy**
- L'app richiede permessi di posizione se usi il servizio in foreground; assicurati di gestire i permessi a runtime (vedi [LocationForegroundService.kt](app/src/main/java/Posizione/LocationForegroundService.kt)).
- I dati sensibili (es. credenziali, chiavi) non dovrebbero essere committati nel repo. Mantieni `google-services.json` fuori da commit pubblico se contiene dati sensibili.

**Testing**
- I test di integrazione/unità sono sotto `app/src/androidTest/` e `app/src/test/`. Esegui i test tramite Android Studio o usando Gradle:

```powershell
./gradlew test
./gradlew connectedAndroidTest
```

**Contribuire**
- Apri un branch per la modifica: `git checkout -b feat/nome-caratteristica`.
- Crea commit chiari e fai una pull request verso `main`.

**File utili**
- [app/build.gradle.kts](app/build.gradle.kts) : configurazione Gradle del modulo app.
- [settings.gradle.kts](settings.gradle.kts) : impostazioni workspace.

**Contatti e autore**
- Repository: https://github.com/zDarius21/Family-Hub

---
Se vuoi, posso aggiungere una sezione `README` in italiano/inglese più breve, oppure rigenerare un `README.md` più visuale con screenshot e badge CI: dimmi quale preferisci.