package uk.ac.tees.mad.canteenmenu.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import uk.ac.tees.mad.canteenmenu.CanteenViewModel

object Routes{
    const val SPLASH = "splash"
    const val AUTHENTICATION = "authentication"
    const val HOME = "home"
    const val WALLET = "wallet"
    const val PROFILE = "profile"
    const val ORDER_HISTORY = "order_history"
    const val FOOD_DETAILS = "details/{id}"
    fun foodDetails(id: String) = "details/$id"
    const val EDIT_PROFILE = "edit_profile"
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
        composable(Routes.WALLET){
            Wallet(viewModel,navController = navController)
        }
        composable(Routes.PROFILE){
            Profile(navController = navController)
        }
        composable(Routes.ORDER_HISTORY){
            Orders(viewModel, navController)
        }
        composable(
            route = Routes.FOOD_DETAILS,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            val item = viewModel.menuItems.collectAsState().value.find { it.id == id }
            item?.let {
                FoodDetails(viewModel = viewModel, navController = navController, item = it)
            }
        }
        composable(Routes.EDIT_PROFILE){
            EditProfile(viewModel = viewModel, navController = navController)
        }
    }
}