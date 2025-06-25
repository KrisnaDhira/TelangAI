package com.example.cobacapstone.ui.fragment

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.cobacapstone.R
import com.example.cobacapstone.data.remote.StoresItem

class StorePopupFragment(private val store: StoresItem) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_store_popup, container, false)

        val tvStoreName: TextView = view.findViewById(R.id.tv_store_name)
        val btnVisitStore: Button = view.findViewById(R.id.btn_visit_store)

        tvStoreName.text = store.namaToko

        btnVisitStore.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(store.urlToko))
            startActivity(intent)
            dismiss()
        }

        // Tutup dialog jika klik di luar
        view.setOnClickListener { dismiss() }

        return view
    }
}