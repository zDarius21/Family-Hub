
import Transazione.Spesa
import junit.framework.TestCase.assertEquals
import org.junit.Test

class SpesaTest {

    @Test
    fun `creazione oggetto Spesa con valori di default`() {
        val spesa = Spesa()

        assertEquals("", spesa.descrizione)
        assertEquals("", spesa.giorno)
        assertEquals("", spesa.idUnicoFamiglia)
        assertEquals(0.0, spesa.importo)
        assertEquals("", spesa.negozio)
        assertEquals("", spesa.tipologia)
        assertEquals("", spesa.utente)
    }










    @Test
    fun `creazione oggetto Spesa con valori specificati`() {
        val spesa = Spesa(
            descrizione = "Pane",
            giorno = "2025-07-09",
            idUnicoFamiglia = "famiglia123",
            importo = 2.5,
            negozio = "Supermercato",
            tipologia = "Alimentari",
            utente = "utente456"
        )

        assertEquals("Pane", spesa.descrizione)
        assertEquals("2025-07-09", spesa.giorno)
        assertEquals("famiglia123", spesa.idUnicoFamiglia)
        assertEquals(2.5, spesa.importo)
        assertEquals("Supermercato", spesa.negozio)
        assertEquals("Alimentari", spesa.tipologia)
        assertEquals("utente456", spesa.utente)
    }


}
