package com.github.peterchaula.classicf1;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.helllabs.android.xmp.ModPlayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.util.DisplayMetrics;

import com.github.peterchaula.classicf1.entity.Opponent;
import com.github.peterchaula.classicf1.entity.Racer;
import com.github.peterchaula.classicf1.resource.ResourceManager;
import com.github.peterchaula.classicf1.scene.GameScene;
import com.github.peterchaula.classicf1.util.Persistance;

public class GameActivity extends SimpleBaseGameActivity implements IGame, IOnSceneTouchListener, IOnAreaTouchListener {

    public static float CAMERA_WIDTH = 480f;
    public static float CAMERA_HEIGHT = 800f;

    Camera mCamera;
    GameScene mMainScene;
    MenuScene mMenuScene;
    ResourceManager mResourceManager;
    VertexBufferObjectManager vbom;
    TimerHandler mLevelHandler;
    Dialog mHelpDialog;

    public float mLeftOffset = 60f;
    public float mRightOffset;

    final static int GAME_STATE_DEFAULT = 0;
    final static int GAME_STATE_LOADING = 1;
    final static int GAME_STATE_RUNNING = 2;
    final static int GAME_STATE_PAUSED = 3;
    final static int GAME_STATE_DYING = 4;
    final static int GAME_STATE_WAITING = 5;

    final static float CAR_SEPERATION = 260f;
    final static float MIN_HEIGHT = -200f;

    public static float LEFT_OFFSET = 60f;
    public static float RIGHT_OFFSET = 195f;
    public static float GAME_SPEED = 4.0f;

    public static final int NUM_OF_LIVES = 3;

    float mCameraYPosition;
    int mCurrentGameState = GAME_STATE_DEFAULT;

    final Random mGenerator = new Random(47);
    final Random mBoolGenerator = new Random(47);

    public final ArrayList<Opponent> mRacers = new ArrayList<Opponent>();
    public final ArrayList<Opponent> mRecyclePool = new ArrayList<Opponent>();
    public final ArrayList<Opponent> mTestRacers = new ArrayList<Opponent>();

    public ModPlayer mModPlayer;
    Racer mRacer;
    int mLevel = 1;
    int mScore = 0;

    @Override
    public EngineOptions onCreateEngineOptions() {
        CAMERA_WIDTH = getScreenWidth(this, CAMERA_HEIGHT);
        mRightOffset = (CAMERA_WIDTH * GameScene.RACE_TRACK_WIDTH) / 2f + 20f;

        mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT) {

            float offsetY = 0;
            float lastPositionY = 0;

            @Override
            public void onUpdate(float pSecondsElapsed) {
                final float speed = getSpeed();

                switch (mCurrentGameState) {
                    case GAME_STATE_DEFAULT:
                        mCameraYPosition -= speed;
                        scrollBackground(lastPositionY, offsetY);
                        autoPlay(speed);
                        break;
                    case GAME_STATE_RUNNING:
                        mCameraYPosition -= speed;
                        scrollBackground(lastPositionY, offsetY);
                        play(speed);
                        break;
                    case GAME_STATE_DYING:

                        break;
                    case GAME_STATE_PAUSED:

                        break;
                    case GAME_STATE_LOADING:
                        startGame(true);
                        break;
                    case GAME_STATE_WAITING:
                        break;
                    default:
                        break;
                }

                super.onUpdate(pSecondsElapsed);
            }
        };

