package com.example.cobacapstone.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.cobacapstone.adapter.PanduanPagerAdapter
import com.example.cobacapstone.databinding.FragmentPanduanBinding
import com.example.cobacapstone.viewmodel.PanduanViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator

class PanduanFragment : Fragment() {

    private var _binding: FragmentPanduanBinding? = null
    private val binding get() = _binding!!
    private var isSnackbarShown = false
    private var snackbar: Snackbar? = null
    private lateinit var viewModel: PanduanViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPanduanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = PanduanPagerAdapter(this)
        binding.viewPager.adapter = adapter

        // Attach TabLayout dengan ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Teks Prosedur"
                1 -> "Video"
                else -> ""
            }
        }.attach()

        viewModel = ViewModelProvider(requireActivity())[PanduanViewModel::class.java]

        showDelayedSnackbar()

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (!isLoading) {
                snackbar?.dismiss()
            }
        }
    }

    private fun showDelayedSnackbar() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (viewModel.isLoading.value == true && !isSnackbarShown) {
                snackbar = Snackbar.make(binding.root, "Mohon menunggu, server sedang diaktifkan...", Snackbar.LENGTH_INDEFINITE)
                snackbar?.show()
                isSnackbarShown = true
            }
        }, 12000) // Tampilkan setelah 12 detik
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}