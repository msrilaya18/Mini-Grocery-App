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
        Product(1, "Apple", 20.0, "https://cdn.dummyjson.com/product-images/groceries/apple/thumbnail.png", "Vegetables"),
        Product(2, "Beef Steak", 165.0, "https://cdn.dummyjson.com/product-images/groceries/beef-steak/thumbnail.png", "Vegetables"),
        Product(3, "Chicken Meat", 150.0, "https://cdn.dummyjson.com/product-images/groceries/chicken-meat/thumbnail.png", "Vegetables"),
        Product(4, "Cooking Oil", 125.0, "https://cdn.dummyjson.com/product-images/groceries/cooking-oil/thumbnail.png", "Snacks"),
        Product(5, "Cucumber", 17.0, "https://cdn.dummyjson.com/product-images/groceries/cucumber/thumbnail.png", "Vegetables"),
        Product(6, "Eggs", 45.0, "https://cdn.dummyjson.com/product-images/groceries/eggs/thumbnail.png", "Dairy"),
        Product(7, "Fish Steak", 175.0, "https://cdn.dummyjson.com/product-images/groceries/fish-steak/thumbnail.png", "Snacks"),
        Product(8, "Green Bell Pepper", 16.0, "https://cdn.dummyjson.com/product-images/groceries/green-bell-pepper/thumbnail.png", "Vegetables"),
        Product(9, "Green Chili Pepper", 15.0, "https://cdn.dummyjson.com/product-images/groceries/green-chili-pepper/thumbnail.png", "Vegetables"),
        Product(10, "Honey Jar", 135.0, "https://cdn.dummyjson.com/product-images/groceries/honey-jar/thumbnail.png", "Snacks"),
        Product(11, "Ice Cream", 127.0, "https://cdn.dummyjson.com/product-images/groceries/ice-cream/thumbnail.png", "Dairy"),
        Product(12, "Juice", 50.0, "https://cdn.dummyjson.com/product-images/groceries/juice/thumbnail.png", "Snacks"),
        Product(13, "Kiwi", 32.0, "https://cdn.dummyjson.com/product-images/groceries/kiwi/thumbnail.png", "Vegetables"),
        Product(14, "Lemon", 14.0, "https://cdn.dummyjson.com/product-images/groceries/lemon/thumbnail.png", "Vegetables"),
        Product(15, "Milk", 47.0, "https://cdn.dummyjson.com/product-images/groceries/milk/thumbnail.png", "Dairy"),
        Product(16, "Mulberry", 55.0, "https://cdn.dummyjson.com/product-images/groceries/mulberry/thumbnail.png", "Snacks"),
        Product(17, "Nescafe Coffee", 140.0, "https://cdn.dummyjson.com/product-images/groceries/nescafe-coffee/thumbnail.png", "Snacks"),
        Product(18, "Potatoes", 31.0, "https://cdn.dummyjson.com/product-images/groceries/potatoes/thumbnail.png", "Vegetables"),
        Product(19, "Red Onions", 45.0, "https://cdn.dummyjson.com/product-images/groceries/red-onions/thumbnail.png", "Vegetables"),
        Product(20, "Strawberry", 60.0, "https://cdn.dummyjson.com/product-images/groceries/strawberry/thumbnail.png", "Vegetables")
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
