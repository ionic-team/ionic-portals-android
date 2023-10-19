package io.ionic.portals.composetestapp

import android.content.Intent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.ionic.portals.PortalManager
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class UnregisteredTests {

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
    fun when_portals_is_not_registered__display_unregistered_view() {
        composeTestRule.waitForIdle()

        // Dismiss the invalid key dialog when it is displayed
        onView(withText("OK")).perform(click())

        // Verify that the unregistered view is displayed
        onView(withText(io.ionic.portals.R.string.unregistered_text)).check(matches(isDisplayed()))
    }

    @Test
    fun when_portals_is_registered_with_bad_key__display_error_dialog() {
        composeTestRule.waitForIdle()

        // Recreate the activity to trigger the dialog
        onView(withText(io.ionic.portals.R.string.invalid_portals_key)).check(matches(isDisplayed()))
    }

    companion object {
        @JvmStatic
        @BeforeClass
        fun classSetUp() {
            PortalManager.register("this is a bad key")
        }
    }
}