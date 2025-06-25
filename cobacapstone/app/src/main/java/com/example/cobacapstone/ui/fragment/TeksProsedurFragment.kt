package com.example.cobacapstone.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cobacapstone.R
import com.example.cobacapstone.adapter.ExtractAdapter
import com.example.cobacapstone.adapter.MixAdapter
import com.example.cobacapstone.data.remote.ExtractResponse
import com.example.cobacapstone.data.remote.MixResponse
import com.example.cobacapstone.databinding.FragmentTeksProsedurBinding
import com.example.cobacapstone.retrofit.ApiConfig
import com.example.cobacapstone.viewmodel.PanduanViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TeksProsedurFragment : Fragment() {

    private var _binding: FragmentTeksProsedurBinding? = null
    private val binding get() = _binding!!
    private lateinit var extractAdapter: ExtractAdapter
    private lateinit var mixAdapter: MixAdapter
    private lateinit var viewModel: PanduanViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeksProsedurBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[PanduanViewModel::class.java]

        extractAdapter = ExtractAdapter()
        mixAdapter = MixAdapter()

        binding.rvExtract.layoutManager = LinearLayoutManager(requireContext())
        binding.rvExtract.adapter = extractAdapter

        binding.rvMix.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMix.adapter = mixAdapter

        observeViewModel()
        viewModel.fetchExtracts()
        viewModel.fetchMixes()
    }

    private fun observeViewModel() {
        viewModel.extracts.observe(viewLifecycleOwner) { extracts ->
            extractAdapter.submitList(extracts)
            binding.progressExtract.visibility = View.GONE
        }

        viewModel.mixes.observe(viewLifecycleOwner) { mixes ->
            mixAdapter.submitList(mixes)
            binding.progressMix.visibility = View.GONE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressExtract.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.progressMix.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}