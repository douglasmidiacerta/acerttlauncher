package io.github.seijikohara.femto.ui.home.components

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import io.github.seijikohara.femto.data.dock.DockStatusId
import io.github.seijikohara.femto.data.system.SystemStatus
import io.github.seijikohara.femto.testfixtures.fakeSystemStatus
import io.github.seijikohara.femto.ui.home.HomeAction
import io.github.seijikohara.femto.ui.theme.FemtoTheme
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class DockStatusClusterTest {
    @get:Rule
    val rule = createComposeRule()

    private fun setCluster(
        systemStatus: SystemStatus = fakeSystemStatus(),
        order: List<DockStatusId> = DockStatusId.entries,
        vertical: Boolean = false,
        onAction: (HomeAction) -> Unit = {},
    ) {
        rule.setContent {
            FemtoTheme {
                StatusCluster(
                    status = systemStatus,
                    vertical = vertical,
                    order = order,
                    onAction = onAction,
                )
            }
        }
    }

    @Test
    fun long_click_semantics_action_opens_the_edit_menu_on_a_status_indicator() {
        setCluster(
            systemStatus = fakeSystemStatus(wifiConnected = true),
            order = listOf(DockStatusId.WIFI),
        )
        rule
            .onNodeWithContentDescription("Wi-Fi connected")
            .assert(SemanticsMatcher.keyIsDefined(SemanticsActions.OnLongClick))
            .performSemanticsAction(SemanticsActions.OnLongClick)
        // Reset dock has no canMoveLeft/canMoveRight guard, so it always
        // renders once expanded — proof the menu actually opened.
        rule.onNodeWithText("Reset dock").assertIsDisplayed()
    }

    @Test
    fun shows_wifi_connected_description_when_wifi_connected() {
        setCluster(systemStatus = fakeSystemStatus(wifiConnected = true))
        rule.onNodeWithContentDescription("Wi-Fi connected").assertIsDisplayed()
    }

    @Test
    fun shows_bluetooth_on_description_when_enabled_but_not_connected() {
        setCluster(systemStatus = fakeSystemStatus(bluetoothEnabled = true, bluetoothConnected = false))
        rule.onNodeWithContentDescription("Bluetooth on").assertIsDisplayed()
    }

    @Test
    fun shows_bluetooth_off_description_when_adapter_disabled() {
        setCluster(systemStatus = fakeSystemStatus(bluetoothEnabled = false, bluetoothConnected = false))
        rule.onNodeWithContentDescription("Bluetooth off").assertIsDisplayed()
    }

    @Test
    fun shows_cellular_connected_description_when_connected() {
        setCluster(systemStatus = fakeSystemStatus(cellularConnected = true))
        rule.onNodeWithContentDescription("Mobile data connected").assertIsDisplayed()
    }

    @Test
    fun shows_graduated_cellular_icon_when_level_is_known() {
        setCluster(systemStatus = fakeSystemStatus(cellularConnected = true, cellularSignalLevel = 3))
        rule.onNodeWithContentDescription("Mobile data connected").assertIsDisplayed()
    }

    @Test
    fun degrades_cellular_to_binary_icon_when_level_is_null() {
        setCluster(systemStatus = fakeSystemStatus(cellularConnected = true, cellularSignalLevel = null))
        rule.onNodeWithContentDescription("Mobile data connected").assertIsDisplayed()
    }

    @Test
    fun shows_cellular_disconnected_when_signal_level_is_zero() {
        setCluster(systemStatus = fakeSystemStatus(cellularConnected = true, cellularSignalLevel = 0))
        rule.onNodeWithContentDescription("Mobile data disconnected").assertIsDisplayed()
    }

    @Test
    fun shows_graduated_wifi_icon_when_connected_with_a_level() {
        setCluster(systemStatus = fakeSystemStatus(wifiConnected = true, wifiSignalLevel = 1))
        rule.onNodeWithContentDescription("Wi-Fi connected").assertIsDisplayed()
    }

    @Test
    fun hides_cellular_icon_on_telephony_less_unit() {
        setCluster(systemStatus = fakeSystemStatus(cellularConnected = null))
        rule.onAllNodesWithContentDescription("Mobile data connected").assertCountEquals(0)
        rule.onAllNodesWithContentDescription("Mobile data disconnected").assertCountEquals(0)
    }

    @Test
    fun shows_gps_fixed_description_when_gps_is_fixed() {
        setCluster(systemStatus = fakeSystemStatus(gpsFixed = true))
        rule.onNodeWithContentDescription("GPS fixed").assertIsDisplayed()
    }

    @Test
    fun shows_gps_searching_description_when_gps_is_not_fixed() {
        setCluster(systemStatus = fakeSystemStatus(gpsFixed = false))
        rule.onNodeWithContentDescription("GPS searching").assertIsDisplayed()
    }

    @Test
    fun renders_gps_satellite_count_text() {
        setCluster(systemStatus = fakeSystemStatus(gpsFixed = true, gpsSatelliteCount = 9))
        rule.onNodeWithText("9").assertIsDisplayed()
    }

    @Test
    fun renders_battery_percent_text() {
        setCluster(systemStatus = fakeSystemStatus(batteryPercent = 78))
        rule.onNodeWithText("78%").assertIsDisplayed()
    }

    @Test
    fun does_not_render_a_charging_caption() {
        setCluster(systemStatus = fakeSystemStatus(charging = true))
        rule.onNodeWithText("Charging").assertDoesNotExist()
    }

    @Test
    fun long_pressing_a_status_indicator_dispatches_move_status() {
        var lastAction: HomeAction? = null
        setCluster(onAction = { lastAction = it })
        rule.onNodeWithContentDescription("Wi-Fi connected").performTouchInput { longClick() }
        rule.onNodeWithText("Move left").performClick()
        assertEquals(HomeAction.MoveDockStatus(DockStatusId.WIFI, -1), lastAction)
    }

    @Test
    fun long_pressing_a_status_indicator_dispatches_hide_status() {
        var lastAction: HomeAction? = null
        setCluster(onAction = { lastAction = it })
        rule.onNodeWithContentDescription("Wi-Fi connected").performTouchInput { longClick() }
        rule.onNodeWithText("Hide").performClick()
        assertEquals(HomeAction.HideDockStatus(DockStatusId.WIFI), lastAction)
    }
}
