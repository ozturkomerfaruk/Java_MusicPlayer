package com.mobile.musicplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.BreakIterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MusicAdapter2 extends RecyclerView.Adapter<MusicAdapter2.MyViewHolder> {

    private List<MusicList> list;
    private final Context context;
    private int playingPosition = 0;
    private final SongChangeListener songChangeListener;

    public MusicAdapter2(List<MusicList> list, Context context) {
        this.list = list;
        this.context = context;
        this.songChangeListener = ((SongChangeListener)context);
    }

    @NonNull
    @Override
    public MusicAdapter2.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate((R.layout.music_adapter_layout2),null));

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MusicList list2 = list.get(position);

        if(list2.isPlaying()) {
            playingPosition = position;
            holder.rootLayout2.setBackgroundResource(R.drawable.round_back_blue_10);
        } else {
            holder.rootLayout2.setBackgroundResource(R.drawable.round_back_10);
        }


        String generateDuration = String.format(Locale.getDefault(), "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(list2.getDuration())),
                TimeUnit.MILLISECONDS.toSeconds(Long.parseLong(list2.getDuration())),
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(
                        Long.parseLong(list2.getDuration()))));
        holder.title2.setText(list2.getTitle());
        holder.artist2.setText(list2.getArtist());
        holder.musicDuration2.setText(generateDuration);

        holder.deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                songChangeListener.onDeleteIconClick(list2);
                notifyDataSetChanged();
            }
        });

        holder.rootLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.get(playingPosition).setPlaying(false);
                list2.setPlaying(true);

                songChangeListener.onChanged(position);
                notifyDataSetChanged();
            }
        });
    }

    public void updateList(List<MusicList> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    static class MyViewHolder extends RecyclerView.ViewHolder {

        private final RelativeLayout rootLayout2;
        private final TextView title2;
        private final TextView artist2;
        private final TextView musicDuration2;
        private final ImageView deleteIcon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            rootLayout2 = itemView.findViewById(R.id.rootLayout2);
            title2 = itemView.findViewById(R.id.musicTitle2);
            artist2 = itemView.findViewById(R.id.musicArtist2);
            musicDuration2 = itemView.findViewById(R.id.musicDuration2);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
        }
    }
}
