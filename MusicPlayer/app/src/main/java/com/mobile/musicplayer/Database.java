package com.mobile.musicplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Database extends SQLiteOpenHelper {
    private static Database instance;
    private static final String databaseName = "MusicPlayer";
    private static final int DB_VERSION = 1;

    public Database(Context context) {
        super(context, databaseName, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS ListOfPlaylist(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " title VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS Playlist(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "playlistName VARCHAR, " +
                "title VARCHAR, " +
                "artist VARCHAR," +
                "duration VARCHAR," +
                "musicFile VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static Database getInstance(Context context) {
        if(instance == null){
            instance = new Database(context);
        }
        return instance;
    }

    public void addPlaylist(String title){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        database.insert("ListOfPlaylist",null, contentValues);
        MainActivity.getInstance().getPlaylists();
    }
    public void deletePlaylist(String title){
        SQLiteDatabase database = this.getWritableDatabase();
        String[] args = {title};
        database.delete("ListOfPlaylist","title =?", args);
        MainActivity.getInstance().getPlaylists();
    }
    public List<String> getListOfPlaylist() {
        SQLiteDatabase database = this.getReadableDatabase();
        List<String> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT title FROM ListOfPlaylist", null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            int columnIndex = cursor.getColumnIndex("title");
            list.add(cursor.getString(columnIndex));
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public void addMusicToPlaylist(String playlistName, MusicList musicList){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("playlistName", playlistName);
        contentValues.put("title", musicList.getTitle());
        contentValues.put("artist", musicList.getArtist());
        contentValues.put("duration", musicList.getDuration());
        contentValues.put("musicFile", String.valueOf(musicList.getMusicFile()));
        database.insert("Playlist",null, contentValues);
        MainActivity.getInstance().getMusicFilesOfPlaylist(playlistName);
    }
    public void deleteMusicFromPlaylist(String playlistName, String title) {
        SQLiteDatabase database = this.getWritableDatabase();
        String[] args = {playlistName,title};
        database.delete("Playlist","playlistName=? AND title =?", args);
        MainActivity.getInstance().getMusicFilesOfPlaylist(playlistName);
    }

    public List<String> getPlaylist(String playlistName) {
        SQLiteDatabase database = this.getReadableDatabase();
        List<String> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM Playlist WHERE playlistName=?", new String[]{playlistName});
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            int titleIndex = cursor.getColumnIndex("title");
            list.add(cursor.getString(titleIndex));
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }
}
