package uk.ac.tees.mad.canteenmenu.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import uk.ac.tees.mad.canteenmenu.CanteenViewModel

@Composable
fun Home(viewModel: CanteenViewModel, navController: NavHostController) {
    val menuItems = viewModel.menuItems.collectAsState().value
    Column() {
        LazyColumn {
            items(menuItems){
                Text(it.name)
                Text(it.price.toString())
            }
        }
    }
}