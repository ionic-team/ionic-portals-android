package io.ionic.portals.composetestapp

import android.content.Intent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.web.assertion.WebViewAssertions.webMatches
import androidx.test.espresso.web.matcher.DomMatchers.containingTextInBody
import androidx.test.espresso.web.model.Atoms.castOrDie
import androidx.test.espresso.web.model.Atoms.script
import androidx.test.espresso.web.sugar.Web
import androidx.test.espresso.web.webdriver.DriverAtoms.findElement
import androidx.test.espresso.web.webdriver.DriverAtoms.getText
import androidx.test.espresso.web.webdriver.Locator
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.ionic.portals.PortalManager
import org.hamcrest.CoreMatchers.containsString
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InitialContextTests {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        ActivityScenario.launch(MainActivity::class.java).onActivity { activity ->
            activity.sendBroadcast(
                Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
            )
        };
    }

    @Test
    fun verify_web_content_is_displayed__when_portal_loads() {
        composeTestRule.waitForIdle()

        Web.onWebView()
            .withElement(findElement(Locator.CLASS_NAME, "container"))
            .check(webMatches(getText(), containsString("The web page loaded successfully.")))
    }

    @Test
    fun verify_initial_context_is_present__when_portal_loads() {
        composeTestRule.waitForIdle()

        val script = script("return window.AndroidInitialContext.initialContext();", castOrDie(String::class.java))
        Web.onWebView().check(webMatches(script, containsString("testportal")))
    }

    companion object {
        @JvmStatic
        @BeforeClass
        fun classSetUp() {
            PortalManager.register(BuildConfig.PORTALS_KEY)
        }
    }
}