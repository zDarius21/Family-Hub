
import DataBase.Attivita.AttivitaConNome
import org.junit.Test
import kotlin.test.assertEquals

class AttivitaConNomeTest {

    @Test
    fun `creazione oggetto AttivitaConNome con tutti i parametri`() {
        val attivita = AttivitaConNome(
            nomeUtente = "Mario Rossi",
            tipo = "Lavoro",
            descrizione = "Scrivere report",
            ora = "09:00",
            coloreUtente = "Blu",
            uid = "user123",
            data = "2025-07-09"
        )

        assertEquals("Mario Rossi", attivita.nomeUtente)
        assertEquals("Lavoro", attivita.tipo)
        assertEquals("Scrivere report", attivita.descrizione)
        assertEquals("09:00", attivita.ora)
        assertEquals("Blu", attivita.coloreUtente)
        assertEquals("user123", attivita.uid)
        assertEquals("2025-07-09", attivita.data)
    }


}
