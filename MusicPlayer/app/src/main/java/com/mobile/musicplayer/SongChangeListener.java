package com.mobile.musicplayer;

public interface SongChangeListener {

    void onChanged(int position);
    void onAddIconClick(MusicList musicList);
    void onDeleteIconClick(MusicList musicList);
}
