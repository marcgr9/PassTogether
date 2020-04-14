package ro.htv;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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

    private Button logout_btn;
    private Button profile_btn;

    private RecyclerView recyclerView ;
    private AdapterList adapter;
    private RecyclerView.LayoutManager layoutManager;

    private String uid = "";
    private String topic = "";

    private FirestoreRepository firestoreRepository;

    private User user;

    private ArrayList<Post> posts = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        logout_btn = findViewById(R.id.LogOutButton);
        profile_btn = findViewById(R.id.ProfileButton);

        uid = getIntent().getStringExtra("uid");
        firestoreRepository = new FirestoreRepository();

        MutableLiveData<Response> userData = firestoreRepository.getUser(uid);
        userData.observe(this, new Observer<Response>() {
            @Override
            public void onChanged(Response response) {
                if (response.ok()) {
                    user = (User) response.getValue();
                    loadPosts();
                }
            }
        });


        recyclerView = findViewById(R.id.review);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthRepository as = new AuthRepository();
                as.logout();

                startActivity(new Intent(Settings.this, Login.class));
            }
        });

        profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), UserProfile.class)
                        .putExtra("uid", uid));
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

                        adapter = new AdapterList(posts);
                        adapter.setOnItemClick(new AdapterList.OnItemClickListener() {
                            @Override
                            public void OnItemClick(int poz) {
                                startActivity(new Intent(getBaseContext(), CometariiPostare.class).putExtra("idPost", posts.get(poz).getIdpost()).putExtra("profileImage", user.getProfileImage()).putExtra("uid", uid).putExtra("currentUserName", user.getName()));
                            }
                        });

                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(adapter);
                    } else {
                        // nu sunt postari
                    }
                }
            });

        }

    }

