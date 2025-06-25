package com.example.cobacapstone.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cobacapstone.adapter.HistoriAdapter
import com.example.cobacapstone.data.remote.HistoryItem
import com.example.cobacapstone.data.remote.HistoryResponse
import com.example.cobacapstone.databinding.FragmentHistoriBinding
import com.example.cobacapstone.retrofit.ApiConfig
import com.example.cobacapstone.ui.activity.DetailHistoryActivity
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoriFragment : Fragment() {

    private var _binding: FragmentHistoriBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: HistoriAdapter
    private var originalHistoryList: List<HistoryItem> = listOf() // Data asli
    private var apiCall: Call<HistoryResponse>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoriBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("HistoriFragment", "onViewCreated: Fragment dimulai")
        binding.recyclerViewHistory.setHasFixedSize(true)

        adapter = HistoriAdapter(listOf()) { historyId ->
            val intent = Intent(requireContext(), DetailHistoryActivity::class.java).apply {
                putExtra("HISTORY_ID", historyId) // Kirim historyId ke DetailHistoryActivity
            }
            startActivity(intent)
        }
        binding.recyclerViewHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewHistory.adapter = adapter

        // Selalu fetch ulang data saat fragment dibuka
        fetchHistory()


        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterHistory(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterHistory(newText)
                return true
            }
        })
    }

    private fun fetchHistory() {
        if (!isAdded) return // Pastikan fragment masih attached sebelum mulai fetch
        Log.d("HistoriFragment", "fetchHistory: Memulai fetch data")

        binding.tvNoData.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerViewHistory.visibility = View.GONE
        binding.tvNoSearchData.visibility = View.GONE

        val apiService = ApiConfig.create(requireContext())
        apiCall = apiService.getHistory(null)

        apiCall?.enqueue(object : Callback<HistoryResponse> {
            override fun onResponse(call: Call<HistoryResponse>, response: Response<HistoryResponse>) {
                if (!isAdded || _binding == null) return // Pastikan fragment masih ada
                Log.d("HistoriFragment", "onResponse: API merespons, sukses = ${response.isSuccessful}")

                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    originalHistoryList = response.body()?.history ?: emptyList()
                    Log.d("HistoriFragment", "onResponse: Data diterima, jumlah item = ${originalHistoryList.size}")


                    if (originalHistoryList.isEmpty()) {
                        Log.d("HistoriFragment", "onResponse: Tidak ada data, menampilkan tvNoData")
                        binding.recyclerViewHistory.visibility = View.GONE
                        binding.tvNoData.visibility = View.VISIBLE
                    } else {
                        Log.d("HistoriFragment", "onResponse: Data tersedia, menampilkan recyclerView")
                        binding.tvNoData.visibility = View.GONE
                        binding.recyclerViewHistory.visibility = View.VISIBLE
                        adapter.updateData(originalHistoryList)
                    }
                } else {
                    Log.e("HistoriFragment", "onResponse: Response error, menampilkan showError()")
                    showError()
                }
            }

            override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
                if (!isAdded || _binding == null) return // Hindari update UI jika fragment sudah dihancurkan
                Log.e("HistoriFragment", "onFailure: Fetch data gagal, menampilkan Snackbar", t)

                try {

                    showServerWaitingSnackbar()
                    showError()
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    binding.progressBar.visibility = View.GONE
                }
            }
        })

        // Jika butuh waktu lama, beri tahu pengguna bahwa server sedang aktif kembali
        binding.root.postDelayed({
            if (isAdded && _binding != null && binding.progressBar.visibility == View.VISIBLE) {
                Log.w("HistoriFragment", "fetchHistory: Server masih loading, menampilkan Snackbar")
                showServerWaitingSnackbar()
            }
        }, 15000) // 15 detik
    }

    private fun filterHistory(query: String?) {
        if (query.isNullOrEmpty()) {
            adapter.updateData(originalHistoryList)
            val isEmpty = originalHistoryList.isEmpty()
            binding.recyclerViewHistory.visibility = if (isEmpty) View.GONE else View.VISIBLE
            binding.tvNoData.visibility = if (isEmpty) View.VISIBLE else View.GONE
            binding.tvNoSearchData.visibility = View.GONE
        } else {
            val filteredList = originalHistoryList.filter {
                it.name?.contains(query, ignoreCase = true) == true ||
                        it.pH?.contains(query, ignoreCase = true) == true
            }
            adapter.updateData(filteredList)

            val isSearchEmpty = filteredList.isEmpty()
            binding.recyclerViewHistory.visibility = if (isSearchEmpty) View.GONE else View.VISIBLE
            binding.tvNoSearchData.visibility = if (isSearchEmpty) View.VISIBLE else View.GONE
            binding.tvNoData.visibility = View.GONE
        }
    }


    private fun showError() {
        Log.e("HistoriFragment", "showError: Menampilkan tvNoData")
        binding.tvNoData.visibility = View.VISIBLE
        binding.recyclerViewHistory.visibility = View.GONE
        binding.progressBar.visibility = View.GONE // Tambahkan agar progress bar di-hide jika terjadi error
    }

    private fun showServerWaitingSnackbar() {
        if (!isAdded || _binding == null) return // Pastikan fragment masih ada sebelum menampilkan Snackbar
    }

    override fun onPause() {
        super.onPause()
        binding.searchView.setQuery("", false) // Hapus teks pencarian
        binding.searchView.clearFocus() // Hilangkan fokus dari SearchView
    }

    override fun onResume() {
        super.onResume()
        binding.tvNoData.visibility = View.GONE
        Log.d("HistoriFragment", "onResume: Memanggil fetchHistory() ulang")
        fetchHistory() // Fetch ulang data saat kembali ke fragment
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("HistoriFragment", "onDestroyView: Fragment dihancurkan, membatalkan API call")
        apiCall?.cancel() // Batalkan request jika fragment dihancurkan
        _binding = null
    }
}
