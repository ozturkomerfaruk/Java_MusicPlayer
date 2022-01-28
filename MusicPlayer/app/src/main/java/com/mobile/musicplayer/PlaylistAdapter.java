package com.mobile.musicplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.MyViewHolder> {

    private List<String> list;
    private final Context context;

    public PlaylistAdapter(List<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public PlaylistAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlaylistAdapter.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate((R.layout.playlist_adapter_layout),null));
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.playlistTitle.setText(list.get(position));
        if(list.get(position) == "All Musics"){
            holder.deletePlayList.setVisibility(View.GONE);
            holder.deletePlayList.setActivated(false);
        }
        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list.get(position) == "All Musics"){
                    MainActivity.getInstance().getMusicFiles();
                }
                else {
                    MainActivity.getInstance().getMusicFilesOfPlaylist(list.get(position));
                }
            }
        });
        holder.deletePlayList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Database.getInstance(context).deletePlaylist(list.get(position));
                MainActivity.getInstance().getPlaylists();
                MainActivity.getInstance().getMusicFiles();
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
        public ImageView deletePlayList;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            playlistTitle = itemView.findViewById(R.id.playlistTitle);
            rootLayout = itemView.findViewById(R.id.rootLayout);
            deletePlayList = itemView.findViewById(R.id.deletePlayList);
        }
    }
}
