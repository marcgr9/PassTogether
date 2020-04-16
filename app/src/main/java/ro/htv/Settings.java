package ro.htv;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ro.htv.model.Post;
import ro.htv.model.PostsResponse;
import ro.htv.model.Response;
import ro.htv.model.User;
import ro.htv.utils.AuthRepository;
import ro.htv.utils.FirestoreRepository;
import ro.htv.utils.Utils;

public class Settings extends AppCompatActivity {
    private RecyclerView recyclerView ;
    private AdapterList adapter;
    private RecyclerView.LayoutManager layoutManager;

    private String uid = "";

    private FirestoreRepository firestoreRepository;

    private User user;

    private ArrayList<Post> posts = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final androidx.appcompat.widget.Toolbar myToolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle(getString(R.string.settings));
        setSupportActionBar(myToolbar);

        uid = getIntent().getStringExtra("uid");
        firestoreRepository = new FirestoreRepository();

        MutableLiveData<Response> userData = firestoreRepository.getUser(uid);
        userData.observe(this, new Observer<Response>() {
            @Override
            public void onChanged(Response response) {
                if (response.ok()) {
                    user = (User) response.getValue();
                    myToolbar.setTitle(getString(R.string.settingsWithName, user.getName()));
                    loadPosts();
                }
            }
        });


        recyclerView = findViewById(R.id.review);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);

        final TextView logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AuthRepository().logout();
                startActivity(new Intent(Settings.this, Login.class));
                finish();
            }
        });

        final TextView profileBtn = findViewById(R.id.profileBtn);
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), UserProfile.class)
                        .putExtra("uid", uid));
            }
        });

        final TextView myPosts = findViewById(R.id.showPosts);
        myPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPostsIfAny();
            }
        });
    }

        private void loadPosts() {
            MutableLiveData<PostsResponse> postsReq = firestoreRepository.getPostsByUser(uid);

            postsReq.observe(this, new Observer<PostsResponse>() {
                @Override
                public void onChanged(PostsResponse postsResponse) {
                    if (postsResponse.getStatus() == Utils.Responses.OK) {
                        posts = postsResponse.getPosts();
                        for (Post post: posts) {
                            post.setLinkToImage("");
                        }

                        adapter = new AdapterList(posts, Glide.with(getBaseContext()));
                        adapter.setOnItemClick(new AdapterList.OnItemClickListener() {
                            @Override
                            public void OnItemClick(int poz) {
                                Log.d("marc", String.valueOf(user.getKarma()));
                                startActivity(new Intent(getBaseContext(), CometariiPostare.class).putExtra("idPost", posts.get(poz).getIdpost()).putExtra("profileImage", user.getProfileImage()).putExtra("uid", uid).putExtra("currentUserProfileImage", user.getProfileImage()).putExtra("currentUserName", user.getName()).putExtra("parentKarma", user.getKarma()).putExtra("userKarma", user.getKarma()));
                            }

                            @Override
                            public void OnPhotoClick(int poz) {
                            }
                            @Override
                            public void OnSmallPhotoClick(int poz) {
                                //
                            }

                            @Override
                            public void OnTextClick(int poz) {
                                startActivity(new Intent(getBaseContext(), CometariiPostare.class).putExtra("idPost", posts.get(poz).getIdpost()).putExtra("profileImage", user.getProfileImage()).putExtra("uid", uid).putExtra("currentUserProfileImage", user.getProfileImage()).putExtra("currentUserName", user.getName()).putExtra("parentKarma", user.getKarma()).putExtra("userKarma", user.getKarma()));

                            }
                        });

                        int resId = R.anim.layout_animation;
                        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getBaseContext(), resId);
                        recyclerView.setLayoutAnimation(animation);

                        recyclerView.setLayoutManager(layoutManager);
                    } else {
                        // nu sunt postari
                    }
                }
            });

        }

    private void showPostsIfAny() {
        findViewById(R.id.showPosts).setVisibility(View.INVISIBLE);
        if (posts.isEmpty()) {
            findViewById(R.id.noPosts).setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.topicsBtn) {
            startActivity(new Intent(getBaseContext(), TopicSelection.class).putExtra("uid", uid));
            finish();
            return false;
        }

        return super.onOptionsItemSelected(item);
    }

}

