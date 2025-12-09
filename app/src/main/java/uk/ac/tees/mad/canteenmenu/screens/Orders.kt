package uk.ac.tees.mad.canteenmenu.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import uk.ac.tees.mad.canteenmenu.CanteenViewModel
import uk.ac.tees.mad.canteenmenu.data.model.MenuItem
import uk.ac.tees.mad.canteenmenu.ui.theme.BackgroundLight
import uk.ac.tees.mad.canteenmenu.ui.theme.CardBackground
import uk.ac.tees.mad.canteenmenu.ui.theme.OrangeDark
import uk.ac.tees.mad.canteenmenu.ui.theme.OrangePrimary
import uk.ac.tees.mad.canteenmenu.ui.theme.TextPrimary
import uk.ac.tees.mad.canteenmenu.ui.theme.TextSecondary


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Orders(
    viewModel : CanteenViewModel,
    navController : NavController
) {
    val orders = viewModel.orders.collectAsState().value
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Orders", color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OrangePrimary)
            )
        },
        bottomBar = { BottomNavigationBar(navController, Routes.ORDER_HISTORY) },
        containerColor = BackgroundLight
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(BackgroundLight)
        ) {
            if (orders.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No orders yet!",
                        style = MaterialTheme.typography.titleMedium.copy(color = TextSecondary)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(orders) { item ->
                        OrderCard(item = item)
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCard(item: MenuItem) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = "$${item.price}",
                    style = MaterialTheme.typography.bodyMedium.copy(color = OrangeDark)
                )
            }
        }
    }
}
