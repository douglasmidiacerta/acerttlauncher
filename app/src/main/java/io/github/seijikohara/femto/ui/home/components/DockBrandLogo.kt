package io.github.seijikohara.femto.ui.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.seijikohara.femto.R
import io.github.seijikohara.femto.ui.theme.FemtoTheme
import io.github.seijikohara.femto.ui.theme.PreviewLightDark

/**
 * Brand logo displayed in the dashboard dock's trailing slot.
 */
@Composable
internal fun DockBrandLogo(
    vertical: Boolean,
    modifier: Modifier = Modifier,
) {
    val description = stringResource(R.string.dock_brand_logo_desc)
    Box(
        modifier = modifier.semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.acertt_dock_logo),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier =
                if (vertical) {
                    Modifier.width(52.dp)
                } else {
                    Modifier.height(38.dp)
                },
        )
    }
}

@PreviewLightDark
@Preview(name = "Dock Brand Logo (Horizontal)", widthDp = 180, heightDp = 64)
@Composable
private fun DockBrandLogoHorizontalPreview() {
    FemtoTheme {
        DockBrandLogo(vertical = false)
    }
}

@PreviewLightDark
@Preview(name = "Dock Brand Logo (Vertical)", widthDp = 64, heightDp = 64)
@Composable
private fun DockBrandLogoVerticalPreview() {
    FemtoTheme {
        DockBrandLogo(vertical = true)
    }
}
