package com.pc.genzwardrobeadmin.di

import com.google.firebase.database.FirebaseDatabase
import com.pc.genzwardrobeadmin.core.data.ProductRepositoryImpl
import com.pc.genzwardrobeadmin.core.data.WalletRepositoryImpl
import com.pc.genzwardrobeadmin.data.remote.ProductRepository
import com.pc.genzwardrobeadmin.data.remote.WalletRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()

    @Provides
    @Singleton
    fun provideProductRepository(
        database: FirebaseDatabase
    ): ProductRepository = ProductRepositoryImpl(database)

    @Provides
    @Singleton
    fun provideWalletRepository(database: FirebaseDatabase): WalletRepository =
        WalletRepositoryImpl(database)
}