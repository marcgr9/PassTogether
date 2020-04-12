package ro.htv

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_topic_selection.*
import ro.htv.utils.FirestoreRepository
import java.util.ArrayList

class TopicSelection : AppCompatActivity() {

    private val TAG = "HackTheVirus Topic"
    private lateinit var firestoreRepository: FirestoreRepository
    private var topicsList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic_selection)

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
            }
        }
    }
}
