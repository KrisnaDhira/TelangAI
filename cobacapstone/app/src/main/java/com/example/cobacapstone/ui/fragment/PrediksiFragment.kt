package com.example.cobacapstone.ui.fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import com.example.cobacapstone.R
import com.example.cobacapstone.adapter.PhotoAdapter
import com.example.cobacapstone.data.remote.PhotoResponse
import com.example.cobacapstone.data.remote.PredictResponse
import com.example.cobacapstone.databinding.FragmentPrediksiBinding
import com.example.cobacapstone.retrofit.ApiConfig
import com.example.cobacapstone.retrofit.ApiConfigPredict
import com.example.cobacapstone.retrofit.ApiService
import com.example.cobacapstone.ui.activity.HasilPrediksiActivity
import com.example.cobacapstone.viewmodel.PrediksiViewModel
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class PrediksiFragment : Fragment() {

    private var _binding: FragmentPrediksiBinding? = null
    private val binding get() = _binding!!
    private var imageUri: Uri? = null
    private var predictCall: Call<PredictResponse>? = null  // Menyimpan request aktif
    private val viewModel: PrediksiViewModel by viewModels({ requireParentFragment() })
    private var photoUri: Uri? = null
    private val CAMERA_PERMISSION_CODE = 100
    private val REQUEST_IMAGE_CAPTURE = 101
    private lateinit var apiService: ApiService  // Untuk prediksi (model server)
    private lateinit var apiServicePhoto: ApiService  // Untuk foto (app server)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrediksiBinding.inflate(inflater, container, false)
        apiService = ApiConfigPredict.create(requireContext())  // Untuk prediksi
        apiServicePhoto = ApiConfig.create(requireContext())   // Untuk foto

        binding.btnUploadGambar.setOnClickListener {
            showImagePickerDialog()
        }

        binding.btnHapusGambar.setOnClickListener {
            clearSelectedImage()
        }

        binding.btnPrediksi.setOnClickListener {
            submitPrediction()
        }

        binding.ivInfoPrediksi.setOnClickListener {
            showImagePopup()
        }

        binding.tvPrediksi.setOnClickListener {
            showImagePopup()
        }

        binding.tvBenar.visibility = View.GONE
        binding.tvSalah.visibility = View.GONE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe data dari ViewModel
        viewModel.photos.observe(viewLifecycleOwner) { photos ->
            if (photos.isNotEmpty()) {
                binding.rvContohGambar.adapter = PhotoAdapter(photos)
                binding.tvBenar.visibility = View.VISIBLE
                binding.tvSalah.visibility = View.VISIBLE
            } else {
                binding.tvBenar.visibility = View.GONE
                binding.tvSalah.visibility = View.GONE
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressContohGambar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Panggil fetchPhotoExamples hanya jika data belum tersedia
        if (viewModel.photos.value.isNullOrEmpty()) { // Fetch hanya jika data kosong
            viewModel.fetchPhotoExamples()
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        val prefs = requireActivity().getSharedPreferences("PrediksiPrefs", 0)
        val prediksiDibatalkan = prefs.getBoolean("prediksiDibatalkan", false)
        val showErrorToast = prefs.getBoolean("SHOW_ERROR_TOAST", false)
        val kembaliDariHasilPrediksi = prefs.getBoolean("KEMBALI_DARI_HASIL", false)

        if (prediksiDibatalkan) {
            Snackbar.make(binding.root, "Proses prediksi dibatalkan karena anda pergi", Snackbar.LENGTH_SHORT).show()
            prefs.edit().remove("prediksiDibatalkan").apply() // Hapus flag agar tidak tampil terus
        }

        if (showErrorToast) {
            val errorMessage = prefs.getString("KETERANGAN", "Gagal Memprediksi. Mohon coba lagi.")
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
            prefs.edit().remove("SHOW_ERROR_TOAST").apply() // Hapus flag setelah menampilkan toast
        }

        if (kembaliDariHasilPrediksi) {
            clearForm()  // Hapus input nama, deskripsi, dan gambar
            prefs.edit().remove("KEMBALI_DARI_HASIL").apply()
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_PREDICTION && resultCode == Activity.RESULT_OK) {
            clearForm()  // Reset form setelah kembali dari HasilPrediksiActivity

            // Hapus SharedPreferences agar data prediksi sebelumnya tidak tersimpan
            val prefs = requireActivity().getSharedPreferences("PrediksiPrefs", 0).edit()
            prefs.clear().apply()
        }

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_PICK -> {
                    imageUri = data?.data
                }
                REQUEST_IMAGE_CAPTURE -> {
                    imageUri = photoUri
                }
            }

            imageUri?.let {
                val fileName = getFileName(it)
                binding.tvFileName.text = fileName
                binding.layoutFileInfo.visibility = View.VISIBLE
                binding.btnUploadGambar.visibility = View.GONE
                binding.btnHapusGambar.visibility = View.VISIBLE
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(requireContext(), "Izin kamera diperlukan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun submitPrediction() {
        val name = binding.inputNamaLarutan.editText?.text.toString().trim()
        val description = binding.inputDeskripsi.editText?.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Nama larutan wajib diisi!", Toast.LENGTH_SHORT).show()
            return
        }
        if (imageUri == null) {
            Toast.makeText(requireContext(), "Silakan pilih gambar!", Toast.LENGTH_SHORT).show()
            return
        }

        // **Pengecekan format file**
        val fileName = getFileName(imageUri!!)
        if (!fileName.endsWith(".jpg", true) && !fileName.endsWith(".jpeg", true) && !fileName.endsWith(".png", true)) {
            Toast.makeText(requireContext(), "Format gambar tidak sesuai! Harap gunakan JPG, JPEG, atau PNG.", Toast.LENGTH_SHORT).show()
            return
        }

        // Hapus data lama sebelum request baru dimulai
        val prefs = requireActivity().getSharedPreferences("PrediksiPrefs", 0).edit()
        prefs.clear().apply()

        val file = File(requireContext().cacheDir, "temp_image.jpg")
        requireContext().contentResolver.openInputStream(imageUri!!)?.use { input ->
            file.outputStream().use { output -> input.copyTo(output) }
        }

        val intent = Intent(requireContext(), HasilPrediksiActivity::class.java).apply {
            putExtra("NAMA_LARUTAN", name)
            putExtra("DESKRIPSI_LARUTAN", description)
            putExtra("GAMBAR_URI", file.absolutePath)
        }

        startActivity(intent)

        // Kirim request prediksi ke server
        val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

        predictCall = apiService.postPrediction(
            name.toRequestBody("text/plain".toMediaTypeOrNull()),
            description.toRequestBody("text/plain".toMediaTypeOrNull()),
            body
        )

        predictCall?.enqueue(object : Callback<PredictResponse> {
            override fun onResponse(call: Call<PredictResponse>, response: Response<PredictResponse>) {
                if (!isAdded) return  // Cegah crash jika fragment sudah tidak attached

                val prefs = requireActivity().getSharedPreferences("PrediksiPrefs", 0).edit()

                if (response.isSuccessful) {
                    response.body()?.let { predictResult ->
                        prefs.putString("HASIL_PH", predictResult.judulPH)
                        prefs.putString("KETERANGAN", predictResult.info)
                        prefs.putString("HEX_COLOR", predictResult.hex)
                        prefs.putString("URL_GAMBAR", predictResult.urlGambar)
                        prefs.apply()
                    }
                } else {
                    val errorMessage = response.errorBody()?.string()?.let {
                        when {
                            it.contains("Invalid file type") -> "Gagal: Format file tidak didukung (Gunakan JPG, JPEG, PNG)"
                            it.contains("Segmentation failed") -> "Gagal: Tidak terdeteksi larutan"
                            it.contains("File size exceeds") -> "Gagal: File gambar lebih dari 5MB"
                            else -> "Gagal Memprediksi. Mohon coba lagi."
                        }
                    } ?: "Gagal Memprediksi. Mohon coba lagi."

                    prefs.putBoolean("SHOW_ERROR_TOAST", true)
                    prefs.putString("HASIL_PH", "Gagal Memprediksi. Mohon Coba Lagi.")
                    prefs.putString("KETERANGAN", errorMessage)
                    prefs.putString("URL_GAMBAR", R.drawable.baseline_image_24.toString()) // Gambar error
                    prefs.apply()
                }
            }

            override fun onFailure(call: Call<PredictResponse>, t: Throwable) {
                if (!isAdded) return  // Cegah crash jika fragment sudah tidak attached

                val prefs = requireActivity().getSharedPreferences("PrediksiPrefs", 0).edit()
                prefs.putBoolean("SHOW_ERROR_TOAST", true)
                prefs.putString("HASIL_PH", "Gagal Memprediksi")
                prefs.putString("KETERANGAN", "Terjadi kesalahan jaringan. Mohon coba lagi.")
                prefs.apply()
            }
        })


    }

    private fun clearSelectedImage() {
        imageUri = null
        binding.layoutFileInfo.visibility = View.GONE
        binding.btnUploadGambar.visibility = View.VISIBLE
        binding.btnHapusGambar.visibility = View.GONE
    }

    private fun getFileName(uri: Uri): String {
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.moveToFirst()
        val index = cursor?.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME)
        val fileName = cursor?.getString(index ?: 0) ?: "Gambar Terpilih"
        cursor?.close()
        return fileName
    }

    private fun getRealPathFromUri(uri: Uri): String? {
        if ("content".equals(uri.scheme, ignoreCase = true)) {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            requireContext().contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                if (cursor.moveToFirst()) {
                    return cursor.getString(columnIndex)
                }
            }
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Ambil Foto", "Pilih dari Galeri")
        AlertDialog.Builder(requireContext())
            .setTitle("Pilih Gambar")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> requestCameraPermission()
                    1 -> pickImageFromGallery()
                }
            }
            .show()
    }

    private fun openCamera() {
        val photoFile = File(requireContext().getExternalFilesDir(null), "photo.jpg")
        photoUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", photoFile)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)

        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        } else {
            Toast.makeText(requireContext(), "Kamera tidak tersedia", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        } else {
            openCamera()
        }
    }

    private fun clearForm() {
        binding.inputNamaLarutan.editText?.setText("")
        binding.inputDeskripsi.editText?.setText("")
        clearSelectedImage()
    }

    private fun showImagePopup() {
        val dialog = AlertDialog.Builder(requireContext()).create()
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.info_prediksi_popup, null)

        dialogView.findViewById<ImageView>(R.id.iv_popup).setImageResource(R.drawable.infoprediksi1)

        dialog.setView(dialogView)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // Supaya background dialog transparan
        dialog.show()

        // Tutup dialog saat area di luar gambar ditekan
        dialogView.setOnClickListener { dialog.dismiss() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        predictCall?.cancel() // Batalkan request yang masih berjalan
        _binding = null
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 100
        private const val REQUEST_IMAGE_CAPTURE = 101
        private const val REQUEST_PREDICTION = 102
    }
}