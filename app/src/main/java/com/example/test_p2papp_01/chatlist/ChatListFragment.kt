package com.example.test_p2papp_01.chatlist

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test_p2papp_01.DBKey.Companion.DB_CHILD_CHAT
import com.example.test_p2papp_01.DBKey.Companion.DB_USERS
import com.example.test_p2papp_01.R
import com.example.test_p2papp_01.chatroom.ChatRoomActivity
import com.example.test_p2papp_01.databinding.FragmentChatlistBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatListFragment : Fragment(R.layout.fragment_chatlist) {

	private val auth: FirebaseAuth by lazy {
		Firebase.auth
	}

	private val chatRoomList = mutableListOf<ChatListItem>()

	private val chatListAdapter = ChatListAdapter(onItemClicked = {
		val intent = Intent(requireContext(), ChatRoomActivity::class.java)
		intent.putExtra("chatKey", it.key)
		startActivity(intent)
	})


	private var binding: FragmentChatlistBinding? = null
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val fragmentChatListBinding = FragmentChatlistBinding.bind(view)
		binding = fragmentChatListBinding

		chatRoomList.clear()

		binding?.let {
			it.chatListRecyclerView.adapter = chatListAdapter
			it.chatListRecyclerView.layoutManager = LinearLayoutManager(context)
		}

		initChatRoomList()

	}

	private fun initChatRoomList() {
		if (auth.currentUser == null) {
			return
		}
		val chatDB = Firebase.database.reference.child(DB_USERS).child(auth.currentUser!!.uid)
			.child(DB_CHILD_CHAT)

		chatDB.addListenerForSingleValueEvent(object : ValueEventListener {
			override fun onDataChange(snapshot: DataSnapshot) {
				snapshot.children.forEach {
					val model = it.getValue(ChatListItem::class.java)
					model ?: return

					chatRoomList.add(model)
				}
				chatListAdapter.submitList(chatRoomList)
				chatListAdapter.notifyDataSetChanged()
			}

			override fun onCancelled(error: DatabaseError) {}

		})
	}

	override fun onResume() {
		super.onResume()
		chatListAdapter.notifyDataSetChanged()
	}
}