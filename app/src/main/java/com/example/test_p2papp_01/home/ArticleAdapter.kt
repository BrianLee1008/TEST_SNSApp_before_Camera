package com.example.test_p2papp_01.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.test_p2papp_01.databinding.ItemArticleBinding
import java.text.SimpleDateFormat
import java.util.*

class ArticleAdapter(val onItemClickListener: (ArticleModel) -> Unit) :
	ListAdapter<ArticleModel, ArticleAdapter.ViewHolder>(diffUtil) {

	inner class ViewHolder(private val binding: ItemArticleBinding) :
		RecyclerView.ViewHolder(binding.root) {
		fun bind(articleModel: ArticleModel) {
			binding.run {
				val format = SimpleDateFormat("MM월 dd일")
				val date = Date(articleModel.createAt)

				titleTextView.text = articleModel.title
				priceTextView.text = articleModel.price
				dateTextView.text = format.format(date).toString()

				if (articleModel.imageUrl.isNotEmpty()) {
					Glide.with(thumbnailImageView)
						.load(articleModel.imageUrl)
						.into(thumbnailImageView)
				}
				binding.root.setOnClickListener {
					onItemClickListener(articleModel)
				}


			}
		}


	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
		ItemArticleBinding.inflate(
			LayoutInflater.from(
				parent.context
			),
			parent,
			false
		)
	)

	override fun onBindViewHolder(holder: ViewHolder, position: Int) =
		holder.bind(currentList[position])

	companion object {
		val diffUtil = object : DiffUtil.ItemCallback<ArticleModel>() {
			override fun areItemsTheSame(oldItem: ArticleModel, newItem: ArticleModel) =
				oldItem.createAt == newItem.createAt

			override fun areContentsTheSame(oldItem: ArticleModel, newItem: ArticleModel) =
				oldItem == newItem
		}
	}
}