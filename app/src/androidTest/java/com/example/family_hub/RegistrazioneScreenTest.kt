package com.example.family_hub
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class RegisterScreenEspressoTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    @Test
    fun testRegisterScreen_inputsAndButton() {
        composeTestRule.onNodeWithText("Non hai un account? Registrati").performClick()
        with(composeTestRule) {
            onNodeWithText("Username").assertExists()
            onNodeWithText("Username").performTextInput("testuser")
            onNodeWithText("Nome").performTextInput("Mario")
            onNodeWithText("Cognome").performTextInput("Rossi")
            onNodeWithText("Email").performTextInput("test@example.com")
            onNodeWithText("Numero di telefono").performTextInput("1234567890")
            onNodeWithText("Password*").performTextInput("Password1")
            onNodeWithText("Remember me").performClick()
            onNodeWithTag("registerButton").performClick()
        }
    }
}

