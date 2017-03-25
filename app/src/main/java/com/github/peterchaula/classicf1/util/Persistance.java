package com.github.peterchaula.classicf1.util;

import android.content.Context;
import android.content.SharedPreferences;

public class Persistance {

	public static final String FILE_KEY = "com.w3wheels.olda.PREFS_KEY";
	public static final String HIGH_SCORE_KEY = "high_score";
	public static final String LEVEL_KEY = "gamel_level";

	private static SharedPreferences mSharedPrefs;

	public static int getHighScore(Context pContext) {
		mSharedPrefs = pContext.getSharedPreferences(FILE_KEY, Context.MODE_PRIVATE);
		return mSharedPrefs.getInt(HIGH_SCORE_KEY, 0);
	}

	public static void saveHighScore(Context pContext, int pHighScore) {
		mSharedPrefs = pContext.getSharedPreferences(FILE_KEY, Context.MODE_PRIVATE);
		int prevScore = mSharedPrefs.getInt(HIGH_SCORE_KEY, 0);

		if (prevScore < pHighScore) {
			SharedPreferences.Editor editor = mSharedPrefs.edit();
			editor.putInt(HIGH_SCORE_KEY, pHighScore).commit();
		}
	}

	public static int getGameLevel(Context pContext) {
		return pContext.getSharedPreferences(FILE_KEY, Context.MODE_PRIVATE).getInt(LEVEL_KEY, 1);
	}

	public static void saveLevel(Context pContext, final int pLevel) {
		mSharedPrefs = pContext.getSharedPreferences(FILE_KEY, Context.MODE_PRIVATE);
		mSharedPrefs.edit().putInt(LEVEL_KEY, pLevel).commit();
	}
}
