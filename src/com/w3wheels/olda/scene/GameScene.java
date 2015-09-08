package com.w3wheels.olda.scene;

import java.util.Random;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import android.util.Log;

import com.w3wheels.olda.GameActivity;
import com.w3wheels.olda.entity.Opponent;
import com.w3wheels.olda.entity.Racer;
import com.w3wheels.olda.entity.VerticalParallaxBackground;
import com.w3wheels.olda.entity.VerticalParallaxBackground.VerticalParallaxEntity;
import com.w3wheels.olda.resource.ResourceManager;
import com.w3wheels.olda.util.Persistance;

public class GameScene extends Scene {

	GameActivity mContext;

	public Text mHighScore;
	public Text mScore;
	public Text mPlayText;
	public Text mHelpText;
	public Text mLevelText;
	public Text mLevel;
	public Text mLives;
	public Text mGameOverText;
	public Text mGameOverScoreText;

	public ButtonSprite mPauseButton;
	public Sprite mBackgroundSprite;
	public Sprite mExplosionSprite;
	public Racer mRacer;
	public Color mGameOverTextColor;

	public static final float RACE_TRACK_WIDTH = 0.65f;
	public VerticalParallaxBackground mBackground;

	public GameScene(final ResourceManager pResourceManager, final Camera pCamera, GameActivity pContext) {
		super();
		mContext = pContext;
		mBackgroundSprite = new Sprite(0, 0, pResourceManager.mBackgroundRegion, mContext.getVertexBufferObjectManager());
		mBackgroundSprite.setHeight(pCamera.getHeight() + 4);
		mBackgroundSprite.setWidth(pCamera.getWidth());

		mBackground = new VerticalParallaxBackground(193 / 255f, 208 / 255f, 170 / 255f) {

		};
		mBackground.attachVerticalParallaxEntity(new VerticalParallaxEntity(1.2f, mBackgroundSprite));
		setBackground(mBackground);
		mGameOverTextColor = new Color(253 / 255f, 255 / 255f, 225 / 255f);
	}

	public void initViews(final ResourceManager pResourceManager, final Camera pCamera) {

		final float cameraHeight = pCamera.getHeight();
		final float cameraWidth = pCamera.getWidth();

		mRacer = new Racer(GameActivity.NUM_OF_LIVES, cameraWidth, cameraHeight, pResourceManager.mRaceCarRegion, mContext.getVertexBufferObjectManager());
		mRacer.setY(cameraHeight - mRacer.getHeight() - 15f);

		final int textOffsetX = (int) (cameraWidth * RACE_TRACK_WIDTH) + 20;
		// scores
		mHighScore = new Text(textOffsetX, 100, pResourceManager.mGameFont, "High Score\n 0123456789", new TextOptions(HorizontalAlign.LEFT), mContext.getVertexBufferObjectManager());
		mHighScore.setText("High Score \n " + Persistance.getHighScore(mContext));
		mScore = new Text(textOffsetX, 180, pResourceManager.mGameFont, "Score \n0123456789", mContext.getVertexBufferObjectManager());
		mLives = new Text(textOffsetX, 260, pResourceManager.mGameFont, "Lives \n01234556789", mContext.getVertexBufferObjectManager());
		mLives.setText("Lives \n " + mRacer.mLivesLeft);
		mLevel = new Text(textOffsetX, 340, pResourceManager.mGameFont, "Level\n 0123456789", mContext.getVertexBufferObjectManager());

		mPauseButton = new ButtonSprite(textOffsetX + 10, 500, pResourceManager.mPauseRegion, pResourceManager.mPausePressedRegion, mContext.getVertexBufferObjectManager());
		mScore.setText("Score \n 0");

		// menus
		mPlayText = new Text(cameraWidth / 2, cameraHeight / 3, pResourceManager.mGamePlayFont, "Play", 5, new TextOptions(HorizontalAlign.CENTER), mContext.getVertexBufferObjectManager());
		mLevelText = new Text(cameraWidth / 2, cameraHeight / 3 + mPlayText.getHeight() + 40f, pResourceManager.mGamePlayFont, "Level", 5, new TextOptions(HorizontalAlign.CENTER),
				mContext.getVertexBufferObjectManager());
		mHelpText = new Text(cameraWidth / 2, cameraHeight / 3 + mLevelText.getHeight() + mPlayText.getHeight() + 80f, pResourceManager.mGamePlayFont, "Help", 4, new TextOptions(
				HorizontalAlign.CENTER), mContext.getVertexBufferObjectManager());

		// game over
		mExplosionSprite = new Sprite(-500f, mRacer.getY(), pResourceManager.mExplosionRegion, mContext.getVertexBufferObjectManager());
		mGameOverText = new Text(cameraWidth / 2, cameraHeight / 3, pResourceManager.mGamePlayFont, "Score", 5, new TextOptions(HorizontalAlign.CENTER), mContext.getVertexBufferObjectManager());
		mGameOverScoreText = new Text(cameraWidth / 2, cameraHeight / 3 + mGameOverText.getHeight() + 40, pResourceManager.mGamePlayFont, " 0123456789", 12, new TextOptions(HorizontalAlign.CENTER),
				mContext.getVertexBufferObjectManager());
		mExplosionSprite.setCullingEnabled(true);
		mExplosionSprite.setZIndex(3);

		mPlayText.setAlpha(0.7f);
		mHelpText.setAlpha(0.7f);
		mLevelText.setAlpha(0.7f);
		mGameOverText.setAlpha(0.7f);
		mGameOverScoreText.setAlpha(0.7f);
		mGameOverText.setColor(mGameOverTextColor);
		mGameOverScoreText.setColor(mGameOverTextColor);

		mPlayText.setZIndex(5);
		mHelpText.setZIndex(6);
		mGameOverText.setZIndex(7);
		mGameOverScoreText.setZIndex(7);

		placeCenter(mPlayText, pCamera);
		placeCenter(mHelpText, pCamera);
		placeCenter(mLevelText, pCamera);
		placeCenter(mGameOverText, pCamera);
		placeCenter(mGameOverScoreText, pCamera);

		registerTouchArea(mHelpText);
		registerTouchArea(mPlayText);
		registerTouchArea(mLevelText);
		registerTouchArea(mPauseButton);
		attachChild(mHelpText);
		attachChild(mPlayText);
		attachChild(mLevelText);
		attachChild(mHighScore);
		attachChild(mScore);
		attachChild(mLives);
		attachChild(mLevel);
		// attachChild(mPauseButton);
		attachChild(mRacer);
		attachChild(mExplosionSprite);
		sortChildren();

	}

