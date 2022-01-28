package com.mobile.musicplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlaylistAdapter2 extends RecyclerView.Adapter<PlaylistAdapter2.MyViewHolder> {

    private List<String> list;
    private final Context context;

    private final PlaylistListener playlistListener;

    public PlaylistAdapter2(List<String> list, Context context) {
        this.list = list;
        this.context = context;
        this.playlistListener = ((PlaylistListener)context);
    }

    @NonNull
    @Override
    public PlaylistAdapter2.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlaylistAdapter2.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate((R.layout.playlist_adapter_layout2),null));
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter2.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.playlistTitle.setText(list.get(position));
        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playlistListener.playlistAdded(list.get(position));
                //Database.getInstance(context).
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView playlistTitle;
        public RelativeLayout rootLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            playlistTitle = itemView.findViewById(R.id.playlistTitle);
            rootLayout = itemView.findViewById(R.id.rootLayout2);
        }
    }
}
