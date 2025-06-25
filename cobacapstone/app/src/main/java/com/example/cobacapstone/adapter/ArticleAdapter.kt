package com.example.cobacapstone.adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cobacapstone.R
import com.example.cobacapstone.data.remote.ArticlesItem

class ArticleAdapter(
    private val context: Context,
    private val itemClickListener: (ArticlesItem) -> Unit
) : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    private var articles: List<ArticlesItem> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_row_image, parent, false)
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]
        Glide.with(context)
            .load(article.urlGambar)
            .centerCrop()
            .placeholder(R.drawable.baseline_image_24)
            .error(R.drawable.baseline_broken_image_24)
            .into(holder.articleImage)

        holder.itemText.text = article.judulArtikel
        holder.itemView.setOnClickListener { itemClickListener(article) }
        holder.itemDescription.text = Html.fromHtml(article.overview, Html.FROM_HTML_MODE_LEGACY)
    }

    override fun getItemCount(): Int = articles.size

    fun submitList(newArticles: List<ArticlesItem>) {
        val diffCallback = ArticlesDiffCallback(articles, newArticles)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        articles = newArticles
        diffResult.dispatchUpdatesTo(this)
    }

    class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val articleImage: ImageView = itemView.findViewById(R.id.item_Image)
        val itemText: TextView = itemView.findViewById(R.id.item_Text)
        val itemDescription: TextView = itemView.findViewById(R.id.item_Description)
    }

    class ArticlesDiffCallback(
        private val oldList: List<ArticlesItem>,
        private val newList: List<ArticlesItem>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].articleId == newList[newItemPosition].articleId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}