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
        Product(1, "Fresh Tomatoes", 40.0, "https://spoonacular.com/cdn/ingredients_250x250/tomato.png", "Vegetables"),
        Product(2, "Red Onions", 35.0, "https://spoonacular.com/cdn/ingredients_250x250/brown-onion.png", "Vegetables"),
        Product(3, "Potatoes", 30.0, "https://spoonacular.com/cdn/ingredients_250x250/potatoes-yukon-gold.png", "Vegetables"),
        Product(4, "Carrots", 25.0, "https://spoonacular.com/cdn/ingredients_250x250/sliced-carrot.png", "Vegetables"),
        Product(5, "Fresh Spinach", 20.0, "https://spoonacular.com/cdn/ingredients_250x250/spinach.jpg", "Vegetables"),
        Product(6, "Green Capsicum", 45.0, "https://spoonacular.com/cdn/ingredients_250x250/green-pepper.jpg", "Vegetables"),
        Product(7, "Cauliflower", 50.0, "https://spoonacular.com/cdn/ingredients_250x250/cauliflower.jpg", "Vegetables"),
        Product(8, "Amul Milk (500ml)", 32.0, "https://spoonacular.com/cdn/ingredients_250x250/milk.png", "Dairy"),
        Product(9, "Britannia Bread", 45.0, "https://spoonacular.com/cdn/ingredients_250x250/white-bread.jpg", "Dairy"),
        Product(10, "Farm Eggs (6 pcs)", 60.0, "https://spoonacular.com/cdn/ingredients_250x250/egg.png", "Dairy"),
        Product(11, "Amul Butter", 55.0, "https://spoonacular.com/cdn/ingredients_250x250/butter.png", "Dairy"),
        Product(12, "Cheddar Cheese", 120.0, "https://spoonacular.com/cdn/ingredients_250x250/cheddar-cheese.png", "Dairy"),
        Product(13, "Curd / Yogurt", 30.0, "https://spoonacular.com/cdn/ingredients_250x250/plain-yogurt.jpg", "Dairy"),
        Product(14, "Lays Classic", 20.0, "https://spoonacular.com/cdn/ingredients_250x250/potato-chips.jpg", "Snacks"),
        Product(15, "Coca Cola (1L)", 60.0, "https://spoonacular.com/cdn/ingredients_250x250/coca-cola.png", "Snacks"),
        Product(16, "Doritos Nacho", 35.0, "https://spoonacular.com/cdn/ingredients_250x250/tortilla-chips.jpg", "Snacks"),
        Product(17, "Oreo Biscuits", 30.0, "https://spoonacular.com/cdn/ingredients_250x250/chocolate-sandwich-cookie.png", "Snacks"),
        Product(18, "Cadbury Silk", 80.0, "https://spoonacular.com/cdn/ingredients_250x250/milk-chocolate.jpg", "Snacks"),
        Product(19, "Orange Juice", 90.0, "https://spoonacular.com/cdn/ingredients_250x250/orange-juice.jpg", "Snacks"),
        Product(20, "Popcorn Pack", 40.0, "https://spoonacular.com/cdn/ingredients_250x250/popcorn.png", "Snacks")
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
