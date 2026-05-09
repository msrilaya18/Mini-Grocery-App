package com.example.minigroceryapp.data

import androidx.room.*
import com.example.minigroceryapp.model.CartItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(cartItem: CartItem)

    @Delete
    suspend fun delete(cartItem: CartItem)

    @Query("SELECT * FROM cart_items")
    fun getAllCartItems(): Flow<List<CartItem>>

    @Query("SELECT * FROM cart_items WHERE productId = :productId")
    suspend fun getCartItemById(productId: Int): CartItem?

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}
