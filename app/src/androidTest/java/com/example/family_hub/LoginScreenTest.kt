
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.family_hub.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
@RunWith(AndroidJUnit4::class)
class LoginScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    @Test
    fun testLoginScreen_showsInputsAndButton() {
        composeTestRule.onNodeWithText("Email o Username").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        composeTestRule.onNodeWithTag("loginButton").assertIsDisplayed()
    }
    @Test
    fun testLoginButton_emptyFields_showsError() {
        composeTestRule.onNodeWithTag("loginButton").performClick()
        composeTestRule.onNodeWithText("Inserisci email o username e password").assertIsDisplayed()
    }
    @Test
    fun testFillFieldsAndClickLogin() {
        composeTestRule.onNode(hasText("Email o Username") and hasSetTextAction()).performTextInput("test@example.com")
        composeTestRule.onNode(hasText("Password") and hasSetTextAction()).performTextInput("password123")
        composeTestRule.onNodeWithTag("loginButton").performClick()
    }
}
