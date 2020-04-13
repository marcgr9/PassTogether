package ro.htv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.common.io.LineReader;

import java.util.ArrayList;

public class PostariTopic extends AppCompatActivity {

    private RecyclerView recyclerView ;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postari_topic);
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
}
