package com.example.musicplayerapp;

import java.util.ArrayList;
import java.util.List;

import com.example.musicplayerapp.pojo.Music;
import com.example.musicplayerapp.pojo.ScreenInfo;
import com.example.musicplayerapp.service.MusicPlayService;
import com.example.musicplayerapp.ui.ArtistActivity;
import com.example.musicplayerapp.ui.MusicActivity;
import com.example.musicplayerapp.ui.MusicListActivity;
import com.example.musicplayerapp.ui.MusicPlayActivity;
import com.example.musicplayerapp.ui.OnlineActivity;
import com.example.musicplayerapp.utils.MusicNum;
import com.example.musicplayerapp.utils.MusicUtils;

import android.app.ActivityGroup;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 主界面
 * 
 * @author Ben
 *
 */
@SuppressWarnings("deprecation")
public class MainActivityGroup extends ActivityGroup implements OnClickListener, MusicUtils.MyMusicAction {

	private final static String ACTIVITY_MUSIC = "ActivityMusic";
	private final static String ACTIVITY_ARTIST = "ActivityArtist";
	private final static String ACTIVITY_LIST = "ActivityList";
	private final static String ACTIVITY_ONLINE = "ActivityOnline";

	SharedPreferences mySharedPreferences;
	boolean isRunning;
	private RelativeLayout rlMain;

	private ImageButton btnMainPlay;
	private TextView tvMainMusicName;
	private TextView tvMainArtistName;

	ViewPager mViewPager;
	ScreenInfo mScreenInfo;

	// ViewPager顶部按钮
	Button btnMusic;
	Button btnArtist;
	Button btnList;
	Button btnOnline;
	// 屏幕宽度
	int screenWidthPixels;
	int screenHeightPixels;

	private CloseBroadcastReiceiver mCloseReceiver;
	private MusicCompletionListener mMusicCompletionListener;
	private MusicProgressBroadcastReceiver mMusicProgressReceiver;

	static ImageView ivTitleCursor;
	Button[] titles;
	private int titlesLen;
	private List<Music> musicList;
	private ArrayList<View> mPageViews;

	private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 获取屏幕宽度
		mScreenInfo = new ScreenInfo(MainActivityGroup.this);
		screenWidthPixels = mScreenInfo.getWidthPixels();
		screenHeightPixels = mScreenInfo.getHeightPixels();

		initUI();
		resgisterAllReceivers();

