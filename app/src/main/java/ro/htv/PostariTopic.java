package ro.htv;

import androidx.appcompat.app.AppCompatActivity;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ro.htv.model.Post;
import ro.htv.model.PostsResponse;
import ro.htv.model.Response;
import ro.htv.model.User;
import ro.htv.utils.FirestoreRepository;
import ro.htv.utils.StorageRepository;
import ro.htv.utils.Utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class PostariTopic extends AppCompatActivity {

    private String TAG = "HackTheVirus PostariTopic";

    private RecyclerView recyclerView ;
    private AdapterList adapter;
    private RecyclerView.LayoutManager layoutManager;

    private String topic = "";
    private String uid = "";
    private String userProfileImage = Utils.defaultProfilePicture;

    private FirestoreRepository firestoreRepository;
    private StorageRepository storageRepository;

    private Post post = new Post();

    private Dialog addPost;

    private ArrayList<Post> posts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postari_topic);

        firestoreRepository = new FirestoreRepository();
        storageRepository = new StorageRepository();

        topic = getIntent().getStringExtra("topic");
        uid = getIntent().getStringExtra("uid");

        MutableLiveData<Response> userData = firestoreRepository.getUser(uid);

        userData.observe(this, new Observer<Response>() {
            @Override
            public void onChanged(Response response) {
                if (response.ok()) {
                    if (response.getValue() instanceof User) {
                        post.setTopic(topic);
                        post.setOwnwer_uid(uid);
                        post.setOwner_name(((User) response.getValue()).getName());
                        userProfileImage = ((User) response.getValue()).getProfileImage();
                        initPopup();
                    }

                }
            }
        });



        recyclerView = findViewById(R.id.review);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        loadPosts();
        System.out.println("Merge pana la 1");



        FloatingActionButton mb = findViewById(R.id.floating_action_button);
        mb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPost.show();
            }
        });

    }

    private void initPopup() {
        addPost = new Dialog(this);
        addPost.setContentView(R.layout.popup_add_post);

        addPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        addPost.getWindow().getAttributes().gravity = Gravity.TOP;

        ImageView popup_user_icon = addPost.findViewById(R.id.popup_user_image);

        ImageView addPhoto = addPost.findViewById(R.id.popup_addImage);
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPicture();
            }
        });

        ImageView post = addPost.findViewById(R.id.popup_add);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validatePost();
            }
        });

        System.out.println(userProfileImage);
        Glide.with(getBaseContext())
                .load(userProfileImage)
                .into(popup_user_icon);

    }

    private void validatePost() {
        EditText post_text = addPost.findViewById(R.id.popup_description);

        if (post_text.getText() != null && post_text.getText().toString().length() > 6) {
            post.setText(post_text.getText().toString());
            post.setTimestamp("0000");

            Log.d(TAG, post.toString());
            System.out.println(post.getLinkToImage());
            if (post.getLinkToImage().equals("")) {
                MutableLiveData<Response> pendingPost = firestoreRepository.addPost(post);

                pendingPost.observe(this, new Observer<Response>() {
                    @Override
                    public void onChanged(Response response) {
                        if (response.ok()) {
                            done();
                        }
                    }
                });
            } else {
                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), Uri.parse(post.getLinkToImage()));
                try {
                    Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                    uploadImage(bitmap);
                } catch (Exception e) {
                }
            }
        } else {
            Log.d(TAG, "erowre");
            TextView err = addPost.findViewById(R.id.errField);
            err.setText(getString(R.string.postError));
        }
    }

    private void done() {
        addPost.hide();
        initPopup();

        post = new Post();
        post.setOwnwer_uid(uid);
        post.setTopic(topic);
    }

    private void selectPicture() {
        startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), Utils.PICK_IMAGE_RC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Utils.PICK_IMAGE_RC) {
            String postImage = data.getData().toString();
            //System.out.println(postImage);
            post.setLinkToImage(postImage);
            ImageView popup_image = addPost.findViewById(R.id.popup_img);

            Glide.with(this)
                    .load(postImage)
                    .apply(new RequestOptions().override(540, 960))
                    .into(popup_image);

            popup_image.setBackground(getDrawable(R.drawable.popup_text_style));
        }
    }


    private void loadPosts() {
        FirestoreRepository fs = new FirestoreRepository();
        MutableLiveData<PostsResponse> postsReq = fs.getPostsByTopic(topic);

        postsReq.observe(this, new Observer<PostsResponse>() {
            @Override
            public void onChanged(PostsResponse postsResponse) {
                if (postsResponse.getStatus() == Utils.Responses.OK) {
                    ArrayList<Post> lista = new ArrayList<>();

                    posts = postsResponse.getPosts();
                    lista = posts;

                    final ArrayList<Post> finalPost = lista;

                    adapter = new AdapterList(lista);
                    adapter.setOnItemClick(new AdapterList.OnItemClickListener() {
                        @Override
                        public void OnItemClick(int poz) {
                            startActivity(new Intent(getBaseContext(), CometariiPostare.class).putExtra("idPost", finalPost.get(poz).getIdpost()));
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

    private void uploadImage(Bitmap bitmap) {
        System.out.println("ndsdifnsi");
        final MutableLiveData<Response> pendingImage = storageRepository.uploadImage(bitmap, "post");
        final LifecycleOwner th = this;
        pendingImage.observe(th, new Observer<Response>() {
            @Override
            public void onChanged(Response response) {
                if (response.ok()) {
                    String downloadUrl = response.getValue().toString();

                    System.out.println("am ajuns dupa poza " + downloadUrl);

                    post.setLinkToImage(downloadUrl);

                    MutableLiveData<Response> pendingPost = firestoreRepository.addPost(post);

                    pendingPost.observe(th, new Observer<Response>() {
                        @Override
                        public void onChanged(Response response) {
                            if (response.ok()) {
                                done();
                            }
                        }
                    });
                }
            }
        });
    }
}
