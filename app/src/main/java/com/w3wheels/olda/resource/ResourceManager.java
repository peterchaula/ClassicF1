/**
 * 
 */
/**
 * @author Peter
 *
 */
package com.w3wheels.olda.resource;

import java.io.IOException;

import org.andengine.audio.music.Music;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.font.StrokeFont;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.color.Color;

import android.graphics.Typeface;
import android.util.Log;

public class ResourceManager {

	private SimpleBaseGameActivity mContext;

	// Game
	public BitmapTextureAtlas mBackgroundTextureAtlas;
	public BitmapTextureAtlas mRaceCarTextureAtlas;
	public BitmapTextureAtlas mOpponentsTextureAtlas;
	public BitmapTextureAtlas mPauseTextureAtlas;

	public TextureRegion mBackgroundRegion;
	public TextureRegion mRaceCarRegion;
	public TextureRegion mOpponentsRegion;
//	public TextureRegion mPauseRegion;
//	public TextureRegion mPausePressedRegion;
	public TextureRegion mExplosionRegion;

//	public ITexture mHighScoreTexture;
//	public ITexture mScoreTexture;
//	public ITexture mLevelTexture;
//	public ITexture mGamePlayTexture;
//	public ITexture mFinalScoreTexture;

	public Font mGameFont;
	public StrokeFont mGamePlayFont;

	public Typeface mTypeface;

	// SplashScene
	BitmapTextureAtlas mSplashBackgroundTextureAtlas;

	public TextureRegion mSplashBackgroundRegion;

	// Menu
	public ITexture mMenuTexture;
	public Font mMenuFont;

	public Sound mExplosionSound;
	public Sound mMenuClickSound;
	public Music mGameMusic;

	private static final ResourceManager INSTANCE = new ResourceManager();

	private ResourceManager() {
	}

	public void loadGame() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		final BitmapTextureAtlas backgroundTextureAtlas = new BitmapTextureAtlas(mContext.getTextureManager(), 712, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mBackgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(backgroundTextureAtlas, mContext.getAssets(), "racetrack.png", 0, 0);
		backgroundTextureAtlas.load();

		final BitmapTextureAtlas raceCarTextureAtlas = new BitmapTextureAtlas(mContext.getTextureManager(), 128, 128, TextureOptions.BILINEAR);
		mRaceCarRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(raceCarTextureAtlas, mContext.getAssets(), "race_car.png", 0, 0);
		raceCarTextureAtlas.load();

		final BitmapTextureAtlas opponentsTextureAtlas = new BitmapTextureAtlas(mContext.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		mOpponentsRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(opponentsTextureAtlas, mContext.getAssets(), "race_car.png", 0, 0);
		mExplosionRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(opponentsTextureAtlas, mContext.getAssets(), "explosion.png", 128, 128);
		opponentsTextureAtlas.load();

		final BitmapTextureAtlas pauseTextureAtlas = new BitmapTextureAtlas(mContext.getTextureManager(), 128, 128, TextureOptions.BILINEAR);
//		mPauseRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(pauseTextureAtlas, mContext.getAssets(), "box.png", 0, 0);
//		mPausePressedRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(pauseTextureAtlas, mContext.getAssets(), "background_grass.png", 64, 64);
		pauseTextureAtlas.load();

		loadGameFonts();
		loadMenuFonts();
		
		try{
		loadMusic();	
		}catch(IOException e){
			Log.e("MUSIC_EXCEPTION", e.getMessage());
		}
	}

	public void loadMenu() {

	}

	public void loadMusic() throws IOException{
		SoundFactory.setAssetBasePath("mfx/");
		mExplosionSound = SoundFactory.createSoundFromAsset(mContext.getSoundManager(), mContext, "explosion.ogg");
		mMenuClickSound = SoundFactory.createSoundFromAsset(mContext.getSoundManager(), mContext, "fwapfwap.ogg");
		mExplosionSound.setVolume(0.2f);
		
	}
	public void loadSplash() {
		final BitmapTextureAtlas splashBackgroundTextureAtlas = new BitmapTextureAtlas(mContext.getTextureManager(), 512, 1024);
		mSplashBackgroundRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashBackgroundTextureAtlas, mContext.getAssets(), "", 0, 0);
		splashBackgroundTextureAtlas.load();
	}

	public void unloadGame() {
		unloadMenu();
		unloadGameFonts();
	}

	public void unloadMenu() {
		unloadMenuFonts();
		mMenuTexture.unload();
	}

	public void unloadSplash() {
		mSplashBackgroundTextureAtlas.unload();
	}

	public void loadGameFonts() {

		mTypeface = Typeface.createFromAsset(mContext.getAssets(), "LCD.ttf");
		final ITexture scoreTexture = new BitmapTextureAtlas(mContext.getTextureManager(), 256, 512, TextureOptions.BILINEAR);
		mGameFont = new Font(mContext.getFontManager(), scoreTexture, mTypeface, 18, true, new Color(Color.BLACK));
		scoreTexture.load();
		mGameFont.load();

		final BitmapTextureAtlas gamePlayTexture = new BitmapTextureAtlas(mContext.getTextureManager(), 512, 512, TextureOptions.BILINEAR);
		mGamePlayFont = new StrokeFont(mContext.getFontManager(), gamePlayTexture, mTypeface, 96, true, new Color(Color.WHITE), 0, new Color(Color.WHITE));
		gamePlayTexture.load();
		mGamePlayFont.load();
		Log.i("LOADING", "Loading fonts");

	}

	public void loadMenuMusic() {

	}

	public void loadMenusounds() {

	}

	public void loadMenuFonts() {

		FontFactory.setAssetBasePath("font/");
		final ITexture menuTexture = new BitmapTextureAtlas(mContext.getTextureManager(), 512, 512, TextureOptions.BILINEAR);
		mMenuFont = FontFactory.createFromAsset(mContext.getFontManager(), menuTexture, mContext.getAssets(), "LCD.ttf", 18f, false, android.graphics.Color.BLACK);
		menuTexture.load();
		mMenuFont.load();

	}

	public void unloadMenuFonts() {
		mMenuTexture.unload();
		mMenuFont.unload();
	}

	public void unloadGameFonts() {
		mGameFont.unload();
//		mScoreTexture.unload();
	}

	// ======================getters===============================
	public static ResourceManager getManager(SimpleBaseGameActivity pContext) {
		INSTANCE.mContext = pContext;
		pContext.getVertexBufferObjectManager();
		return INSTANCE;
	}
}