	public void updateHighScore(final int pHighScore) {
		mHighScore.setText("High Score\n " + pHighScore);
	}

	public void updateScore(final int pScore) {
		mScore.setText("Score\n " + pScore);
	}

	public void updateLevel(final int pLevel) {
		mLevel.setText("Level\n " + pLevel);
	}

	public void updateLives(final int pLives) {
		mRacer.mLivesLeft = pLives;
		mLives.setText("Lives\n " + pLives);
	}

	public void showMenu(final MenuScene pMenuScene) {
		pMenuScene.reset();
		setChildScene(pMenuScene);
	}

	public void destroyMenu(final MenuScene pMenuScene) {
		pMenuScene.back();
	}

	public void showPlayText() {

		attachChild(mPlayText);
		attachChild(mHelpText);
		attachChild(mLevelText);
		registerTouchArea(mPlayText);
		registerTouchArea(mHelpText);
		registerTouchArea(mLevelText);
		sortChildren();
	}

	public void showGameOverText(final int pScore, final Camera pCamera) {
		mGameOverScoreText.setCharactersToDraw(String.valueOf(pScore).length());
		mGameOverScoreText.setText(String.valueOf(pScore));
		placeCenter(mGameOverScoreText, pCamera);
		attachChild(mGameOverText);
		attachChild(mGameOverScoreText);
		Log.d("GTXT", mGameOverScoreText.getWidth() + " " + mGameOverScoreText.getX());
		sortChildren();
	}

	public void onGameReset(final Engine pEngine) {
		pEngine.getEngineLock().lock();
		detachChild(mGameOverText);
		detachChild(mGameOverScoreText);
		showPlayText();
		pEngine.getEngineLock().unlock();
	}

	public void addNewRaceCar(final Opponent pRacer, final GameActivity pGame) {
		if (!pRacer.mAttached) {
			attachChild(pRacer);
			pRacer.mAttached = true;
		}
		pGame.mRacers.add(pRacer);
		sortChildren();
	}

	public void placeCenter(final Text pText, final Camera pCamera) {
		pText.setX(pCamera.getWidth() / 2 - pText.getWidth() / 2);
	}

	public void placeCenter(final Sprite pSprite, final Camera pCamera) {
		pSprite.setX(pCamera.getWidth() / 2 - pSprite.getWidth() / 2);
	}

	public void removePlayText() {

		unregisterTouchArea(mLevelText);
		unregisterTouchArea(mHelpText);
		unregisterTouchArea(mPlayText);

		detachChild(mPlayText);
		detachChild(mHelpText);
		detachChild(mLevelText);
		mLevelText.detachSelf();
		mHelpText.detachSelf();
		mPlayText.detachSelf();
		sortChildren();

	}

	public void onCollision(final Opponent pOpponent, final float pLeftOffset, final float pRightOffset) {

		if (Math.abs(mRacer.getY() - pOpponent.getY()) < mRacer.getHeight() / 2) {
			Log.i("X", "" + mRacer.getX());
			mRacer.setX(mRacer.getX() == pLeftOffset ? pLeftOffset + mRacer.getWidth() / 3 : pRightOffset - mRacer.getWidth() / 3);
		}

		mExplosionSprite.setX(mRacer.getX());
	}

	public void onGameRestart() {
		mExplosionSprite.setX(-500f);
		mRacer.setX(new Random().nextBoolean() ? GameActivity.LEFT_OFFSET : mContext.mRightOffset);
	}

}
