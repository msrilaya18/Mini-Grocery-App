package com.example.minigroceryapp.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.minigroceryapp.R
import com.example.minigroceryapp.databinding.FragmentHomeBinding
import com.example.minigroceryapp.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        setupCategories()
        setupProducts()
        setupSearch()
        setupCartFab()
    }

    private fun setupCategories() {
        val categories = listOf("All", "Vegetables", "Dairy", "Snacks")
        val adapter = CategoryAdapter(categories) { selectedCategory ->
            viewModel.setCategory(selectedCategory)
        }
        binding.recyclerCategories.adapter = adapter
    }

    private fun setupProducts() {
        val adapter = ProductAdapter { product ->
            viewModel.addToCart(product)
        }
        binding.recyclerProducts.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.products.collectLatest { productList ->
                adapter.submitList(productList)
            }
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.search(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupCartFab() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.cartItems.collectLatest { items ->
                val totalItems = items.sumOf { it.quantity }
                val totalPrice = items.sumOf { it.price * it.quantity }
                binding.fabCart.text = "Cart: $totalItems items (₹$totalPrice)"
                
                if (totalItems > 0) {
                    binding.fabCart.show()
                } else {
                    binding.fabCart.hide()
                }
            }
        }

        binding.fabCart.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_cartFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
