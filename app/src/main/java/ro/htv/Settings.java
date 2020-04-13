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

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import ro.htv.model.Post;
import ro.htv.model.PostsResponse;
import ro.htv.utils.AuthRepository;
import ro.htv.utils.FirestoreRepository;
import ro.htv.utils.Utils;

public class Settings extends AppCompatActivity {

    Button logout_btn;
    Button profile_btn;
    //si postarile

    private String uid = "";

    ArrayList<Post> posts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        logout_btn = findViewById(R.id.LogOutButton);
        profile_btn = findViewById(R.id.ProfileButton);
        uid = getIntent().getStringExtra("uid");
        //loadPosts();

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
}
