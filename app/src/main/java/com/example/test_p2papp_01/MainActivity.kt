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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/* xo 흐름
* 1. 각각의 Fragment와 NavigationBar를 연결
* 2. RealTime DB 구축 (addChildEventListener) 후 RecyclerView submitList 잘 되는지 테스트
* 3. 실제 아이템을 등록하면 Storage와 RealTime Database에 저장 시키기 구현
* 4. Auth를 통해 회원가입과 로그인을 관리하고 고유의 sellerId부여. 모든 작업을 로그인을 해야 할수 있게끔 설정
* 5. 등록된 아이템 클릭하면 ArticleModel에 저장되어있던 정보들을 ChatListItem에 다시 저장하고 Firebase RealTime Database / UserDB에 저장 -> 리스트 생성
* 6. DB에 ChatListItem 불러와 ChatList Fragment에서 리스트 생성
* 7. ChatList Fragment에서 Item 클릭하면 ChatRoom으로 입장. 이떄 DB에는 내 아이디와 message 내용 저장*/

/* xo 이렇게 흐름을 바꿀수도 있겠다.
*  1. 각각의 Fragment와 NavigationBar를 연결
*  2. Auth를 통해 회원가입과 로그인을 관리하고 고유의 sellerId부여. 모든 작업을 로그인을 해야 할수 있게끔 설정
*  3. 실제 아이템을 등록하면 Storage와 RealTime Database에 저장 시키기 구현
*  4. RealTime DB 구축 (addChildEventListener) 후 RecyclerView submitList 잘 되는지 테스트
*  5. 등록된 아이템 클릭하면 ArticleModel에 저장되어있던 정보들을 ChatListItem에 다시 저장하고 Firebase RealTime Database / UserDB에 저장
*  6. DB에 ChatListItem 불러와 ChatList Fragment에서 리스트 생성
*  7. ChatList Fragment에서 Item 클릭하면 ChatRoom으로 입장. 이떄 DB에는 내 아이디와 message 내용 저장*/

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