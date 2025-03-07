package com.pc.genzwardrobe.core.data

import com.pc.genzwardrobe.data.local.wishlistProducts.WishlistProductDao
import com.pc.genzwardrobe.data.local.wishlistProducts.WishlistProducts

class WishlistProductImpl(
    private val wishlistProductDao: WishlistProductDao
) {
    suspend fun insertItem(wishlistProducts: WishlistProducts) = wishlistProductDao.insertItem(wishlistProducts)

    fun getMaxItemId() = wishlistProductDao.getMaxItemId()

    fun getAllWishlistItem() = wishlistProductDao.getAllWishlistProducts()

    suspend fun deleteItem(wishlistProducts: WishlistProducts) = wishlistProductDao.deleteItem(wishlistProducts)
}