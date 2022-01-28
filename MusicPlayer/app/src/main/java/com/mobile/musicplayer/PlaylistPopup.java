package com.mobile.musicplayer;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;

public class PlaylistPopup extends Activity {
    private Database db;
    private EditText editText;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_playlist_popup);
        editText = findViewById(R.id.editTextPlaylistName);
        db = Database.getInstance(this);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*0.8),(int)(height*0.4));
    }

    public void addNewPlaylist(View view) {
        String title = editText.getText().toString();
        db.addPlaylist(title);
        MainActivity.getInstance().getPlaylists();
        finish();
    }
}
