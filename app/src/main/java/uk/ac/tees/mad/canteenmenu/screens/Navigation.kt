package uk.ac.tees.mad.canteenmenu.screens

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

object Routes{
    const val SPLASH = "splash"
    const val AUTHENTICATION = "authentication"
    const val HOME = "home"
}

@Composable
fun navigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.SPLASH){
        composable(Routes.SPLASH){
            Splash(navController)
        }
        composable(Routes.AUTHENTICATION){
            Authentication()
        }
        composable(Routes.HOME){
            //Home()
        }

    }
}