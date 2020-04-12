package ro.htv

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_register.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate

class Register : AppCompatActivity() {

    private val TAG = "HackTheVirus Register"

    private val PICK_IMAGE_REQUEST = 123
    private var pictureUri: Uri = Uri.parse("https://pozadefault")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        registerBtn.setOnClickListener {
            validateInputs()
        }

        logo.setOnClickListener {
            selectPicture()
        }
    }

    fun validateInputs() {
        val email = email.text.toString()
        val pass = pass.text.toString()
        val birthday = birthday.text.toString()
        val name = nume.text.toString()

        val err: ArrayList<Int> = ArrayList()

        if (email.isNullOrBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches())
            err.add(R.string.invalidEmail)

        if (pass.isNullOrBlank() || pass.length < 6 || !pass.matches(Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$")))
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
            // firebase

        } else errField.text = errors
    }

    private fun selectPicture() {
        Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI
        ).also {
            startActivityForResult(it, PICK_IMAGE_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST) {
            pictureUri = data!!.data!!

            Log.d(TAG, pictureUri.toString())

            Glide.with(this)
                    .load(pictureUri)
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
}
