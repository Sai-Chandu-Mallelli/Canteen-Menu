package uk.ac.tees.mad.canteenmenu.di

import android.content.Context
import androidx.room.Room
import com.cloudinary.Cloudinary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uk.ac.tees.mad.canteenmenu.data.dao.MenuItemDao
import uk.ac.tees.mad.canteenmenu.data.database.AppDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CanteenModule {

    @Provides
    @Singleton
    fun providesAuthentication() : FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun providesFirestore() : FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun providesDatabase() : DatabaseReference = FirebaseDatabase.getInstance().reference

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideMenuItemDao(appDatabase: AppDatabase): MenuItemDao {
        return appDatabase.menuItemDao()
    }

    @Provides
    @Singleton
    fun provideCloudinary(): Cloudinary {
        val config: MutableMap<String, String> = HashMap()
        config["cloud_name"] = "dux792jx1"
        config["api_key"] = "762416657415368"
        config["api_secret"] = "lA2D1GpovSQIfAvmVoJ_AmpvfWw"
        return Cloudinary(config)
    }
}