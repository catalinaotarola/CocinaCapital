package cl.DSY1105.cocinacapital

import AppNavigation
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import cl.DSY1105.cocinacapital.ui.theme.CocinaCapitalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CocinaCapitalTheme {
                val navController = rememberNavController()
                AppNavigation(navController)
            }
        }
    }
}



