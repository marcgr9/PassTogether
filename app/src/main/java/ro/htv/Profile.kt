package ro.htv

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_profile.*
import ro.htv.model.Post
import ro.htv.model.User
import ro.htv.utils.FirestoreRepository
import ro.htv.utils.Utils

class Profile : AppCompatActivity() {

    private lateinit var firestoreRepository: FirestoreRepository
    private lateinit var targetUid: String
    private lateinit var currentUid: String

    private var target = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setSupportActionBar(my_toolbar)
        supportActionBar!!.title = getString(R.string.loading)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        intent.getStringExtra("targetUid")?.let {
            targetUid = it
            currentUid = intent.getStringExtra("currentUid")!!

            firestoreRepository = FirestoreRepository()

            firestoreRepository.getUser(targetUid).observe(this, Observer { user ->
                if (user.ok()) {
                    target = user.value as User
                    Log.d("marc", target.karma.toString())

                    Glide.with(this)
                            .load(target.profileImage)
                            .into(profilePicture) // ar trebui sa se incarce pana face load la postari

                    firestoreRepository.getPostsByUser(targetUid).observe(this, Observer {posts ->
                        if (posts.status == Utils.Responses.OK) {
                            if (posts.posts!!.size > 0) {
                                recyclerView.adapter = AdapterList(posts.posts as ArrayList<Post>)

                                // TODO() in getPostsByUser nu se iau si comentariile, deci e nul calculul de cate comentarii is

                                val postsSize = posts.posts!!.size
                                val numberOfComments = posts.posts!!.count { post ->
                                    !post.post
                                }

                                val animation = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation)
                                recyclerView.layoutAnimation = animation

                                updateUi(postsSize - numberOfComments, numberOfComments)
                            } else {
                                updateUi(0, 0)
                            }
                        }
                    })
                } else print("mhm")
            })

        }

    }

    private fun updateUi(posts: Int, comments: Int) {
        val total = posts + comments
        View.VISIBLE.apply {
            recyclerView.visibility = if (total != 0) View.VISIBLE else View.GONE
            l_age.visibility = this
            l_karma.visibility = this
            l_numberOfPosts.visibility = this
            profilePicture.visibility = this
        }

        age.setText(target.birthday)
        karma.setText(target.karma.toString())


        numberOfPosts.setText(getString(R.string.numberOfPosts, posts.toString(), comments.toString()))

        supportActionBar!!.title = target.name
    }
}
