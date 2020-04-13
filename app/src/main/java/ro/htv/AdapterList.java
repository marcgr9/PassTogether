package ro.htv;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ro.htv.model.PostsResponse;

import static androidx.core.content.ContextCompat.startActivity;

public class AdapterList extends RecyclerView.Adapter<AdapterList.Viewholder> {
    private ArrayList<Postare> listaelem;

    private OnItemClickListener mListener;
    public interface OnItemClickListener{
        void OnItemClick(int poz);
    }
    void setOnItemClick(OnItemClickListener listener)
    {
        mListener = listener;
    }

    public static class Viewholder extends RecyclerView.ViewHolder  {
        public ImageView Im1;
        public ImageView Im2;
        public TextView Nume;
        public TextView Desc;
        public RelativeLayout up;
        public RelativeLayout down;

        public Viewholder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            Im1 = itemView.findViewById(R.id.imagineUser);
            Im2 = itemView.findViewById(R.id.imagineExercitiu);
            Nume = itemView.findViewById(R.id.numePersoana);
            Desc = itemView.findViewById(R.id.descriere);
            up = itemView.findViewById(R.id.up);
            down = itemView.findViewById(R.id.down);
            Desc.setMovementMethod(new ScrollingMovementMethod());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int pos = getAdapterPosition();
                        listener.OnItemClick(pos);
                    }
                }
            });
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
        RecyclerView.ViewHolder hvs = new Viewholder(v, mListener);
        return (Viewholder) hvs;
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        Postare PostareActuala = listaelem.get(position);

        if (PostareActuala.tip == 1) {
            holder.up.setBackgroundColor(Color.parseColor("#dedd8c"));
            holder.down.setBackgroundColor(Color.parseColor("#8cdec7"));
        }
        if (PostareActuala.imRezEx == null)
            holder.Im2.setImageDrawable(null);
        holder.Nume.setText(PostareActuala.getNumePrenume());
        holder.Desc.setText(PostareActuala.getDescriere());
    }

    @Override
    public int getItemCount() {
        return listaelem.size();
    }

}
