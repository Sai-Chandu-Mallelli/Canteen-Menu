package uk.ac.tees.mad.canteenmenu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import uk.ac.tees.mad.canteenmenu.screens.Splash
import uk.ac.tees.mad.canteenmenu.screens.navigation
import uk.ac.tees.mad.canteenmenu.ui.theme.CanteenMenuTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CanteenMenuTheme {
                navigation()
            }
        }
    }
}

