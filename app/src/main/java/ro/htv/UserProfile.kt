package ro.htv

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.activity_user_profile.*
import ro.htv.model.Response
import ro.htv.model.User
import ro.htv.utils.AuthRepository
import ro.htv.utils.FirestoreRepository
import ro.htv.utils.StorageRepository
import ro.htv.utils.Utils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate

//TODO updateaza firestore-ul numa daca ii cv diferit
// da ii prea mult de scris si n am chef

class UserProfile : AppCompatActivity() {

    private val TAG = "HackTheVirus Profile"
    private lateinit var firestoreRepository: FirestoreRepository
    private lateinit var storageRepository: StorageRepository
    private lateinit var authRepository: AuthRepository

    private var uid: String? = ""
    private var firstProfileImg = Utils.defaultProfilePicture
    private var profileImg = Utils.defaultProfilePicture

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        ui(false)

        uid = intent.getStringExtra("uid")

        if (uid.isNullOrEmpty()) {
            Log.wtf(TAG, "ce pula mea")
            return
        } else {
            firestoreRepository = FirestoreRepository()
            storageRepository = StorageRepository()
            authRepository = AuthRepository()

            val user = firestoreRepository.getUser(uid!!)

            user.observe(this, Observer {
                if (it.ok()) {
                    it.value?.let {user ->
                        populateFields(user)
                    }
                }
            })
        }
    }

    private fun populateFields(userArg: Any) {
        val user = userArg as User
        //Log.d(TAG, user.toString())
        email.setText(user.email)
        pass.setText("")
        nume.setText(user.name)
        birthday.setText(user.birthday)
        firstProfileImg = user.profileImage
        profileImg = firstProfileImg

        Glide.with(this)
                .load(firstProfileImg)
                .circleCrop()
                .apply(RequestOptions().override(400, 400))
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable?>, isFirstResource: Boolean): Boolean {
                        ui(true)
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any, target: Target<Drawable?>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        ui(true)
                        return false
                    }
                })
                .into(logo)

        //ui(true)

    }

    private fun ui(visible: Boolean) {
        loading.visibility = if (visible) View.GONE else View.VISIBLE
        val tag = if (visible) View.VISIBLE else View.GONE

        tag.apply {
            //logo.visibility = this
            titlu.visibility = this
            l_email.visibility = this
            l_nume.visibility = this
            l_pass.visibility = this
            l_birthday.visibility = this
            errField.visibility = this
            updateBtn.visibility = this

            if (visible) {
                updateBtn.setOnClickListener {
                    validateInputs()
                }

                logo.setOnClickListener {
                    selectPicture()
                }
            }
        }
    }

    private fun validateInputs() {
        val email = email.text.toString()
        val pass = pass.text.toString()
        val birthday = birthday.text.toString()
        val name = nume.text.toString()

        val err: ArrayList<Int> = ArrayList()

        if (email.isNullOrBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches())
            err.add(R.string.invalidEmail)

        if (!pass.isNullOrBlank() && (pass.length < 6 || !pass.matches(Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$"))))
            err.add(R.string.invalidPass)

        if (name.isNullOrBlank() || name.length < 3 || !name.matches(Regex("[a-zA-Z ]+")))
            err.add(R.string.invalidName)

        checkDate(birthday)?.let {
            err.add(it)
        }

        var errors = ""
        err.forEach {
            errors += getString(it) + '\n'
        }

        if (errors.isEmpty()) {
            Log.d(TAG, "firebase")

            updateBtn.isClickable = false

            if (!pass.isNullOrEmpty()) {
                val passwordUpdated = authRepository.updatePassword(pass)

                passwordUpdated.observe(this, Observer {
                    if (it.ok()) {
                        Log.d(TAG, "parola updatata")
                        val user = firestoreRepository.createUser(User(
                                uid!!,
                                email,
                                name,
                                birthday,
                                profileImg
                        ))

                        user.observe(this, Observer {userIt ->
                            if (userIt.ok()) {
                                Log.d(TAG, "parola, firestore updatat")
                                if (profileImg != firstProfileImg) {
                                    Log.d(TAG, "parola, firestore, urmeaza img")
                                    val source = ImageDecoder.createSource(this.contentResolver, Uri.parse(profileImg))
                                    val bitmap = ImageDecoder.decodeBitmap(source)

                                    uploadImage(bitmap, uid!!)
                                } else done()
                            } else {
                                errField.text = it.value.toString()
                                updateBtn.isClickable = true
                            }
                        })
                    } else {
                        errField.text = it.value.toString()
                        updateBtn.isClickable = true
                    }
                })
            } else {
                val user = firestoreRepository.createUser(User(
                        uid!!,
                        email,
                        name,
                        birthday,
                        profileImg
                ))

                user.observe(this, Observer {user ->
                    if (user.ok()) {
                        Log.d(TAG, "firestore")
                        if (profileImg != firstProfileImg) {
                            Log.d(TAG, "firestore, urmeaza img")
                            val source = ImageDecoder.createSource(this.contentResolver, Uri.parse(profileImg))
                            val bitmap = ImageDecoder.decodeBitmap(source)

                            uploadImage(bitmap, uid!!)
                        } else done()
                    } else {
                        errField.text = user.value.toString()
                        updateBtn.isClickable = true
                    }
                })
            }

        } else {
            errField.text = errors
        }
    }

    private fun selectPicture() {
        Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI
        ).also {
            startActivityForResult(it, Utils.PICK_IMAGE_RC)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Utils.PICK_IMAGE_RC && resultCode == Activity.RESULT_OK) {
            profileImg = data!!.data!!.toString()

            //Log.d(TAG, pictureUri.toString())

            Glide.with(this)
                    .load(profileImg)
                    .circleCrop()
                    .into(logo)
        }
    }

    fun checkDate(_date: String?): Int? {
        if (_date.isNullOrBlank()) return R.string.invalidDateFormat
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        dateFormat.isLenient = false
        try {
            dateFormat.parse(_date.trim { it <= ' ' })
        } catch (pe: ParseException) {
            return R.string.invalidDateFormat
        }

        val dateList: List<String> = _date.split("/")

        val date: LocalDate = LocalDate.of(dateList[2].toInt(), dateList[1].toInt(), dateList[0].toInt())
        val today: LocalDate = LocalDate.now()

        if (date.isBefore(today.minusYears(124)) || date.isAfter(today.minusYears(14)))
            return R.string.invalidDate

        return null
    }

    fun uploadImage(bitmap: Bitmap, uid: String) {
        val img: MutableLiveData<Response> = storageRepository.uploadImage(bitmap, uid)

        img.observe(this, Observer {
            if (it.ok()) {
                Log.d(TAG, "imagine uploadata: ${it.value}")
                //done()

                val imgUpdated = firestoreRepository.setImage(uid, it.value.toString())
                imgUpdated.observe(this, Observer {img ->
                    if (img) {
                        firestoreRepository.updatePostsWithNewProfileImage(uid, it.value.toString()).observe(this, Observer {done ->
                            if (done) done()
                        })
                    }
                })

            } else {
                errField.text = it.value.toString()
                updateBtn.isClickable = true
            }
        })
    }

    private fun done() {
        pass.setText("")
        startActivity(Intent(this, TopicSelection::class.java).putExtra("uid", uid!!))
        Toast.makeText(this, "profil updatat", Toast.LENGTH_SHORT).show()
    }
}
