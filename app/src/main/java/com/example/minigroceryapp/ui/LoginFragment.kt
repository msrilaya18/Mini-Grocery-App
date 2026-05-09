package com.example.minigroceryapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.minigroceryapp.R
import com.example.minigroceryapp.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            val mobile = binding.etMobile.text.toString().trim()
            val otp = binding.etOtp.text.toString().trim()

            // Phone Validation
            if (mobile.length != 10) {
                binding.mobileInputLayout.error = "Enter valid 10-digit number"
                return@setOnClickListener
            } else {
                binding.mobileInputLayout.error = null
            }

            // Fake OTP Verification (per assignment logic)
            if (otp != "1234") {
                binding.otpInputLayout.error = "Invalid OTP. Use 1234"
                return@setOnClickListener
            } else {
                binding.otpInputLayout.error = null
            }

            // Navigate to Home Component on Success
            Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
