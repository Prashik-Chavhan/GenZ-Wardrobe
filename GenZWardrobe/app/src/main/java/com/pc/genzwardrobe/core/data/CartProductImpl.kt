package com.pc.genzwardrobe.core.data

import com.pc.genzwardrobe.data.local.cartProducts.CartProductDao
import com.pc.genzwardrobe.data.local.cartProducts.CartProducts

class CartProductImpl(
    private val cartProductDao: CartProductDao
) {
    fun getAllCartProducts() = cartProductDao.getAllCartProducts()

    fun getTotalOriginalPrice() = cartProductDao.getTotalOriginalPrice()

    fun getTotalDiscountedPrice() = cartProductDao.getTotalDiscountedPrice()

    fun getProductById(itemId: Int) = cartProductDao.getProductById(itemId)

    fun getMaxItemId() = cartProductDao.getMaxItemId()

    fun getItemId(
        variantId: String,
        variantSize: String,
        variantColor: String
    ) = cartProductDao.getItemId(variantId = variantId, variantSize = variantSize, variantColor = variantColor)

    suspend fun insertCartProduct(
        cartProducts: CartProducts
    ) = cartProductDao.insertCartProduct(cartProducts)

    suspend fun deleteCartProductById(
        productId: Int
    ) = cartProductDao.deleteCartProductById(productId)

    suspend fun updateCartProduct(
        variantId: String,
        variantColor: String,
        variantSize: String,
        newQuantity: Int
    ) = cartProductDao.updateCartProduct(variantId, variantColor, variantSize, newQuantity)

    suspend fun deleteAllCartProducts() = cartProductDao.deleteAllCartProducts()
}