package ro.htv

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_topic_selection.*
import ro.htv.model.User
import ro.htv.utils.FirestoreRepository
import java.util.*

class TopicSelection : AppCompatActivity() {

    private val TAG = "HackTheVirus Topic"
    private lateinit var firestoreRepository: FirestoreRepository
    private var topicsList = ArrayList<String>()

    private var uid = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic_selection)

        setSupportActionBar(my_toolbar)
        supportActionBar!!.title = getString(R.string.loading)

        ui(View.INVISIBLE)

        uid = intent.getStringExtra("uid")!!

        firestoreRepository = FirestoreRepository()

        val userPending = firestoreRepository.getUser(uid)
        userPending.observe(this, Observer {
            if (it.ok()) {
                val user = it.value as User
                hello.text = getString(R.string.hello, user.name)
            } else {
                hello.text = getString(R.string.hello, "")
            }
        })

        val topics: MutableLiveData<ArrayList<String>> = firestoreRepository.getTopics() // ma pis pe el error handling

        topics.observe(this, Observer {
            topicsList = it

            val adapter = ArrayAdapter(this,
                    android.R.layout.simple_spinner_dropdown_item, it)


            autocomplete.setAdapter(adapter)
            ui(View.VISIBLE)
            supportActionBar!!.title = getString(R.string.app_name)
        })

        autocomplete.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            hideKeyboard()
        }

        access.setOnClickListener {
            if (!topicsList.contains(autocomplete.text.toString())) {
                errField.text = getString(R.string.invalidTopic)
            } else {
                // bravo
                //Log.d(TAG, autocomplete.text.toString())

                startActivity(Intent(this, PostariTopic::class.java).putExtra("topic", autocomplete.text.toString()).putExtra("uid", uid))

            }
        }
    }

    private fun ui(visibility: Int) {
        visibility.let {
            l_autocomplete.visibility = it
            hello.visibility = it
            access.visibility = it
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.settingsBtn) {
            startActivity(Intent(baseContext, Settings::class.java).putExtra("uid", uid))
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(window.decorView.windowToken, 0)
    }
}
