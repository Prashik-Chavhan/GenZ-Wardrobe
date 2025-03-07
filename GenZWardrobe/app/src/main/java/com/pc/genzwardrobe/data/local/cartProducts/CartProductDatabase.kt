package com.pc.genzwardrobe.data.local.cartProducts

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CartProducts::class], version = 4, exportSchema = false)
abstract class CartProductDatabase: RoomDatabase() {
    abstract fun cartProductDao(): CartProductDao
}