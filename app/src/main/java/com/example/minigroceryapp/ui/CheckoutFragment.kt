package com.example.minigroceryapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.minigroceryapp.R
import com.example.minigroceryapp.databinding.FragmentCheckoutBinding

class CheckoutFragment : Fragment() {

    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnPlaceOrder.setOnClickListener {
            val address = binding.etAddress.text.toString().trim()
            if (address.isEmpty()) {
                binding.addressInputLayout.error = "Please enter delivery address"
                return@setOnClickListener
            }

            findNavController().navigate(R.id.action_checkoutFragment_to_successFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
