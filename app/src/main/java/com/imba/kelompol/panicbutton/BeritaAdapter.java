package com.imba.kelompol.panicbutton;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class BeritaAdapter extends RecyclerView.Adapter<BeritaAdapter.MyViewHolder> {
    private Context context;
    private List<String> dataBerita;
    private List<String> dataLink;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView Berita,LinkBerita;
        private CardView CVBerita;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            Berita =(TextView)itemView.findViewById(R.id.Berita);
            LinkBerita = (TextView)itemView.findViewById(R.id.LinkBerita);
            CVBerita = (CardView)itemView.findViewById(R.id.CVBerita);
        }
    }
    public BeritaAdapter(List<String> dataBerita, List<String> dataLink, Context context){
        this.dataBerita =dataBerita;
        this.dataLink = dataLink;
        this.context = context;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.berita_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final String listBerita = dataBerita.get(position);
        final String listLink = dataLink.get(position);
        holder.Berita.setText(""+listBerita);
        holder.LinkBerita.setText(""+listLink);
    }

    @Override
    public int getItemCount() { return dataBerita.size();}


}
