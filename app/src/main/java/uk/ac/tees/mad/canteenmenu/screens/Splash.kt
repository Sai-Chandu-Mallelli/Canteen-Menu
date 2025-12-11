package uk.ac.tees.mad.canteenmenu.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import uk.ac.tees.mad.canteenmenu.CanteenViewModel
import uk.ac.tees.mad.canteenmenu.R

@Composable
fun Splash(navController: NavHostController, viewModel: CanteenViewModel) {
    val loggedIn = viewModel.loggedIn.collectAsState().value
    LaunchedEffect(Unit) {
        delay(2000)
        if (!loggedIn) {
            navController.navigate(Routes.AUTHENTICATION)
        }else{
            navController.navigate(Routes.HOME)
        }
    }


    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(painterResource(R.drawable.app_icon), contentDescription = null, modifier = Modifier.size(240.dp).clip(
            RoundedCornerShape(55.dp)
        ).shadow(40.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun splashDemo(){
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(painterResource(R.drawable.app_icon), contentDescription = null, modifier = Modifier.size(240.dp).clip(
            RoundedCornerShape(55.dp)
        ).shadow(40.dp))
    }
}