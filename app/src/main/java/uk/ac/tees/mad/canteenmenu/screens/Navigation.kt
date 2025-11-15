package uk.ac.tees.mad.canteenmenu.screens

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import uk.ac.tees.mad.canteenmenu.CanteenViewModel

object Routes{
    const val SPLASH = "splash"
    const val AUTHENTICATION = "authentication"
    const val HOME = "home"
}

@Composable
fun navigation(){
    val navController = rememberNavController()
    val viewModel : CanteenViewModel = viewModel()
    NavHost(navController = navController, startDestination = Routes.SPLASH){
        composable(Routes.SPLASH){
            Splash(navController, viewModel)
        }
        composable(Routes.AUTHENTICATION){
            Authentication(viewModel, navController)
        }
        composable(Routes.HOME){
            Home(viewModel, navController)
        }

    }
}