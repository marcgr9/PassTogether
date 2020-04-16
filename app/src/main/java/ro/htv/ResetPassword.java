package ro.htv;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPassword extends AppCompatActivity {

    private Button reset_btn;
    private EditText editTextEmail;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        reset_btn = findViewById(R.id.ResetButton);
        final TextView errField = findViewById(R.id.errField);

        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = editTextEmail.getText().toString();

                if(TextUtils.isEmpty(userEmail))
                {
                    errField.setText("Email-ul nu poate fi gol");
                }
                else
                {
                    mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener
                            (new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(ResetPassword.this,
                                        "Email-ul de resetare a parolei a fost trimis!",
                                Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ResetPassword.this, Login.class));
                                finish();
                            }
                            else
                            {
                                errField.setText(task.getException().getMessage());
                            }
                        }
                    });
                }
            }
        });
    }
}
