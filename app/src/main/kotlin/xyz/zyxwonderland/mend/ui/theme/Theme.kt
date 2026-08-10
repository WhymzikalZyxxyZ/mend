package xyz.zyxwonderland.mend.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Primary = Color(0xFF2E7D32) // green-800, evokes health/nutrition
private val OnPrimary = Color(0xFFFFFFFF)
private val Secondary = Color(0xFFF9A825) // amber-800, warmth of food
private val Background = Color(0xFF0F1410)
private val Surface = Color(0xFF1B231C)
private val OnSurface = Color(0xFFE2E8F0)

private val DarkColors =
    darkColorScheme(
        primary = Primary,
        onPrimary = OnPrimary,
        secondary = Secondary,
        background = Background,
        surface = Surface,
        onSurface = OnSurface,
        onBackground = OnSurface,
    )

private val LightColors =
    lightColorScheme(
        primary = Primary,
        onPrimary = OnPrimary,
        secondary = Secondary,
    )

@Composable
fun MendTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        content = content,
    )
}
