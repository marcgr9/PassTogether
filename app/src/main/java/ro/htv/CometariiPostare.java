package ro.htv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

import ro.htv.model.Post;
import ro.htv.model.PostsResponse;
import ro.htv.model.Response;
import ro.htv.utils.FirestoreRepository;
import ro.htv.utils.Utils;

public class CometariiPostare extends AppCompatActivity {

    private RecyclerView recyclerView ;
    private AdapterList adapter;
    private RecyclerView.LayoutManager layoutManager;

    private String TAG = "HackTheVirus Comentarii";

    private FirestoreRepository firestoreRepository;
    private Post currentPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cometarii_postare);

        final String idParent = getIntent().getExtras().getString("idPost");

        Log.d(TAG, "id paret " + idParent);

        firestoreRepository = new FirestoreRepository();

        getParentPost(idParent);
        getComments(idParent);

        recyclerView = findViewById(R.id.commview);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void updateCurrentPost(Post currentPost) {
        /// updateaza postarea actuala cu datele din currentPost


    }

    private void getParentPost(String id) {
        MutableLiveData<Response> parentPost = firestoreRepository.getPostById(id);
        parentPost.observe(this, new Observer<Response>() {
            @Override
            public void onChanged(Response response) {
                if (response.ok()) {
                    if (response.getValue() instanceof Post) {
                        currentPost = (Post) response.getValue();
                        Log.d(TAG, currentPost.toString());

                        updateCurrentPost(currentPost);
                    }
                }
            }
        });
    }

    private void getComments(String id) {
        MutableLiveData<PostsResponse> comments = firestoreRepository.getPostsByParentId(id);
        comments.observe(this, new Observer<PostsResponse>() {
            @Override
            public void onChanged(PostsResponse response) {
                if (response.getStatus() == Utils.Responses.OK) {
                    System.out.println(response.getPosts().size());
                    ArrayList<Post> listOfPosts = response.getPosts();
                    adapter = new AdapterList(listOfPosts);

                    recyclerView.setAdapter(adapter);
                }
            }
        });
    }

}
