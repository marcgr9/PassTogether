package ro.htv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

import ro.htv.model.Post;

public class CometariiPostare extends AppCompatActivity {

    private RecyclerView recyclerView ;
    private AdapterList adapter;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cometarii_postare);
        recyclerView = findViewById(R.id.commview);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        TryFill();
        TextView tv = (TextView)findViewById(R.id.numePersoana);
        tv.setText("Merge Asta");
        TextView desc = (TextView)findViewById(R.id.descriere);
        desc.setText("DEscrierea");
        ArrayList<Post> lista = new ArrayList<>();
        adapter = new AdapterList(lista);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
    void TryFill()
    {
        ;
    }
}
