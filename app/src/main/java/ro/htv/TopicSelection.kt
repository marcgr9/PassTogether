package ro.htv

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_topic_selection.*
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

        uid = intent.getStringExtra("uid")!!

        firestoreRepository = FirestoreRepository()

        val topics: MutableLiveData<ArrayList<String>> = firestoreRepository.getTopics() // ma pis pe el error handling

        topics.observe(this, Observer {
            topicsList = it

            val adapter = ArrayAdapter(this,
                    android.R.layout.simple_dropdown_item_1line, it)
            autocomplete.setAdapter(adapter)
        })

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.settingsBtn) {
            startActivity(Intent(baseContext, Settings::class.java).putExtra("uid", uid))
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
