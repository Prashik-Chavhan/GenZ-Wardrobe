package com.pc.genzwardrobe.data.local.wishlistProducts

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WishlistProductDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertItem(wishlistProducts: WishlistProducts)

    @Query("SELECT MAX(itemId) FROM wishlistProducts")
    fun getMaxItemId(): Flow<Int?>

    @Query("SELECT * FROM wishlistProducts")
    fun getAllWishlistProducts(): Flow<List<WishlistProducts>>

    @Delete
    suspend fun deleteItem(wishlistProducts: WishlistProducts)
}
