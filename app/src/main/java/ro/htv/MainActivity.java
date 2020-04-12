package ro.htv;

import androidx.appcompat.app.AppCompatActivity;
import ro.htv.utils.AuthRepository;
import ro.htv.utils.FirestoreRepository;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // sa ma pis pe el java

        String uid = getIntent().getStringExtra("uid");

        TextView txtView = findViewById(R.id.textView);

        txtView.setText("apasa-ma pt logout \n " + uid);

        txtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthRepository as = new AuthRepository();
                as.logout();
                startActivity(new Intent(getBaseContext(), Login.class));
            }
        });
    }
}
