package ro.htv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import ro.htv.model.Post;
import ro.htv.model.PostsResponse;
import ro.htv.model.Response;
import ro.htv.utils.FirestoreRepository;
import ro.htv.utils.StorageRepository;
import ro.htv.utils.Utils;

public class CometariiPostare extends AppCompatActivity {

    private RecyclerView recyclerView ;
    private AdapterList adapter;
    private RecyclerView.LayoutManager layoutManager;
    private RelativeLayout relativeLayout;

    private String TAG = "HackTheVirus Comentarii";

    private FirestoreRepository firestoreRepository;
    private StorageRepository storageRepository;

    private Post currentPost = new Post();
    private Post myComment = new Post();

    private ArrayList<Post> listOfPosts;

    private Dialog addComment;

    private String uidUser;
    private String idParent;
    private String userProfileImage;
    private String currentUserName;
    private String currentUserProfileImage;
    private int userKarma;
    private int parentKarma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cometarii_postare);

        androidx.appcompat.widget.Toolbar myToolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle(getString(R.string.comments));
        setSupportActionBar(myToolbar);

        idParent = getIntent().getExtras().getString("idPost");
        userProfileImage = getIntent().getExtras().getString("profileImage");
        currentUserProfileImage = getIntent().getExtras().getString("currentUserProfileImage");
        uidUser = getIntent().getExtras().getString("uid");
        currentUserName = getIntent().getExtras().getString("currentUserName");
        userKarma = getIntent().getExtras().getInt("userKarma");
        parentKarma = getIntent().getExtras().getInt("parentKarma");


        initEmptyComment();

        Log.d(TAG, "id paret " + idParent);

        firestoreRepository = new FirestoreRepository();
        storageRepository = new StorageRepository();

        getParentPost(idParent);
        getComments(idParent);

        //initFloatingButton();
        relativeLayout = findViewById(R.id.postare);
        recyclerView = findViewById(R.id.commview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settingsBtn) {
            startActivity(new Intent(getBaseContext(), Settings.class).putExtra("uid", uidUser));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateCurrentPost(final Post currentPost) {
        initFloatingButton();

        TextView nume = (TextView) findViewById(R.id.numePersoana);
        TextView Desc = (TextView) findViewById(R.id.descriere);
        ImageView imv = (ImageView)findViewById(R.id.imagineExercitiu);
        imv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView imzoom = (ImageView)findViewById(R.id.imagMare);
                Glide.with(getBaseContext())
                        .load(currentPost.getLinkToImage())
                        .transition(DrawableTransitionOptions.withCrossFade(1000))
                        .skipMemoryCache(true)
                        .into(imzoom);
                imzoom.setVisibility(View.VISIBLE);
                //relativeLayout.setVisibility(View.INVISIBLE);
                //recyclerView.setVisibility(View.INVISIBLE);

                findViewById(R.id.blur).setVisibility(View.VISIBLE);
            }
        });
        ImageView postOwnerProfilePicture = findViewById(R.id.imagineUser);
        TextView date = findViewById(R.id.data);

        TextView karma = findViewById(R.id.karma);

        int karmaVal = currentPost.getOwner_karma();
        int id = R.color.lowKarma;
        if (karmaVal > 15 && karmaVal <= 30) id = R.color.mediumKarma;
        if (karmaVal > 30) id = R.color.highKarma;

        int color = ContextCompat.getColor(this, id);
        karma.setTextColor(color);

        Date time = new Date((long)Integer.parseInt(currentPost.getTimestamp())*1000);
        date.setText(time.toString());

        karma.setText(String.valueOf(currentPost.getOwner_karma()));
        nume.setText(currentPost.getOwner_name());
        Desc.setText(currentPost.getText());

        Glide.with(this)
                .load(currentPost.getLinkToImage())
                .apply(new RequestOptions().override(400, 400))
                .into(imv);

        Glide.with(this)
                .load(currentPost.getOwner_profilePicture())
                .circleCrop()
                .into(postOwnerProfilePicture);
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
                    listOfPosts = response.getPosts();

                    Collections.sort(listOfPosts, new Comparator<Post>() {
                        public int compare(Post u1, Post u2) {
                            return u2.getTimestamp().toString().compareTo(u1.getTimestamp().toString());
                        }
                    });

                    adapter = new AdapterList(listOfPosts, Glide.with(getBaseContext()));
                    adapter.setOnItemClick(new AdapterList.OnItemClickListener() {
                        @Override
                        public void OnItemClick(int poz) {

                        }

                        @Override
                        public void OnPhotoClick(int poz) {
                            Post X = listOfPosts.get(poz);
                            ImageView imzoom = (ImageView)findViewById(R.id.imagMare);
                            Glide.with(getBaseContext())
                                    .load(X.getLinkToImage())
                                    .transition(DrawableTransitionOptions.withCrossFade(1000))
                                    .skipMemoryCache(true)
                                    .into(imzoom);
                            imzoom.setVisibility(View.VISIBLE);
                            //relativeLayout.setVisibility(View.INVISIBLE);
                            //recyclerView.setVisibility(View.INVISIBLE);

                            findViewById(R.id.blur).setVisibility(View.VISIBLE);
                        }
                        @Override
                        public void OnSmallPhotoClick(int poz) {
                            startActivity(new Intent(getBaseContext(), Profile.class).putExtra("targetUid", listOfPosts.get(poz).getOwnwer_uid()).putExtra("currentUid", uidUser));
                        }
                    });
                    recyclerView.setAdapter(adapter);
                }
            }
        });
    }

    private void initFloatingButton() {
        FloatingActionButton fb = findViewById(R.id.floating_action_button);
        initCommentDialog();

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComment.show();
            }
        });
    }

    private void initCommentDialog() {
        addComment = new Dialog(this);
        addComment.setContentView(R.layout.popup_add_post);
        TextView popup_title = addComment.findViewById(R.id.popup_newpost);
        popup_title.setText(getString(R.string.commentTo) + currentPost.getOwner_name());

        addComment.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addComment.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT,Toolbar.LayoutParams.WRAP_CONTENT);
        addComment.getWindow().getAttributes().gravity = Gravity.TOP;

        ImageView userProfilePicture = addComment.findViewById(R.id.popup_user_image);

        Log.d(TAG, currentUserProfileImage);
        Glide.with(this)
                .load(currentUserProfileImage)
                .into(userProfilePicture);

        ImageView addPhoto = addComment.findViewById(R.id.popup_addImage);
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPicture();
            }
        });

        ImageView post = addComment.findViewById(R.id.popup_add);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateComment();
            }
        });
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
            myComment.setLinkToImage(postImage);
            ImageView popup_image = addComment.findViewById(R.id.popup_img);

            Glide.with(this)
                    .load(postImage)
                    .apply(new RequestOptions().override(540, 960))
                    .into(popup_image);

            popup_image.setBackground(getDrawable(R.drawable.popup_text_style));
        }
    }

    private void validateComment() {
        EditText post_text = addComment.findViewById(R.id.popup_description);

        if (post_text.getText() != null && post_text.getText().toString().length() > 6) {
            myComment.setText(post_text.getText().toString());

            //if (!currentPost.getOwnwer_uid().equals(myComment.getOwnwer_uid())) cresteKarma = true;
            myComment.setOwner_karma(userKarma);

            Log.d(TAG, myComment.toString());
            System.out.println(myComment.getLinkToImage());
            if (myComment.getLinkToImage().equals("")) {
                MutableLiveData<Response> pendingPost = firestoreRepository.addPost(myComment);

                pendingPost.observe(this, new Observer<Response>() {
                    @Override
                    public void onChanged(Response response) {
                        if (response.ok()) {
                            done();
                        }
                    }
                });
            } else {
                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), Uri.parse(myComment.getLinkToImage()));
                try {
                    Bitmap bitmap = ImageDecoder.decodeBitmap(source);
                    uploadImage(bitmap);
                } catch (Exception e) {
                }
            }
        } else {
            Log.d(TAG, "erowre");
            TextView err = addComment.findViewById(R.id.errField);
            err.setText(getString(R.string.postError));
        }
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

                    myComment.setLinkToImage(downloadUrl);

                    MutableLiveData<Response> pendingPost = firestoreRepository.addPost(myComment);

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

    private void done() {
//        listOfPosts.add(myComment);
//        adapter = new AdapterList(listOfPosts);
//        recyclerView.setAdapter(adapter);
        getComments(idParent);

        addComment.hide();
        initFloatingButton();
        //getComments(idParent);

        myComment = new Post();

        initEmptyComment();
    }

    private void initEmptyComment() {
        myComment.setPost(false);
        myComment.setParent(idParent);
        myComment.setTimestamp("0000");
        myComment.setOwnwer_uid(uidUser);
        myComment.setOwner_name(currentUserName);
        myComment.setOwner_profilePicture(currentUserProfileImage);
        myComment.setOwner_karma(userKarma);

        Log.d(TAG, "user karma ii " + userKarma);
    }
    public void comeBack(View view) {
        ImageView imzoom = (ImageView)findViewById(R.id.imagMare);
        imzoom.setVisibility(View.INVISIBLE);

        findViewById(R.id.blur).setVisibility(View.INVISIBLE);
    }


}