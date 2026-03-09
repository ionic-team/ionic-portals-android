package io.ionic.portals.testapp

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.ionic.portals.PortalManager
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class UnregisteredTests {
    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun setUp() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun when_portals_is_not_registered__display_unregistered_view() {
        onView(withText("OK")).inRoot(isDialog()).perform(click())

        // Verify that the unregistered view is displayed
        onView(withText(io.ionic.portals.R.string.unregistered_text)).check(matches(isDisplayed()))
    }

    @Test
    fun when_portals_is_registered_with_bad_key__display_error_dialog() {
        onView(withText(io.ionic.portals.R.string.invalid_portals_key))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    companion object {
        @JvmStatic
        @BeforeClass
        fun classSetUp() {
            PortalManager.register("this is a bad key")
        }
    }
}
