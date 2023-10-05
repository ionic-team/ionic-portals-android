package io.ionic.portals.composetestapp

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.web.assertion.WebViewAssertions.webMatches
import androidx.test.espresso.web.model.Atoms.castOrDie
import androidx.test.espresso.web.model.Atoms.script
import androidx.test.espresso.web.sugar.Web
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InitialContextTests {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun verify_initial_context_is_present__when_portal_loads() {
        composeTestRule.waitForIdle()

        val script = script("return window.AndroidInitialContext.initialContext();", castOrDie(String::class.java))
        Web.onWebView().check(webMatches(script, CoreMatchers.containsString("testportal")))
    }

}