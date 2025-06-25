package com.example.cobacapstone.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.cobacapstone.R

class BiodataActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_biodata)

        val backBtn = findViewById<ImageView>(R.id.btn_back)
        backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Danar
        findViewById<ImageView>(R.id.btn_github_1).setOnClickListener {
            openUrl("https://github.com/DanarBaadilla")
        }
        findViewById<ImageView>(R.id.btn_linkedin_1).setOnClickListener {
            openUrl("https://www.linkedin.com/in/danar-rafiardi/")
        }

        // Satyananda
        findViewById<ImageView>(R.id.btn_github_2).setOnClickListener {
            openUrl("https://github.com/SatyanandaGautama")
        }
        findViewById<ImageView>(R.id.btn_linkedin_2).setOnClickListener {
            openUrl("https://www.linkedin.com/in/i-gede-satyananda-gautama-3429052ba/")
        }

        // Krisna
        findViewById<ImageView>(R.id.btn_github_3).setOnClickListener {
            openUrl("https://github.com/KrisnaDhira")
        }
        findViewById<ImageView>(R.id.btn_linkedin_3).setOnClickListener {
            openUrl("https://www.linkedin.com/in/krisna-dhira-1643b0249/")
        }
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}
