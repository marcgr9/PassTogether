package ro.htv;

import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterList extends RecyclerView.Adapter<AdapterList.Viewholder> {
    private ArrayList<Postare> listaelem;


    public static class Viewholder extends RecyclerView.ViewHolder{
        public ImageView Im1;
        public ImageView Im2;
        public TextView Nume;
        public TextView Desc;


        public Viewholder(@NonNull View itemView) {
            super(itemView);
            Im1 = itemView.findViewById(R.id.imagineUser);
            Im2 = itemView.findViewById(R.id.imagineExercitiu);
            Nume = itemView.findViewById(R.id.numePersoana);
            Desc = itemView.findViewById(R.id.descriere);
        }
    }
    public AdapterList (ArrayList<Postare>lista)
    {
        listaelem = lista;
    }
    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_test, parent, false);
        RecyclerView.ViewHolder hvs = new Viewholder(v);
        return (Viewholder) hvs;
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        Postare PostareActuala = listaelem.get(position);

        ///holder.Im1.
        holder.Nume.setText(PostareActuala.getNumePrenume());
        holder.Desc.setText(PostareActuala.getDescriere());
    }

    @Override
    public int getItemCount() {
        return listaelem.size();
    }
}
