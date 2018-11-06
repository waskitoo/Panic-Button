package com.imba.kelompol.panicbutton;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.imba.kelompol.panicbutton.Models.API.History;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<History> list;

    public HistoryAdapter(List<History> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_history,parent,false);
        return new HistoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        History item = list.get(position);
        holder.lblUuid.setText(item.getUuid());
        holder.lblStatus.setText(item.getStatus());
        holder.lblTime.setText(item.getTime());
        holder.lblLatlon.setText(item.getLatlon());
        holder.lblType.setText(item.getType());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView lblUuid, lblStatus, lblTime, lblLatlon, lblType;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lblUuid = (TextView) itemView.findViewById(R.id.itemHistoryUUID);
            lblStatus = (TextView) itemView.findViewById(R.id.itemHistoryStatus);
            lblTime = (TextView) itemView.findViewById(R.id.itemHistoryTime);
            lblLatlon = (TextView) itemView.findViewById(R.id.itemLatlon);
            lblType = (TextView) itemView.findViewById(R.id.itemType);
        }
    }
}
