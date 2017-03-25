package com.github.peterchaula.classicf1.entity;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Racer extends Sprite{

	public int mLivesLeft;
	
	public Racer(final int pLives, final float pX, final float pY, final ITextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager
			) {
		super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
		mLivesLeft = pLives;
		setZIndex(1);
	}
	
	public boolean isDead(){
		return mLivesLeft == 0;
	}
	
	public void resetPosition(Opponent pOpponent, final float pLeftOffset, final float pRightOffset){
		if(pOpponent.getY() > 200f)
			setX(pOpponent.getX() == pRightOffset? pLeftOffset : pRightOffset);
	}

	public void switchSide(final float pLeftOffset, final float pRightOffset) {
		setX(getX() == pLeftOffset ? pRightOffset : pLeftOffset);
	}
	
}
