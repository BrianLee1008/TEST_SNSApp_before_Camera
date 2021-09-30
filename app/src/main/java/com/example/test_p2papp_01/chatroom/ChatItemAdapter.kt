package com.example.fastcampus_intermediate_p2papp_until_realtimedb.chatdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fastcampus_intermediate_p2papp_until_realtimedb.chatdetail.ChatItemAdapter.ChatItemViewHolder
import com.example.test_p2papp_01.databinding.ItemChatBinding

class ChatItemAdapter : ListAdapter<ChatItem, ChatItemViewHolder>(diffUtil) {

	inner class ChatItemViewHolder(private val binding: ItemChatBinding) :
		RecyclerView.ViewHolder(binding.root) {

		fun bind(chatItem: ChatItem) {
			binding.run {
				senderTextView.text = chatItem.senderId
				messageTextView.text = chatItem.message

			}
		}

	}


	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatItemViewHolder {
		return ChatItemViewHolder(
			ItemChatBinding.inflate(
				LayoutInflater.from(parent.context),
				parent,
				false
			)
		)

	}

	override fun onBindViewHolder(holder: ChatItemViewHolder, position: Int) {
		holder.bind(currentList[position])
	}


	companion object {
		val diffUtil = object : DiffUtil.ItemCallback<ChatItem>() {
			override fun areItemsTheSame(oldItem: ChatItem, newItem: ChatItem) =
				oldItem == newItem

			override fun areContentsTheSame(oldItem: ChatItem, newItem: ChatItem) =
				oldItem == newItem

		}
	}
}
