package com.example.musicplayerapp.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.MediaStore.Audio.Media;

public class MusicPlayService extends Service implements Runnable {

	public static MediaPlayer musicPlayer;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void run() {
	}

}
