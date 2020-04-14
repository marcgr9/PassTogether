package ro.htv

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_login.*
import ro.htv.model.Response
import ro.htv.utils.AuthRepository

class Login : AppCompatActivity() {

    private val TAG = "HackTheVirus Login"
    private lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        authRepository = AuthRepository()

        logo.setOnClickListener {
            authRepository.logout()
        }

        loginBtn.setOnClickListener {
            login()
        }

        noAccount.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
        }

        resetPassword.setOnClickListener {
            startActivity(Intent(this, ResetPassword::class.java))
        }
    }

    fun login() {
        val email = email.text.toString()
        val password = pass.text.toString()

        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            errField.text = getString(R.string.blankInputs)
            return
        }

        val user: MutableLiveData<Response> = authRepository.login(email, password)
        user.observe(this, Observer {
            if (it.ok()) {
                Log.d(TAG, "user logat cu uid ${it.value}")
                startActivity(Intent(this, TopicSelection::class.java).putExtra("uid", it.value.toString()))
            } else {
                errField.text = it.value.toString()
            }
        })
    }
}
