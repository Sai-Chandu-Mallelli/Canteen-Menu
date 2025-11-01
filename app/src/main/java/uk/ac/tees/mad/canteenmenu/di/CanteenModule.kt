package uk.ac.tees.mad.canteenmenu.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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

}