package com.example.test_p2papp_01.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test_p2papp_01.DBKey.Companion.DB_ARTICLES
import com.example.test_p2papp_01.DBKey.Companion.DB_CHILD_CHAT
import com.example.test_p2papp_01.DBKey.Companion.DB_USERS
import com.example.test_p2papp_01.R
import com.example.test_p2papp_01.chatlist.ChatListItem
import com.example.test_p2papp_01.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment(R.layout.fragment_home) {

	// auth
	private val auth: FirebaseAuth by lazy {
		Firebase.auth
	}
	private val userDB : DatabaseReference by lazy {
		Firebase.database.reference.child(DB_USERS)
	}

	private lateinit var articleAdapter : ArticleAdapter


	private val articleList = mutableListOf<ArticleModel>()

	// RTDB
	private val articleDB: DatabaseReference by lazy {
		Firebase.database.reference.child(DB_ARTICLES)
	}
	private val listener = object : ChildEventListener {
		override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
			val articleModel = snapshot.getValue(ArticleModel::class.java)
			articleModel ?: return

			articleList.add(articleModel)
			articleAdapter.submitList(articleList)

		}

		override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
		override fun onChildRemoved(snapshot: DataSnapshot) {}
		override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
		override fun onCancelled(error: DatabaseError) {}

	}

	private var binding: FragmentHomeBinding? = null
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val fragmentHomeBinding = FragmentHomeBinding.bind(view)
		binding = fragmentHomeBinding

		articleList.clear()

		setArticleAdapter()
		articleDB.addChildEventListener(listener)

		fragmentHomeBinding.let {
			it.articleRecyclerView.adapter = articleAdapter
			it.articleRecyclerView.layoutManager = LinearLayoutManager(context)
		}
	}

	private fun setArticleAdapter(){
		articleAdapter = ArticleAdapter(onItemClickListener = { articleModel ->
			//로그인 했는지부터 확인 후
			if (auth.currentUser != null) {
				if (auth.currentUser?.uid != articleModel.sellerId) { // 아이디 다를경우
					val chatRoom = ChatListItem(
						buyerId = auth.currentUser!!.uid,
						sellerId = articleModel.sellerId,
						itemTitle = articleModel.title,
						key = System.currentTimeMillis()
					)

					userDB.child(auth.currentUser!!.uid)
						.child(DB_CHILD_CHAT)
						.push()
						.setValue(chatRoom)

					userDB.child(articleModel.sellerId)
						.child(DB_CHILD_CHAT)
						.push()
						.setValue(chatRoom)

					Snackbar.make(binding!!.root, "채팅방이 생성 됨. 채팅 탭 화인.", Snackbar.LENGTH_LONG).show()
				} else { // 아이디 같을경우
					Snackbar.make(binding!!.root, "니가 올림거임.", Snackbar.LENGTH_LONG).show()
				}
			} else {
				// 로그인 안함
				Snackbar.make(binding!!.root, "로그인 후 사용해 주세요.", Snackbar.LENGTH_LONG).show()
			}
		})
	}


	@SuppressLint("NotifyDataSetChanged")
	override fun onResume() {
		super.onResume()
		articleAdapter.notifyDataSetChanged()
	}

	override fun onDestroyView() {
		super.onDestroyView()
		articleDB.removeEventListener(listener)
	}
}