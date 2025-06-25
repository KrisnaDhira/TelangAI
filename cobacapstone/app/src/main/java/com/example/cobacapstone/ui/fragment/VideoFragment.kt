package com.example.cobacapstone.ui.fragment

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.cobacapstone.databinding.FragmentVideoBinding
import com.example.cobacapstone.viewmodel.VideoViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class VideoFragment : Fragment() {
    private var _binding: FragmentVideoBinding? = null
    private val binding get() = _binding!!
    private val videoViewModel: VideoViewModel by viewModels()
    private var youTubePlayerInstance: YouTubePlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressBar = binding.progressBar
        val youtubePlayerView: YouTubePlayerView = binding.youtubePlayerView
        val videoCard = binding.youtubePlayerView.parent as CardView
        val tvVideoLink = binding.tvVideoLink

        lifecycle.addObserver(youtubePlayerView)

        videoViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            videoCard.visibility = View.VISIBLE
        }

        videoViewModel.videoUrl.observe(viewLifecycleOwner) { url ->
            if (!url.isNullOrEmpty()) {
                val videoId = extractYouTubeVideoId(url)

                youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youTubePlayerInstance = youTubePlayer
                        youTubePlayer.cueVideo(videoId, 0f)
                    }
                })
            }
        }

        // Menambahkan tampilan daftar bagian video dengan bullet points dan underline
        setVideoLinks(tvVideoLink)

        if (videoViewModel.videoUrl.value.isNullOrEmpty()) {
            videoViewModel.fetchVideoUrl()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.youtubePlayerView.release()
        _binding = null
    }

    private fun extractYouTubeVideoId(url: String): String {
        val regex = Regex("(?:https?://)?(?:www\\.)?(?:youtube\\.com/(?:[^/]+/.+/|(?:v|e(?:mbed)?)/|.*[?&]v=)|youtu\\.be/)([^\"&?/ ]{11})")
        return regex.find(url)?.groups?.get(1)?.value ?: ""
    }

    private fun setVideoLinks(textView: TextView) {
        val videoLinks = listOf(
            "• Pembuatan Ekstrak Bunga Telang" to 7,
            "• Pencampuran Larutan Uji" to 24,
            "• Penggunaan Fitur Prediksi" to 43,
            "• Penjelasan Fitur Lainnya" to 65
        )

        val spannableString = SpannableString(videoLinks.joinToString("\n\n") { it.first })

        var start = 0
        for ((text, seconds) in videoLinks) {
            val end = start + text.length
            spannableString.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    youTubePlayerInstance?.seekTo(seconds.toFloat())
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = true // Tambahkan underline
                    ds.color = Color.parseColor("#00796B") // Warna lebih terlihat seperti hyperlink
                }
            }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            start = end + 2
        }

        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()
    }
}
