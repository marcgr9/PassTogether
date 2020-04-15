package ro.htv;

import android.content.Context;
import android.graphics.Color;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Date;

import kotlin.jvm.JvmStatic;
import ro.htv.model.Post;
import ro.htv.utils.Utils;

public class AdapterList extends RecyclerView.Adapter<AdapterList.Viewholder> {
    private ArrayList<Post> listaelem;

    private OnItemClickListener mListener;
    private static Context localcontext;
    public interface OnItemClickListener{
        void OnItemClick(int poz);
        void OnPhotoClick(int poz);
        void OnSmallPhotoClick(int poz);
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
        public TextView dataa;
        public RelativeLayout up;
        public RelativeLayout down;
        public RelativeLayout Tot;

        TextView karma;

        public Viewholder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            Im1 = itemView.findViewById(R.id.imagineUser);
            Im1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null)
                    listener.OnSmallPhotoClick(getAdapterPosition());
                }
            });
            localcontext = itemView.getContext();
            Im2 = itemView.findViewById(R.id.imagineExercitiu);
            Im2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null)
                    listener.OnPhotoClick(getAdapterPosition());
                }
            });
            Nume = itemView.findViewById(R.id.numePersoana);
            Desc = itemView.findViewById(R.id.descriere);
            up = itemView.findViewById(R.id.SUUS);
            down = itemView.findViewById(R.id.down);
            Tot = itemView.findViewById(R.id.Cardpost);
            dataa = itemView.findViewById(R.id.data);
            karma = itemView.findViewById(R.id.karma);
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
    public AdapterList (ArrayList<Post>lista)
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
        Post PostareActuala = listaelem.get(position);

        if (PostareActuala.getPost() == false) {
            holder.Tot.setBackgroundColor(Color.parseColor("#8cdec7"));
            if (!PostareActuala.getLinkToImage().equals(""))  {
                Glide.with(localcontext)
                        .load(PostareActuala.getLinkToImage())
                        .apply(new RequestOptions().override(540, 960))
                        .into(holder.Im2);
            }
        }
        holder.karma.setText(String.valueOf(PostareActuala.getOwner_karma()));
        holder.Nume.setText(PostareActuala.getOwner_name());
        holder.Desc.setText(PostareActuala.getText());
        Date time = new Date((long)Integer.parseInt(PostareActuala.getTimestamp())*1000);
        holder.dataa.setText(time.toString());
        if (PostareActuala.getPost() == true)
        if (!PostareActuala.getLinkToImage().equals(""))  {
            Glide.with(localcontext)
                    .load(PostareActuala.getLinkToImage())
                    .into(holder.Im2);
        }

        Glide.with(localcontext)
                .load(PostareActuala.getOwner_profilePicture())
                .circleCrop()
                .into(holder.Im1);

    }

    @Override
    public int getItemCount() {
        return listaelem.size();
    }

    public void setContext(Context context) {
        localcontext = context;
    }


}
