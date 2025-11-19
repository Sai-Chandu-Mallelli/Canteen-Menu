package uk.ac.tees.mad.canteenmenu.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Wallet
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import uk.ac.tees.mad.canteenmenu.ui.theme.BackgroundLight
import uk.ac.tees.mad.canteenmenu.ui.theme.CardBackground
import uk.ac.tees.mad.canteenmenu.ui.theme.OrangeDark
import uk.ac.tees.mad.canteenmenu.ui.theme.OrangePrimary
import uk.ac.tees.mad.canteenmenu.ui.theme.OrangeSecondary
import uk.ac.tees.mad.canteenmenu.ui.theme.TextPrimary
import uk.ac.tees.mad.canteenmenu.ui.theme.TextSecondary

@Composable
fun Wallet(
    balance: Int = 250,
    onAddAmount: (Int) -> Unit = {}
) {
    Scaffold(
        containerColor = BackgroundLight
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(BackgroundLight),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // 👜 Big Wallet Icon
            Icon(
                imageVector = Icons.Rounded.Wallet,
                contentDescription = "Wallet",
                tint = OrangePrimary,
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 💰 Balance Text
            Text(
                text = "Your Balance",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = TextSecondary
                )
            )
            Text(
                text = "₹$balance",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = OrangeDark,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(40.dp))

            // ➕ Add Money Section
            Text(
                text = "Add Amount",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 🔘 Quick Add Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                QuickAddButton(amount = 50, onClick = onAddAmount)
                QuickAddButton(amount = 100, onClick = onAddAmount)
                QuickAddButton(amount = 200, onClick = onAddAmount)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 📝 Custom Amount
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .clickable { /* open custom input dialog */ }
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Enter Custom Amount",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = TextSecondary
                        )
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = OrangePrimary
                    )
                }
            }
        }
    }
}

@Composable
fun QuickAddButton(amount: Int, onClick: (Int) -> Unit) {
    Card(
        shape = RoundedCornerShape(50),
        colors = CardDefaults.cardColors(containerColor = OrangeSecondary),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .size(80.dp)
            .clickable { onClick(amount) }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "₹$amount",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}
