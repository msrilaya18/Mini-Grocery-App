package com.example.minigroceryapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.minigroceryapp.data.AppDatabase
import com.example.minigroceryapp.data.CartRepository
import com.example.minigroceryapp.model.CartItem
import com.example.minigroceryapp.model.Product
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CartRepository

    // Exposing the cart items directly from the DB as a reactive state
    val cartItems: StateFlow<List<CartItem>>

    // Hardcoding some beautiful mock data with Unsplash images
    private val allProducts = listOf(
        Product(1, "Fresh Tomatoes (500g)", 40.0, "https://images.unsplash.com/photo-1592924357228-91a4daadcfea?w=300", "Vegetables"),
        Product(2, "Onions (1kg)", 35.0, "https://images.unsplash.com/photo-1618512496248-a07ce83aa8cb?w=300", "Vegetables"),
        Product(3, "Amul Milk (500ml)", 32.0, "https://images.unsplash.com/photo-1550583724-b2692b85b150?w=300", "Dairy"),
        Product(4, "Britannia Bread", 45.0, "https://images.unsplash.com/photo-1598373182133-52452f7691ef?w=300", "Dairy"),
        Product(5, "Lays Classic", 20.0, "https://images.unsplash.com/photo-1566478989037-eade3f7e1c84?w=300", "Snacks"),
        Product(6, "Coca Cola (1L)", 60.0, "https://plus.unsplash.com/premium_photo-1663454743275-5caad55cdade?w=300", "Snacks")
    )

    // The currently displayed products (can be filtered)
    private val _products = MutableStateFlow(allProducts)
    val products = _products.asStateFlow()

    // Tracking the UI state for filters
    private val _currentCategory = MutableStateFlow("All")
    val currentCategory = _currentCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        // Initialize Room DB connection and repository
        val dao = AppDatabase.getDatabase(application).cartDao()
        repository = CartRepository(dao)

        // Convert the Flow from Room into a StateFlow so UI can read the latest value instantly
        cartItems = repository.allCartItems.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        
        // This is where StateFlow magic happens:
        // We observe both search queries and category changes and automatically output filtered products!
        viewModelScope.launch {
            combine(_searchQuery, _currentCategory) { query, category ->
                var filtered = allProducts
                if (category != "All") {
                    filtered = filtered.filter { it.category == category }
                }
                if (query.isNotBlank()) {
                    filtered = filtered.filter { it.name.contains(query, ignoreCase = true) }
                }
                filtered
            }.collect {
                _products.value = it
            }
        }
    }

    fun setCategory(category: String) {
        _currentCategory.value = category
    }

    fun search(query: String) {
        _searchQuery.value = query
    }

    // Connects to Repository functionality using Coroutines to avoid freezing the app
    fun addToCart(product: Product) {
        viewModelScope.launch {
            repository.addToCart(
                CartItem(productId = product.id, name = product.name, price = product.price, quantity = 1)
            )
        }
    }

    fun decreaseOrRemove(productId: Int) {
        viewModelScope.launch {
            repository.decreaseOrRemove(productId)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }
}