		Button[] titles = { btnMusic, btnArtist, btnList, btnOnline };
		titlesLen = titles.length;
		musicList = MusicUtils.getAllSongs(this);
		mySharedPreferences = getSharedPreferences("music", Context.MODE_PRIVATE);
		for (int i = 0; i < titlesLen; i++) {
			final int index = i;
			titles[index].setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					switchTitle(index);
					mViewPager.setCurrentItem(index);
				}
			});
		}

		isRunning = true;
		mHandler.postDelayed(task, 10);
	}

	private Runnable task = new Runnable() {

		@Override
		public void run() {
		}
	};

	@Override
	protected void onDestroy() {
		unResgisterAllReceivers();
		super.onDestroy();
	}

	private void unResgisterAllReceivers() {
		this.unregisterReceiver(mCloseReceiver);
		this.unregisterReceiver(mMusicCompletionListener);
		this.unregisterReceiver(mMusicProgressReceiver);
	}

	// 注册BroadcastReiceiver
	private void resgisterAllReceivers() {
		mCloseReceiver = new CloseBroadcastReiceiver();
		IntentFilter closeIntentFilter = new IntentFilter(ACTION_CLOSE);
		this.registerReceiver(mCloseReceiver, closeIntentFilter);

		mMusicCompletionListener = new MusicCompletionListener();
		IntentFilter musicCompletionFilter = new IntentFilter(ACTION_MUSIC_SERVER_PLAYER);
		this.registerReceiver(mMusicCompletionListener, musicCompletionFilter);

		mMusicProgressReceiver = new MusicProgressBroadcastReceiver();
		IntentFilter progressFilter = new IntentFilter(ACTION_MUSIC_PROGRESS);
		this.registerReceiver(mMusicProgressReceiver, progressFilter);

	}

	// 初始化UI
	private void initUI() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 整体布局
		setContentView(R.layout.group_activity_main);

		initViewPager();
		findViews();
		initTitleCursor();

	}

	// 切换标题栏
	private void switchTitle(int titleIndex) {
		for (int i = 0; i < titlesLen; i++) {
			final int index = i;
			if (index == titleIndex) {
				titles[index].setTextColor(Color.parseColor("#ffffff"));
			} else {
				titles[index].setTextColor(Color.parseColor("#adadad"));
			}
		}
	}

	private void initTitleCursor() {
		RelativeLayout.LayoutParams rlParams = (RelativeLayout.LayoutParams) ivTitleCursor.getLayoutParams();
		rlParams.width = screenWidthPixels / 4;
		ivTitleCursor.setLayoutParams(rlParams);

	}

	private void findViews() {
		rlMain = (RelativeLayout) findViewById(R.id.rl_main);
		tvMainMusicName = (TextView) findViewById(R.id.tv_main_music_name);
		tvMainArtistName = (TextView) findViewById(R.id.tv_main_artist_name);

		// 播放按钮
		btnMainPlay = (ImageButton) findViewById(R.id.btn_main_play);

		btnMusic = (Button) findViewById(R.id.btn_music);
		btnArtist = (Button) findViewById(R.id.btn_artist);
		btnList = (Button) findViewById(R.id.btn_list);
		btnOnline = (Button) findViewById(R.id.btn_online);

		ivTitleCursor = (ImageView) findViewById(R.id.iv_title_cursor);

	}

	private void initViewPager() {
		mViewPager = (ViewPager) findViewById(R.id.main_view_pager);
		RelativeLayout.LayoutParams rlParams = (RelativeLayout.LayoutParams) mViewPager.getLayoutParams();
		rlParams.height = (int) (screenHeightPixels * 3.8 / 5);
		mViewPager.setLayoutParams(rlParams);
	}

	private void initPageViews() {
		mPageViews = new ArrayList<View>();
		View viewMusic = getLocalActivityManager().startActivity(ACTIVITY_MUSIC, new Intent(this, MusicActivity.class))
				.getDecorView();
		View viewArtist = getLocalActivityManager()
				.startActivity(ACTIVITY_ARTIST, new Intent(this, ArtistActivity.class)).getDecorView();
		View viewList = getLocalActivityManager()
				.startActivity(ACTIVITY_LIST, new Intent(this, MusicListActivity.class)).getDecorView();
		View viewOnline = getLocalActivityManager()
				.startActivity(ACTIVITY_ONLINE, new Intent(this, OnlineActivity.class)).getDecorView();
		mPageViews.add(viewMusic);
		mPageViews.add(viewArtist);
		mPageViews.add(viewList);
		mPageViews.add(viewOnline);
	}

	/**
	 * android.view.Window.etDecorView();
	 * 
	 * Retrieve the top-level window decor view (containing the standard window
	 * frame/decorations and the client's content inside of that), which can be
	 * added as a window to the window manager.
	 * 
	 * Note that calling this function for the first time "locks in" various
	 * window characteristics as described in setContentView(View,
	 * android.view.ViewGroup.LayoutParams).
	 * 
	 * Returns: Returns the top-level window decor view.
	 */
	public class CloseBroadcastReiceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
			btnMainPlay.setBackgroundResource(R.drawable.btn_main_play);
		}

	}

	private class MusicCompletionListener extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

		}

	}

	private class MusicProgressBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
		}

	}

	@Override
	public void onClick(View v) {

		if (v == rlMain) {
			Intent muiscPlayIntent = new Intent(MainActivityGroup.this, MusicPlayActivity.class);
			startActivity(muiscPlayIntent);
		}
		if (v == btnMainPlay) {
			if (musicList.size() > 0) {
				if (MusicPlayService.musicPlayer != null && MusicPlayService.musicPlayer.isPlaying()) {
					btnMainPlay.setBackgroundResource(R.drawable.btn_main_play);
				} else {
					btnMainPlay.setBackgroundResource(R.drawable.btn_main_pause);
				}

				// 播放
				Intent musicServiceIntent = new Intent(MainActivityGroup.this, MusicPlayService.class);
				musicServiceIntent.putExtra("play", 3);
				MusicNum.putplay(3);
				MusicNum.putisok(true);
				startService(musicServiceIntent);
			} else {
				String nosongs = this.getResources().getString(R.string.nosongs);
				Toast.makeText(getApplicationContext(), nosongs, Toast.LENGTH_SHORT).show();
			}
		}
	}

}
