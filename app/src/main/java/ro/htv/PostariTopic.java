package ro.htv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ro.htv.model.Post;
import ro.htv.model.PostsResponse;
import ro.htv.utils.FirestoreRepository;
import ro.htv.utils.Utils;

import android.os.Bundle;

import java.util.ArrayList;

public class PostariTopic extends AppCompatActivity {

    private String TAG = "HackTheVirus PostariTopic";

    private RecyclerView recyclerView ;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    ArrayList<Post> posts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postari_topic);

        loadPosts();

        ArrayList<Postare> lista = new ArrayList<>();
        lista.add(new Postare(1,1,"Cosmin Candrea", "aicie e un text complex despre postare din topicul matemaatica"));
        lista.add(new Postare(1,1,"MArC gRuiTA", "#6b9fed#6b9fed#6b9fed#6b9fed#6b9fed#6b9fed#6b9fed#6b9fed"));


        recyclerView = findViewById(R.id.review);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        adapter = new AdapterList(lista);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }

    private void loadPosts() {
        FirestoreRepository fs = new FirestoreRepository();
        MutableLiveData<PostsResponse> postsReq = fs.getPostsByTopic(getIntent().getStringExtra("topic"));


        postsReq.observe(this, new Observer<PostsResponse>() {
            @Override
            public void onChanged(PostsResponse postsResponse) {
                if (postsResponse.getStatus() == Utils.Responses.OK) {
                    posts = postsResponse.getPosts();
                    //System.out.println(posts.size());
                } else {
                    // nu sunt postari
                }
            }
        });

    }
}
