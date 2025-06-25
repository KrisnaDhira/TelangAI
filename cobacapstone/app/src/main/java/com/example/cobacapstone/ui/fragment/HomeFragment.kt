package com.example.cobacapstone.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cobacapstone.R
import com.example.cobacapstone.adapter.ArticleAdapter
import com.example.cobacapstone.adapter.BannerAdapter
import com.example.cobacapstone.adapter.ProductAdapter
import com.example.cobacapstone.adapter.StoreAdapter
import com.example.cobacapstone.data.remote.ArticlesItem
import com.example.cobacapstone.databinding.FragmentHomeBinding
import com.example.cobacapstone.ui.activity.ArtikelFullActivity
import com.example.cobacapstone.ui.activity.BiodataActivity
import com.example.cobacapstone.ui.activity.DetailActivity
import com.example.cobacapstone.viewmodel.MainViewModel
import com.example.cobacapstone.viewmodel.MainViewModelFactory
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var isSnackbarShown = false
    private var snackbar: Snackbar? = null

    private lateinit var mainViewModel: MainViewModel
    private lateinit var bannerAdapter: BannerAdapter
    private lateinit var storeAdapter: StoreAdapter
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvWelcomeMessage: TextView = view.findViewById(R.id.tv_welcome_message)
        val tvAppName: TextView = view.findViewById(R.id.tv_app_name)
        val ivSetting: ImageView = view.findViewById(R.id.iv_setting)

        // event listener jika ikon setting diklik
        ivSetting.setOnClickListener {
            val intent = Intent(requireContext(), BiodataActivity::class.java)
            startActivity(intent)
        }

        mainViewModel = ViewModelProvider(
            this,
            MainViewModelFactory(requireActivity().application)
        )[MainViewModel::class.java]

        setupRecyclerViews()

        // Adapter untuk Rekomendasi Produk dalam format grid 2x2
        productAdapter = ProductAdapter(requireContext(), this) { product ->
        }

        // Adapter untuk Banner
        bannerAdapter = BannerAdapter(requireContext()) { banner ->
            when (banner.bannerId) {
                "B001" -> findNavController().navigate(R.id.navigation_panduan)
                "B002" -> findNavController().navigate(R.id.navigation_prediksi)
                else -> Snackbar.make(binding.root, "Banner tidak memiliki tujuan.", Snackbar.LENGTH_SHORT).show()
            }
        }
        binding.viewpagerBanner.adapter = bannerAdapter

//        binding.recyclerViewRecommendedProducts.layoutManager = GridLayoutManager(context, 2)
        binding.recyclerViewRecommendedProducts.layoutManager = GridLayoutManager(context, 2)
        binding.recyclerViewRecommendedProducts.adapter = productAdapter

        binding.progressBarBanner.visibility = View.VISIBLE
        binding.progressBarActiveEvents.visibility = View.VISIBLE
        binding.progressBarFinishedEvents.visibility = View.VISIBLE
        binding.progressBarRecommendedProducts.visibility = View.VISIBLE

        binding.tvSeeAllFinishedEvents.setOnClickListener {
            val intent = Intent(requireContext(), ArtikelFullActivity::class.java)
            startActivity(intent)
        }

        //observe data banners
        //val bannerEvents = events.take(2)
        mainViewModel.banners.observe(viewLifecycleOwner) { banners ->
            bannerAdapter.submitList(banners)
            binding.progressBarBanner.visibility = View.GONE

            // Attach dotsIndicator hanya setelah adapter punya data
            if (banners.isNotEmpty()) {
                binding.dotIndicator.attachTo(binding.viewpagerBanner)
            }
        }

        binding.viewpagerBanner.apply {
            offscreenPageLimit = 1
            clipToPadding = false
            clipChildren = false
            setPadding(40, 0, 40, 0) // Atur padding kiri dan kanan agar banner berikutnya terlihat
            setPageTransformer { page, position ->
                val scaleFactor = 0.85f + (1 - kotlin.math.abs(position)) * 0.15f
                page.scaleY = scaleFactor
                page.alpha = scaleFactor
            }
        }

        mainViewModel.stores.observe(viewLifecycleOwner) { stores ->
            storeAdapter.submitList(stores) // Gunakan storeAdapter hanya untuk toko
            binding.progressBarActiveEvents.visibility = View.GONE
        }

        mainViewModel.products.observe(viewLifecycleOwner) { products ->
            productAdapter.submitList(products) // Gunakan productAdapter hanya untuk produk
            binding.progressBarRecommendedProducts.visibility = View.GONE
        }

        mainViewModel.articles.observe(viewLifecycleOwner) { articles ->
            articleAdapter.submitList(articles)
            binding.progressBarFinishedEvents.visibility = View.GONE
        }

        // Observe state loading untuk menampilkan loading indicator
        mainViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        // Observe pesan error jika ada masalah dalam pengambilan data
        mainViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
            }
        }

//        showDelayedSnackbar()
        mainViewModel.fetchBanners() // Mengambil data

        mainViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (!isLoading) {
                snackbar?.dismiss() // Tutup snackbar ketika data sudah dimuat
            }
        }
    }

    private fun setupRecyclerViews() {
        // Setup ViewPager2 untuk Banner
        bannerAdapter = BannerAdapter(requireContext()) {}
        binding.viewpagerBanner.adapter = bannerAdapter

        // Hubungkan dengan DotsIndicator
        binding.dotIndicator.attachTo(binding.viewpagerBanner)

        // Adapter untuk store dalam horizontal
        storeAdapter = StoreAdapter(requireContext(), this) { store ->
            // Tidak perlu intent langsung, karena sudah di handle di adapter
        }
        binding.recyclerViewActiveEvents.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewActiveEvents.adapter = storeAdapter

        // Adapter untuk artikel dalam format vertikal biasa
        articleAdapter = ArticleAdapter(requireContext()) { article ->
            openDetailActivity(article)
        }
        binding.recyclerViewFinishedEvents.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewFinishedEvents.adapter = articleAdapter
    }

    private fun showLoading(isLoading: Boolean) {
    }

    private fun openStoreLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun openDetailActivity(article: ArticlesItem) {
        val intent = Intent(requireContext(), DetailActivity::class.java).apply {
            putExtra("article", article)
        }
        startActivity(intent)
    }

//    private fun showDelayedSnackbar() {
//        Handler(Looper.getMainLooper()).postDelayed({
//            if (mainViewModel.isLoading.value == true && !isSnackbarShown) {
//                snackbar = Snackbar.make(binding.root, "Mohon menunggu, server sedang diaktifkan...", Snackbar.LENGTH_INDEFINITE)
//                snackbar?.show()
//                isSnackbarShown = true
//            }
//        }, 12000)
//    }


    private fun openProductLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}