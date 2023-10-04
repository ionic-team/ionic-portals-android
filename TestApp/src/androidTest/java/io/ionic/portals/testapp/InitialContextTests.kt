package io.ionic.portals.testapp

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.web.assertion.WebViewAssertions.webMatches
import androidx.test.espresso.web.model.Atoms.castOrDie
import androidx.test.espresso.web.model.Atoms.script
import androidx.test.espresso.web.sugar.Web.onWebView
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.ionic.portals.PortalManager
import org.hamcrest.CoreMatchers.containsString
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InitialContextTests {
    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setUp() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun verify_initial_context_is_present__when_portal_loads() {
        val script = script("return window.AndroidInitialContext.initialContext();", castOrDie(String::class.java))
        onWebView().check(webMatches(script, containsString("testportal")))
    }

    companion object {
        @JvmStatic
        @BeforeClass
        fun classSetUp() {
            PortalManager.register(BuildConfig.PORTALS_KEY)
        }
    }
}