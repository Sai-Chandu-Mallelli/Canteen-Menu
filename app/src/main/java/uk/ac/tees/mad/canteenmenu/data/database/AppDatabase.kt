package uk.ac.tees.mad.canteenmenu.data.database
import androidx.room.Database
import androidx.room.RoomDatabase
import uk.ac.tees.mad.canteenmenu.data.dao.MenuItemDao
import uk.ac.tees.mad.canteenmenu.data.model.MenuItem

@Database(entities = [MenuItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun menuItemDao(): MenuItemDao
}