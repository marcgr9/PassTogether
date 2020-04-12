package ro.htv.utils

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import ro.htv.model.Response

class AuthRepository {

    private val TAG = "HackTheVirus AuthRepository"

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun login(email: String, password: String): MutableLiveData<Response> {
        val response = MutableLiveData<Response>()

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    response.value = Response(Utils.Responses.OK, it.user!!.uid)
                }.addOnFailureListener {
                    response.value = Response(Utils.Responses.ERROR, it.message)
                }
        return response
    }

    fun register(email: String, password: String): MutableLiveData<Response> {
        val response = MutableLiveData<Response>()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    response.value = Response(Utils.Responses.OK, it.user!!.uid)
                }.addOnFailureListener {
                    response.value = Response(Utils.Responses.ERROR, it.message)
                }
        return response
    }

    fun logout() {
        firebaseAuth.signOut()
    }
}