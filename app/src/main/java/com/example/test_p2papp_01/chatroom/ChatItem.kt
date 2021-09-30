package com.example.fastcampus_intermediate_p2papp_until_realtimedb.chatdetail

data class ChatItem(
	val senderId : String,
	val message : String,

){
	constructor() : this("","")
}
