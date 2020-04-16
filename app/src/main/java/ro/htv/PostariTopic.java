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
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

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

        androidx.appcompat.widget.Toolbar myToolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle(topic);
        setSupportActionBar(myToolbar);

        MutableLiveData<Response> userData = firestoreRepository.getUser(uid);

        userData.observe(this, new Observer<Response>() {
            @Override
            public void onChanged(Response response) {
                if (response.ok()) {
                    if (response.getValue() instanceof User) {
                        post.setTopic(topic);
                        post.setOwnwer_uid(uid);
                        post.setOwner_name(((User) response.getValue()).getName());
                        post.setOwner_profilePicture(((User) response.getValue()).getProfileImage());
                        post.setOwner_karma(((User) response.getValue()).getKarma());
                        //Log.d(TAG, post.getOwner_karma());
                        initPopup();
                        loadPosts();
                    }

                }
            }
        });



        recyclerView = findViewById(R.id.review);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);

        FloatingActionButton mb = findViewById(R.id.floating_action_button);
        mb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post.setText("");
                post.setLinkToImage("");
                addPost.show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settingsBtn) {
            startActivity(new Intent(getBaseContext(), Settings.class).putExtra("uid", uid));
            return true;
        }

        return super.onOptionsItemSelected(item);
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

        ImageView postButton = addPost.findViewById(R.id.popup_add);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validatePost();
            }
        });

        Glide.with(getBaseContext())
                .load(post.getOwner_profilePicture())
                .into(popup_user_icon);

    }

    private void validatePost() {
        EditText post_text = addPost.findViewById(R.id.popup_description);

        if (post_text.getText() != null && post_text.getText().toString().length() > 6) {
            post.setText(post_text.getText().toString());
            post.setTimestamp("0000");

            addPost.findViewById(R.id.popup_add).setClickable(false);
            addPost.findViewById(R.id.popup_addImage).setClickable(false);
            addPost.findViewById(R.id.popup_progressBar).setVisibility(View.VISIBLE);

            Log.d(TAG, post.toString());
            System.out.println(post.getLinkToImage());
            if (post.getLinkToImage().equals("")) {
                MutableLiveData<Response> pendingPost = firestoreRepository.addPost(post);

                pendingPost.observe(this, new Observer<Response>() {
                    @Override
                    public void onChanged(Response response) {
                        if (response.ok()) {
                            Log.d(TAG, response.getValue().toString());
                            post.setIdpost((String)response.getValue());
                            Log.d(TAG, post.toString());
                            done();
                            TextView tvv = (TextView)findViewById(R.id.lipsaPostari);
                            tvv.setVisibility(View.INVISIBLE);

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
        if (!posts.isEmpty()) {
            posts.add(0, post);
            Log.d(TAG, post.toString());

            adapter.notifyDataSetChanged();
            layoutManager.scrollToPosition(0);

            //loadPosts();

        } else loadPosts();

        addPost.hide();
        initPopup();
        addPost.findViewById(R.id.popup_add).setClickable(true);
        addPost.findViewById(R.id.popup_addImage).setClickable(true);
        addPost.findViewById(R.id.popup_progressBar).setVisibility(View.INVISIBLE);
    }
    private void selectPicture() {
        startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), Utils.PICK_IMAGE_RC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Utils.PICK_IMAGE_RC && resultCode == RESULT_OK) {
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

        recyclerView.setVisibility(View.INVISIBLE);

        postsReq.observe(this, new Observer<PostsResponse>() {
            @Override
            public void onChanged(PostsResponse postsResponse) {
                if (postsResponse.getStatus() == Utils.Responses.OK) {
                    posts = postsResponse.getPosts();

                    Collections.sort(posts, new Comparator<Post>() {
                        public int compare(Post u1, Post u2) {
                            return u2.getTimestamp().toString().compareTo(u1.getTimestamp().toString());
                        }
                    });

                    adapter = new AdapterList(posts, Glide.with(getBaseContext()));
                    adapter.setOnItemClick(new AdapterList.OnItemClickListener() {
                        @Override
                        public void OnItemClick(int poz) {
                            Log.d(TAG, post.getOwner_profilePicture());
                            startActivity(new Intent(getBaseContext(), CometariiPostare.class).putExtra("idPost", posts.get(poz).getIdpost()).putExtra("profileImage", posts.get(poz).getOwner_profilePicture()).putExtra("uid", uid).putExtra("currentUserName", post.getOwner_name()).putExtra("currentUserProfileImage", post.getOwner_profilePicture()).putExtra("parentKarma", posts.get(poz).getOwner_karma()).putExtra("userKarma", post.getOwner_karma()));
                        }
                        @Override
                        public void OnPhotoClick(int poz) {
                            Post X = posts.get(poz);
                            System.out.println("AMAJUNS");
                            String url = X.getLinkToImage();
                            ImageView imgzoom = (ImageView)findViewById(R.id.imgzoom);
                            Glide.with(getBaseContext())
                                    .load(url)
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .skipMemoryCache(true)
                                    .into(imgzoom);
                            //recyclerView.setVisibility(View.INVISIBLE);
                            imgzoom.setVisibility(View.VISIBLE);
                            findViewById(R.id.blur).setVisibility(View.VISIBLE);
                            System.out.println("AMINCARCAT");
                        }

                        @Override
                        public void OnSmallPhotoClick(int poz) {
                                startActivity(new Intent(getBaseContext(), Profile.class).putExtra("targetUid", posts.get(poz).getOwnwer_uid()).putExtra("currentUid", uid));
                        }

                        @Override
                        public void OnTextClick(int poz) {
                            startActivity(new Intent(getBaseContext(), CometariiPostare.class).putExtra("idPost", posts.get(poz).getIdpost()).putExtra("profileImage", posts.get(poz).getOwner_profilePicture()).putExtra("uid", uid).putExtra("currentUserName", post.getOwner_name()).putExtra("currentUserProfileImage", post.getOwner_profilePicture()).putExtra("parentKarma", posts.get(poz).getOwner_karma()).putExtra("userKarma", post.getOwner_karma()));
                        }
                    });

                    recyclerView.setVisibility(View.VISIBLE);

                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);
                } else {
                    TextView tvv = (TextView)findViewById(R.id.lipsaPostari);
                    tvv.setVisibility(View.VISIBLE);
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
                                post.setIdpost((String)response.getValue());
                                done();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateKarma();
    }
    public void comeBack(View view){
        ImageView imgzoomm = (ImageView)findViewById(R.id.imgzoom);
        imgzoomm.setVisibility(View.INVISIBLE);

        findViewById(R.id.blur).setVisibility(View.INVISIBLE);
    }

    private void updateKarma() {
        if (!posts.isEmpty()) {
            MutableLiveData<Response> userData = firestoreRepository.getUser(uid);

            userData.observe(this, new Observer<Response>() {
                @Override
                public void onChanged(Response response) {
                    if (response.ok()) {
                        if (response.getValue() instanceof User) {
                            post.setOwner_karma(((User) response.getValue()).getKarma());

                            for (Post p: posts) {
                                if (p.getOwnwer_uid().equals(post.getOwnwer_uid())) {
                                    p.setOwner_karma(post.getOwner_karma());
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }

                    }
                }
            });


        }
    }

}
