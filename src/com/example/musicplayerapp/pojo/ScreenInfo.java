package com.example.musicplayerapp.pojo;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * 得到屏幕宽高，密度等信息
 * 
 * @author Ben
 *
 */
public class ScreenInfo {
	private Activity activity;
	/** 屏幕宽度（像素） */
	private int widthPixels;
	/** 屏幕高度（像素） */
	private int heightPixels;
	/** 屏幕密度（0.75 / 1.0 / 1.5） */
	private float density;
	/** 屏幕密度DPI（120 / 160 / 240） */
	private int densityDpi;
	public Activity getActivity() {
		return activity;
	}
	public void setActivity(Activity activity) {
		this.activity = activity;
	}
	public int getWidthPixels() {
		return widthPixels;
	}
	public void setWidthPixels(int widthPixels) {
		this.widthPixels = widthPixels;
	}
	public int getHeightPixels() {
		return heightPixels;
	}
	public void setHeightPixels(int heightPixels) {
		this.heightPixels = heightPixels;
	}
	public float getDensity() {
		return density;
	}
	public void setDensity(float density) {
		this.density = density;
	}
	public int getDensityDpi() {
		return densityDpi;
	}
	public void setDensityDpi(int densityDpi) {
		this.densityDpi = densityDpi;
	}

	public ScreenInfo(Activity activity) {
		this.activity=activity;
		DisplayMetrics metric = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		widthPixels = metric.widthPixels;
		widthPixels = metric.heightPixels;
		density = metric.density;
		densityDpi = metric.densityDpi;
	}
	
	

}
