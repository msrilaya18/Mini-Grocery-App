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
        Product(1, "Fresh Tomatoes", 40.0, "https://loremflickr.com/320/240/tomato?lock=1", "Vegetables"),
        Product(2, "Red Onions", 35.0, "https://loremflickr.com/320/240/onion?lock=2", "Vegetables"),
        Product(3, "Potatoes", 30.0, "https://loremflickr.com/320/240/potato?lock=3", "Vegetables"),
        Product(4, "Carrots", 25.0, "https://loremflickr.com/320/240/carrot?lock=4", "Vegetables"),
        Product(5, "Fresh Spinach", 20.0, "https://loremflickr.com/320/240/spinach?lock=5", "Vegetables"),
        Product(6, "Green Capsicum", 45.0, "https://loremflickr.com/320/240/capsicum?lock=6", "Vegetables"),
        Product(7, "Cauliflower", 50.0, "https://loremflickr.com/320/240/cauliflower?lock=7", "Vegetables"),
        Product(8, "Amul Milk (500ml)", 32.0, "https://loremflickr.com/320/240/milk?lock=8", "Dairy"),
        Product(9, "Britannia Bread", 45.0, "https://loremflickr.com/320/240/bread?lock=9", "Dairy"),
        Product(10, "Farm Eggs (6 pcs)", 60.0, "https://loremflickr.com/320/240/eggs?lock=10", "Dairy"),
        Product(11, "Amul Butter", 55.0, "https://loremflickr.com/320/240/butter?lock=11", "Dairy"),
        Product(12, "Cheddar Cheese", 120.0, "https://loremflickr.com/320/240/cheese?lock=12", "Dairy"),
        Product(13, "Curd / Yogurt", 30.0, "https://loremflickr.com/320/240/yogurt?lock=13", "Dairy"),
        Product(14, "Lays Classic", 20.0, "https://loremflickr.com/320/240/potatochips?lock=14", "Snacks"),
        Product(15, "Coca Cola (1L)", 60.0, "https://loremflickr.com/320/240/cola?lock=15", "Snacks"),
        Product(16, "Doritos Nacho", 35.0, "https://loremflickr.com/320/240/doritos?lock=16", "Snacks"),
        Product(17, "Oreo Biscuits", 30.0, "https://loremflickr.com/320/240/oreo?lock=17", "Snacks"),
        Product(18, "Cadbury Silk", 80.0, "https://loremflickr.com/320/240/chocolate?lock=18", "Snacks"),
        Product(19, "Orange Juice", 90.0, "https://loremflickr.com/320/240/orangejuice?lock=19", "Snacks"),
        Product(20, "Popcorn Pack", 40.0, "https://loremflickr.com/320/240/popcorn?lock=20", "Snacks")
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
