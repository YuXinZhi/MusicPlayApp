package com.example.musicplayerapp.utils;

import java.util.ArrayList;
import java.util.List;

import com.example.musicplayerapp.pojo.Music;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

public class MusicUtils {
	public interface MyMusicAction {
		public static String ACTION_CLOSE = "com.example.action.close";
		public static String ACTION_MUSIC_SERVER_PLAYER = "com.example.action.musicserviceplayer";
		public static String ACTION_MUSIC_PROGRESS = "com.example.action.musicprogress";
	}

	public static String formatTimeString(int time) {
		time /= 1000;
		int minute = time / 60;
		minute %= 60;
		int second = time % 60;
		int hour = minute / 60;
		return String.format("%02d:%02d", minute, second);
	}

	// 获取所有歌曲
	public static List<Music> getAllSongs(Context context) {

		List<Music> musicList = new ArrayList<Music>();
		ContentResolver cr = context.getContentResolver();
		if (cr != null) {
			// 获取所有歌曲
			Cursor cursor = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
					MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
			if (null == cursor) {
				return null;
			}
			if (cursor.moveToFirst()) {
				do {
					Music m = new Music();
					String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
					String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
					if ("<unknown>".equals(artist)) {
						artist = "未知艺术家";
					}
					String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
					long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
					long time = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
					String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
					String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
					String sbr = name.substring(name.length() - 3, name.length());
					if (sbr.equals("mp3") && (time >= 1000 && time <= 900000)) {
						m.setTitle(title);
						m.setArtist(artist);
						m.setAlbum(album);
						m.setSize(size);
						m.setTime(time);
						m.setUrl(url);
						m.setName(name);
						musicList.add(m);
					}
				} while (cursor.moveToNext());
			}
			if (cursor != null) {
				cursor.close();
			}
		}
		return musicList;
	}
}
