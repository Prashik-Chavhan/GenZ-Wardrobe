package com.pc.genzwardrobe.data.local.cartProducts

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CartProductDao {

    @Query("SELECT * FROM cartProducts")
    fun getAllCartProducts(): Flow<List<CartProducts>>

    @Query("SELECT * FROM cartProducts WHERE itemId = :itemId")
    fun getProductById(itemId: Int): Flow<CartProducts>

    @Query("SELECT itemId FROM cartProducts WHERE variantId = :variantId AND variantColor = :variantColor AND size = :variantSize")
    fun getItemId(variantId: String, variantColor: String, variantSize: String): Flow<Int?>

    // COALESCE is useful for providing default values when a column might be null
    @Query("SELECT COALESCE(SUM(productQuantity * originalPrice), 0) FROM cartProducts")
    fun getTotalOriginalPrice(): Flow<Int>

    @Query("SELECT COALESCE(SUM(productQuantity * discountPrice), 0) FROM cartProducts")
    fun getTotalDiscountedPrice(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCartProduct(cartProducts: CartProducts)

    @Query("DELETE FROM cartProducts WHERE itemId = :id")
    suspend fun deleteCartProductById(id: Int)

    @Query("UPDATE cartProducts SET productQuantity = :newQuantity WHERE variantId = :variantId AND variantColor = :variantColor AND size = :variantSize")
    suspend fun updateCartProduct(
        variantId: String,
        variantColor: String,
        variantSize: String,
        newQuantity: Int
    )

    @Query("SELECT MAX(itemId) from cartProducts")
    fun getMaxItemId(): Flow<Int?>

    @Query("DELETE FROM cartProducts")
    suspend fun deleteAllCartProducts()
}