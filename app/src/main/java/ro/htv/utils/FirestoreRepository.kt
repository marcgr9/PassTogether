package ro.htv.utils

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import ro.htv.model.Response
import ro.htv.model.User

class FirestoreRepository {

    private val TAG = "HackTheVirus FirestoreRepository"
    private val root: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val usersCollection = root.collection("users")

    fun createUser(user: User): MutableLiveData<Response> {
        val response = MutableLiveData<Response>()

        usersCollection.document(user.uid).set(user)
                .addOnSuccessListener {
                    response.value = Response(Utils.Responses.OK, user.name)
                }.addOnFailureListener {
                    response.value = Response(Utils.Responses.ERROR, it.message)
                }

        return response
    }

    fun setImage(uid: String, uri: String): MutableLiveData<Boolean> {
        // imi bag pula in el fail gracefully
        // is prea mici sansele sa esueze aici
        val response = MutableLiveData<Boolean>()

        usersCollection.document(uid).update("profileImage", uri)
                .addOnSuccessListener {
                    response.value = true
                }.addOnFailureListener {
                    response.value = false
                }

        return response
    }
}