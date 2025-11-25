package uk.ac.tees.mad.canteenmenu.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import uk.ac.tees.mad.canteenmenu.data.model.MenuItem
import uk.ac.tees.mad.canteenmenu.ui.theme.*
val dummyOrders = listOf(
    MenuItem(
        id = "1",
        name = "Chicken Biryani",
        price = 120.0,
        isVeg = false,
        isSpecial = false,
        imageUrl = "https://source.unsplash.com/OterGMpkdsM/600x400"
    ),
    MenuItem(
        id = "2",
        name = "Paneer Butter Masala",
        price = 130.0,
        isVeg = true,
        isSpecial = false,
        imageUrl = "https://source.unsplash.com/7zTClpLI60c/600x400"
    ),
    MenuItem(
        id = "3",
        name = "Veggie Burger",
        price = 80.0,
        isVeg = true,
        isSpecial = false,
        imageUrl = "https://source.unsplash.com/yE9Rq_KGrLI/600x400"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Orders(

    orders: List<MenuItem> = dummyOrders // using dummy data
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Orders", color = TextPrimary) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = OrangePrimary)
            )
        },
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
                    text = "₹${item.price}",
                    style = MaterialTheme.typography.bodyMedium.copy(color = OrangeDark)
                )
                Text(
                    text = "Delivered", // hardcoded for now
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                )
            }
        }
    }
}