        EngineOptions mEngineOptions = new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), mCamera);
        mEngineOptions.getAudioOptions().setNeedsMusic(true);
        mEngineOptions.getAudioOptions().setNeedsSound(true);

        return mEngineOptions;
    }

    @Override
    public Engine onCreateEngine(EngineOptions pEngineOptions) {
        return super.onCreateEngine(pEngineOptions);
    }

    @Override
    protected void onCreateResources() {

        mResourceManager = ResourceManager.getManager(this);
        mResourceManager.loadGame();
        vbom = getVertexBufferObjectManager();
    }

    @Override
    protected Scene onCreateScene() {
        createHelpDialog();
        mMainScene = new GameScene(mResourceManager, mCamera, this);
        mMainScene.initViews(mResourceManager, mCamera);
        mMainScene.updateLevel(mLevel);
        mMainScene.setOnAreaTouchListener(this);
        mRacer = mMainScene.mRacer;
        mRacer.setX(LEFT_OFFSET);
        mLevelHandler = new TimerHandler(45.0f, true, new ITimerCallback() {

            @Override
            public void onTimePassed(TimerHandler pTimerHandler) {
                mEngine.getEngineLock().lock();
                if (mLevel <= 15)
                    mLevel += 1;

                mMainScene.updateLevel(mLevel);
                mEngine.getEngineLock().unlock();
            }
        });

        return mMainScene;
    }

    public final void scrollBackground(float pLastPosY, float pOffsetY) {
        final float currentPositionY = mCameraYPosition;

        if (currentPositionY != pLastPosY) {
            pOffsetY += (currentPositionY - pLastPosY);
            pLastPosY = currentPositionY;
            mMainScene.mBackground.setParallaxValue(pOffsetY);
        }
    }

    @Override
    public void play(final float pSpeed) {
        final int numOfRacers = mRacers.size();

        for (int i = 0; i < numOfRacers; i++) {
            final Opponent racer = mRacers.get(i);
            racer.move(pSpeed);

            if (i == numOfRacers - 1) {
                if (racer.getY() - (-200) > CAR_SEPERATION) {
                    spawnNewRacer();
                }
            }
        }

        if (numOfRacers > 0) {
            if (mRacers.get(0).collidesWith(mRacer)) {
                onCollisionDetected(mRacers.get(0));
            }
            if (mRacers.get(0).shouldBeRecycled(CAMERA_HEIGHT)) {
                recycle(mRacers.remove(0));
                score();
            }
        }
    }

    @Override
    public float getSpeed() {
        return GAME_SPEED + (mLevel * 0.7f);
    }

    @Override
    public void onCollisionDetected(final Opponent pOpponent) {
        mResourceManager.mExplosionSound.play();
        mCurrentGameState = GAME_STATE_WAITING;
        mMainScene.onCollision(pOpponent, LEFT_OFFSET, mRightOffset);
        TimerHandler delay;

        if (mRacer.mLivesLeft > 0) {
            mRacer.mLivesLeft -= 1;
            mMainScene.updateLives(mRacer.mLivesLeft);
            delay = new TimerHandler(2.0f, false, new ITimerCallback() {

                @Override
                public void onTimePassed(TimerHandler pTimerHandler) {
                    mCurrentGameState = GAME_STATE_RUNNING;
                    startGame(false);
                    mEngine.clearUpdateHandlers();
                }
            });
            mEngine.registerUpdateHandler(delay);
        } else {
            // game over stuff here
            die();
            delay = new TimerHandler(3.0f, false, new ITimerCallback() {
                @Override
                public void onTimePassed(TimerHandler pTimerHandler) {
                    reset();
                    mEngine.clearUpdateHandlers();
                }
            });
            mEngine.registerUpdateHandler(delay);
        }

    }

    public void onModMusicLoaded(ModPlayer pModPlayer, File src) {
        this.mModPlayer = pModPlayer;
        mModPlayer.setLooping(true);
        mModPlayer.play(src.getAbsolutePath());
    }

    @Override
    public void die() {
        mMainScene.unregisterUpdateHandler(mLevelHandler);
        mMainScene.showGameOverText(mScore, mCamera);
        Persistance.saveHighScore(this, mScore);
    }

    @Override
    public void reset() {
        mMainScene.onGameRestart();
        mMainScene.onGameReset(mEngine);
        mMainScene.updateHighScore(Persistance.getHighScore(this));
        mScore = 0;
        // reset scores
        mMainScene.updateScore(mScore);
        mMainScene.updateLives(NUM_OF_LIVES);

        mCurrentGameState = GAME_STATE_DEFAULT;
    }

    @Override
    public void pause() {

    }

    @Override
    public boolean isOver() {
        return false;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public void updateScore() {

    }

    @Override
    public void startGame(final boolean pFromScratch) {
        // clear screen
        dumpAll();
        mMainScene.updateLives(mRacer.mLivesLeft);
        if (pFromScratch) {
            mMainScene.setOnSceneTouchListener(this);
            mMainScene.updateScore(0);
            mMainScene.removePlayText();
            mMainScene.updateLevel(mLevel);
            GAME_SPEED = 4f;
            mMainScene.registerUpdateHandler(mLevelHandler);
        }
        mMainScene.onGameRestart();
        spawnNewRacer();
        mCurrentGameState = GAME_STATE_RUNNING;
    }

    @Override
    public void autoPlay(final float pSpeed) {
        mCurrentGameState = GAME_STATE_DEFAULT;
        final int numRacers = mRacers.size();

        if (numRacers == 0) {
            spawnNewRacer();
        }

        for (int i = 0; i < numRacers; i++) {
            mRacers.get(i).move(pSpeed);

            if (i == 0)
                mRacer.resetPosition(mRacers.get(i), LEFT_OFFSET, mRightOffset);
        }

        if (numRacers != 0) {
            if (mRacers.get(numRacers - 1).getY() - MIN_HEIGHT >= CAR_SEPERATION)
                spawnNewRacer();
            if (mRacers.get(0).shouldBeRecycled(CAMERA_HEIGHT)) {
                recycle(mRacers.remove(0));
            }
        }

    }

    public void spawnNewRacer() {
        final int seed = mGenerator.nextInt(20);
        int gapFactor = 0;

        if (seed > 18) {
            gapFactor = 3;
        } else if (seed > 13) {
            gapFactor = 2;
        } else if (seed > 9) {
            gapFactor = 1;
        }

        Opponent newRacer;
        // reuse sprites if got some in the trash
        if (mRecyclePool.size() != 0) {
            newRacer = mRecyclePool.remove(0).setYPos(MIN_HEIGHT - (gapFactor * CAR_SEPERATION));
            newRacer.setX(mBoolGenerator.nextBoolean() ? LEFT_OFFSET : mRightOffset);
            mMainScene.addNewRaceCar(newRacer, this);
            return;
        }

        mMainScene.addNewRaceCar(new Opponent(mBoolGenerator.nextBoolean() ? LEFT_OFFSET : mRightOffset, MIN_HEIGHT - (gapFactor * CAR_SEPERATION), mResourceManager.mOpponentsRegion, vbom), this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mModPlayer != null)
            mModPlayer.stop();

        mResourceManager.unloadGame();
    }

    @Override
    public void score() {
        mScore += mLevel;
        mMainScene.updateScore(mScore);
    }

    public void recycle(final Opponent pOpponent) {
        pOpponent.reset();
        pOpponent.setY(-500f + mGenerator.nextInt(50));
        mRecyclePool.add(pOpponent);
    }

    // ===============================Inner classes ====================
    @Override
    public boolean onAreaTouched(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) {
        if (pSceneTouchEvent.isActionDown()) {
            mResourceManager.mMenuClickSound.play();
            if ((Text) pTouchArea == mMainScene.mPlayText) {
                startGame(true);
                return true;
            } else if ((Text) pTouchArea == mMainScene.mHelpText) {
                GameActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        GameActivity.this.mHelpDialog.show();
                    }
                });
                return true;
            } else if ((Text) pTouchArea == mMainScene.mLevelText) {
                mLevel = !(mLevel > 14) ? mLevel += 1 : 1;
                mMainScene.updateLevel(mLevel);
                return true;
            }
        }

        return true;
    }

    @Override
    public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
        if (pSceneTouchEvent.isActionOutside()) {
            return true;
        }
        if (pSceneTouchEvent.isActionDown()) {
            switch (mCurrentGameState) {
                case GAME_STATE_RUNNING:
                    mRacer.switchSide(LEFT_OFFSET, mRightOffset);
                    return true;
            }

        }
        return false;
    }

    public void dumpAll() {
        mEngine.stop();
        for (final Opponent opp : mRacers) {
            opp.setY(-500f + mGenerator.nextInt(60));
            mRecyclePool.add(opp);
        }
        mRacers.clear();
        mEngine.start();
    }


    public void createHelpDialog() {
        this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
                builder.setView(getLayoutInflater().inflate(R.layout.help_dialog, null));
                mHelpDialog = builder.create();
            }
        });

    }

    public static float getScreenWidth(final Activity pContext, final float pWindowHeight) {

        DisplayMetrics metrics = new DisplayMetrics();
        pContext.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        final float displayWidth = metrics.widthPixels;
        final float displayHeight = metrics.heightPixels;
        final float aspectRatio = displayWidth / displayHeight;
        final float width = aspectRatio * pWindowHeight;
        return width;

    }
}
