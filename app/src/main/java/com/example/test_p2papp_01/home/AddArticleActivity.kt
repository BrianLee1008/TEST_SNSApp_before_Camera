package com.example.test_p2papp_01.home

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.test_p2papp_01.DBKey.Companion.DB_ARTICLES
import com.example.test_p2papp_01.DBKey.Companion.STORAGE
import com.example.test_p2papp_01.R
import com.example.test_p2papp_01.databinding.ActivityAddArticleBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

@Suppress("DEPRECATION")
class AddArticleActivity : AppCompatActivity() {

	private var selectedUri: Uri? = null

	private val auth: FirebaseAuth by lazy {
		Firebase.auth
	}
	private val articleDB: DatabaseReference by lazy {
		Firebase.database.reference.child(DB_ARTICLES)
	}
	private val storage: FirebaseStorage by lazy {
		Firebase.storage
	}


	private lateinit var binding: ActivityAddArticleBinding
	override fun onCreate(savedInstanceState: Bundle?) {
		binding = ActivityAddArticleBinding.inflate(layoutInflater)
		super.onCreate(savedInstanceState)
		setContentView(binding.root)

		setImageAddButtonListener()
		saveReferenceInDB()


	}

	// 이미지 등록버튼 리스너
	private fun setImageAddButtonListener() {
		binding.imageAddButton.setOnClickListener {
			when {
				// 처음 권한 승인 체크하고 승인 되있다면 ContentProvider
				ContextCompat.checkSelfPermission(
					this,
					android.Manifest.permission.READ_EXTERNAL_STORAGE
				) == PackageManager.PERMISSION_GRANTED -> {
					startContentProvider()
				}
				// 이미 거부되어서 교육용 팝업이 필요
				shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
					showPermissionContextPopup()
				}
				else -> {
					// 승인 요청
					requestPermissions(
						arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
						1010
					)
				}
			}
		}
	}


	// 권한 요청
	override fun onRequestPermissionsResult(
		requestCode: Int,
		permissions: Array<out String>,
		grantResults: IntArray
	) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		when (requestCode) {
			1010 -> {
				if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					startContentProvider()
				} else {
					Toast.makeText(this, "권한 거부됨", Toast.LENGTH_SHORT).show()
				}
			}
		}
	}

	// 갤러리 시작
	private fun startContentProvider() {
		val intent = Intent(Intent.ACTION_GET_CONTENT)
		intent.type = "image/*"
		startActivityForResult(intent, 2020)
	}

	// 갤러리 이동 이후 액션
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		when (requestCode) {
			2020 -> {
				val uri = data?.data
				if (uri != null) {
					findViewById<ImageView>(R.id.photoImageView).setImageURI(uri)
					selectedUri = uri
				} else {
					Toast.makeText(this, "사진 거부됨", Toast.LENGTH_SHORT).show()
				}
			}
			else -> {
				Toast.makeText(this, "사진 거부됨", Toast.LENGTH_SHORT).show()
			}
		}
	}


	// 교육용 다이얼로그
	private fun showPermissionContextPopup() {
		AlertDialog.Builder(this)
			.setTitle("권한이 필요합니다.")
			.setMessage("사진을 가져오기 위해 필요합니다.")
			.setPositiveButton("동의") { _, _ ->
				requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1010)
			}
			.setNegativeButton("거부") { _, _ ->
				return@setNegativeButton
			}
			.create().show()
	}


	// 이미지 업로드
	private fun saveReferenceInDB() {
		// 이미지 등록 버튼 누르면 storage에 저장, Uri는 DB에 저장
		binding.submitButton.setOnClickListener {
			val title = binding.titleEditText.text.toString()
			val price = binding.priceEditText.text.toString()
			val sellerId = auth.currentUser?.uid.orEmpty()

			showProgress()

			if (selectedUri != null) {
				val photoUri = selectedUri ?: return@setOnClickListener
				uploadPhoto(photoUri,
					successHandler = { uri ->
						uploadArticles(title, price, sellerId, uri)
					},
					errorHandler = {
						Toast.makeText(this, "사진 업로드 실패", Toast.LENGTH_SHORT).show()
						hideProgress()
					})
			} else {
				// 이미지 없으면 나머지 항목만 저장후 RecyclerView에 갱신
				uploadArticles(title, price, sellerId, "")
			}
		}
	}

	// Firebase storage 업로드
	private fun uploadPhoto(uri: Uri, successHandler: (String) -> Unit, errorHandler: () -> Unit) {
		val fileName = "${System.currentTimeMillis()}.png"

		storage.reference.child(STORAGE).child(fileName)
			.putFile(uri)
			.addOnCompleteListener {
				if (it.isSuccessful) {
					storage.reference.child(STORAGE).child(fileName)
						.downloadUrl
						.addOnSuccessListener { uri ->
							successHandler(uri.toString())
						}
						.addOnFailureListener { errorHandler() }
				} else {
					errorHandler()
				}
			}

	}

	//Firebase RealTime Database 저장
	private fun uploadArticles(title: String, price: String, sellerId: String, imageUrl: String) {
		val model = ArticleModel(sellerId, title, System.currentTimeMillis(), "$price 원", imageUrl)

		articleDB.push().setValue(model)

		hideProgress()
		finish()

	}

	private fun showProgress() {
		binding.progressBar.isVisible = true
	}
	private fun hideProgress(){
		binding.progressBar.isVisible = false
	}


}
