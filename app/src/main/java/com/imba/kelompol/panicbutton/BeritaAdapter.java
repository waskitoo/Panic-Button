package com.imba.kelompol.panicbutton;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.imba.kelompol.panicbutton.Models.API.Article.Article;

import java.util.List;

public class BeritaAdapter extends RecyclerView.Adapter<BeritaAdapter.MyViewHolder> {
    private Context context;
    private List<String> dataBerita;
    private List<String> dataLink;
    private List<Article> dataArticles;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView Berita,LinkBerita, BeritaTitle;
        private CardView CVBerita;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            BeritaTitle =(TextView)itemView.findViewById(R.id.BeritaTitle);
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
    public BeritaAdapter(List<Article> dataArticles, Context context){
        this.dataArticles = dataArticles;
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
        if(this.dataArticles == null){
            final String listBerita = dataBerita.get(position);
            final String listLink = dataLink.get(position);
            holder.Berita.setText(""+listBerita);
            holder.LinkBerita.setText(""+listLink);
        }else{
            Article article = dataArticles.get(position);
            holder.BeritaTitle.setText(article.getTitle());
            holder.Berita.setText(article.getDescription());
            holder.LinkBerita.setText(article.getUrl());
        }
    }

    @Override
    public int getItemCount() {
        if(this.dataArticles == null){
            return dataBerita.size();
        }else{
            return dataArticles.size();
        }
    }


}
