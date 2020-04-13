package ro.htv.utils

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import ro.htv.model.Post
import ro.htv.model.PostsResponse
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

    fun getTopics(): MutableLiveData<ArrayList<String>> {
        val response = MutableLiveData<ArrayList<String>>()
        val arr = ArrayList<String>()
        root.collection("topics").get()
                .addOnSuccessListener {
                    it.forEach { it2 ->
                        Log.d(TAG, it2.data["name"].toString())
                        arr.add(it2.data["name"].toString())
                        response.value = arr
                    }
                }
        return response
    }

    fun getPostsByTopic(topic: String): MutableLiveData<PostsResponse> {
        val response = MutableLiveData<PostsResponse>()

        root.collection("posts")
                .whereEqualTo("post", true)
                .whereEqualTo("topic", topic)
                .get()
                .addOnSuccessListener {
                    val arr = ArrayList<Post>()
                    it.forEach { snapshot ->
                        //Log.d(TAG, "asd ${snapshot.data.toMap()}")
                        arr.add(snapshot.toObject(Post::class.java))
                    }
                    if (arr.isNotEmpty()) {
                        response.value = PostsResponse(
                                Utils.Responses.OK,
                                arr
                        )
                    } else {
                        response.value = PostsResponse(
                                Utils.Responses.ERROR,
                                null
                        )
                    }
                  }
//                .addOnFailureListener {
//                    response.value = Response(
//                            Utils.Responses.ERROR,
//                            it.message
//                    )
//                }

        return response
    }

    fun getUser(uid: String): MutableLiveData<Response> {
        val response = MutableLiveData<Response>()

        root.collection("users").document(uid).get()
                .addOnSuccessListener {
                    if (it != null) {
                        response.value = Response(
                                Utils.Responses.OK,
                                it!!.toObject(User::class.java)
                        )
                        Log.d(TAG, it.toString())
                    } else {
                        response.value = Response(
                                Utils.Responses.ERROR,
                                Utils.Errors.EMPTY
                        )
                    }
                }.addOnFailureListener {
                    response.value = Response(
                            Utils.Responses.ERROR,
                            it.message
                    )
                }

        return response
    }

    fun addPost(post: Post): MutableLiveData<Response> {
        val response = MutableLiveData<Response>()

        root.collection("posts").add(post)
                .addOnSuccessListener {
                    root.collection("posts").document(it.id).update("idpost", it.id)
                    response.value  = Response(
                            Utils.Responses.OK,
                            ""
                    )
                }.addOnFailureListener {
                    response.value = Response(
                            Utils.Responses.ERROR,
                            it.message
                    )
                }

        return response
    }

    fun getPostsByUser(uid: String): MutableLiveData<PostsResponse> {
        val response = MutableLiveData<PostsResponse>()

        root.collection("posts")
                .whereEqualTo("owner_uid", uid)
                .whereEqualTo("post", true).get()
                .addOnSuccessListener {
                    val arr = ArrayList<Post>()
                    it.forEach { snapshot ->
                        arr.add(snapshot.toObject(Post::class.java))
                    }

                    if (arr.isNotEmpty()) {
                        response.value = PostsResponse(
                                Utils.Responses.OK,
                                arr
                        )
                    } else {
                        response.value = PostsResponse(
                                Utils.Responses.ERROR,
                                null
                        )
                    }
                }

        return response
    }
}