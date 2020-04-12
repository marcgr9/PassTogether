package ro.htv

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import ro.htv.utils.AuthRepository

class Splash : AppCompatActivity() {

    private val TAG = "HackTheVirus Splash"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authRepository = AuthRepository()

        val user = authRepository.getUser()

        user.observe(this, Observer {
            if (it.ok()) {
                startActivity(Intent(this, MainActivity::class.java).putExtra("uid", it.value.toString()))
            } else {
                startActivity(Intent(this, Login::class.java))
            }
        })

    }
}
