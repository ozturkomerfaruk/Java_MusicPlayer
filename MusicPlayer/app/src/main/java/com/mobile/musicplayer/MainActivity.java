package com.mobile.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements SongChangeListener, PlaylistListener {
    private static MainActivity instance;

    private final List<MusicList> musicLists = new ArrayList<>();
    private List<String> listOfPlaylist;
    private List<String> listOfPlaylist2;

    private RecyclerView musicRecyclerView, playlistRecyclerView, playlistRecyclerView2;
    private MediaPlayer mediaPlayer;
    private ImageView playPauseImg;
    private TextView endTime, startTime;
    private DrawerLayout drawerLayout;
    private boolean isPlaying = false;
    private SeekBar playerSeekBar;
    private Timer timer;
    private int currentSongListPosition = 0;
    private MusicAdapter musicAdapter;
    private MusicAdapter2 musicAdapter2;
    private PlaylistAdapter playlistAdapter;
    private PlaylistAdapter2 playlistAdapter2;

    private String publicPlaylistName;
    private float playbackSpeed = 1;

    public Database db;

    public static MainActivity getInstance() {
        if (instance == null)
            instance = new MainActivity();
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final LinearLayout menuBtn = findViewById(R.id.menuBtn);
        final LinearLayout playbackSpeedBtn = findViewById(R.id.playbackSpeedBtn);
        final TextView playbackSpeedText = findViewById(R.id.playbackSpeedText);
        final RelativeLayout newPlaylistBtn = findViewById(R.id.drawerBottomBar);
        musicRecyclerView = findViewById(R.id.musicRecyclerView);
        playlistRecyclerView = findViewById(R.id.playlistRecyclerView);
        playlistRecyclerView2 = findViewById(R.id.playlistRecyclerView2);
        final CardView playPauseCard = findViewById(R.id.playPauseCard);
        playPauseImg = findViewById(R.id.playPauseImg);
        final ImageView nextBtn = findViewById(R.id.nextBtn);
        final ImageView previousBtn = findViewById(R.id.previousBtn);
        drawerLayout = findViewById(R.id.drawerLayout);

        instance = this;
        db = Database.getInstance(this);
        startTime = findViewById(R.id.startTime);
        endTime = findViewById(R.id.endTime);
        playerSeekBar = findViewById(R.id.playerSeekBar);

        musicRecyclerView.setHasFixedSize(true);
        musicRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        playlistRecyclerView.setHasFixedSize(true);
        playlistRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        playlistRecyclerView2.setHasFixedSize(true);
        playlistRecyclerView2.setLayoutManager(new LinearLayoutManager(this));

        playerSeekBar.setProgress(0);
        mediaPlayer = new MediaPlayer();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            getMusicFiles();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
                }, 11);
            } else {
                getMusicFiles();
            }
        }
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPlaylists();
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int nextSongListPosition = currentSongListPosition + 1;

                if (nextSongListPosition >= musicLists.size()) {
                    nextSongListPosition = 0;
                }

                musicLists.get(currentSongListPosition).setPlaying(false);
                musicLists.get(nextSongListPosition).setPlaying(true);

                musicAdapter.updateList(musicLists);
                musicAdapter2.updateList(musicLists);

                musicRecyclerView.scrollToPosition(nextSongListPosition);
                onChanged(nextSongListPosition);
            }
        });

        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int previousSongListPosition = currentSongListPosition - 1;

                if (previousSongListPosition < 0) {
                    previousSongListPosition = musicLists.size() - 1;
                }

                musicLists.get(currentSongListPosition).setPlaying(false);
                musicLists.get(previousSongListPosition).setPlaying(true);

                musicAdapter.updateList(musicLists);
                musicAdapter2.updateList(musicLists);

                musicRecyclerView.scrollToPosition(previousSongListPosition);
                onChanged(previousSongListPosition);
            }
        });

        playPauseCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying) {
                    isPlaying = false;

                    mediaPlayer.pause();
                    playPauseImg.setImageResource(R.drawable.play_icon);
                } else {
                    isPlaying = true;
                    if(currentSongListPosition == 0){
                        musicLists.get(currentSongListPosition).setPlaying(true);
                    }
                    mediaPlayer.start();
                    playPauseImg.setImageResource(R.drawable.pause_icon);
                    setPlaySpeed(playbackSpeed);
                }
            }
        });

        playerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    if (isPlaying) {
                        mediaPlayer.seekTo(i);
                    } else {
                        mediaPlayer.seekTo(0);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        newPlaylistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PlaylistPopup.class);
                startActivity(intent);
            }
        });

        playbackSpeedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String speed = playbackSpeedText.getText().toString();
                switch (speed){
                    case "x0.5" :
                        playbackSpeedText.setText("x1.0");
                        playbackSpeed = 1;
                        break;
                    case "x1.0" :
                        playbackSpeedText.setText("x1.5");
                        playbackSpeed = 1.5f;
                        break;
                    case "x1.5" :
                        playbackSpeedText.setText("x2.0");
                        playbackSpeed = 2;
                        break;
                    case "x2.0" :
                        playbackSpeedText.setText("x0.5");
                        playbackSpeed = 0.5f;
                        break;
                }
                if(!isPlaying){
                    return;
                }
                setPlaySpeed(playbackSpeed);
            }
        });
    }

    public void getPlaylists() {
        listOfPlaylist = db.getListOfPlaylist();
        listOfPlaylist.add(0, "All Musics");
        playlistAdapter = new PlaylistAdapter(listOfPlaylist, this);
        playlistRecyclerView.setAdapter(playlistAdapter);
    }

    public void getPlaylists2() {
        listOfPlaylist2 = db.getListOfPlaylist();
        playlistAdapter2 = new PlaylistAdapter2(listOfPlaylist2, this);
        playlistRecyclerView2.setAdapter(playlistAdapter2);
    }

    @SuppressLint("Range")
    public void getMusicFiles() {
        musicLists.clear();
        mediaPlayer.reset();
        currentSongListPosition = 0;
        playerSeekBar.setProgress(0);
        startTime.setText("00:00");
        endTime.setText("00:00");
        ContentResolver contentResolver = getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor = contentResolver.query(uri, null, MediaStore.Audio.Media.DATA + " LIKE?", new String[]{"%.mp3"}, null);

        if (cursor == null) {
            Toast.makeText(this, "Something went wrong!!!", Toast.LENGTH_SHORT).show();
        } else if (!cursor.moveToNext()) {
            Toast.makeText(this, "No Music Found", Toast.LENGTH_SHORT).show();
        } else {
            do {
                @SuppressLint("Range") final String getMusicFileName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                @SuppressLint("Range") final String getArtistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                @SuppressLint("Range") long cursorID = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));

                Uri musicFileUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursorID);

                String getDuration = "00:00";

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    getDuration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION));
                }

                final MusicList musicList = new MusicList(getMusicFileName, getArtistName, getDuration, false, musicFileUri);
                musicLists.add(musicList);
            } while (cursor.moveToNext());

            musicAdapter = new MusicAdapter(musicLists, this);
            musicRecyclerView.setAdapter(musicAdapter);
        }

        cursor.close();
    }
    @SuppressLint("Range")
    public void getMusicFilesOfPlaylist(String playlistName) {
        publicPlaylistName = playlistName;
        musicLists.clear();
        mediaPlayer.reset();
        currentSongListPosition = 0;
        playerSeekBar.setProgress(0);
        startTime.setText("00:00");
        endTime.setText("00:00");
        List<String> musics = Database.getInstance(MainActivity.this).getPlaylist(playlistName);
        ContentResolver contentResolver = getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor = contentResolver.query(uri, null, MediaStore.Audio.Media.DATA + " LIKE?", new String[]{"%.mp3"}, null);

        if (cursor == null) {
            Toast.makeText(this, "Something went wrong!!!", Toast.LENGTH_SHORT).show();
        } else if (!cursor.moveToNext()) {
            Toast.makeText(this, "No Music Found", Toast.LENGTH_SHORT).show();
        } else {
            do {
                @SuppressLint("Range") final String getMusicFileName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                @SuppressLint("Range") final String getArtistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                @SuppressLint("Range") long cursorID = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));

                Uri musicFileUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, cursorID);

                String getDuration = "00:00";

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    getDuration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION));
                }

                final MusicList musicList = new MusicList(getMusicFileName, getArtistName, getDuration, false, musicFileUri);
                if(musics.contains(getMusicFileName)){
                    musicLists.add(musicList);
                }
            } while (cursor.moveToNext());

            musicAdapter2 = new MusicAdapter2(musicLists, this);
            musicRecyclerView.setAdapter(musicAdapter2);
        }

        cursor.close();
    }

    private void setPlaySpeed(float speed) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                PlaybackParams params = mediaPlayer.getPlaybackParams();
                params.setSpeed(speed);
                mediaPlayer.setPlaybackParams(params);
            }catch (Exception e){
                System.out.println(e.getCause());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getMusicFiles();
        } else {
            Toast.makeText(this, "Permission Declined By User", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onChanged(int position) {

        currentSongListPosition = position;

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.reset();
        }

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mediaPlayer.setDataSource(MainActivity.this, musicLists.get(position).getMusicFile());
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                final int getTotalDuration = mp.getDuration();

                String generateDuration = String.format(Locale.getDefault(), "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(getTotalDuration),
                    TimeUnit.MILLISECONDS.toSeconds(getTotalDuration),
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getTotalDuration)));

                endTime.setText(generateDuration);
                isPlaying = true;

                mp.start();
                playerSeekBar.setMax(getTotalDuration);
                playPauseImg.setImageResource(R.drawable.pause_icon);
            }
        });

        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final int getCurrentDuration = mediaPlayer.getCurrentPosition();

                        String generateDuration = String.format(Locale.getDefault(), "%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(getCurrentDuration),
                            TimeUnit.MILLISECONDS.toSeconds(getCurrentDuration),
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getCurrentDuration)));

                        playerSeekBar.setProgress(getCurrentDuration);

                        startTime.setText(generateDuration);
                    }
                });

            }
        }, 1000, 1000);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.reset();

                timer.purge();
                timer.cancel();

                isPlaying = false;

                playPauseImg.setImageResource(R.drawable.play_icon);

                playerSeekBar.setProgress(0);
            }
        });
    }

    MusicList musicList2;

    @Override
    public void onAddIconClick(MusicList musicList) {
        getPlaylists2();
        musicList2 = musicList;
        drawerLayout.openDrawer(GravityCompat.END);
    }

    @Override
    public void onDeleteIconClick(MusicList musicList) {
        Database.getInstance(MainActivity.this).deleteMusicFromPlaylist(publicPlaylistName,musicList.getTitle());
        getMusicFilesOfPlaylist(publicPlaylistName);
        Toast.makeText(MainActivity.this, "Müzik Listeden Kaldırıldı", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void playlistAdded(String playlistName) {
        Database.getInstance(MainActivity.this).addMusicToPlaylist(playlistName, musicList2);
        getMusicFilesOfPlaylist(playlistName);
        Toast.makeText(MainActivity.this, "Müzik Listeye Eklendi", Toast.LENGTH_SHORT).show();
    }
}
