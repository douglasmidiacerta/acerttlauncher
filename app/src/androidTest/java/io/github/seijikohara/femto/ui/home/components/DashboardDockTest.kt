package io.github.seijikohara.femto.ui.home.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import io.github.seijikohara.femto.data.display.DockPosition
import io.github.seijikohara.femto.data.dock.DockNavId
import io.github.seijikohara.femto.ui.home.HomeAction
import io.github.seijikohara.femto.ui.theme.FemtoTheme
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DashboardDockTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun renders_apps_and_settings_nav_buttons() {
        rule.setContent {
            FemtoTheme {
                DashboardDock(
                    onAction = {},
                )
            }
        }
        // The launcher dashboard IS home, so there is no Home button.
        rule.onNodeWithContentDescription("Apps").assertIsDisplayed()
        rule.onNodeWithContentDescription("Settings").assertIsDisplayed()
    }

    @Test
    fun tapping_apps_dispatches_open_app_drawer() {
        var lastAction: HomeAction? = null
        rule.setContent {
            FemtoTheme {
                DashboardDock(
                    onAction = { lastAction = it },
                )
            }
        }
        rule.onNodeWithContentDescription("Apps").performClick()
        assertEquals(HomeAction.OpenAppDrawer, lastAction)
    }

    @Test
    fun tapping_assistant_dispatches_open_assistant() {
        var lastAction: HomeAction? = null
        rule.setContent {
            FemtoTheme {
                DashboardDock(
                    onAction = { lastAction = it },
                )
            }
        }
        rule.onNodeWithContentDescription("Assistant").performClick()
        assertEquals(HomeAction.OpenAssistant, lastAction)
    }

    @Test
    fun renders_brand_logo_in_horizontal_dock() {
        rule.setContent {
            FemtoTheme {
                DashboardDock(
                    onAction = {},
                )
            }
        }
        rule.onNodeWithContentDescription("Acertt Car").assertIsDisplayed()
    }

    @Test
    fun renders_brand_logo_in_vertical_dock() {
        rule.setContent {
            FemtoTheme {
                DashboardDock(
                    onAction = {},
                    position = DockPosition.LEFT,
                )
            }
        }
        rule.onNodeWithContentDescription("Acertt Car").assertIsDisplayed()
    }

    @Test
    fun vertical_rail_renders_all_nav_buttons() {
        rule.setContent {
            FemtoTheme {
                DashboardDock(
                    onAction = {},
                    position = DockPosition.LEFT,
                )
            }
        }
        rule.onNodeWithContentDescription("Apps").assertIsDisplayed()
        rule.onNodeWithContentDescription("Settings").assertIsDisplayed()
        rule.onNodeWithContentDescription("Phone").assertIsDisplayed()
    }

    // --- Long-press dock edit menu (nav buttons) ---

    private fun setDock(
        dockConfig: DockConfig = DockConfig(),
        onAction: (HomeAction) -> Unit = {},
    ) {
        rule.setContent {
            FemtoTheme {
                DashboardDock(
                    onAction = onAction,
                    dockConfig = dockConfig,
                )
            }
        }
    }

    @Test
    fun long_pressing_a_nav_button_dispatches_move_left() {
        var lastAction: HomeAction? = null
        setDock(onAction = { lastAction = it })
        // A long press opens the button's edit menu (combinedClickable's long-press);
        // Apps is not the first button, so Move left swaps it one step earlier.
        rule.onNodeWithContentDescription("Apps").performTouchInput { longClick() }
        rule.onNodeWithText("Move left").performClick()
        assertEquals(HomeAction.MoveDockNav(DockNavId.APPS, -1), lastAction)
    }

    @Test
    fun long_pressing_a_nav_button_dispatches_move_right() {
        var lastAction: HomeAction? = null
        setDock(onAction = { lastAction = it })
        rule.onNodeWithContentDescription("Apps").performTouchInput { longClick() }
        rule.onNodeWithText("Move right").performClick()
        assertEquals(HomeAction.MoveDockNav(DockNavId.APPS, 1), lastAction)
    }

    @Test
    fun long_pressing_a_nav_button_dispatches_hide() {
        var lastAction: HomeAction? = null
        setDock(onAction = { lastAction = it })
        rule.onNodeWithContentDescription("Apps").performTouchInput { longClick() }
        rule.onNodeWithText("Hide").performClick()
        assertEquals(HomeAction.HideDockNav(DockNavId.APPS), lastAction)
    }

    @Test
    fun nav_button_menu_reset_dock_dispatches_reset() {
        var lastAction: HomeAction? = null
        setDock(onAction = { lastAction = it })
        rule.onNodeWithContentDescription("Apps").performTouchInput { longClick() }
        rule.onNodeWithText("Reset dock").performClick()
        assertEquals(HomeAction.ResetDock, lastAction)
    }

    @Test
    fun the_first_nav_button_menu_omits_move_left() {
        setDock()
        // Phone is the first visible button (canMoveLeft = index > 0 is false), so
        // its menu offers Move right but not Move left.
        rule.onNodeWithContentDescription("Phone").performTouchInput { longClick() }
        rule.onNodeWithText("Move right").assertExists()
        rule.onNodeWithText("Move left").assertDoesNotExist()
    }

    @Test
    fun the_last_nav_button_menu_omits_move_right() {
        setDock()
        // Settings is the last visible button (canMoveRight = index < lastIndex is
        // false), so its menu offers Move left but not Move right.
        rule.onNodeWithContentDescription("Settings").performTouchInput { longClick() }
        rule.onNodeWithText("Move left").assertExists()
        rule.onNodeWithText("Move right").assertDoesNotExist()
    }

    @Test
    fun the_sole_visible_nav_button_menu_omits_hide() {
        // The nav floor holds: once everything but one button is hidden the
        // survivor's menu omits Hide, so the dock can never lose its last
        // actionable button.
        setDock(dockConfig = DockConfig(navHidden = DockNavId.entries.toSet() - DockNavId.APPS))
        rule.onNodeWithContentDescription("Apps").performTouchInput { longClick() }
        rule.onNodeWithText("Hide").assertDoesNotExist()
        // Reset dock stays available even for the sole survivor.
        rule.onNodeWithText("Reset dock").assertExists()
    }

    @Test
    fun a_reordered_and_hidden_dock_config_renders_the_order_and_drops_the_hidden() {
        // Settings moved to the front, Music hidden: the visible buttons follow the
        // configured order and the hidden one is absent.
        setDock(
            dockConfig =
                DockConfig(
                    navOrder = listOf(DockNavId.SETTINGS) + (DockNavId.entries - DockNavId.SETTINGS),
                    navHidden = setOf(DockNavId.MUSIC),
                ),
        )
        rule.onNodeWithContentDescription("Music").assertDoesNotExist()
        // Settings now precedes Phone in the row (it is the last button by default).
        val settingsLeft = rule.onNodeWithContentDescription("Settings").getUnclippedBoundsInRoot().left
        val phoneLeft = rule.onNodeWithContentDescription("Phone").getUnclippedBoundsInRoot().left
        // kotlin.test.assertTrue, not a bare assert(): ART runs with JVM assertions
        // disabled, so assert() would silently pass on-device even if the order broke.
        assertTrue(settingsLeft < phoneLeft)
    }
}
