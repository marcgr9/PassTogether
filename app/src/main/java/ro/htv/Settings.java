package ro.htv;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import ro.htv.model.Post;
import ro.htv.model.PostsResponse;
import ro.htv.model.Response;
import ro.htv.model.User;
import ro.htv.utils.AuthRepository;
import ro.htv.utils.FirestoreRepository;
import ro.htv.utils.StorageRepository;
import ro.htv.utils.Utils;

public class Settings extends AppCompatActivity {

    Button logout_btn;
    Button profile_btn;
    //si postarile

    private RecyclerView recyclerView ;
    private AdapterList adapter;
    private RecyclerView.LayoutManager layoutManager;

    private String uid = "";
    private String topic = "";
    private String userProfileImage = Utils.defaultProfilePicture;

    private FirestoreRepository firestoreRepository;
    private StorageRepository storageRepository;

    //private Post post = new Post();

    //private Dialog addPost;

    private ArrayList<Post> posts = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        logout_btn = findViewById(R.id.LogOutButton);
        profile_btn = findViewById(R.id.ProfileButton);
        uid = getIntent().getStringExtra("uid");
        topic = "Concurs UBB Mate";
        firestoreRepository = new FirestoreRepository();
        storageRepository = new StorageRepository();

        MutableLiveData<Response> userData = firestoreRepository.getUser(uid);

        recyclerView = findViewById(R.id.review);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        loadPosts();

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthRepository as = new AuthRepository();
                as.logout();
                /// sau FirebaseAuth.getInstance().signOut();
                //                finish();
                startActivity(new Intent(Settings.this, Login.class));
            }
        });

        profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), UserProfile.class).putExtra("uid", uid));
            }
        });
    }

        private void loadPosts() {
            FirestoreRepository fs = new FirestoreRepository();
            MutableLiveData<PostsResponse> postsReq = fs.getPostsByUser(uid);

            postsReq.observe(this, new Observer<PostsResponse>() {
                @Override
                public void onChanged(PostsResponse postsResponse) {
                    if (postsResponse.getStatus() == Utils.Responses.OK) {
                        ArrayList<Postare> lista = new ArrayList<>();

                        posts = postsResponse.getPosts();
                        for(Post X: posts)
                        {
                            lista.add(new Postare(X.getLinkToImage(),X.getLinkToImage(),X.getOwner_name(), X.getText(), 1));
                        }

                        adapter = new AdapterList(lista);
                        adapter.setOnItemClick(new AdapterList.OnItemClickListener() {
                            @Override
                            public void OnItemClick(int poz) {
                                startActivity(new Intent(getBaseContext(), CometariiPostare.class));
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

