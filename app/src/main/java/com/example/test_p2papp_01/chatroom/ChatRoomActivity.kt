package com.example.test_p2papp_01.chatroom

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fastcampus_intermediate_p2papp_until_realtimedb.chatdetail.ChatItem
import com.example.fastcampus_intermediate_p2papp_until_realtimedb.chatdetail.ChatItemAdapter
import com.example.test_p2papp_01.DBKey.Companion.DB_CHAT
import com.example.test_p2papp_01.databinding.ActivityChatroomBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatRoomActivity : AppCompatActivity() {

	private val auth: FirebaseAuth by lazy {
		Firebase.auth
	}
	private lateinit var chatDB: DatabaseReference
	private val chatRoomAdapter = ChatItemAdapter()

	private val chatList = mutableListOf<ChatItem>()


	private lateinit var binding: ActivityChatroomBinding
	override fun onCreate(savedInstanceState: Bundle?) {
		binding = ActivityChatroomBinding.inflate(layoutInflater)
		super.onCreate(savedInstanceState)
		setContentView(binding.root)

		binding.let {
			it.chatRoomRecyclerView.adapter = chatRoomAdapter
			it.chatRoomRecyclerView.layoutManager = LinearLayoutManager(this)
		}

		setChatRoomDB()



	}

	private fun setChatRoomDB(){

		val chatKey = intent.getLongExtra("chatKey", -1)

		// DB에서 정보 가져올떄
		chatDB = Firebase.database.reference.child(DB_CHAT).child("$chatKey")

		// 샌드버튼 누르면 DB에 정보 입력
		binding.sendButton.setOnClickListener {
			val chatItem = ChatItem(
				senderId = auth.currentUser!!.uid,
				message = binding.messageEditText.text.toString()
			)

			chatDB.push().setValue(chatItem)
			binding.messageEditText.text.clear()
		}

		//DB에 정보가 추가될때마다 Listener가 받아서 RecyclerView 갱신
		chatDB.addChildEventListener(object : ChildEventListener{
			override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
				val chat = snapshot.getValue(ChatItem::class.java)
				chat ?: return

				chatList.add(chat)
				chatRoomAdapter.submitList(chatList)
				chatRoomAdapter.notifyDataSetChanged()

			}
			override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
			override fun onChildRemoved(snapshot: DataSnapshot) {}
			override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
			override fun onCancelled(error: DatabaseError) {}

		})
	}
}