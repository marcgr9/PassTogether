package ro.htv

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {

    private val TAG = "HackTheVirus Login"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginBtn.setOnClickListener {
            login() // n avem ce erori sa gestionam la login ca se ocupa firebase de toate
        }
    }

    fun login() {
        val email = email.text.toString()
        val password = pass.text.toString()

        // firebase
    }
}
