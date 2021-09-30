package com.example.test_p2papp_01.mypage

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.test_p2papp_01.R
import com.example.test_p2papp_01.databinding.FragmentMypageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MyPageFragment : Fragment(R.layout.fragment_mypage) {

	private val auth: FirebaseAuth by lazy {
		Firebase.auth
	}

	private var binding: FragmentMypageBinding? = null
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		val fragMyPageFragment = FragmentMypageBinding.bind(view)
		binding = fragMyPageFragment

		setSignInOutButtonListener()
		setSignUpButtonListener()
		canUseSignUpButton()


	}

	private fun setSignInOutButtonListener() {
		binding?.let { binding ->
			binding.signInOutButton.setOnClickListener {
				val email = binding.emailEditText.text.toString()
				val password = binding.passwordEditText.text.toString()

				if (auth.currentUser == null) {
					// 로그인한다.
					auth.signInWithEmailAndPassword(email, password)
						.addOnCompleteListener { task ->
							if (task.isSuccessful) {
								successSignIn()
							} else {
								Toast.makeText(context, "로그인에 실패함. 이메인 비번 확인", Toast.LENGTH_SHORT)
									.show()
							}
						}
				} else {
					// 로그아웃한다.
					auth.signOut()
					binding.let {
						it.emailEditText.text.clear()
						it.emailEditText.isEnabled = true
						it.passwordEditText.text.clear()
						it.passwordEditText.isEnabled = true

						it.signInOutButton.isEnabled = false
						it.signUpButton.isEnabled = false
						it.signInOutButton.text = "로그인"
					}
				}
			}
		}
	}

	private fun successSignIn() {
		if (auth.currentUser == null) { // 로그인 실패시
			Toast.makeText(context, "로그인에 실패함. 다시 시도해주세요", Toast.LENGTH_SHORT).show()
			return
		}
		binding?.let {
			it.emailEditText.isEnabled = false
			it.passwordEditText.isEnabled = false
			it.signUpButton.isEnabled = false
			it.signInOutButton.text = "로그아웃"
		}
	}

	private fun setSignUpButtonListener() {
		binding?.let { binding ->
			binding.signUpButton.setOnClickListener {
				val email = binding.emailEditText.text.toString()
				val password = binding.passwordEditText.text.toString()

				auth.createUserWithEmailAndPassword(email, password)
					.addOnCompleteListener(requireActivity()) { task ->
						if (task.isSuccessful) {
							Toast.makeText(context, "회원가입에 성공함. 로그인 버튼 누르셈", Toast.LENGTH_SHORT).show()
						} else {
							Toast.makeText(context, "회원가입에 실패함. 이미 가입한 이메일이거나 오류", Toast.LENGTH_SHORT).show()
						}
					}
			}
		}
	}

	// TextChangeListener() 함수로 특정 조건에서만 회원가입 로그인 버튼 사용할 수 있게끔 설정
	private fun canUseSignUpButton() {
		binding?.let { binding ->

			binding.emailEditText.addTextChangedListener {
				val enabled =
					binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()
				binding.signInOutButton.isEnabled = enabled
				binding.signUpButton.isEnabled = enabled
			}
			binding.passwordEditText.addTextChangedListener {
				val enabled =
					binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()
				binding.signInOutButton.isEnabled = enabled
				binding.signUpButton.isEnabled = enabled
			}
		}
	}

	// onStart에서 로그인 풀릴수도 있으니 확인
	override fun onStart() {
		super.onStart()

		if (auth.currentUser == null) {
			binding?.let { binding ->
				binding.emailEditText.text.clear()
				binding.emailEditText.isEnabled = true
				binding.passwordEditText.text.clear()
				binding.passwordEditText.isEnabled = true

				binding.signInOutButton.text = "로그인"
				binding.signInOutButton.isEnabled = false
				binding.signUpButton.isEnabled = false
			}

		} else {
			binding?.let { binding ->
				binding.emailEditText.setText(auth.currentUser!!.email)
				binding.emailEditText.isEnabled = false
				binding.passwordEditText.setText("*********")
				binding.passwordEditText.isEnabled = false

				binding.signInOutButton.text = "로그아웃"
				binding.signInOutButton.isEnabled = true
				binding.signUpButton.isEnabled = false
			}
		}
	}
}