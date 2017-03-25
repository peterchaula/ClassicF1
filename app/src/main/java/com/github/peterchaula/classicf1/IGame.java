package com.github.peterchaula.classicf1;

import com.github.peterchaula.classicf1.entity.Opponent;

public interface IGame {

	public void play(final float pSpeed);
	public void die();
	public void pause();
	public boolean isOver();
	public int getLevel();
	public void updateScore();
	public void score();
	public void startGame(final boolean pFromScratch);
	public void autoPlay(final float pSpeed);
	public void onCollisionDetected(final Opponent pOpponent);
	public float getSpeed();
	public void reset();
}
