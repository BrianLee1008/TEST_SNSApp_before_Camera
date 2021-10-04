package com.example.test_p2papp_01

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.test_p2papp_01.chatlist.ChatListFragment
import com.example.test_p2papp_01.databinding.ActivityMainBinding
import com.example.test_p2papp_01.home.AddArticleActivity
import com.example.test_p2papp_01.home.HomeFragment
import com.example.test_p2papp_01.mypage.MyPageFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

	private val auth : FirebaseAuth by lazy {
		Firebase.auth
	}


	private lateinit var binding : ActivityMainBinding
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)

		val homeFragment = HomeFragment()
		val chatListFragment = ChatListFragment()
		val myPageFragment = MyPageFragment()

		replaceFragment(homeFragment)

		binding.bottomNavigationBar.setOnItemSelectedListener {
			when (it.itemId) {
				R.id.home -> replaceFragment(homeFragment)
				R.id.chatList -> replaceFragment(chatListFragment)
				R.id.myPage -> replaceFragment(myPageFragment)
			}
			return@setOnItemSelectedListener true
		}

		binding.bottomNavigationBar.background = null
		binding.bottomNavigationBar.menu.getItem(2).isEnabled = false

		startAddArticleActivity()

	}

	private fun replaceFragment(fragment: Fragment) {
		supportFragmentManager.beginTransaction().apply {
			replace(R.id.fragmentContainer, fragment)
			commit()
		}
	}

	private fun startAddArticleActivity(){
		binding.addFloatingButton.setOnClickListener {
			if (auth.currentUser != null) {
				startActivity(Intent(this, AddArticleActivity::class.java))
			} else {
				Snackbar.make(binding.root, "로그인 후 사용해 주세요.", Snackbar.LENGTH_LONG).show()
				return@setOnClickListener
			}
		}
	}



}