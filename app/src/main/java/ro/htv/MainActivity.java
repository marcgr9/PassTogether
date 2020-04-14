package ro.htv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import ro.htv.utils.AuthRepository;
import ro.htv.utils.FirestoreRepository;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private String TAG = "HackTheVirus Main";

    private String uid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "main");

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // sa ma pis pe el java

        uid = getIntent().getStringExtra("uid");

        TextView txtView = findViewById(R.id.textView);
        Button btn = findViewById(R.id.button);
        ImageView settings = findViewById(R.id.settingsbtn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), TopicSelection.class).putExtra("uid", uid));
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Settings.class);
                startActivity((intent).putExtra("uid", uid));
            }
        });

        txtView.setText("apasa-ma pt logout \n " + uid);

        txtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthRepository as = new AuthRepository();
                as.logout();
                startActivity(new Intent(getBaseContext(), Login.class));
            }
        });

        FirestoreRepository fs = new FirestoreRepository();
        fs.getTopics();

        TextView txt2 = findViewById(R.id.textView2);

        txt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getBaseContext(), UserProfile.class).putExtra("uid", uid));
            }
        });
    }
}
