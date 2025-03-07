package com.pc.genzwardrobe.data.local.wishlistProducts

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [WishlistProducts::class], version = 1, exportSchema = false)
abstract class WishlistProductDatabase: RoomDatabase() {
    abstract fun wishlistProductDao(): WishlistProductDao
}