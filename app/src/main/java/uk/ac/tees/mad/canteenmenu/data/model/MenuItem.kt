package uk.ac.tees.mad.canteenmenu.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "menu_items")
data class MenuItem(
    @PrimaryKey(autoGenerate = true) val dbID: Int = 0,
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val isVeg: Boolean = true,
    var isSpecial: Boolean = false,
    val imageUrl: String = "",
    val description : String = ""
)
