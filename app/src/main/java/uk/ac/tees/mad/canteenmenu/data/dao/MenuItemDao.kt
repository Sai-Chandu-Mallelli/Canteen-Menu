package uk.ac.tees.mad.canteenmenu.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import uk.ac.tees.mad.canteenmenu.data.model.MenuItem

@Dao
interface MenuItemDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMenuItems(menuItems:MenuItem)

    @Query("SELECT * FROM menu_items ORDER BY dbId ASC")
    fun getAllMenuItems(): Flow<List<MenuItem>>

    @Query("DELETE FROM menu_items")
    suspend fun deleteAllMenuItems()

    @Query("DELETE FROM menu_items WHERE id = :itemId")
    suspend fun deleteMenuItemById(itemId: String)

    @Query("SELECT * FROM menu_items WHERE id = :itemId")
    suspend fun getMenuItemById(itemId: String): MenuItem?
}