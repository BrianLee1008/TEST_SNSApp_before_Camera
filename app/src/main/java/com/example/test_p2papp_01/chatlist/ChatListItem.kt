package com.example.test_p2papp_01.chatlist

data class ChatListItem(
	val buyerId: String,
	val sellerId: String,
	val itemTitle: String,
	val key: Long
) {
	constructor() : this("", "", "", 0)
}