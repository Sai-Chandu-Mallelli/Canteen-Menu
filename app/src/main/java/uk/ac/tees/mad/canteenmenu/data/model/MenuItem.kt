package uk.ac.tees.mad.canteenmenu.data.model

data class MenuItem(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val isVeg: Boolean = true,
    var isSpecial: Boolean = false,
    val imageUrl: String = ""
)
