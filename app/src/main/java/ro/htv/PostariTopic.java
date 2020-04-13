package ro.htv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ro.htv.model.Post;
import ro.htv.model.PostsResponse;
import ro.htv.utils.FirestoreRepository;
import ro.htv.utils.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;


import java.util.ArrayList;

public class PostariTopic extends AppCompatActivity  {

    private String TAG = "HackTheVirus PostariTopic";

    private RecyclerView recyclerView ;
    private AdapterList adapter;
    private RecyclerView.LayoutManager layoutManager;

    ArrayList<Post> posts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postari_topic);

        recyclerView = findViewById(R.id.review);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        loadPosts();
        System.out.println("Merge pana la 1");
        final Button btn = (Button)findViewById(R.id.buttonAddPost);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Merge 2");
                RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.createPost);
                relativeLayout.setVisibility(View.VISIBLE);
                btn.setVisibility(View.INVISIBLE);
            }

        });

    }


    private void loadPosts() {
        FirestoreRepository fs = new FirestoreRepository();
        MutableLiveData<PostsResponse> postsReq = fs.getPostsByTopic(getIntent().getStringExtra("topic"));


        postsReq.observe(this, new Observer<PostsResponse>() {
            @Override
            public void onChanged(PostsResponse postsResponse) {
                if (postsResponse.getStatus() == Utils.Responses.OK) {
                    ArrayList<Postare> lista = new ArrayList<>();

                    posts = postsResponse.getPosts();
                    for(Post X: posts)
                    {
                        lista.add(new Postare(X.getLinkToImage(),X.getLinkToImage(),X.getOwner_name(), X.getText()));
                    }
                    adapter = new AdapterList(lista);
                    adapter.setOnItemClick(new AdapterList.OnItemClickListener() {
                        @Override
                        public void OnItemClick(int poz) {
                            faCeTrebe(poz, posts.get(poz));

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
    void faCeTrebe(int pozitie,Post X)
    {
        Intent nw = new Intent(this, CometariiPostare.class);
        nw.putExtra("id", X.getIdpost());
        startActivity(nw);
    }

}
