package com.example.minigroceryapp.data

import com.example.minigroceryapp.model.CartItem
import kotlinx.coroutines.flow.Flow

class CartRepository(private val cartDao: CartDao) {
    
    val allCartItems: Flow<List<CartItem>> = cartDao.getAllCartItems()

    suspend fun addToCart(cartItem: CartItem) {
        val existingItem = cartDao.getCartItemById(cartItem.productId)
        if (existingItem != null) {
            cartDao.insertOrUpdate(existingItem.copy(quantity = existingItem.quantity + 1))
        } else {
            cartDao.insertOrUpdate(cartItem)
        }
    }

    suspend fun decreaseOrRemove(productId: Int) {
        val existingItem = cartDao.getCartItemById(productId)
        if (existingItem != null) {
            if (existingItem.quantity > 1) {
                cartDao.insertOrUpdate(existingItem.copy(quantity = existingItem.quantity - 1))
            } else {
                cartDao.delete(existingItem)
            }
        }
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }
}
