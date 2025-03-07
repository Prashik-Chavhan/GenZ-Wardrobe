package com.pc.genzwardrobe.di

import android.content.Context
import androidx.room.Room
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.FirebaseDatabase
import com.pc.genzwardrobe.core.data.CartProductImpl
import com.pc.genzwardrobe.core.data.InternetConnectivityImpl
import com.pc.genzwardrobe.core.data.LocationRepositoryImpl
import com.pc.genzwardrobe.core.data.OrderReviewRepositoryImpl
import com.pc.genzwardrobe.core.data.PaymentRepositoryImpl
import com.pc.genzwardrobe.core.data.PreferenceDatastoreImpl
import com.pc.genzwardrobe.core.data.ProductRepositoryImpl
import com.pc.genzwardrobe.core.data.UserRepositoryImpl
import com.pc.genzwardrobe.core.data.WishlistProductImpl
import com.pc.genzwardrobe.data.local.cartProducts.CartProductDao
import com.pc.genzwardrobe.data.local.cartProducts.CartProductDatabase
import com.pc.genzwardrobe.data.local.wishlistProducts.WishlistProductDao
import com.pc.genzwardrobe.data.local.wishlistProducts.WishlistProductDatabase
import com.pc.genzwardrobe.data.remote.InternetConnectivity
import com.pc.genzwardrobe.data.remote.LocationRepository
import com.pc.genzwardrobe.data.remote.OpenCageApi
import com.pc.genzwardrobe.data.remote.OrderReviewRepository
import com.pc.genzwardrobe.data.remote.PaymentRepository
import com.pc.genzwardrobe.data.remote.PreferenceDatastore
import com.pc.genzwardrobe.data.remote.ProductRepository
import com.pc.genzwardrobe.data.remote.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideInternetConnectivity(): InternetConnectivity = InternetConnectivityImpl()

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()

    @Provides
    @Singleton
    fun provideProductRepository(database: FirebaseDatabase): ProductRepository = ProductRepositoryImpl(database)

    @Provides
    @Singleton
    fun provideOrderReviewRepository(database: FirebaseDatabase): OrderReviewRepository = OrderReviewRepositoryImpl(database)

    @Provides
    @Singleton
    fun provideUserRepository(database: FirebaseDatabase): UserRepository = UserRepositoryImpl(database)

    @Provides
    @Singleton
    fun providePaymentRepository(): PaymentRepository = PaymentRepositoryImpl()

    @Provides
    @Singleton
    fun providePreferenceDatastore(
        @ApplicationContext context: Context
    ): PreferenceDatastore = PreferenceDatastoreImpl(context)

    @Provides
    @Singleton
    fun provideCartProductDatabase(
        @ApplicationContext context: Context
    ): CartProductDatabase = Room
        .databaseBuilder(
        context = context,
        klass = CartProductDatabase::class.java,
        name = "cart_product_database"
        )
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideWishlistProductDatabase(
        @ApplicationContext context: Context
    ): WishlistProductDatabase = Room
        .databaseBuilder(
            context = context,
            klass = WishlistProductDatabase::class.java,
            name = "wishlist_product_database"
        )
        .build()

    @Provides
    fun provideCartProductDao(
        cartProductDatabase: CartProductDatabase
    ): CartProductDao = cartProductDatabase.cartProductDao()

    @Provides
    fun provideWishlistProductDao(
        wishlistProductDatabase: WishlistProductDatabase
    ): WishlistProductDao = wishlistProductDatabase.wishlistProductDao()

    @Provides
    @Singleton
    fun provideCartProductImpl(
        cartProductDao: CartProductDao
    ): CartProductImpl = CartProductImpl(cartProductDao)

    @Provides
    @Singleton
    fun provideWishlistProductImpl(
        wishlistProductDao: WishlistProductDao
    ): WishlistProductImpl = WishlistProductImpl(wishlistProductDao)

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    @Singleton
    fun provideLocationRepository(
        api: OpenCageApi,
        fusedLocationProviderClient: FusedLocationProviderClient
    ): LocationRepository {
        return LocationRepositoryImpl(api, fusedLocationProviderClient)
    }

    @Provides
    @Singleton
    fun provideOpenCageApi(): OpenCageApi {
        return Retrofit.Builder()
            .baseUrl("https://us1.locationiq.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create()
    }
}