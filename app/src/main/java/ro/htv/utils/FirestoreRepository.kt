package ro.htv.utils

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import ro.htv.model.Post
import ro.htv.model.PostsResponse
import ro.htv.model.Response
import ro.htv.model.User
import java.util.*
import kotlin.collections.ArrayList

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
        var incrementKarma = false
        if (!post.post) incrementKarma = true

        post.timestamp = (Date().time / 1000).toString()

        root.collection("posts").add(post)
                .addOnSuccessListener {
                    root.collection("posts").document(it.id).update("idpost", it.id)
                    if (incrementKarma) {
                        root.collection("posts").document(post.parent).get()
                                .addOnSuccessListener { parent ->
                                    if (parent.toObject(Post::class.java)!!.ownwer_uid != post.ownwer_uid) {
                                        root.collection("users").document(post.ownwer_uid).update("karma", FieldValue.increment(2));
                                        root.collection("posts").whereEqualTo("ownwer_uid", post.ownwer_uid).get()
                                                .addOnSuccessListener {posts ->
                                                    posts.forEach {
                                                        root.collection("posts").document(it.id).update("owner_karma", FieldValue.increment(2))
                                                                .addOnSuccessListener {
                                                                    response.value = Response(
                                                                            Utils.Responses.OK,
                                                                            "karma crescuta"
                                                                    )
                                                                }
                                                    }
                                                }
                                    } else {
                                        response.value = Response(
                                                Utils.Responses.OK,
                                                ""
                                        )
                                    }
                                }
                    } else {
                        response.value = Response(
                                Utils.Responses.OK,
                                ""
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

    fun getPostsByParentId(id: String): MutableLiveData<PostsResponse> {
        val response = MutableLiveData<PostsResponse>()

        root.collection("posts")
                .whereEqualTo("post", false)
                .whereEqualTo("parent", id).get()
                .addOnSuccessListener {
                    val arr = ArrayList<Post>()
                    it.forEach { snapshot ->
                        Log.d(TAG, "am gaasit")
                        arr.add(snapshot.toObject(Post::class.java))
                    }

                    response.value = PostsResponse(
                            Utils.Responses.OK,
                            arr
                    )
                }.addOnFailureListener {
                    response.value = PostsResponse(
                            Utils.Responses.ERROR,
                            null
                    )
                }

        return response
    }

    fun getPostById(id: String): MutableLiveData<Response> {
        val response = MutableLiveData<Response>()

        root.collection("posts").document(id).get()
                .addOnSuccessListener {
                    response.value = Response(
                            Utils.Responses.OK,
                            it.toObject(Post::class.java)
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
                .whereEqualTo("post", true)
                .whereEqualTo("ownwer_uid", uid)
                .get()
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