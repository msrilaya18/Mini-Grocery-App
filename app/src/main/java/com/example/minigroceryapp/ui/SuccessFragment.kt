package com.example.minigroceryapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.minigroceryapp.R
import com.example.minigroceryapp.databinding.FragmentSuccessBinding
import com.example.minigroceryapp.viewmodel.MainViewModel
import kotlin.random.Random

class SuccessFragment : Fragment() {

    private var _binding: FragmentSuccessBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        
        val orderId = Random.nextInt(100000, 999999)
        binding.tvOrderId.text = "Order ID: #$orderId"

        binding.btnBackHome.setOnClickListener {
            viewModel.clearCart()
            findNavController().popBackStack(R.id.homeFragment, false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
