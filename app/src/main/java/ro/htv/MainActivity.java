package ro.htv;

import androidx.appcompat.app.AppCompatActivity;
import ro.htv.utils.AuthRepository;
import ro.htv.utils.FirestoreRepository;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private String TAG = "HackTheVirus Main";

    private String uid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "main");


        // sa ma pis pe el java

        uid = getIntent().getStringExtra("uid");

        TextView txtView = findViewById(R.id.textView);
        Button btn = findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), TopicSelection.class));
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
